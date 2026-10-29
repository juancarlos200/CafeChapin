package com.example.cafechapin

import android.content.Context
import com.example.cafechapin.data.firestore.FirebaseAuthRepository
import com.example.cafechapin.data.firestore.FirestoreProductRepository
import com.example.cafechapin.data.local.DataStorePreferencesRepository
import com.example.cafechapin.data.local.LocalProducerRepository
import com.example.cafechapin.data.local.RoomCurrentOrderRepository
import com.example.cafechapin.data.local.RoomFavoriteRepository
import com.example.cafechapin.data.local.StoreDatabase
import com.example.cafechapin.data.local.ThemePreferences
import com.example.cafechapin.data.remote.MockApiOrderRepository
import com.example.cafechapin.data.remote.StoreApi
import com.example.cafechapin.data.repository.AuthRepository
import com.example.cafechapin.data.repository.CurrentOrderRepository
import com.example.cafechapin.data.repository.FavoriteRepository
import com.example.cafechapin.data.repository.OrderRepository
import com.example.cafechapin.data.repository.PreferencesRepository
import com.example.cafechapin.data.repository.ProducerRepository
import com.example.cafechapin.data.repository.ProductRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class AppContainer(context: Context) {

    private val database = StoreDatabase.getDatabase(context)

    val authRepository: AuthRepository =
        FirebaseAuthRepository(Firebase.auth)

    val productRepository: ProductRepository =
        FirestoreProductRepository(Firebase.firestore)

    val producerRepository: ProducerRepository =
        LocalProducerRepository()

    val favoriteRepository: FavoriteRepository =
        RoomFavoriteRepository(database.favoriteDao())

    val currentOrderRepository: CurrentOrderRepository =
        RoomCurrentOrderRepository(database.orderLineDao())

    val preferencesRepository: PreferencesRepository =
        DataStorePreferencesRepository(ThemePreferences(context))

    val orderRepository: OrderRepository =
        MockApiOrderRepository(StoreApi.createOrderApi())
}
