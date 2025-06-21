package com.example.financeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.db.budget.BudgetViewModel
import com.example.financeapp.db.budget.Budgetlist
import com.example.financeapp.ui.components.items.BudgetItem
import com.example.financeapp.ui.components.pbs.SelectableTypeTile
import com.example.financeapp.ui.components.textfields.OutlinedTextField
import com.example.financeapp.ui.components.pbs.PartialBottomSheet
import com.example.financeapp.utils.AddType
import com.example.financeapp.ui.theme.ColorPalette
import com.example.financeapp.utils.predeterminedBudgetTypes
import com.example.financeapp.db.customoutgoingtype.CustomOutgoingViewModel
import com.example.financeapp.ui.theme.DatePickerColours
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BudgetScreen(customOutgoingViewModel: CustomOutgoingViewModel = viewModel()) {
    val budgetViewModel: BudgetViewModel = viewModel()
    var editingBudget by remember { mutableStateOf<Budgetlist?>(null) }
    val monthlyBudgets by budgetViewModel.monthlyBudgets.observeAsState(emptyList())
    val yearlyBudgets by budgetViewModel.yearlyBudgets.observeAsState(emptyList())
    val customBudgets by budgetViewModel.customBudgets.observeAsState(emptyList())
    val allBudgets = remember(monthlyBudgets, yearlyBudgets, customBudgets) {
        monthlyBudgets + yearlyBudgets + customBudgets
    }
    val existingBudgetNameTypes = remember(allBudgets) {
        allBudgets.map { Pair(it.budgetName, it.budgetType) }
    }
    val existingBudgetNameTypesState: State<List<Pair<String, String>>> = remember(allBudgets) {
        derivedStateOf {
            allBudgets.map { Pair(it.budgetName, it.budgetType) }
        }
    }
    val customBudgetTypesFromDb by customOutgoingViewModel.customOutgoingTypeNames.observeAsState(emptyList())
    val predeterminedTypesFromAppConstants = predeterminedBudgetTypes.sorted()
    val allAvailableCategoryNamesForDialog by remember(predeterminedTypesFromAppConstants, customBudgetTypesFromDb) {
        derivedStateOf {
            (predeterminedTypesFromAppConstants + customBudgetTypesFromDb).distinct()
        }
    }
    var selectedTransactionType by remember { mutableStateOf(AddType.BUDGET) }

    if (editingBudget != null) {
        val budgetNameTypesToExcludeForEditState: State<List<Pair<String, String>>> = remember(existingBudgetNameTypesState.value, editingBudget) {
            derivedStateOf {
                val currentExistingTypes = existingBudgetNameTypesState.value
                editingBudget?.let { budgetToEdit ->
                    currentExistingTypes.filterNot { (name, type) ->
                        name == budgetToEdit.budgetName && type == budgetToEdit.budgetType
                    }
                } ?: currentExistingTypes
            }
        }

        EditBudgetDialog(
            budget = editingBudget!!,
            onDismiss = { editingBudget = null },
            onSave = { updatedBudget ->
                budgetViewModel.updateBudget(updatedBudget)
                editingBudget = null
            },
            existingBudgetNameTypes = budgetNameTypesToExcludeForEditState.value,
            allAvailableBudgetCategories = allAvailableCategoryNamesForDialog
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Spacer(Modifier.height(8.dp)) }

            if (allBudgets.isNotEmpty()) {
                if (monthlyBudgets.isNotEmpty()) {
                    item {
                        Text(
                            "For this month",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 0.dp),
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp, fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
                        )
                    }
                    item { HorizontalDivider(modifier = Modifier.padding(4.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) }
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    itemsIndexed(monthlyBudgets) { _, item ->
                        BudgetItem(
                            item = item,
                            onDelete = { budgetViewModel.deleteBudget(item.budgetId) },
                            onEdit = { budgetToEdit -> editingBudget = budgetToEdit }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                if (yearlyBudgets.isNotEmpty()) {
                    item {
                        Text(
                            "For this year",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 0.dp),
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp, fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
                        )
                    }
                    item { HorizontalDivider(modifier = Modifier.padding(4.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) }
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    itemsIndexed(yearlyBudgets) { _, item ->
                        BudgetItem(
                            item = item,
                            onDelete = { budgetViewModel.deleteBudget(item.budgetId) },
                            onEdit = { budgetToEdit -> editingBudget = budgetToEdit }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                if (customBudgets.isNotEmpty()) {
                    item {
                        Text(
                            "Custom budget periods",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 0.dp),
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp, fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
                        )
                    }
                    item { HorizontalDivider(modifier = Modifier.padding(4.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.5f)) }
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    itemsIndexed(customBudgets) { _, item ->
                        BudgetItem(
                            item = item,
                            onDelete = { budgetViewModel.deleteBudget(item.budgetId) },
                            onEdit = { budgetToEdit -> editingBudget = budgetToEdit }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

            } else {
                item {
                    Column(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Click the '+' button to add a budget",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic,
                        )
                    }
                }
            }
        }

        PartialBottomSheet(
            initialSelectedTransactionType = selectedTransactionType,
            addType = AddType.BUDGET,
            existingBudgetNameTypes = existingBudgetNameTypes,
            customOutgoingViewModel = customOutgoingViewModel,
            showTransactionTypeToggle = false,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditBudgetDialog(
    budget: Budgetlist,
    onDismiss: () -> Unit,
    onSave: (Budgetlist) -> Unit,
    existingBudgetNameTypes: List<Pair<String, String>>,
    allAvailableBudgetCategories: List<String>
) {
    val budgetCategoriesToShow = allAvailableBudgetCategories
    var selectedBudgetCategory by remember { mutableStateOf(budget.budgetName) }
    var budgetLimitState by remember { mutableStateOf(TextFieldValue(budget.budgetLimit.toString())) }
    var budgetSpentState by remember { mutableStateOf(TextFieldValue(budget.budgetSpent.toString())) }
    var selectedBudgetType by remember { mutableStateOf(budget.budgetType) }
    var editedStartDate by remember { mutableStateOf(budget.startDate) }
    var editedEndDate by remember { mutableStateOf(budget.endDate) }
    var showEditStartDatePicker by remember { mutableStateOf(false) }
    var showEditEndDatePicker by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val usedBudgetNameTypes = remember(existingBudgetNameTypes) {
        existingBudgetNameTypes.toSet()
    }
    val scrollState = rememberScrollState()

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.onTertiary,
        onDismissRequest = onDismiss,
        title = { Text("Edit Budget", color = MaterialTheme.colorScheme.onSurfaceVariant) },
        text = {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                OutlinedTextField(
                    keyboardController = keyboardController,
                    value = budgetLimitState,
                    onValueChange = { newValueState ->
                        budgetLimitState = newValueState
                    },
                    label =
                        if (budget.budgetName.equals("Savings", ignoreCase = true)) {
                            { Text("Savings Goal") }
                        } else {
                            { Text("Budget Limit") }
                        },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Decimal,
                    ),
                    visualTransformation = object : VisualTransformation {
                        override fun filter(text: AnnotatedString): TransformedText {
                            return TransformedText(text, OffsetMapping.Identity)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (budget.budgetName.equals("Savings", ignoreCase = true)) {
                    OutlinedTextField(
                        keyboardController = keyboardController,
                        value = budgetSpentState,
                        onValueChange = { newValueState ->
                            budgetSpentState = newValueState
                        },
                        label = { Text("Amount Saved") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Decimal,
                        ),
                        visualTransformation = object : VisualTransformation {
                            override fun filter(text: AnnotatedString): TransformedText {
                                return TransformedText(text, OffsetMapping.Identity)
                            }
                        },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text("Select Budget Type:", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    budgetCategoriesToShow.forEach { category ->
                        val isCurrentlySelected = selectedBudgetCategory == category
                        val isUsedByAnotherBudgetOfType = usedBudgetNameTypes.any { (name, type) ->
                            name == category && type == selectedBudgetType && (name != budget.budgetName || type != budget.budgetType)
                        }
                        val isTileEnabled = isCurrentlySelected || !isUsedByAnotherBudgetOfType
                        val colorInt = ColorPalette.getOutgoingColor(category)
                        val tileActualColor = Color(colorInt)

                        SelectableTypeTile(
                            typeName = category,
                            isSelected = isCurrentlySelected,
                            onTypeSelected = { newSelection ->
                                selectedBudgetCategory = newSelection
                            },
                            baseColor = tileActualColor,
                            enabled = isTileEnabled
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Select Budget Period:", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val budgetPeriods = listOf("Monthly", "Yearly", "Custom")
                    budgetPeriods.forEach { periodType ->
                        val isCurrentlySelected = selectedBudgetType == periodType
                        val isUsedByAnotherBudgetWithName = usedBudgetNameTypes.any { (name, budgetType) ->
                            name == selectedBudgetCategory && budgetType == periodType && (name != budget.budgetName || budgetType != budget.budgetType)
                        }
                        val isButtonEnabled = isCurrentlySelected || !isUsedByAnotherBudgetWithName

                        Button(
                            onClick = { selectedBudgetType = periodType },
                            modifier = Modifier.weight(1f),
                            enabled = isButtonEnabled,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCurrentlySelected) {
                                    MaterialTheme.colorScheme.secondaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                contentColor = if (isCurrentlySelected) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } ),
                            contentPadding = PaddingValues(
                                horizontal = 0.dp,
                                vertical = 0.dp
                            )
                        ) {
                            Text(periodType, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp))
                        }
                    }
                }

                if (selectedBudgetType == "Custom") {

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Custom Date Range:", style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Button(
                            onClick = { showEditStartDatePicker = true },
                            colors =
                            if (editedStartDate != null) {
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) }
                            else {
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ) },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(
                                horizontal = 0.dp,
                                vertical = 0.dp
                            )
                        ) {
                            Text(
                                text = if (editedStartDate != null) {
                                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(editedStartDate!!))
                                } else {
                                    "Select Start Date"
                                },
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = { showEditEndDatePicker = true },
                            colors =
                            if (editedEndDate != null) {
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) }
                            else {
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ) },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(
                                horizontal = 0.dp,
                                vertical = 0.dp
                            )
                        ) {
                            Text(
                                text = if (editedEndDate != null) {
                                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(editedEndDate!!))
                                } else {
                                    "Select End Date"
                                },
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 13.sp
                            )
                        }
                    }

                    if (showEditStartDatePicker) {
                        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = editedStartDate)
                        DatePickerDialog(
                            onDismissRequest = { showEditStartDatePicker = false },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        editedStartDate = datePickerState.selectedDateMillis
                                        showEditStartDatePicker = false },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                ) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                Button(
                                    onClick = { showEditStartDatePicker = false },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                ) {
                                    Text("Cancel")
                                }
                            },
                            colors = DatePickerColours.customDatePickerColors
                        ) {
                            DatePicker(
                                state = datePickerState,
                                colors = DatePickerColours.customDatePickerColors
                            )
                        }
                    }

                    if (showEditEndDatePicker) {
                        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = editedEndDate)
                        DatePickerDialog(
                            onDismissRequest = { showEditEndDatePicker = false },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        editedEndDate = datePickerState.selectedDateMillis
                                        showEditEndDatePicker = false },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                ) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                Button(
                                    onClick = { showEditEndDatePicker = false },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                ) {
                                    Text("Cancel")
                                }
                            },
                            colors = DatePickerColours.customDatePickerColors
                        ) {
                            DatePicker(
                                state = datePickerState,
                                colors = DatePickerColours.customDatePickerColors
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedBudget = budget.copy(
                        budgetName = selectedBudgetCategory,
                        budgetLimit = budgetLimitState.text.toDoubleOrNull() ?: 0.0,
                        budgetSpent = budgetSpentState.text.toDoubleOrNull() ?: 0.0,
                        budgetType = selectedBudgetType,
                        startDate = editedStartDate,
                        endDate = editedEndDate
                    )
                    onSave(updatedBudget)
                },
                enabled = budgetLimitState.text.toDoubleOrNull() != null && budgetSpentState.text.toDoubleOrNull() != null &&
                        if (selectedBudgetType == "Custom") {
                            val start = editedStartDate
                            val end = editedEndDate
                            start != null && end != null && start <= end
                        } else {
                            true
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