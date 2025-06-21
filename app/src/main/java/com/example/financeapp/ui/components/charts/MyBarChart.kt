package com.example.financeapp.ui.components.charts

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.financeapp.db.transaction.Transactionlist
import com.example.financeapp.utils.ChartTimePeriod
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.patrykandpatrick.vico.core.common.shape.CorneredShape.Corner
import com.patrykandpatrick.vico.core.common.shape.CorneredShape.Corner.Relative
import com.patrykandpatrick.vico.core.common.shape.CorneredShape.CornerTreatment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TreeMap

@Composable
fun MyBarChart(
    transactions: List<Transactionlist>,
    transactionColor: Color,
    timePeriod: ChartTimePeriod
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val (chartData, xAxisLabels) = remember(transactions, timePeriod) {
        when (timePeriod) {
            ChartTimePeriod.Week -> processWeeklyData(transactions)
            ChartTimePeriod.Month -> processMonthlyDataForDays(transactions)
            ChartTimePeriod.Year -> processYearlyDataForMonths(transactions)
            ChartTimePeriod.All_Time -> processAllTimeDataForMonths(transactions)
        }
    }

    LaunchedEffect(chartData) {
        modelProducer.runTransaction {
            if (chartData.isNotEmpty()) {
                columnSeries {
                    series(chartData.map { it.second })
                }
            }
        }
    }

    val columnLineComponent = rememberLineComponent(
        fill = fill(transactionColor),
        thickness = 20.dp,
        shape = CorneredShape(
            topLeft = Relative(sizePercent = 15, treatment = CornerTreatment.Rounded),
            topRight = Relative(sizePercent = 15, treatment = CornerTreatment.Rounded),
            bottomRight = Corner.Sharp,
            bottomLeft = Corner.Sharp,
        )
    )
    val startAxisValueFormatter = remember {
        CartesianValueFormatter { chartContext, axisValue, verticalPos ->
            "£${String.format(Locale.getDefault(), "%.0f", axisValue)}"
        }
    }

    val bottomAxisValueFormatter = remember(xAxisLabels) {
        CartesianValueFormatter { chartContext, axisValue, verticalPos ->
            val index = axisValue.toInt()

            if (index >= 0 && index < xAxisLabels.size) {
                xAxisLabels[index]
            } else {
                "?"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(225.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        if (chartData.isNotEmpty()) {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        columnProvider = ColumnCartesianLayer.ColumnProvider.series(columnLineComponent),
                        columnCollectionSpacing = 4.dp
                    ),
                    startAxis = VerticalAxis.rememberStart(
                        valueFormatter = startAxisValueFormatter,
                        line = rememberLineComponent(fill =  fill(MaterialTheme.colorScheme.outline)),
                        tick = rememberLineComponent(fill =  fill(MaterialTheme.colorScheme.outline)),
                        guideline = rememberLineComponent(fill = fill(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))),
                        label = rememberTextComponent(color = MaterialTheme.colorScheme.outline),
                    ),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = bottomAxisValueFormatter,
                        line = rememberLineComponent(fill = fill(MaterialTheme.colorScheme.outline)),
                        tick = rememberLineComponent(fill = fill(MaterialTheme.colorScheme.outline)),
                        guideline = rememberLineComponent(fill = fill(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))),
                        label = rememberTextComponent(color = MaterialTheme.colorScheme.outline),
                    ),
                ),
                modelProducer = modelProducer,
            )
        }
    }
}

private fun processWeeklyData(transactions: List<Transactionlist>): Pair<List<Pair<String, Double>>, List<String>> {
    val calendar = Calendar.getInstance()
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val weeklyTotals = TreeMap<Int, Double>()
    val dayLabels = mutableListOf<String>()
    val tempCal = calendar.clone() as Calendar
    tempCal.firstDayOfWeek = Calendar.MONDAY
    tempCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    val targetWeekDayKeys = mutableSetOf<Int>()
    for (i in 0..6) {
        val dayKey = tempCal.get(Calendar.DAY_OF_YEAR)
        weeklyTotals[dayKey] = 0.0
        dayLabels.add(dayFormat.format(tempCal.time))
        targetWeekDayKeys.add(dayKey)
        tempCal.add(Calendar.DAY_OF_MONTH, 1)
    }

    transactions.forEach { transaction ->
        calendar.time = transaction.transactionCreatedAt
        val transactionDayKey = calendar.get(Calendar.DAY_OF_YEAR)

        if (targetWeekDayKeys.contains(transactionDayKey)) {
            weeklyTotals[transactionDayKey] = (weeklyTotals[transactionDayKey] ?: 0.0) + transaction.transactionValue
        }
    }

    val chartData = weeklyTotals.values.mapIndexed { index, total ->
        dayLabels[index] to total
    }.toList()

    return chartData to dayLabels
}

private fun processMonthlyDataForDays(transactions: List<Transactionlist>): Pair<List<Pair<String, Double>>, List<String>> {
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)
    val numDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val dailyTotals = TreeMap<Int, Double>()
    for (i in 1..numDaysInMonth) {
        dailyTotals[i] = 0.0
    }

    transactions.forEach {
        calendar.time = it.transactionCreatedAt
        if (calendar.get(Calendar.MONTH) == currentMonth && calendar.get(Calendar.YEAR) == currentYear) {
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            dailyTotals[dayOfMonth] = (dailyTotals[dayOfMonth] ?: 0.0) + it.transactionValue
        }
    }
    val chartData = dailyTotals.map { (day, total) -> day.toString() to total }
    val xAxisLabels = dailyTotals.keys.map { it.toString() }

    return chartData to xAxisLabels
}

private fun processYearlyDataForMonths(transactions: List<Transactionlist>): Pair<List<Pair<String, Double>>, List<String>> {
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
    val monthlyTotals = TreeMap<Int, Double>()
    val monthLabelsList = mutableListOf<String>()
    val tempCal = Calendar.getInstance()
    tempCal.set(Calendar.YEAR, currentYear)

    for (i in 0..11) {
        monthlyTotals[i] = 0.0
        tempCal.set(Calendar.MONTH, i)
        monthLabelsList.add(monthFormat.format(tempCal.time))
    }

    transactions.forEach {
        calendar.time = it.transactionCreatedAt
        if (calendar.get(Calendar.YEAR) == currentYear) {
            val month = calendar.get(Calendar.MONTH)
            monthlyTotals[month] = (monthlyTotals[month] ?: 0.0) + it.transactionValue
        }
    }

    val chartData = monthlyTotals.values.mapIndexed { index, total -> monthLabelsList[index] to total }.toList()

    return chartData to monthLabelsList
}

private fun processAllTimeDataForMonths(transactions: List<Transactionlist>): Pair<List<Pair<String, Double>>, List<String>> {
    if (transactions.isEmpty()) return emptyList<Pair<String, Double>>() to emptyList()

    val calendar = Calendar.getInstance()
    val monthYearFormat = SimpleDateFormat("MMM yy", Locale.getDefault())
    val monthlyTotals = TreeMap<String, Double>()

    transactions.forEach {
        calendar.time = it.transactionCreatedAt
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val yearMonthKey = String.format("%04d%02d", year, month)
        monthlyTotals[yearMonthKey] = (monthlyTotals[yearMonthKey] ?: 0.0) + it.transactionValue
    }

    val chartData = mutableListOf<Pair<String, Double>>()
    val xAxisLabels = mutableListOf<String>()

    monthlyTotals.forEach { (yearMonthKey, total) ->
        val year = yearMonthKey.substring(0, 4).toInt()
        val month = yearMonthKey.substring(4, 6).toInt()
        calendar.set(year, month, 1)
        val label = monthYearFormat.format(calendar.time)
        chartData.add(label to total)
        xAxisLabels.add(label)
    }
    return chartData to xAxisLabels
}