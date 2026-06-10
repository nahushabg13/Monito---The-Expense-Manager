package com.example.monito_theexpensemanager

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monito_theexpensemanager.ui.theme.MonitoTheExpenseManagerTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.innerShadow
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.exp

// Temporary data class — we'll move this later
//data class Expense(
//    val name: String,
//    val category: String,
//    val amount: Double,
//    val color: Color
//)
//
//// Temporary fake data — we'll replace with Room database later
//val sampleExpenses = mutableStateListOf(
//    Expense("Swiggy dinner", "Food", 380.0, Color(0xFF1D9E75)),
//    Expense("Ola cab", "Transport", 210.0, Color(0xFF378ADD)),
//    Expense("Hot Wheels Corvette", "Hobbies", 650.0, Color(0xFFEF9F27)),
//    Expense("Bigbasket groceries", "Groceries", 1240.0, Color(0xFFD4537E)),
//    Expense("Netflix", "Subscriptions", 649.0, Color(0xFF7F77DD))
//)


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as MonitoApp

        setContent {
            val viewModel: ExpenseViewModel = viewModel(
                factory = ExpenseViewModel.ExpenseViewModelFactory(app.repository)
            )
            MonitoTheExpenseManagerTheme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = {},
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { navController.navigate("addExpense") }
                        ) {
                            Text("+")
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
//                    NavHost(navController, startDestination = "list",) {
//                        composable("list") {ExpenseListScreen()}
//                        composable("addExpense") {AddExpenseScreen()}
//                    }
//                    ExpenseListScreen(
//                        modifier = Modifier.padding(innerPadding)
//                    )

                    NavHost(
                        navController = navController,
                        startDestination = "list",
                        modifier = Modifier.padding(innerPadding) // ✅ innerPadding used here
                    ) {
                        composable("list") {  ExpenseListScreen(navController, viewModel = viewModel) }
                        composable("addExpense") { AddExpenseScreen(navController, viewModel = viewModel) }
                    }
                }
            }
        }
    }
}


@Composable
fun ExpenseListScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ExpenseViewModel,
) {
    val expenses by viewModel.expenses.collectAsState()
    Column(modifier = modifier) {

        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1D9E75))
                .padding(16.dp)
        ) {
            Text(
                text = "Monito",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "June 2026 · total spent",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )
            Text(
                text = "\u20B9 ${expenses.sumOf{it.amount}.toInt()}",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Expense list
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(expenses) { expense ->
                ExpenseItem(expense)
            }
        }

    }
}

@Composable
fun ExpenseItem(expense: ExpenseEntity) {
    val categoryColor = when (expense.category) {
        "Food" -> Color(0xFF1D9E75)
        "Transport" -> Color(0xFF378ADD)
        "Hobbies" -> Color(0xFFEF9F27)
        "Groceries" -> Color(0xFFD4537E)
        "Subscriptions" -> Color(0xFF7F77DD)
        else -> Color.Gray
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(categoryColor, shape = RoundedCornerShape(50))
            )
            Column {
                Text(
                    text = expense.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = expense.category,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Text(
            text = "₹ ${expense.amount.toInt()}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AddExpenseScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ExpenseViewModel,
) {
    val categories = listOf("Food", "Transport", "Groceries", "Hobbies", "Other")
    var amount by remember  {mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Food") }
    val date = remember {
        java.time.LocalDate.now().toString()
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value =  amount,
            label =  {Text("Amount")},
            onValueChange = { amount = it },
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value =  note,
            label = { Text("Note")},
            onValueChange = {note = it}
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach {cateogory ->
                FilterChip(
                    selected = selectedCategory == cateogory,
                    onClick = {selectedCategory = cateogory},
                    label = {Text(cateogory)}
                )
            }
        }

        Text(
            text = date
        )

        Button(
            onClick = {
                val amountValue = amount.toDoubleOrNull()
                if (amountValue != null && note.isNotBlank()) {
                    viewModel.insertExpense(
                        ExpenseEntity(
                            name = note,
                            category = selectedCategory,
                            amount = amountValue,
                            date = date
                        )
                    )
                    navController.popBackStack()
                }
            }
        ) {
            Text("Save")
        }
    }



}

