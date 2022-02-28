package de.erikspall.audiobookapp.domain.util.audiobook.order

sealed class OrderType {
    object Ascending: OrderType()
    object Descending: OrderType()
}
