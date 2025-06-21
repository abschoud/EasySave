package com.example.financeapp.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import com.example.financeapp.ui.components.textfields.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.db.budget.BudgetViewModel
import com.example.financeapp.db.customincometype.CustomIncomeViewModel
import com.example.financeapp.db.transaction.TransactionViewModel
import com.example.financeapp.db.transaction.Transactionlist
import com.example.financeapp.ui.components.pbs.SelectableTypeTile
import com.example.financeapp.ui.components.pbs.PartialBottomSheet
import com.example.financeapp.ui.components.charts.PieChartComposable
import com.example.financeapp.ui.components.items.TransactionItem
import com.example.financeapp.utils.AddType
import com.example.financeapp.ui.theme.ColorPalette
import com.example.financeapp.utils.predeterminedOutgoingTransactionTypes
import com.example.financeapp.utils.transactionToBudgetMapping
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.financeapp.db.customoutgoingtype.CustomOutgoingViewModel

@Composable
fun HomeScreen(customOutgoingViewModel: CustomOutgoingViewModel = viewModel()) {
    val context = LocalContext.current
    val transactionViewModel: TransactionViewModel = viewModel()
    val budgetViewModel: BudgetViewModel = viewModel()
    var selectedDate by remember { mutableStateOf(Calendar.getInstance().time) }

    LaunchedEffect(Unit) {
        selectedDate = Calendar.getInstance().time
    }

    val transactionsForSelectedDate by transactionViewModel.getTransactionsForDate(selectedDate).observeAsState()
    var selectedTransactionType by remember { mutableStateOf(AddType.OUTGOING) }
    val filteredTransactions = remember(transactionsForSelectedDate, selectedTransactionType) {
        transactionsForSelectedDate?.filter { transaction ->
            transaction.transactionType == selectedTransactionType
        } ?: emptyList()
    }
    val monthlyBudgets by budgetViewModel.monthlyBudgets.observeAsState(emptyList())
    val yearlyBudgets by budgetViewModel.yearlyBudgets.observeAsState(emptyList())
    val customBudgets by budgetViewModel.customBudgets.observeAsState(emptyList())
    val allBudgets by remember(monthlyBudgets, yearlyBudgets, customBudgets) {
        derivedStateOf {
            monthlyBudgets + yearlyBudgets + customBudgets
        }
    }
    val existingBudgetNameTypes by remember(allBudgets) {
        derivedStateOf {
            allBudgets.map { Pair(it.budgetName, it.budgetType) }
        }
    }
    val predeterminedOutgoingTransactionTypes = remember { predeterminedOutgoingTransactionTypes.sorted() }
    val customBudgetTypesFromDb by customOutgoingViewModel.customOutgoingTypeNames.observeAsState(emptyList())
    val allOutgoingCategoriesForDialog by remember(predeterminedOutgoingTransactionTypes, customBudgetTypesFromDb) {
        derivedStateOf {
            (predeterminedOutgoingTransactionTypes + customBudgetTypesFromDb).distinct()
        }
    }
    var editingTransaction by remember { mutableStateOf<Transactionlist?>(null) }

    if (editingTransaction != null) {
        EditTransactionDialog(
            transaction = editingTransaction!!,
            onDismiss = { editingTransaction = null },
            onSave = { updatedTransaction ->
                transactionViewModel.updateTransaction(updatedTransaction)
                editingTransaction = null
            },
            allOutgoingTransactionCategories = allOutgoingCategoriesForDialog
        )
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { selectedTransactionType = AddType.OUTGOING },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTransactionType == AddType.OUTGOING) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            contentColor = if (selectedTransactionType == AddType.OUTGOING) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Outgoing", fontSize = 14.sp)
                    }

                    Button(
                        onClick = { selectedTransactionType = AddType.INCOME },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTransactionType == AddType.INCOME) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            contentColor = if (selectedTransactionType == AddType.INCOME) {
                                MaterialTheme.colorScheme.onPrimary
                            }
                            else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Income", fontSize = 14.sp)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            val calendar = Calendar.getInstance()
                            calendar.time = selectedDate
                            calendar.add(Calendar.DAY_OF_YEAR, -1)
                            selectedDate = calendar.time
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Day",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(start = 8.dp, end = 2.dp, top = 4.dp, bottom = 4.dp)
                    ) {
                        Text(
                            text = getDateLabel(selectedDate),
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .clickable {
                                    val calendar = Calendar.getInstance()
                                    calendar.time = selectedDate
                                    val year = calendar.get(Calendar.YEAR)
                                    val month = calendar.get(Calendar.MONTH)
                                    val day = calendar.get(Calendar.DAY_OF_MONTH)
                                    val datePickerDialog = DatePickerDialog(
                                        context,
                                        { _, selectedYear, selectedMonth, selectedDay ->
                                            val newCalendar = Calendar.getInstance()
                                            newCalendar.set(selectedYear, selectedMonth, selectedDay)
                                            selectedDate = newCalendar.time
                                        },
                                        year, month, day
                                    )
                                    datePickerDialog.show()
                                }
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Date",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    IconButton(
                        onClick = {
                            val calendar = Calendar.getInstance()
                            calendar.time = selectedDate
                            calendar.add(Calendar.DAY_OF_YEAR, 1)
                            selectedDate = calendar.time
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Day",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            item {
                PieChartComposable(transactions = filteredTransactions, context = context)
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            if (filteredTransactions.isNotEmpty()) {
                itemsIndexed(filteredTransactions) { index, item ->
                    TransactionItem(
                        item = item, onDelete = { transactionId ->
                            transactionViewModel.deleteTransaction(transactionId)
                        },
                        onEdit = { transactionToEdit ->
                            editingTransaction = transactionToEdit
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                item {
                    Text(
                        "Click the '+' button to add a transaction",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(64.dp)) }
        }

        PartialBottomSheet(
            initialSelectedTransactionType = selectedTransactionType,
            addType = if (selectedTransactionType == AddType.OUTGOING) AddType.OUTGOING else AddType.INCOME,
            existingBudgetNameTypes = existingBudgetNameTypes,
            customOutgoingViewModel = customOutgoingViewModel,
            showTransactionTypeToggle = true,
        )
    }
}

fun getDateLabel(date: Date): String {
    val todayCalendar = Calendar.getInstance()
    val dateCalendar = Calendar.getInstance()
    dateCalendar.time = date

    if (todayCalendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
        todayCalendar.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)) {
        return "Today"
    }

    val yesterdayCalendar = Calendar.getInstance()
    yesterdayCalendar.add(Calendar.DAY_OF_YEAR, -1)

    if (yesterdayCalendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
        yesterdayCalendar.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)) {
        return "Yesterday"
    }

    val tomorrowCalendar = Calendar.getInstance()
    tomorrowCalendar.add(Calendar.DAY_OF_YEAR, 1)

    if (tomorrowCalendar.get(Calendar.YEAR) == dateCalendar.get(Calendar.YEAR) &&
        tomorrowCalendar.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR)) {
        return "Tomorrow"
    }

    return SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.ENGLISH).format(date)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditTransactionDialog(
    transaction: Transactionlist,
    onDismiss: () -> Unit,
    onSave: (Transactionlist) -> Unit,
    allOutgoingTransactionCategories: List<String>,
    customOutgoingViewModel: CustomOutgoingViewModel = viewModel(),
    customIncomeViewModel: CustomIncomeViewModel = viewModel()
) {
    val customOutgoingTypesFromDb by customOutgoingViewModel.customOutgoingTypeNames.observeAsState(emptyList())
    val customIncomeTypesFromDb by customIncomeViewModel.customIncomeTypeNames.observeAsState(emptyList())
    val globalPredeterminedIncomeTypes = remember { com.example.financeapp.utils.predeterminedIncomeTypes }
    val globalPredeterminedOutgoingTypes = remember { com.example.financeapp.utils.predeterminedOutgoingTypes }
    val displayedBudgetAndOutgoingTypes by remember(customOutgoingTypesFromDb, globalPredeterminedOutgoingTypes) {
        derivedStateOf {
            (globalPredeterminedOutgoingTypes + customOutgoingTypesFromDb).distinct()
        }
    }
    val displayedIncomeTransactionTypes by remember(customIncomeTypesFromDb, globalPredeterminedIncomeTypes) {
        derivedStateOf {
            (globalPredeterminedIncomeTypes + customIncomeTypesFromDb).distinct()
        }
    }
    var transactionValueState by remember { mutableStateOf(TextFieldValue(transaction.transactionValue.toString())) }
    var selectedTransactionName by remember { mutableStateOf(transaction.transactionName) }
    var selectedTransactionType by remember { mutableStateOf(transaction.transactionType) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val transactionNamesToDisplay = remember(selectedTransactionType, displayedIncomeTransactionTypes, displayedBudgetAndOutgoingTypes) {
        when (selectedTransactionType) {
            AddType.INCOME -> displayedIncomeTransactionTypes.sorted()
            AddType.OUTGOING -> displayedBudgetAndOutgoingTypes
            else -> emptyList()
        }
    }
    val scrollState = rememberScrollState()

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.onTertiary,
        onDismissRequest = onDismiss,
        title = { Text("Edit Transaction", color = MaterialTheme.colorScheme.onSurfaceVariant) },
        text = {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = { selectedTransactionType = AddType.OUTGOING },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTransactionType == AddType.OUTGOING) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (selectedTransactionType == AddType.OUTGOING) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Outgoing", fontSize = 14.sp)
                    }
                    Button(
                        onClick = { selectedTransactionType = AddType.INCOME },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTransactionType == AddType.INCOME) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (selectedTransactionType == AddType.INCOME) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Income", fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    keyboardController = keyboardController,
                    value = transactionValueState,
                    onValueChange = { newValueState ->
                        transactionValueState = newValueState
                    },
                    label = { Text("Transaction Amount") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Decimal,
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Select Transaction Type:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    transactionNamesToDisplay.forEach { transactionName ->
                        if (!(transactionName.equals("Savings", ignoreCase = true))) {
                            val colorInt = if (selectedTransactionType != AddType.INCOME) ColorPalette.getOutgoingColor(transactionName) else ColorPalette.getIncomeColor(transactionName)
                            val tileActualColor = Color(colorInt)
                            SelectableTypeTile(
                                typeName = transactionName,
                                isSelected = selectedTransactionName == transactionName,
                                onTypeSelected = { newSelection ->
                                    selectedTransactionName = newSelection
                                },
                                baseColor = tileActualColor,
                                enabled = true
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedValue = transactionValueState.text.toDoubleOrNull()
                    if (updatedValue != null) {
                        val finalBudgetCategory: String? = if (selectedTransactionType == AddType.OUTGOING) {
                            val mappedCategory = transactionToBudgetMapping[selectedTransactionName]

                            if (mappedCategory != null) {
                                mappedCategory
                            } else {
                                if (allOutgoingTransactionCategories.contains(selectedTransactionName)) {
                                    selectedTransactionName
                                } else {
                                    null
                                }
                            }
                        } else {
                            null
                        }

                        val updatedTransaction = transaction.copy(
                            transactionValue = updatedValue,
                            transactionName = selectedTransactionName,
                            transactionType = selectedTransactionType,
                            budgetCategory = finalBudgetCategory
                        )
                        onSave(updatedTransaction)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text("Cancel")
            }
        }
    )
}