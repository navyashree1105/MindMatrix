package com.nammaSanthe.ledger.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nammaSanthe.ledger.data.model.Customer
import com.nammaSanthe.ledger.data.model.CustomerWithBalance
import com.nammaSanthe.ledger.data.model.DailySummary
import com.nammaSanthe.ledger.data.repository.LedgerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(private val repository: LedgerRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val customers: StateFlow<List<CustomerWithBalance>> = _searchQuery
        .debounce(100)
        .flatMapLatest { repository.getCustomerWithBalance(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val totalOutstanding: StateFlow<Double> = repository.getTotalOutstanding()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    private val todayRange = createTodayRange()
    val dailySummary: StateFlow<DailySummary> = repository.getDailySummary(todayRange.first, todayRange.second)
        .stateIn(viewModelScope, SharingStarted.Eagerly, DailySummary())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addCustomer(name: String, phone: String?) {
        viewModelScope.launch {
            repository.addCustomer(Customer(name = name.trim(), phone = phone?.trim().takeIf { !it.isNullOrBlank() }))
        }
    }

    private fun createTodayRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val end = calendar.timeInMillis
        return start to end
    }
}

class HomeViewModelFactory(private val repository: LedgerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
