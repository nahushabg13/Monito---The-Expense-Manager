package com.example.monito_theexpensemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.monito_theexpensemanager.ui.theme.MonitoTheExpenseManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val route = navBackStackEntry?.destination?.route


            MonitoTheExpenseManagerTheme {
                Scaffold(
                    bottomBar = {
                        BottomAppBar {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = {Icon(imageVector = Icons.Filled.CurrencyRupee, contentDescription = null)},
                                    label = {Text("Transactions")},
                                    onClick = { navController.navigate("transactions") },
                                    selected = route =="transactions"
                                )
                                NavigationBarItem(
                                    icon = {Icon(imageVector = Icons.Filled.AutoGraph, contentDescription = null)},
                                    label = {Text("Graphs")},
                                    onClick = { navController.navigate("graphs") },
                                    selected = route =="graphs"
                                )
                            }
                        }
                    }
                ) {innerPadding ->
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        startDestination = "transactions"
                    ) {
                        composable("transactions") {
                            TransactionsScreen()
                        }
                        composable("graphs") {
                            GraphsScreen()
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun TransactionsScreen() {
    Text("Transactions screen")
}

@Composable
fun GraphsScreen() {
    Text("Graphs screen")
}
