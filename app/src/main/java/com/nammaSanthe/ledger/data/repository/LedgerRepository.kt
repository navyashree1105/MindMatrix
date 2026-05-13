package com.nammaSanthe.ledger.data.repository

import com.nammaSanthe.ledger.data.db.AppDatabase
import com.nammaSanthe.ledger.data.model.Customer
import com.nammaSanthe.ledger.data.model.CustomerWithBalance
import com.nammaSanthe.ledger.data.model.DailySummary
import com.nammaSanthe.ledger.data.model.LedgerTransaction
import kotlinx.coroutines.flow.Flow

class LedgerRepository(private val database: AppDatabase) {
    private val customerDao = database.customerDao()
    private val transactionDao = database.transactionDao()

    suspend fun addCustomer(customer: Customer): Long = customerDao.insert(customer)
    suspend fun updateCustomer(customer: Customer) = customerDao.update(customer)
    suspend fun deleteCustomer(customer: Customer) = customerDao.delete(customer)
    suspend fun getCustomerById(id: Int): Customer? = customerDao.getCustomerById(id)

    fun getCustomerWithBalance(query: String): Flow<List<CustomerWithBalance>> {
        return customerDao.getCustomerWithBalance(query)
    }

    fun searchCustomers(query: String): Flow<List<Customer>> {
        return customerDao.searchCustomersByName(query)
    }

    suspend fun addTransaction(transaction: LedgerTransaction): Long = transactionDao.insert(transaction)
    suspend fun updateTransaction(transaction: LedgerTransaction) = transactionDao.update(transaction)
    suspend fun deleteTransaction(transaction: LedgerTransaction) = transactionDao.delete(transaction)

    fun getTransactionsForCustomer(customerId: Int): Flow<List<LedgerTransaction>> =
        transactionDao.getTransactionsByCustomer(customerId)

    fun getBalanceForCustomer(customerId: Int): Flow<Double> = transactionDao.getBalanceForCustomer(customerId)

    fun getTotalOutstanding(): Flow<Double> = transactionDao.getTotalOutstanding()

    fun getDailySummary(dayStart: Long, dayEnd: Long): Flow<DailySummary> =
        transactionDao.getDailySummary(dayStart, dayEnd)
}
