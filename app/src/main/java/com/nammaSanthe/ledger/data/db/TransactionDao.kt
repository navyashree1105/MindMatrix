package com.nammaSanthe.ledger.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nammaSanthe.ledger.data.model.DailySummary
import com.nammaSanthe.ledger.data.model.LedgerTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: LedgerTransaction): Long

    @Update
    suspend fun update(transaction: LedgerTransaction)

    @Delete
    suspend fun delete(transaction: LedgerTransaction)

    @Query("SELECT * FROM LedgerTransaction WHERE customerId = :customerId ORDER BY date DESC")
    fun getTransactionsByCustomer(customerId: Int): Flow<List<LedgerTransaction>>

    @Query(
        "SELECT COALESCE(SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE 0 END), 0) - " +
            "COALESCE(SUM(CASE WHEN type = 'PAYMENT' THEN amount ELSE 0 END), 0) FROM LedgerTransaction " +
            "WHERE customerId = :customerId"
    )
    fun getBalanceForCustomer(customerId: Int): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE 0 END), 0) - " +
            "COALESCE(SUM(CASE WHEN type = 'PAYMENT' THEN amount ELSE 0 END), 0) FROM LedgerTransaction"
    )
    fun getTotalOutstanding(): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE 0 END), 0) AS totalSold, " +
            "COALESCE(SUM(CASE WHEN type = 'PAYMENT' THEN amount ELSE 0 END), 0) AS totalReceived " +
            "FROM LedgerTransaction WHERE date >= :dayStart AND date < :dayEnd"
    )
    fun getDailySummary(dayStart: Long, dayEnd: Long): Flow<DailySummary>
}
