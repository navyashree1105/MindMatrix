package com.nammaSanthe.ledger

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nammaSanthe.ledger.data.repository.LedgerRepository
import com.nammaSanthe.ledger.data.db.AppDatabase
import com.nammaSanthe.ledger.ui.add.AddCustomerScreen
import com.nammaSanthe.ledger.ui.customer.CustomerDetailScreen
import com.nammaSanthe.ledger.ui.customer.CustomerDetailViewModel
import com.nammaSanthe.ledger.ui.customer.CustomerDetailViewModelFactory
import com.nammaSanthe.ledger.ui.home.HomeScreen
import com.nammaSanthe.ledger.ui.home.HomeViewModel
import com.nammaSanthe.ledger.ui.home.HomeViewModelFactory

object Routes {
    const val Home = "home"
    const val AddCustomer = "addCustomer"
    const val CustomerDetail = "customerDetail"
    fun customerDetailRoute(customerId: Int) = "$CustomerDetail/$customerId"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val repository = LedgerRepository(AppDatabase.getInstance(context))

    NavHost(navController = navController, startDestination = Routes.Home) {
        composable(Routes.Home) {
            val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
            val customers by homeViewModel.customers.collectAsState()
            val totalOutstanding by homeViewModel.totalOutstanding.collectAsState()
            val dailySummary by homeViewModel.dailySummary.collectAsState()
            val searchQuery by homeViewModel.searchQuery.collectAsState()

            HomeScreen(
                customers = customers,
                totalOutstanding = totalOutstanding,
                dailySold = dailySummary.totalSold,
                dailyDue = dailySummary.totalSold - dailySummary.totalReceived,
                searchQuery = searchQuery,
                onSearchQueryChange = homeViewModel::updateSearchQuery,
                onAddCustomer = { navController.navigate(Routes.AddCustomer) },
                onCustomerClick = { navController.navigate(Routes.customerDetailRoute(it)) }
            )
        }
        composable(Routes.AddCustomer) {
            val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
            AddCustomerScreen(
                onSaveCustomer = { name, phone -> homeViewModel.addCustomer(name, phone) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "${Routes.CustomerDetail}/{customerId}",
            arguments = listOf(navArgument("customerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getInt("customerId") ?: 0
            val customerDetailViewModel: CustomerDetailViewModel = viewModel(
                factory = CustomerDetailViewModelFactory(repository, customerId)
            )
            val customer by customerDetailViewModel.customer.collectAsState()
            val transactions by customerDetailViewModel.transactions.collectAsState()
            val balance by customerDetailViewModel.netBalance.collectAsState()

            CustomerDetailScreen(
                customerName = customer?.name.orEmpty(),
                phone = customer?.phone,
                balance = balance,
                transactions = transactions,
                onBack = { navController.popBackStack() },
                onAddTransaction = { amt, note -> customerDetailViewModel.addTransaction(amt, "CREDIT", note) },
                onRecordPayment = { amt, note -> customerDetailViewModel.addTransaction(amt, "PAYMENT", note) },
                onWhatsApp = {}
            )
        }
    }
}
