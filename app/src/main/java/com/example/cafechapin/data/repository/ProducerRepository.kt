package com.example.cafechapin.data.repository

import com.example.cafechapin.model.ProducerProfile
import kotlinx.coroutines.flow.Flow

interface ProducerRepository {
    fun observeProducers(): Flow<List<ProducerProfile>>
}
