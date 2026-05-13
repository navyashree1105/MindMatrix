package com.nammaSanthe.ledger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LedgerTransaction")
data class LedgerTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerId: Int,
    val amount: Double,
    val type: String,
    val note: String?,
    val date: Long = System.currentTimeMillis()
)
