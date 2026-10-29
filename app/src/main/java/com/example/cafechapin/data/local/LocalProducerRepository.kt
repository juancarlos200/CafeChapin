package com.example.cafechapin.data.local

import com.example.cafechapin.data.repository.ProducerRepository
import com.example.cafechapin.model.ProducerProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class LocalProducerRepository : ProducerRepository {

    private val producers = listOf(
        ProducerProfile(
            id = "producer_1",
            name = "Finca Los Volcanes",
            role = "Productor de café",
            location = "Antigua Guatemala",
            description = "Finca familiar dedicada al cultivo responsable de café de especialidad."
        ),
        ProducerProfile(
            id = "producer_2",
            name = "Cooperativa La Sierra",
            role = "Cooperativa productora",
            location = "Huehuetenango",
            description = "Grupo de productores especializados en café cultivado a gran altura."
        )
    )

    override fun observeProducers(): Flow<List<ProducerProfile>> = flowOf(producers)
}
