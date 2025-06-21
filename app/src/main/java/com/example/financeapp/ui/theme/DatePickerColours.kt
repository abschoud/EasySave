package com.example.financeapp.ui.theme

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable

object DatePickerColours {
    val customDateTextFieldColors: TextFieldColors
        @Composable
        get() = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedLabelColor = MaterialTheme.colorScheme.secondaryContainer,
            cursorColor = MaterialTheme.colorScheme.secondaryContainer,
            selectionColors = TextSelectionColors(
                handleColor = MaterialTheme.colorScheme.secondaryContainer,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer
            )
        )

    @OptIn(ExperimentalMaterial3Api::class)
    val customDatePickerColors: DatePickerColors
        @Composable
        get() = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            headlineContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            weekdayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            subheadContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            yearContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedYearContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedYearContainerColor = MaterialTheme.colorScheme.secondary,
            dayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledDayContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
            selectedDayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedDayContainerColor = MaterialTheme.colorScheme.secondary,
            todayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            todayDateBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            navigationContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledYearContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            currentYearContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledSelectedYearContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledSelectedYearContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledSelectedDayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledSelectedDayContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
            dayInSelectionRangeContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
            dividerColor = MaterialTheme.colorScheme.onSurfaceVariant,
            dateTextFieldColors = customDateTextFieldColors
        )
}