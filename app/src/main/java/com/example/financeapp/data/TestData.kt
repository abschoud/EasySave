package com.example.financeapp.data

import com.example.financeapp.db.budget.Budgetlist
import com.example.financeapp.db.transaction.Transactionlist
import com.example.financeapp.utils.AddType
import com.example.financeapp.ui.theme.ColorPalette
import java.util.Calendar
import java.util.Date
import kotlin.random.Random

fun generateComprehensiveTestTransactions(
    numberOfMonths: Int = 12
): List<Transactionlist> {
    val testTransactions = mutableListOf<Transactionlist>()
    val calendar = Calendar.getInstance()
    var transactionIdCounter = 2000

    val transactionPatterns = listOf(
        TransactionPattern(
            name = "Salary",
            type = AddType.INCOME,
            frequency = Frequency.MONTHLY,
            valueRange = 2500.0 to 3500.0,
            dayOfMonth = 25
        ),
        TransactionPattern(
            name = "Freelance",
            type = AddType.INCOME,
            frequency = Frequency.IRREGULAR,
            valueRange = 100.0 to 800.0,
            irregularityDays = 30
        ),
        TransactionPattern(
            name = "Bonus",
            type = AddType.INCOME,
            frequency = Frequency.IRREGULAR,
            valueRange = 50.0 to 500.0,
            irregularityDays = 90
        ),
        TransactionPattern(
            name = "Gift",
            type = AddType.INCOME,
            frequency = Frequency.IRREGULAR,
            valueRange = 20.0 to 200.0,
            irregularityDays = 60
        ),
        TransactionPattern(
            name = "Bills",
            type = AddType.OUTGOING,
            frequency = Frequency.MONTHLY,
            valueRange = 150.0 to 300.0,
            dayOfMonth = 15
        ),
        TransactionPattern(
            name = "Rent",
            type = AddType.OUTGOING,
            frequency = Frequency.MONTHLY,
            valueRange = 800.0 to 1200.0,
            dayOfMonth = 1
        ),

        TransactionPattern(
            name = "Food",
            type = AddType.OUTGOING,
            frequency = Frequency.WEEKLY,
            valueRange = 70.0 to 200.0,
            dayOfWeek = Calendar.SATURDAY
        ),
        TransactionPattern(
            name = "Transportation",
            type = AddType.OUTGOING,
            frequency = Frequency.DAILY_WEEKDAYS,
            valueRange = 5.0 to 25.0,
            dayOfWeek = null
        ),
        TransactionPattern(
            name = "Entertainment",
            type = AddType.OUTGOING,
            frequency = Frequency.IRREGULAR,
            valueRange = 30.0 to 150.0,
            irregularityDays = 10
        ),
        TransactionPattern(
            name = "Shopping",
            type = AddType.OUTGOING,
            frequency = Frequency.IRREGULAR,
            valueRange = 50.0 to 300.0,
            irregularityDays = 20
        ),
        TransactionPattern(
            name = "Health",
            type = AddType.OUTGOING,
            frequency = Frequency.IRREGULAR,
            valueRange = 10.0 to 50.0,
            irregularityDays = 45
        ),
        TransactionPattern(
            name = "Education",
            type = AddType.OUTGOING,
            frequency = Frequency.MONTHLY,
            valueRange = 80.0 to 160.0,
            dayOfMonth = 5
        ),
        TransactionPattern(
            name = "Car",
            type = AddType.OUTGOING,
            frequency = Frequency.IRREGULAR,
            valueRange = 50.0 to 500.0,
            irregularityDays = 60
        ),
        TransactionPattern(
            name = "Clothing",
            type = AddType.OUTGOING,
            frequency = Frequency.IRREGULAR,
            valueRange = 20.0 to 100.0,
            irregularityDays = 30
        ),
        TransactionPattern(
            name = "Restaurant",
            type = AddType.OUTGOING,
            frequency = Frequency.WEEKLY,
            valueRange = 20.0 to 80.0,
            dayOfWeek = Calendar.FRIDAY
        )
    )

    for (monthOffset in 0 until numberOfMonths) {
        calendar.time = Date()
        calendar.add(Calendar.MONTH, -monthOffset)

        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        transactionPatterns.forEach { pattern ->
            when (pattern.frequency) {
                Frequency.MONTHLY -> {
                    calendar.set(Calendar.MONTH, currentMonth)
                    calendar.set(Calendar.YEAR, currentYear)
                    val day = minOf(pattern.dayOfMonth ?: 1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                    calendar.set(Calendar.DAY_OF_MONTH, day)
                    calendar.set(Calendar.HOUR_OF_DAY, Random.nextInt(8, 18))
                    calendar.set(Calendar.MINUTE, Random.nextInt(0, 59))
                    calendar.set(Calendar.SECOND, Random.nextInt(0, 59))
                    calendar.set(Calendar.MILLISECOND, Random.nextInt(0, 999))

                    testTransactions.add(
                        Transactionlist(
                            transactionId = transactionIdCounter++,
                            transactionName = pattern.name,
                            transactionValue = String.format("%.2f", Random.nextDouble(pattern.valueRange.first, pattern.valueRange.second)).toDouble(),
                            transactionCreatedAt = calendar.time,
                            transactionType = pattern.type,
                            transactionColor = ColorPalette.getOutgoingColor(pattern.name)
                        )
                    )
                }
                Frequency.WEEKLY -> {
                    calendar.set(Calendar.MONTH, currentMonth)
                    calendar.set(Calendar.YEAR, currentYear)
                    calendar.set(Calendar.DAY_OF_MONTH, 1)

                    while (calendar.get(Calendar.DAY_OF_WEEK) != pattern.dayOfWeek) {
                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                    }

                    while (calendar.get(Calendar.MONTH) == currentMonth) {
                        calendar.set(Calendar.HOUR_OF_DAY, Random.nextInt(8, 20))
                        calendar.set(Calendar.MINUTE, Random.nextInt(0, 59))
                        calendar.set(Calendar.SECOND, Random.nextInt(0, 59))
                        calendar.set(Calendar.MILLISECOND, Random.nextInt(0, 999))

                        testTransactions.add(
                            Transactionlist(
                                transactionId = transactionIdCounter++,
                                transactionName = pattern.name,
                                transactionValue = String.format("%.2f", Random.nextDouble(pattern.valueRange.first, pattern.valueRange.second)).toDouble(),
                                transactionCreatedAt = calendar.time,
                                transactionType = pattern.type,
                                transactionColor = ColorPalette.getOutgoingColor(pattern.name)
                            )
                        )
                        calendar.add(Calendar.DAY_OF_MONTH, 7)
                    }
                }
                Frequency.DAILY_WEEKDAYS -> {
                    calendar.set(Calendar.MONTH, currentMonth)
                    calendar.set(Calendar.YEAR, currentYear)
                    calendar.set(Calendar.DAY_OF_MONTH, 1)

                    while (calendar.get(Calendar.MONTH) == currentMonth) {
                        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                        if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                            calendar.set(Calendar.HOUR_OF_DAY, Random.nextInt(7, 10))
                            calendar.set(Calendar.MINUTE, Random.nextInt(0, 59))
                            calendar.set(Calendar.SECOND, Random.nextInt(0, 59))
                            calendar.set(Calendar.MILLISECOND, Random.nextInt(0, 999))

                            testTransactions.add(
                                Transactionlist(
                                    transactionId = transactionIdCounter++,
                                    transactionName = pattern.name,
                                    transactionValue = String.format("%.2f", Random.nextDouble(pattern.valueRange.first, pattern.valueRange.second)).toDouble(),
                                    transactionCreatedAt = calendar.time,
                                    transactionType = pattern.type,
                                    transactionColor = ColorPalette.getOutgoingColor(pattern.name)
                                )
                            )
                        }
                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                    }
                }
                Frequency.IRREGULAR -> {
                    calendar.set(Calendar.MONTH, currentMonth)
                    calendar.set(Calendar.YEAR, currentYear)
                    calendar.set(Calendar.DAY_OF_MONTH, 1)

                    while (calendar.get(Calendar.MONTH) == currentMonth) {
                        val probability = 1.0 / (pattern.irregularityDays ?: 30)

                        if (Random.nextDouble() < probability) {
                            calendar.set(Calendar.HOUR_OF_DAY, Random.nextInt(9, 21))
                            calendar.set(Calendar.MINUTE, Random.nextInt(0, 59))
                            calendar.set(Calendar.SECOND, Random.nextInt(0, 59))
                            calendar.set(Calendar.MILLISECOND, Random.nextInt(0, 999))

                            testTransactions.add(
                                Transactionlist(
                                    transactionId = transactionIdCounter++,
                                    transactionName = pattern.name,
                                    transactionValue = String.format("%.2f", Random.nextDouble(pattern.valueRange.first, pattern.valueRange.second)).toDouble(),
                                    transactionCreatedAt = calendar.time,
                                    transactionType = pattern.type,
                                    transactionColor = ColorPalette.getOutgoingColor(pattern.name)
                                )
                            )
                        }
                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                    }
                }
            }
        }
    }
    return testTransactions.sortedBy { it.transactionCreatedAt }
}

val generatedComprehensiveTestTransactions: List<Transactionlist> = generateComprehensiveTestTransactions(numberOfMonths = 12)

fun generateTestBudgets(): List<Budgetlist> {
    val testBudgets = mutableListOf<Budgetlist>()
    var budgetIdCounter = 100

    fun getBudgetTimestamps(period: BudgetPeriod): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        val startDate: Date
        val endDate: Date

        when (period) {
            BudgetPeriod.MONTHLY -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                startDate = cal.time
                cal.add(Calendar.MONTH, 1)
                cal.add(Calendar.DAY_OF_MONTH, -1)
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                endDate = cal.time
            }
            BudgetPeriod.YEARLY -> {
                cal.set(Calendar.DAY_OF_YEAR, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                startDate = cal.time
                cal.add(Calendar.YEAR, 1)
                cal.add(Calendar.DAY_OF_YEAR, -1)
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                endDate = cal.time
            }
        }
        return Pair(startDate.time, endDate.time)
    }

    val budgetPatterns = listOf(
        BudgetPattern(name = "Food", limit = 600.0, spent = 550.0, remaining = 50.0, period = BudgetPeriod.MONTHLY),
        BudgetPattern(name = "Transportation", limit = 300.0, spent = 280.0, remaining = 20.0, period = BudgetPeriod.MONTHLY),
        BudgetPattern(name = "Entertainment", limit = 200.0, spent = 180.0, remaining = 20.0, period = BudgetPeriod.MONTHLY),
        BudgetPattern(name = "Shopping", limit = 150.0, spent = 160.0, remaining = -10.0, period = BudgetPeriod.MONTHLY),
        BudgetPattern(name = "Health", limit = 100.0, spent = 50.0, remaining = 50.0, period = BudgetPeriod.MONTHLY),
        BudgetPattern(name = "Education", limit = 1000.0, spent = 800.0, remaining = 200.0, period = BudgetPeriod.YEARLY),
        BudgetPattern(name = "Car", limit = 800.0, spent = 300.0, remaining = 500.0, period = BudgetPeriod.YEARLY),
        BudgetPattern(name = "Clothing", limit = 500.0, spent = 450.0, remaining = 50.0, period = BudgetPeriod.YEARLY),
        BudgetPattern(name = "Savings Goal", limit = 5000.0, spent = 1500.0, remaining = 3500.0, period = BudgetPeriod.YEARLY)
    )

    budgetPatterns.forEach { pattern ->
        val timestamps = getBudgetTimestamps(pattern.period)
        testBudgets.add(
            Budgetlist(
                budgetId = budgetIdCounter++,
                budgetCreatedAt = Date(),
                budgetName = pattern.name,
                budgetLimit = pattern.limit,
                budgetSpent = pattern.spent,
                budgetRemaining = pattern.remaining,
                budgetComplete = pattern.remaining < 0,
                budgetColor = ColorPalette.getOutgoingColor(pattern.name),
                budgetType = pattern.period.name,
                startDate = timestamps.first,
                endDate = timestamps.second
            )
        )
    }

    return testBudgets
}

val generatedTestBudgets: List<Budgetlist> = generateTestBudgets()