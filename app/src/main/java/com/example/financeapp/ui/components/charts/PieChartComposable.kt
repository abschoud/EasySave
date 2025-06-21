package com.example.financeapp.ui.components.charts

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.financeapp.db.transaction.Transactionlist
import com.example.financeapp.ui.theme.ColorPalette
import com.example.financeapp.utils.AddType

@Composable
fun PieChartComposable(transactions: List<Transactionlist>, context: android.content.Context) {
    var pieList by remember { mutableStateOf(emptyList<PieChartData>()) }
    val pieChartKey = remember(transactions) {
        transactions.joinToString { "${it.transactionName}-${it.transactionValue}" }
    }
    val totalTransactionValue = transactions.sumOf { it.transactionValue }
    val isEmptyState = transactions.isEmpty()

    if (!isEmptyState) {
        val dataWithTypes = transactions.groupBy { it.transactionName }
            .map { (categoryName, transactionsInCategory) ->
                val total = transactionsInCategory.sumOf { it.transactionValue }
                val type = transactionsInCategory.firstOrNull()?.transactionType ?: AddType.OUTGOING
                Triple(categoryName, total, type)
            }

        pieList = dataWithTypes.map { (category, total, type) ->
            val colorInt = if (type == AddType.INCOME) {
                ColorPalette.getIncomeColor(category)
            } else {
                ColorPalette.getOutgoingColor(category)
            }
            val baseColor = Color(colorInt)
            val transparentColor = baseColor.copy(alpha = 0.5f)

            PieChartData(
                value = total,
                description = category,
                color = transparentColor
            )
        }
    } else {
        pieList = listOf(
            PieChartData(
                value = 1.0,
                description = "No Transactions",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        )
    }

    key(pieChartKey) {
        PieChart(
            dataPoints = pieList,
            totalValue = if (isEmptyState) 0.0 else totalTransactionValue,
            isEmptyState = isEmptyState,
            onSliceClick = { }
        )
    }
}