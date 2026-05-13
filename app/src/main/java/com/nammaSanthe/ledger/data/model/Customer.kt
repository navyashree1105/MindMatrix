package com.nammaSanthe.ledger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String?,
    val createdAt: Long = System.currentTimeMillis()
)
