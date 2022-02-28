package de.erikspall.audiobookapp.domain.util.audiobook.order

sealed class AudiobookOrder(val orderType: OrderType) {
    class Title(orderType: OrderType): AudiobookOrder(orderType)
    class Progress(orderType: OrderType): AudiobookOrder(orderType)
    class Author(orderType: OrderType): AudiobookOrder(orderType)
    class Narrator(orderType: OrderType): AudiobookOrder(orderType)
    class Duration(orderType: OrderType): AudiobookOrder(orderType)
}