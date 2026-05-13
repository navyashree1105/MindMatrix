package com.nammaSanthe.ledger.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nammaSanthe.ledger.data.model.Customer
import com.nammaSanthe.ledger.data.model.CustomerWithBalance
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Insert
    suspend fun insert(customer: Customer): Long

    @Update
    suspend fun update(customer: Customer)

    @Delete
    suspend fun delete(customer: Customer)

    @Query("SELECT * FROM Customer WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: Int): Customer?

    @Query(
        "SELECT c.*, COALESCE(SUM(CASE WHEN t.type = 'CREDIT' THEN t.amount ELSE 0 END), 0) - " +
            "COALESCE(SUM(CASE WHEN t.type = 'PAYMENT' THEN t.amount ELSE 0 END), 0) AS balance " +
            "FROM Customer c LEFT JOIN LedgerTransaction t ON c.id = t.customerId " +
            "WHERE c.name LIKE '%' || :query || '%' GROUP BY c.id ORDER BY balance DESC, c.name ASC"
    )
    fun getCustomerWithBalance(query: String): Flow<List<CustomerWithBalance>>

    @Query("SELECT * FROM Customer WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchCustomersByName(query: String): Flow<List<Customer>>
}
