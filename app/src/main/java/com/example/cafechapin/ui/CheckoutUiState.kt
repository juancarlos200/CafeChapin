package com.example.cafechapin.ui

import com.example.cafechapin.domain.validateBusinessName
import com.example.cafechapin.domain.validateFullName
import com.example.cafechapin.domain.validateNit
import com.example.cafechapin.domain.validatePhoneNumber
import com.example.cafechapin.model.BillingType
import com.example.cafechapin.model.PaymentMethod

data class CheckoutUiState(
    val fullName: String = "",
    val fullNameTouched: Boolean = false,
    val phone: String = "",
    val phoneTouched: Boolean = false,
    val billingType: BillingType = BillingType.CF,
    val nit: String = "",
    val nitTouched: Boolean = false,
    val businessName: String = "",
    val businessNameTouched: Boolean = false,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH_ON_DELIVERY
) {
    val fullNameError: String?
        get() = validateFullName(fullName)

    val phoneError: String?
        get() = validatePhoneNumber(phone)

    val nitError: String?
        get() {
            if (billingType != BillingType.NIT) {
                return null
            }

            return validateNit(nit)
        }

    val businessNameError: String?
        get() {
            if (billingType != BillingType.NIT) {
                return null
            }

            return validateBusinessName(businessName)
        }

    val isFormValid: Boolean
        get() {
            val basicDataValid = fullNameError == null && phoneError == null

            if (billingType == BillingType.NIT) {
                return basicDataValid && nitError == null && businessNameError == null
            }

            return basicDataValid
        }
}
