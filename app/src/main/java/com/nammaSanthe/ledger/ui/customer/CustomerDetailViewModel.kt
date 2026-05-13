package com.nammaSanthe.ledger.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nammaSanthe.ledger.data.model.Customer
import com.nammaSanthe.ledger.data.model.LedgerTransaction
import com.nammaSanthe.ledger.data.repository.LedgerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CustomerDetailViewModel(
    private val repository: LedgerRepository,
    private val customerId: Int
) : ViewModel() {
    private val _customer = MutableStateFlow<Customer?>(null)
    val customer: StateFlow<Customer?> = _customer

    val transactions: StateFlow<List<LedgerTransaction>> = repository.getTransactionsForCustomer(customerId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val netBalance: StateFlow<Double> = repository.getBalanceForCustomer(customerId)
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    init {
        viewModelScope.launch {
            _customer.value = repository.getCustomerById(customerId)
        }
    }

    fun addTransaction(amount: Double, type: String, note: String?) {
        viewModelScope.launch {
            val transaction = LedgerTransaction(
                customerId = customerId,
                amount = amount,
                type = type,
                note = note?.takeIf { it.isNotBlank() }
            )
            repository.addTransaction(transaction)
        }
    }
}

class CustomerDetailViewModelFactory(
    private val repository: LedgerRepository,
    private val customerId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CustomerDetailViewModel(repository, customerId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
