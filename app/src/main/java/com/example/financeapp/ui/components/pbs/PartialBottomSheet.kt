package com.example.financeapp.ui.components.pbs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.db.budget.BudgetViewModel
import com.example.financeapp.db.customoutgoingtype.CustomOutgoingViewModel
import com.example.financeapp.db.customincometype.CustomIncomeViewModel
import com.example.financeapp.db.transaction.TransactionViewModel
import com.example.financeapp.ui.theme.ColorPalette
import com.example.financeapp.ui.theme.DatePickerColours
import com.example.financeapp.utils.AddType
import com.example.financeapp.utils.predeterminedIncomeTypes
import com.example.financeapp.utils.predeterminedOutgoingTypes
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.equals
import kotlin.text.toDoubleOrNull
import kotlin.text.trim

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PartialBottomSheet(
    addType: AddType,
    existingBudgetNameTypes: List<Pair<String, String>>,
    customOutgoingViewModel: CustomOutgoingViewModel = viewModel(),
    customIncomeViewModel: CustomIncomeViewModel = viewModel(),
    budgetViewModel: BudgetViewModel = viewModel(),
    transactionViewModel: TransactionViewModel = viewModel(),
    showTransactionTypeToggle: Boolean = false,
    initialSelectedTransactionType : AddType
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isSheetContentVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val localContext = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var currentOperationType by remember(initialSelectedTransactionType) {
        mutableStateOf(initialSelectedTransactionType)
    }
    val customOutgoingTypesFromDb by customOutgoingViewModel.customOutgoingTypeNames.observeAsState(emptyList())
    val customIncomeTypesFromDb by customIncomeViewModel.customIncomeTypeNames.observeAsState(emptyList())
    val allCustomTypesFromDb = customOutgoingTypesFromDb + customIncomeTypesFromDb
    val globalPredeterminedIncomeTypes = remember { predeterminedIncomeTypes }
    val globalPredeterminedOutgoingTypes = remember { predeterminedOutgoingTypes }
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
    var selectedCategoryName by remember { mutableStateOf<String?>(null) }
    var customCategoryNameInput by remember { mutableStateOf(TextFieldValue("")) }
    var showCustomCategoryNameInput by remember { mutableStateOf(false) }
    var budgetValueState by remember { mutableStateOf(TextFieldValue("")) }
    var transactionValueState by remember { mutableStateOf(TextFieldValue("")) }
    var selectedBudgetPeriod by remember { mutableStateOf<String?>(null) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var selectedStartDate by remember { mutableStateOf<Long?>(null) }
    var selectedEndDate by remember { mutableStateOf<Long?>(null) }
    var isDeleteModeActive by remember { mutableStateOf(false) }
    val addCustomCategoryAction: () -> Unit = myLambda@{
        val newTypeName = customCategoryNameInput.text.trim()
        if (newTypeName.isEmpty()) {
            keyboardController?.hide()
            return@myLambda
        }

        var messageShown = false
        scope.launch {
            val success: Boolean
            when (currentOperationType) {
                AddType.BUDGET, AddType.OUTGOING -> {
                    if (globalPredeterminedOutgoingTypes.any { it.equals(newTypeName, ignoreCase = true) }) {
                        messageShown = true
                        success = false
                    } else if (customOutgoingTypesFromDb.any { it.equals(newTypeName, ignoreCase = true) }) {
                        messageShown = true
                        success = false
                    }
                    else {
                        success = customOutgoingViewModel.addCustomOutgoingTypeName(newTypeName)
                    }
                }
                AddType.INCOME -> {
                    if (globalPredeterminedIncomeTypes.any { it.equals(newTypeName, ignoreCase = true) }) {
                        messageShown = true
                        success = false
                    } else if (customIncomeTypesFromDb.any { it.equals(newTypeName, ignoreCase = true) }) {
                        messageShown = true
                        success = false
                    }
                    else {
                        success = customIncomeViewModel.addCustomIncomeTypeName(newTypeName)
                    }
                }
            }

            if (success) {
                selectedCategoryName = newTypeName
                customCategoryNameInput = TextFieldValue("")
                showCustomCategoryNameInput = false
            } else if (!messageShown) {
            }
            if (messageShown || !success) {
                keyboardController?.hide()
            }
        }
    }
    val categoriesToDisplayForSelection: List<String> by remember(
        currentOperationType,
        displayedBudgetAndOutgoingTypes,
        displayedIncomeTransactionTypes
    ) {
        derivedStateOf {
            when (currentOperationType) {
                AddType.BUDGET, AddType.OUTGOING -> displayedBudgetAndOutgoingTypes
                AddType.INCOME -> displayedIncomeTransactionTypes
            }
        }
    }

    fun resetFormState() {
        selectedCategoryName = null
        budgetValueState = TextFieldValue("")
        transactionValueState = TextFieldValue("")
        selectedBudgetPeriod = ""
        selectedStartDate = null
        selectedEndDate = null
    }

    LaunchedEffect(initialSelectedTransactionType) {
        currentOperationType = initialSelectedTransactionType
    }

    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible) {
            selectedCategoryName = null
            customCategoryNameInput = TextFieldValue("")
            showCustomCategoryNameInput = false
            isDeleteModeActive = false
            budgetValueState = TextFieldValue("")
            selectedBudgetPeriod = null
            selectedStartDate = null
            selectedEndDate = null
            transactionValueState = TextFieldValue("")
        }
    }

    LaunchedEffect(currentOperationType) {
        selectedCategoryName = null
        customCategoryNameInput = TextFieldValue("")
        showCustomCategoryNameInput = false
        isDeleteModeActive = false

        if (currentOperationType == AddType.BUDGET) {
            transactionValueState = TextFieldValue("")
        } else {
            budgetValueState = TextFieldValue("")
            selectedBudgetPeriod = null
            selectedStartDate = null
            selectedEndDate = null
        }
    }

    LaunchedEffect(selectedCategoryName) {
        if (selectedCategoryName != null) {
            if (showCustomCategoryNameInput) {
                customCategoryNameInput = TextFieldValue("")
                showCustomCategoryNameInput = false
            }
        }
    }

    LaunchedEffect(showCustomCategoryNameInput) {
        if (showCustomCategoryNameInput) {
            if (selectedCategoryName != null) {
                selectedCategoryName = null
            }
        }
    }

    LaunchedEffect(selectedBudgetPeriod, selectedCategoryName, existingBudgetNameTypes) {
        if (currentOperationType == AddType.BUDGET && selectedCategoryName != null && selectedBudgetPeriod != null) {
            val isSelectedTypeDuplicateForNewPeriod = existingBudgetNameTypes
                .any { (name, period) ->
                    name.equals(selectedCategoryName, ignoreCase = true) && period == selectedBudgetPeriod
                }

            if (isSelectedTypeDuplicateForNewPeriod) {
                selectedCategoryName = null
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        SmallFloatingActionButton(
            onClick = { isSheetContentVisible = true },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, "Small floating action button.")
        }

        if (isSheetContentVisible) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxHeight(),
                sheetState = sheetState,
                onDismissRequest = { isSheetContentVisible = false },
                containerColor = MaterialTheme.colorScheme.onTertiary
            ) {
                val keyboardController = LocalSoftwareKeyboardController.current

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    Text(
                        text = when (currentOperationType) {
                            AddType.BUDGET -> "Add New Budget"
                            AddType.INCOME -> "Add New Income"
                            AddType.OUTGOING -> "Add New Outgoing"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (showTransactionTypeToggle) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { currentOperationType = AddType.OUTGOING },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentOperationType == AddType.OUTGOING) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (currentOperationType == AddType.OUTGOING) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Outgoing")
                            }
                            Button(
                                onClick = { currentOperationType = AddType.INCOME },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentOperationType == AddType.INCOME) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (currentOperationType == AddType.INCOME) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Income")
                            }
                        }
                    }

                    when (currentOperationType) {
                        AddType.BUDGET -> {
                            com.example.financeapp.ui.components.textfields.OutlinedTextField(
                                keyboardController = keyboardController,
                                value = budgetValueState,
                                onValueChange = { budgetValueState = it },
                                label = { Text("Budget Limit") },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done,
                                    keyboardType = KeyboardType.Decimal,
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        AddType.INCOME, AddType.OUTGOING -> {
                            com.example.financeapp.ui.components.textfields.OutlinedTextField(
                                keyboardController = keyboardController,
                                value = transactionValueState,
                                onValueChange = { transactionValueState = it },
                                label = { Text("Transaction Amount") },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done,
                                    keyboardType = KeyboardType.Decimal,
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select Category:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = { isDeleteModeActive = !isDeleteModeActive },
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = if (isDeleteModeActive) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isDeleteModeActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Icon(
                                imageVector = if (isDeleteModeActive) Icons.Filled.Done else Icons.Filled.Edit,
                                contentDescription = if (isDeleteModeActive) "Done Managing" else "Manage Categories",
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(if (isDeleteModeActive) "Done" else "Manage")
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val usedBudgetCategoriesForPeriod = remember(existingBudgetNameTypes, selectedBudgetPeriod, currentOperationType) {
                        if (currentOperationType == AddType.BUDGET) {
                            existingBudgetNameTypes
                                .filter { it.second == selectedBudgetPeriod }
                                .map { it.first }
                                .toSet()
                        } else {
                            emptySet()
                        }
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categoriesToDisplayForSelection.forEach { categoryName ->
                            if (!(currentOperationType == AddType.OUTGOING && categoryName.equals("Savings", ignoreCase = true))) {
                                val isCustomType = allCustomTypesFromDb.contains(categoryName) &&
                                        !(if (currentOperationType == AddType.INCOME) predeterminedIncomeTypes else predeterminedOutgoingTypes)
                                            .any { it.equals(categoryName, ignoreCase = true) }
                                val isTileEnabled = if (currentOperationType == AddType.BUDGET) {
                                    !usedBudgetCategoriesForPeriod.contains(categoryName) && !isDeleteModeActive
                                } else {
                                    !isDeleteModeActive
                                }

                                SelectableTypeTile(
                                    typeName = categoryName,
                                    isSelected = selectedCategoryName == categoryName && !isDeleteModeActive,
                                    onTypeSelected = { selectedItemName ->
                                        if (!isDeleteModeActive) {
                                            selectedCategoryName = selectedItemName
                                        }
                                    },
                                    baseColor = if (currentOperationType != AddType.INCOME) Color(
                                        ColorPalette.getOutgoingColor(categoryName)
                                    ) else Color(ColorPalette.getIncomeColor(categoryName)),
                                    enabled = isTileEnabled,
                                    isDeletable = isCustomType,
                                    isDeleteModeActive = isDeleteModeActive,
                                    onDeleteClicked = { typeNameToDelete ->
                                        scope.launch {
                                            val success =
                                                if (currentOperationType != AddType.INCOME) customOutgoingViewModel.deleteCustomOutgoingTypeName(
                                                    typeNameToDelete
                                                ) else customIncomeViewModel.deleteCustomIncomeTypeName(
                                                    typeNameToDelete
                                                )
                                            if (success) {
                                                if (selectedCategoryName == typeNameToDelete) {
                                                    selectedCategoryName = null
                                                }
                                                if (customCategoryNameInput.text.equals(
                                                        typeNameToDelete,
                                                        ignoreCase = true
                                                    )
                                                ) {
                                                    customCategoryNameInput = TextFieldValue("")
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        if (!isDeleteModeActive) {
                            Button(
                                onClick = {
                                    showCustomCategoryNameInput = !showCustomCategoryNameInput
                                    if (!showCustomCategoryNameInput) {
                                        keyboardController?.hide()
                                    } else {
                                        customCategoryNameInput = TextFieldValue("")
                                    }
                                },
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (showCustomCategoryNameInput) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (showCustomCategoryNameInput) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(
                                    imageVector = if (showCustomCategoryNameInput) Icons.Filled.Close else Icons.Filled.Add,
                                    contentDescription = if (showCustomCategoryNameInput) "Cancel Custom Category" else "Add Custom Category"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (showCustomCategoryNameInput && !isDeleteModeActive) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = customCategoryNameInput,
                                onValueChange = { customCategoryNameInput = it },
                                label = { Text("New Category Name") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done,
                                    capitalization = KeyboardCapitalization.Words
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (customCategoryNameInput.text.trim().isNotEmpty()) {
                                            addCustomCategoryAction()
                                        } else {
                                            keyboardController?.hide()
                                        }
                                    }
                                ),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    cursorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    focusedLabelColor = MaterialTheme.colorScheme.secondaryContainer,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = addCustomCategoryAction,
                                enabled = customCategoryNameInput.text.trim().isNotEmpty(),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.12f),
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.38f)
                                ),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Save Category Name"
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (currentOperationType == AddType.BUDGET) {
                        Text(
                            "Select Budget Period:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Monthly", "Yearly", "Custom").forEach { period ->
                                Button(
                                    onClick = { selectedBudgetPeriod = period },
                                    modifier = Modifier.weight(1f),
                                    enabled = !isDeleteModeActive,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedBudgetPeriod == period) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (selectedBudgetPeriod == period) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(period)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (selectedBudgetPeriod == "Custom") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { showStartDatePicker = true },
                                    modifier = Modifier.weight(1f),
                                    enabled = !isDeleteModeActive,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedStartDate != null) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (selectedStartDate != null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = selectedStartDate?.let { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it)) } ?: "Select Start Date",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Button(
                                    onClick = { showEndDatePicker = true },
                                    modifier = Modifier.weight(1f),
                                    enabled = !isDeleteModeActive,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedEndDate != null) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (selectedEndDate != null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = selectedEndDate?.let { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it)) } ?: "Select End Date",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    val isFormValid by remember(
                        currentOperationType,
                        selectedCategoryName,
                        budgetValueState,
                        transactionValueState,
                        selectedBudgetPeriod,
                        selectedStartDate,
                        selectedEndDate
                    ) {
                        derivedStateOf {
                            when (currentOperationType) {
                                AddType.BUDGET -> {
                                    val limit = budgetValueState.text.toDoubleOrNull()
                                    val currentSelectedStartDate = selectedStartDate
                                    val currentSelectedEndDate = selectedEndDate

                                    selectedCategoryName != null &&
                                            limit != null && limit > 0 &&
                                            selectedBudgetPeriod != null &&
                                            (
                                                    selectedBudgetPeriod != "Custom" ||
                                                            (currentSelectedStartDate != null && currentSelectedEndDate != null && currentSelectedStartDate < currentSelectedEndDate)
                                                    )
                                }
                                AddType.INCOME, AddType.OUTGOING -> {
                                    val amount = transactionValueState.text.toDoubleOrNull()
                                    selectedCategoryName != null &&
                                            amount != null && amount > 0
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            when (currentOperationType) {
                                AddType.BUDGET -> {
                                    val categoryName = selectedCategoryName
                                    val limitText = budgetValueState.text
                                    val period = selectedBudgetPeriod
                                    val startDateMillis = selectedStartDate
                                    val endDateMillis = selectedEndDate

                                    if (categoryName != null && limitText.isNotBlank() && period != null) {
                                        val limitAmount = limitText.toDoubleOrNull()
                                        if (limitAmount != null && limitAmount > 0) {
                                            budgetViewModel.addBudget(
                                                budgetName = categoryName,
                                                budgetLimit = limitAmount,
                                                budgetType = period,
                                                customStartDate = if (period == "Custom") startDateMillis else null,
                                                customEndDate = if (period == "Custom") endDateMillis else null
                                            )
                                            isSheetContentVisible = false
                                            resetFormState()
                                        }
                                    }
                                }
                                AddType.INCOME, AddType.OUTGOING -> {
                                    val nameFromCategory = selectedCategoryName
                                    val valueString = transactionValueState.text.trim()
                                    val amount = valueString.toDoubleOrNull()

                                    if (!nameFromCategory.isNullOrEmpty() && amount != null && amount > 0) {
                                        transactionViewModel.addTransaction(
                                            transactionName = nameFromCategory,
                                            transactionValue = amount,
                                            type = currentOperationType,
                                            budgetCategoryForTransaction = if (currentOperationType == AddType.OUTGOING) {
                                                nameFromCategory
                                            } else {
                                                null
                                            }
                                        )
                                        isSheetContentVisible = false
                                        resetFormState()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = isFormValid,
                        colors = if (isFormValid) {
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.12f),
                                disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.38f)
                            )
                        } else {
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f),
                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                            )
                        }
                    ) {
                        Text(
                            text = when (currentOperationType) {
                                AddType.BUDGET -> "Add Budget"
                                AddType.INCOME -> "Add Income"
                                AddType.OUTGOING -> "Add Outgoing"
                            },
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        selectedStartDate = datePickerState.selectedDateMillis
                        showStartDatePicker = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showStartDatePicker = false },
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

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        selectedEndDate = datePickerState.selectedDateMillis
                        showEndDatePicker = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showEndDatePicker = false },
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

@Composable
fun SelectableTypeTile(
    typeName: String,
    isSelected: Boolean,
    onTypeSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    baseColor: Color,
    enabled: Boolean = true,
    isDeletable: Boolean = false,
    onDeleteClicked: ((String) -> Unit)? = null,
    isDeleteModeActive: Boolean = false
) {
    val containerTargetColor: Color
    val textTargetColor: Color

    val deleteModeContainerColor = MaterialTheme.colorScheme.errorContainer
    val deleteModeTextColor = MaterialTheme.colorScheme.onErrorContainer

    when {
        isDeletable && isDeleteModeActive -> {
            containerTargetColor = deleteModeContainerColor
            textTargetColor = deleteModeTextColor
        }
        isSelected && enabled -> {
            containerTargetColor = baseColor
            textTargetColor = MaterialTheme.colorScheme.onSurfaceVariant
        }
        !enabled && !isDeleteModeActive -> {
            containerTargetColor = baseColor.copy(alpha = 0.12f)
            textTargetColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        }
        else -> {
            containerTargetColor = baseColor.copy(alpha = 0.2f)
            textTargetColor = baseColor
        }
    }

    Card(
        modifier = modifier
            .clickable(enabled = enabled && !(isDeletable && isDeleteModeActive)) {
                if (!(isDeletable && isDeleteModeActive)) {
                    onTypeSelected(typeName)
                }
            }
            .alpha(
                when {
                    isDeleteModeActive && !isDeletable -> 0.5f
                    else -> 1f
                }
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerTargetColor,
            contentColor = textTargetColor
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(
                        start = 12.dp,
                        end = if (isDeletable && isDeleteModeActive && onDeleteClicked != null) 0.dp else 12.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
            ) {
                Text(
                    text = typeName,
                    color = textTargetColor,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (isDeletable && isDeleteModeActive && onDeleteClicked != null) {
                    IconButton(
                        onClick = { onDeleteClicked(typeName) },
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .size(24.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = deleteModeTextColor.copy(alpha = 0.9f)
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Delete $typeName",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}