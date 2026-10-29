package com.example.cafechapin.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cafechapin.CafeChapinApplication
import com.example.cafechapin.data.repository.AuthRepository
import com.example.cafechapin.data.repository.CurrentOrderRepository
import com.example.cafechapin.data.repository.FavoriteRepository
import com.example.cafechapin.data.repository.OrderRepository
import com.example.cafechapin.data.repository.PreferencesRepository
import com.example.cafechapin.data.repository.ProducerRepository
import com.example.cafechapin.data.repository.ProductRepository
import com.example.cafechapin.domain.validateReservation
import com.example.cafechapin.model.BillingType
import com.example.cafechapin.model.OrderReceipt
import com.example.cafechapin.model.PaymentMethod
import com.example.cafechapin.model.ProductSort
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val CATALOG_TIMEOUT_MILLIS = 15_000L

class StoreViewModel(
    private val authRepository: AuthRepository,
    private val productRepository: ProductRepository,
    private val producerRepository: ProducerRepository,
    private val favoriteRepository: FavoriteRepository,
    private val currentOrderRepository: CurrentOrderRepository,
    private val preferencesRepository: PreferencesRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoreUiState())

    val uiState: StateFlow<StoreUiState> = _uiState.asStateFlow()

    private val _submitState =
        MutableStateFlow<SubmitOrderUiState>(SubmitOrderUiState.Idle)

    val submitState: StateFlow<SubmitOrderUiState> = _submitState.asStateFlow()

    private var catalogJob: Job? = null

    private var catalogTimeoutJob: Job? = null

    init {
        viewModelScope.launch {
            producerRepository.observeProducers().collect { profiles ->
                _uiState.update { it.copy(profiles = profiles) }
            }
        }

        viewModelScope.launch {
            favoriteRepository.observeFavoriteIds().collect { favoriteIds ->
                _uiState.update { it.copy(favoriteProductIds = favoriteIds) }
            }
        }

        viewModelScope.launch {
            currentOrderRepository.observeItems().collect { items ->
                _uiState.update { it.copy(orderItems = items) }
            }
        }

        viewModelScope.launch {
            preferencesRepository.observeDarkTheme().collect { isDarkTheme ->
                _uiState.update { it.copy(isDarkTheme = isDarkTheme) }
            }
        }

        loadCatalog()
    }

    fun loadCatalog() {
        catalogJob?.cancel()
        catalogTimeoutJob?.cancel()

        _uiState.update { it.copy(catalogState = CatalogUiState.Loading) }

        catalogJob = viewModelScope.launch {
            val session = authRepository.ensureSignedIn()
            val sessionError = session.exceptionOrNull()

            if (sessionError != null) {
                showCatalogError(sessionError.toUserMessage())
                return@launch
            }

            productRepository.observeProducts()
                .catch { error ->
                    showCatalogError(error.toUserMessage())
                }
                .collect { products ->
                    catalogTimeoutJob?.cancel()
                    _uiState.update {
                        it.copy(
                            products = products,
                            catalogState = CatalogUiState.Ready
                        )
                    }
                }
        }

        catalogTimeoutJob = viewModelScope.launch {
            delay(CATALOG_TIMEOUT_MILLIS)

            if (_uiState.value.catalogState == CatalogUiState.Loading) {
                catalogJob?.cancel()
                showCatalogError("El catálogo tardó demasiado en cargar. Revisa tu conexión.")
            }
        }
    }

    private fun showCatalogError(message: String) {
        catalogTimeoutJob?.cancel()
        _uiState.update {
            it.copy(catalogState = CatalogUiState.Error(message))
        }
    }

    fun toggleFavorite(productId: String) {
        val isFavorite = productId in _uiState.value.favoriteProductIds

        viewModelScope.launch {
            favoriteRepository.setFavorite(productId, !isFavorite)
        }
    }

    fun toggleDarkTheme() {
        val isDarkTheme = _uiState.value.isDarkTheme

        viewModelScope.launch {
            preferencesRepository.setDarkTheme(!isDarkTheme)
        }
    }

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun updateSort(sort: ProductSort) {
        _uiState.update { it.copy(sort = sort) }
    }

    fun addProductToOrder(productId: String) {
        reserveUnitForOrder(
            productId = productId,
            successMessage = "Se reservó 1 unidad para tu pedido."
        )
    }

    fun increaseOrderItem(productId: String) {
        reserveUnitForOrder(
            productId = productId,
            successMessage = null
        )
    }

    private fun reserveUnitForOrder(
        productId: String,
        successMessage: String?
    ) {
        val currentState = _uiState.value

        if (productId in currentState.reservingProductIds) {
            return
        }

        val validationError = validateReservation(
            currentState.products.find { it.id == productId }
        )

        if (validationError != null) {
            _uiState.update { it.copy(orderMessage = validationError) }
            return
        }

        _uiState.update {
            it.copy(
                reservingProductIds = it.reservingProductIds + productId,
                orderMessage = null
            )
        }

        viewModelScope.launch {
            val message = productRepository.reserveUnit(productId).fold(
                onSuccess = {
                    currentOrderRepository.addUnit(productId).fold(
                        onSuccess = { successMessage },
                        onFailure = { error ->
                            productRepository.releaseUnits(productId, 1)
                            error.toUserMessage()
                        }
                    )
                },
                onFailure = { error ->
                    error.toUserMessage()
                }
            )

            _uiState.update {
                it.copy(
                    reservingProductIds = it.reservingProductIds - productId,
                    orderMessage = message
                )
            }
        }
    }

    fun decreaseOrderItem(productId: String) {
        _uiState.update { it.copy(orderMessage = null) }

        viewModelScope.launch {
            currentOrderRepository.removeUnit(productId).fold(
                onSuccess = { releasedUnits ->
                    releaseUnits(productId, releasedUnits)
                },
                onFailure = { error ->
                    _uiState.update { it.copy(orderMessage = error.toUserMessage()) }
                }
            )
        }
    }

    fun removeOrderItem(productId: String) {
        _uiState.update { it.copy(orderMessage = null) }

        viewModelScope.launch {
            currentOrderRepository.removeLine(productId).fold(
                onSuccess = { releasedUnits ->
                    releaseUnits(productId, releasedUnits)
                },
                onFailure = { error ->
                    _uiState.update { it.copy(orderMessage = error.toUserMessage()) }
                }
            )
        }
    }

    private suspend fun releaseUnits(productId: String, quantity: Int) {
        productRepository.releaseUnits(productId, quantity).onFailure { error ->
            _uiState.update { it.copy(orderMessage = error.toUserMessage()) }
        }
    }

    fun onConcurrencyTest(productId: String, attempts: Int) {
        if (_uiState.value.isConcurrencyTestRunning) {
            return
        }

        _uiState.update {
            it.copy(
                isConcurrencyTestRunning = true,
                testResult = null
            )
        }

        viewModelScope.launch {
            val results = List(attempts) {
                async { productRepository.reserveUnit(productId) }
            }.awaitAll()

            val successes = results.count { it.isSuccess }

            _uiState.update {
                it.copy(
                    isConcurrencyTestRunning = false,
                    testResult = "Éxitos: $successes · Rechazos: ${attempts - successes}"
                )
            }
        }
    }

    fun onFullNameChange(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                checkout = currentState.checkout.copy(
                    fullName = value,
                    fullNameTouched = true
                )
            )
        }
    }

    fun onPhoneChange(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                checkout = currentState.checkout.copy(
                    phone = value,
                    phoneTouched = true
                )
            )
        }
    }

    fun onNitChange(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                checkout = currentState.checkout.copy(
                    nit = value,
                    nitTouched = true
                )
            )
        }
    }

    fun onBusinessNameChange(value: String) {
        _uiState.update { currentState ->
            currentState.copy(
                checkout = currentState.checkout.copy(
                    businessName = value,
                    businessNameTouched = true
                )
            )
        }
    }

    fun onBillingTypeChange(billingType: BillingType) {
        _uiState.update { currentState ->
            val currentCheckout = currentState.checkout

            val updatedCheckout = if (billingType == BillingType.CF) {
                currentCheckout.copy(
                    billingType = BillingType.CF,
                    nitTouched = false,
                    businessNameTouched = false
                )
            } else {
                currentCheckout.copy(
                    billingType = BillingType.NIT
                )
            }

            currentState.copy(checkout = updatedCheckout)
        }
    }

    fun onPaymentMethodChange(paymentMethod: PaymentMethod) {
        _uiState.update { currentState ->
            currentState.copy(
                checkout = currentState.checkout.copy(
                    paymentMethod = paymentMethod
                )
            )
        }
    }

    fun confirmOrder() {
        val currentState = _uiState.value
        val checkout = currentState.checkout

        if (_submitState.value == SubmitOrderUiState.Sending) {
            return
        }

        if (!checkout.isFormValid || currentState.orderUnits <= 0) {
            return
        }

        _submitState.value = SubmitOrderUiState.Sending

        viewModelScope.launch {
            orderRepository.submitOrder(
                items = currentState.orderItems,
                products = currentState.products,
                billingType = checkout.billingType,
                paymentMethod = checkout.paymentMethod
            ).fold(
                onSuccess = { order ->
                    currentOrderRepository.clear()

                    val isNit = checkout.billingType == BillingType.NIT

                    val receipt = OrderReceipt(
                        folio = "#${order.id}",
                        customerName = checkout.fullName,
                        billingType = checkout.billingType,
                        nit = if (isNit) checkout.nit else null,
                        businessName = if (isNit) checkout.businessName else null,
                        paymentMethod = checkout.paymentMethod,
                        total = order.total
                    )

                    _uiState.update {
                        it.copy(
                            checkout = CheckoutUiState(),
                            receipt = receipt
                        )
                    }

                    _submitState.value = SubmitOrderUiState.Success
                },
                onFailure = { error ->
                    _submitState.value = SubmitOrderUiState.Error(error.toUserMessage())
                }
            )
        }
    }

    fun onSubmitHandled() {
        _submitState.value = SubmitOrderUiState.Idle
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CafeChapinApplication
                val container = application.container

                StoreViewModel(
                    authRepository = container.authRepository,
                    productRepository = container.productRepository,
                    producerRepository = container.producerRepository,
                    favoriteRepository = container.favoriteRepository,
                    currentOrderRepository = container.currentOrderRepository,
                    preferencesRepository = container.preferencesRepository,
                    orderRepository = container.orderRepository
                )
            }
        }
    }
}
