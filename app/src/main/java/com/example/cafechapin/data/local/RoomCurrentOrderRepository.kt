package com.example.cafechapin.data.local

import com.example.cafechapin.data.repository.CurrentOrderRepository
import com.example.cafechapin.model.OrderItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class RoomCurrentOrderRepository(
    private val orderLineDao: OrderLineDao
) : CurrentOrderRepository {

    private val mutex = Mutex()

    override fun observeItems(): Flow<List<OrderItem>> {
        return orderLineDao.getOrderLines().map { orderLines ->
            orderLines.map {
                OrderItem(
                    productId = it.productId,
                    quantity = it.quantity
                )
            }
        }
    }

    override suspend fun addUnit(productId: String): Result<Unit> {
        return runLocal {
            mutex.withLock {
                val quantity = orderLineDao.getOrderLine(productId)?.quantity ?: 0

                orderLineDao.insert(
                    OrderLineEntity(
                        productId = productId,
                        quantity = quantity + 1
                    )
                )
            }
        }
    }

    override suspend fun removeUnit(productId: String): Result<Int> {
        return runLocal {
            mutex.withLock {
                val quantity = orderLineDao.getOrderLine(productId)?.quantity ?: 0

                if (quantity <= 1) {
                    orderLineDao.deleteByProductId(productId)
                } else {
                    orderLineDao.insert(
                        OrderLineEntity(
                            productId = productId,
                            quantity = quantity - 1
                        )
                    )
                }

                minOf(quantity, 1)
            }
        }
    }

    override suspend fun removeLine(productId: String): Result<Int> {
        return runLocal {
            mutex.withLock {
                val quantity = orderLineDao.getOrderLine(productId)?.quantity ?: 0
                orderLineDao.deleteByProductId(productId)
                quantity
            }
        }
    }

    override suspend fun clear(): Result<Unit> {
        return runLocal {
            mutex.withLock {
                orderLineDao.clearOrder()
            }
        }
    }
}
