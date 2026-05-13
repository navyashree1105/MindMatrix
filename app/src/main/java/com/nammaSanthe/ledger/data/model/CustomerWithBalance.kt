package com.nammaSanthe.ledger.data.model

import androidx.room.Embedded

data class CustomerWithBalance(
    @Embedded val customer: Customer,
    val balance: Double
)
