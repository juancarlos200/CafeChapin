package com.example.cafechapin.data.remote

import com.example.cafechapin.data.repository.OrderRepository
import com.example.cafechapin.model.BillingType
import com.example.cafechapin.model.CoffeeProduct
import com.example.cafechapin.model.Order
import com.example.cafechapin.model.OrderItem
import com.example.cafechapin.model.PaymentMethod
import com.example.cafechapin.model.StoreError
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

private const val HTTP_NOT_FOUND = 404

class MockApiOrderRepository(
    private val orderApi: OrderApi
) : OrderRepository {

    override suspend fun submitOrder(
        items: List<OrderItem>,
        products: List<CoffeeProduct>,
        billingType: BillingType,
        paymentMethod: PaymentMethod
    ): Result<Order> {
        val order = createOrderDto(
            items = items,
            products = products,
            billingType = billingType,
            paymentMethod = paymentMethod,
            createdAt = System.currentTimeMillis()
        )

        return runRemote {
            orderApi.createOrder(order).toOrder()
        }
    }

    override suspend fun getOrders(): Result<List<Order>> {
        val result = runRemote {
            orderApi.getOrders().map { it.toOrder() }
        }

        return if (result.exceptionOrNull() is StoreError.NotFound) {
            Result.success(emptyList())
        } else {
            result
        }
    }

    override suspend fun getOrder(orderId: String): Result<Order> {
        return runRemote {
            orderApi.getOrder(orderId).toOrder()
        }
    }

    private suspend fun <T> runRemote(block: suspend () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: IOException) {
            Result.failure(StoreError.NoConnection(e))
        } catch (e: HttpException) {
            if (e.code() == HTTP_NOT_FOUND) {
                Result.failure(StoreError.NotFound(e))
            } else {
                Result.failure(StoreError.Server(e))
            }
        } catch (e: SerializationException) {
            Result.failure(StoreError.UnexpectedResponse(e))
        }
    }
}
