package com.example.cafechapin.data.firestore

import com.example.cafechapin.model.CoffeeProduct
import com.google.firebase.firestore.DocumentSnapshot

const val PRODUCTS_COLLECTION = "products"
const val NAME_FIELD = "name"
const val DESCRIPTION_FIELD = "description"
const val PRICE_FIELD = "price"
const val PRODUCER_ID_FIELD = "producerId"
const val TECHNICAL_INFO_FIELD = "technicalInfo"
const val STOCK_FIELD = "stock"
const val IMAGE_URL_FIELD = "imageUrl"

fun DocumentSnapshot.toCoffeeProduct(): CoffeeProduct? {
    val name = getString(NAME_FIELD) ?: return null
    val price = getDouble(PRICE_FIELD) ?: return null
    val stock = getLong(STOCK_FIELD) ?: return null

    return CoffeeProduct(
        id = id,
        name = name,
        description = getString(DESCRIPTION_FIELD).orEmpty(),
        price = price,
        producerId = getString(PRODUCER_ID_FIELD).orEmpty(),
        technicalInfo = getString(TECHNICAL_INFO_FIELD).orEmpty(),
        stock = stock.toInt(),
        imageUrl = getString(IMAGE_URL_FIELD).orEmpty()
    )
}
