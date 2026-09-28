package com.example.cafechapin.domain

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CheckoutValidatorsTest {

    @Test
    fun fullName_empty_isInvalid() {
        assertNotNull(validateFullName(""))
    }

    @Test
    fun fullName_tooShort_isInvalid() {
        assertNotNull(validateFullName("Jo"))
    }

    @Test
    fun fullName_withAccent_isValid() {
        assertNull(validateFullName("José"))
    }

    @Test
    fun fullName_withSpace_isValid() {
        assertNull(validateFullName("Ana María"))
    }

    @Test
    fun fullName_withDigit_isInvalid() {
        assertNotNull(validateFullName("Ana2"))
    }

    @Test
    fun phone_sevenDigits_isInvalid() {
        assertNotNull(validatePhoneNumber("1234567"))
    }

    @Test
    fun phone_eightDigits_isValid() {
        assertNull(validatePhoneNumber("12345678"))
    }

    @Test
    fun phone_withSpace_isInvalid() {
        assertNotNull(validatePhoneNumber("1234 5678"))
    }

    @Test
    fun phone_nineDigits_isInvalid() {
        assertNotNull(validatePhoneNumber("123456789"))
    }

    @Test
    fun nit_fourDigits_isInvalid() {
        assertNotNull(validateNit("4512"))
    }

    @Test
    fun nit_fiveDigits_isValid() {
        assertNull(validateNit("45123"))
    }

    @Test
    fun nit_withLetter_isInvalid() {
        assertNotNull(validateNit("4512A"))
    }

    @Test
    fun businessName_tooShortAfterTrim_isInvalid() {
        assertNotNull(validateBusinessName(" AB "))
    }

    @Test
    fun businessName_validAfterTrim_isValid() {
        assertNull(validateBusinessName(" ABC "))
    }
}
