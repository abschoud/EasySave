package com.example.financeapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.analysis.AnalysisViewModel
import com.example.financeapp.analysis.AnalysisViewModelFactory
import com.example.financeapp.data.BudgetEntry
import com.example.financeapp.data.DataRequest
import com.example.financeapp.data.SpendingEntry
import com.example.financeapp.data.generatedComprehensiveTestTransactions
import com.example.financeapp.data.generatedTestBudgets
import com.example.financeapp.db.budget.BudgetViewModel
import com.example.financeapp.db.budget.Budgetlist
import com.example.financeapp.db.customincometype.CustomIncomeViewModel
import com.example.financeapp.db.customoutgoingtype.CustomOutgoingViewModel
import com.example.financeapp.db.transaction.TransactionViewModel
import com.example.financeapp.db.transaction.Transactionlist
import com.example.financeapp.network.RetrofitClient
import com.example.financeapp.ui.components.MarkdownText
import com.example.financeapp.ui.components.charts.MyBarChart
import com.example.financeapp.utils.AddType
import com.example.financeapp.utils.ChartTimePeriod
import com.example.financeapp.ui.theme.ColorPalette
import com.example.financeapp.utils.getEndOfMonthMillis
import com.example.financeapp.utils.getStartOfWeekMillis
import com.example.financeapp.utils.getEndOfWeekMillis
import com.example.financeapp.utils.getEndOfYearMillis
import com.example.financeapp.utils.getStartOfMonthMillis
import com.example.financeapp.utils.getStartOfYearMillis
import com.example.financeapp.utils.predeterminedIncomeTransactionTypes
import com.example.financeapp.utils.predeterminedOutgoingTransactionTypes
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(customOutgoingViewModel: CustomOutgoingViewModel = viewModel(), customIncomeViewModel: CustomIncomeViewModel = viewModel()) {
    val includeTestDataForAI = true
    val transactionViewModel: TransactionViewModel = viewModel()
    val reversedTransactionList by transactionViewModel.reversedTransactionlist.observeAsState()
    val budgetViewModel: BudgetViewModel = viewModel()
    val reversedBudgetList by budgetViewModel.reversedBudgetList.observeAsState()
    var viewModelDataLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(reversedTransactionList) {
        if (reversedTransactionList != null) {
            viewModelDataLoaded = true
        }
    }
    val analysisViewModel: AnalysisViewModel = viewModel(
        factory = AnalysisViewModelFactory(RetrofitClient.instance)
    )
    val aiAnalysis by analysisViewModel.aiAnalysis.collectAsState()
    val isLoading by analysisViewModel.isLoading.collectAsState()
    val errorMessage by analysisViewModel.errorMessage.collectAsState()
    val testData: List<Transactionlist> = if (includeTestDataForAI) {
        generatedComprehensiveTestTransactions
    } else {
        emptyList()
    }
    val budgetData: List<Budgetlist> = if (includeTestDataForAI) {
        generatedTestBudgets
    } else {
        emptyList()
    }
    val combinedTransactionList =
        remember(reversedTransactionList, testData) {
            val originalList = reversedTransactionList ?: emptyList()
            originalList + testData
        }
    val combinedBudgetList =
        remember(reversedBudgetList, testData) {
            val originalList = reversedBudgetList ?: emptyList()
            originalList + budgetData
        }
    val allTransactionsForAI = remember(combinedTransactionList) {
        combinedTransactionList
    }
    val allBudgetsForAI = remember(combinedBudgetList) {
        combinedBudgetList
    }
    var selectedFilter by remember { mutableStateOf<String?>("All Outgoing") }
    LaunchedEffect(selectedFilter) {
    }
    var expanded by remember { mutableStateOf(false) }
    var selectedTimePeriod by remember { mutableStateOf(ChartTimePeriod.Year) }
    val customOutgoingTypesFromDb by customOutgoingViewModel.customOutgoingTypeNames.observeAsState(emptyList())
    val customIncomeTypesFromDb by customIncomeViewModel.customIncomeTypeNames.observeAsState(emptyList())
    val allFilterOptions = remember(customOutgoingTypesFromDb, customIncomeTypesFromDb) {
        listOf("All Outgoing", "All Income") +
                predeterminedOutgoingTransactionTypes +
                predeterminedIncomeTransactionTypes +
                customOutgoingTypesFromDb + customIncomeTypesFromDb
    }
    val transactionsForChart = remember(combinedTransactionList, selectedFilter, selectedTimePeriod) {
        val categoryFilteredTransactions = when (selectedFilter) {
            "All Outgoing" -> combinedTransactionList.filter { it.transactionType == AddType.OUTGOING }
            "All Income" -> combinedTransactionList.filter { it.transactionType == AddType.INCOME }
            else -> combinedTransactionList.filter { it.transactionName == selectedFilter }
        }
        val calendar = Calendar.getInstance()

        when (selectedTimePeriod) {
            ChartTimePeriod.Week -> {
                val startOfWeekMs = getStartOfWeekMillis(calendar)
                val endOfWeekMs = getEndOfWeekMillis(calendar)
                categoryFilteredTransactions.filter {
                    it.transactionCreatedAt.time in startOfWeekMs..endOfWeekMs
                }
            }
            ChartTimePeriod.Month -> {
                val startOfMonthMs = getStartOfMonthMillis(calendar)
                val endOfMonthMs = getEndOfMonthMillis(calendar)
                categoryFilteredTransactions.filter {
                    it.transactionCreatedAt.time in startOfMonthMs..endOfMonthMs
                }
            }
            ChartTimePeriod.Year -> {
                val startOfYearMs = getStartOfYearMillis(calendar)
                val endOfYearMs = getEndOfYearMillis(calendar)
                categoryFilteredTransactions.filter {
                    it.transactionCreatedAt.time in startOfYearMs..endOfYearMs
                }
            }
            ChartTimePeriod.All_Time -> {
                categoryFilteredTransactions
            }
        }
    }
    val chartColor = remember(selectedFilter) {

        when (selectedFilter) {
            "All Outgoing" -> Color(0xFFBB3E3E)
            "All Income" -> Color(0xFF59BB3E)
            else -> {
                val intColor = ColorPalette.getOutgoingColor(selectedFilter ?: "")
                Color(intColor)
            }
        }
    }
    val analysisScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
            .verticalScroll(analysisScrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = selectedFilter.orEmpty(),
                onValueChange = {},
                label = { Text("Filter Transactions") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    focusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.heightIn(max = 185.dp)
            ) {
                allFilterOptions.forEach { filterOption ->
                    DropdownMenuItem(
                        text = { Text(filterOption, color = MaterialTheme.colorScheme.onPrimaryContainer) },
                        onClick = {
                            selectedFilter = filterOption
                            expanded = false
                        },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val timePeriods = ChartTimePeriod.values()

            timePeriods.forEach { period ->
                Button(
                    onClick = { selectedTimePeriod = period },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTimePeriod == period) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selectedTimePeriod == period) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 0.dp,
                        vertical = 0.dp
                    )
                ) {
                    Text(
                        text = period.name.replace("_", " ").lowercase()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        MyBarChart(
            transactions = transactionsForChart,
            transactionColor = chartColor,
            timePeriod = selectedTimePeriod
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (allTransactionsForAI.isNotEmpty() && viewModelDataLoaded && !isLoading) {
                        val spendingEntries = allTransactionsForAI.map { transaction ->
                            SpendingEntry(
                                category = transaction.transactionName,
                                amount = transaction.transactionValue,
                                date = transaction.transactionCreatedAt,
                                type = transaction.transactionType.name
                            )
                        }
                        val budgetEntries = allBudgetsForAI.map { budget ->
                            BudgetEntry(
                                category = budget.budgetName,
                                limit = budget.budgetLimit,
                                spent = budget.budgetSpent,
                                period = budget.budgetType,
                                startDate = budget.startDate?.let { Date(it) } ?: Date(),
                                endDate = budget.endDate?.let { Date(it) } ?: Date()
                            )
                        }
                        val dataRequest = DataRequest(
                            spendingData = spendingEntries,
                            budgetData = budgetEntries
                        )
                        analysisViewModel.analyzeSpending("gemini-2.0-flash", dataRequest)
                    }
                },
                enabled = allTransactionsForAI.isNotEmpty() && viewModelDataLoaded && !isLoading,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(
                    "Quick Analysis",
                    fontSize = 13.sp
                )
            }

            Button(
                onClick = {
                    if (allTransactionsForAI.isNotEmpty() && viewModelDataLoaded && !isLoading) {
                        val spendingEntries = allTransactionsForAI.map { transaction ->
                            SpendingEntry(
                                category = transaction.transactionName,
                                amount = transaction.transactionValue,
                                date = transaction.transactionCreatedAt,
                                type = transaction.transactionType.name
                            )
                        }
                        val budgetEntries = allBudgetsForAI.map { budget ->
                            BudgetEntry(
                                category = budget.budgetName,
                                limit = budget.budgetLimit,
                                spent = budget.budgetSpent,
                                period = budget.budgetType,
                                startDate = budget.startDate?.let { Date(it) } ?: Date(),
                                endDate = budget.endDate?.let { Date(it) } ?: Date()
                            )
                        }
                        val dataRequest = DataRequest(
                            spendingData = spendingEntries,
                            budgetData = budgetEntries
                        )
                        analysisViewModel.analyzeSpending("gemini-2.5-pro-preview-05-06", dataRequest)
                    }
                },
                enabled = allTransactionsForAI.isNotEmpty() && viewModelDataLoaded && !isLoading,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSecondaryContainer)
            ) {
                Text(
                    "Advanced Analysis",
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Advanced analysis may take a few minutes to generate",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontStyle = FontStyle.Italic
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Analyzing spending...")
            }
            errorMessage != null -> {
                Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
            }
            aiAnalysis != null -> {
                val analysisText = aiAnalysis
                MarkdownText(
                    markdownText = analysisText!!,
                )
            }
            else -> {
                Text("Tap a button above to generate AI analysis", fontStyle = FontStyle.Italic)
            }
        }
    }
}

