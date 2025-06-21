package com.example.financeapp.ui.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun PieChart(
    innerRadius: Double = 120.0,
    dataPoints: List<PieChartData>,
    isEmptyState: Boolean,
    totalValue: Double,
    onSliceClick: (PieChartData) -> Unit,
    modifier: Modifier = Modifier,
    animationDurationMillis: Int = 1000,
    forceAnimateKey: Any? = dataPoints
) {
    if (dataPoints.isEmpty() && isEmptyState) {
        return
    }

    var inputList by remember(dataPoints, isEmptyState) {
        mutableStateOf(
            if (isEmptyState && dataPoints.isNotEmpty()) {
                listOf(dataPoints.first().copy(value = 1.0, description = "No Transactions"))
            } else {
                dataPoints
            }
        )
    }
    var pieCenter by remember {
        mutableStateOf(Offset.Zero)
    }
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(key1 = forceAnimateKey) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = animationDurationMillis)
        )
    }
    val textMeasurer = rememberTextMeasurer()
    val gapDegrees = if (isEmptyState && inputList.size == 1) 0.0 else 2.0
    val numberOfGaps = if (isEmptyState && inputList.size == 1) 0 else inputList.size
    val remainingDegrees = 360.0 - (gapDegrees * numberOfGaps)
    val calculatedTotalValueForAngles = inputList.sumOf { it.value }
    val anglePerValue = if (calculatedTotalValueForAngles > 0) remainingDegrees / calculatedTotalValueForAngles else 0.0
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer

    Canvas(
        modifier = modifier
            .size(300.dp)
            .pointerInput(inputList, isEmptyState, innerRadius) {
                detectTapGestures(onTap = { offset ->
                    if (isEmptyState && inputList.isNotEmpty()) {
                        onSliceClick(inputList.first())
                        return@detectTapGestures
                    }
                    if (inputList.isEmpty()) return@detectTapGestures

                    val angle = Math.toDegrees(
                        atan2(
                            offset.y - pieCenter.y,
                            offset.x - pieCenter.x
                        ).toDouble()
                    )
                    val tapAngleInDegrees = (if (angle < 0) angle + 360 else angle)
                    val distanceToCenter = kotlin.math.sqrt(
                        (offset.x - pieCenter.x).pow(2) + (offset.y - pieCenter.y).pow(2)
                    )

                    if (distanceToCenter > innerRadius) {
                        var currentAngleSum = 0.0
                        inputList.forEach { pieChartData ->
                            val sliceAngle = pieChartData.value * anglePerValue
                            if (tapAngleInDegrees >= currentAngleSum && tapAngleInDegrees < (currentAngleSum + sliceAngle)) {
                                val tappedSlice = pieChartData.copy(isTapped = !pieChartData.isTapped)
                                inputList = inputList.map {
                                    if (it.description == pieChartData.description) tappedSlice else it.copy(isTapped = false)
                                }
                                onSliceClick(tappedSlice)
                                return@detectTapGestures
                            }
                            currentAngleSum += sliceAngle + gapDegrees
                        }
                    }
                })
            }
    ) {
        val width = size.width
        val height = size.height
        val radius = width / 2f
        pieCenter = Offset(x = width / 2f, y = height / 2f)
        var currentStartAngle = 0.0

        inputList.forEach { pieChartData ->
            val scaleFactor = if (pieChartData.isTapped) 0.78f else .75f
            val angleToDraw = pieChartData.value * anglePerValue
            val animatedSweepAngle = (angleToDraw * animationProgress.value).toFloat()

            if (animatedSweepAngle > 0f) {
                scale(scaleFactor) {
                    drawArc(
                        color = pieChartData.color,
                        startAngle = currentStartAngle.toFloat(),
                        sweepAngle = animatedSweepAngle,
                        useCenter = false,
                        size = Size(radius * 2f, radius * 2f),
                        topLeft = Offset(
                            (this.size.width - radius * 2f) / 2f,
                            (this.size.height - radius * 2f) / 2f
                        ),
                        style = Stroke(width = 150f, cap = StrokeCap.Butt)
                    )
                }
            }
            val fullSliceSweepAngle = angleToDraw

            if (animationProgress.value == 1f) {
                if (!isEmptyState && calculatedTotalValueForAngles > 0) {
                    val percentage = (pieChartData.value / calculatedTotalValueForAngles * 100).toInt()
                    if (percentage > 5) {
                        val midAngleDegrees = currentStartAngle + fullSliceSweepAngle / 2.0
                        val textPlacementRadius = radius - (460f / 2f) / 2f
                        val textX = (pieCenter.x + textPlacementRadius * cos(Math.toRadians(midAngleDegrees))).toFloat()
                        val textY = (pieCenter.y + textPlacementRadius * sin(Math.toRadians(midAngleDegrees))).toFloat()
                        val textStyle = TextStyle(fontSize = 12.sp, color = onSurfaceVariantColor)
                        val textLayoutResult = textMeasurer.measure("$percentage %", style = textStyle)
                        drawText(
                            textLayoutResult,
                            topLeft = Offset(
                                textX - textLayoutResult.size.width / 2,
                                textY - textLayoutResult.size.height / 2
                            )
                        )
                    }
                }

                if (!isEmptyState && pieChartData.isTapped) {
                    val midAngleDegrees = currentStartAngle + fullSliceSweepAngle / 2.0
                    val textOuterRadius = innerRadius.toFloat() + 40.dp.toPx()
                    var textX = (pieCenter.x + textOuterRadius * cos(Math.toRadians(midAngleDegrees))).toFloat()
                    var textY = (pieCenter.y + textOuterRadius * sin(Math.toRadians(midAngleDegrees))).toFloat()
                    val descriptionText = pieChartData.description
                    val valueText = "£${String.format("%.2f", pieChartData.value)}"
                    val descriptionLayoutResult = textMeasurer.measure(descriptionText, style = TextStyle(color = onSurfaceVariantColor))
                    val valueLayoutResult = textMeasurer.measure(valueText, style = TextStyle(color = onSurfaceVariantColor))
                    val verticalSpacing = 4.dp.toPx()
                    val backgroundPadding = 8.dp.toPx()
                    val totalTextHeight = descriptionLayoutResult.size.height + verticalSpacing + valueLayoutResult.size.height
                    val backgroundWidth = max(descriptionLayoutResult.size.width, valueLayoutResult.size.width).toFloat() + backgroundPadding * 2
                    val backgroundHeight = totalTextHeight + backgroundPadding * 2
                    var backgroundX = textX - backgroundWidth / 2
                    var backgroundY = textY - backgroundHeight / 2

                    if (backgroundX < 0) backgroundX = backgroundPadding / 2
                    if (backgroundX + backgroundWidth > size.width) backgroundX = size.width - backgroundWidth - backgroundPadding / 2
                    if (backgroundY < 0) backgroundY = backgroundPadding / 2
                    if (backgroundY + backgroundHeight > size.height) backgroundY = size.height - backgroundHeight - backgroundPadding / 2

                    drawRoundRect(
                        color = surfaceVariantColor.copy(alpha = 0.9f),
                        topLeft = Offset(backgroundX, backgroundY),
                        size = Size(backgroundWidth, backgroundHeight),
                        cornerRadius = CornerRadius(8.dp.toPx())
                    )
                    drawText(
                        descriptionLayoutResult,
                        topLeft = Offset(backgroundX + backgroundPadding, backgroundY + backgroundPadding)
                    )
                    drawText(
                        valueLayoutResult,
                        topLeft = Offset(backgroundX + backgroundPadding, backgroundY + backgroundPadding + descriptionLayoutResult.size.height + verticalSpacing)
                    )
                }
            }
            currentStartAngle += fullSliceSweepAngle + gapDegrees
        }

        if (animationProgress.value == 1f) {
            val totalLabelText = "Total:"
            val totalValueText = "£${String.format("%.2f", totalValue)}"
            val totalLabelLayoutResult = textMeasurer.measure(totalLabelText, style = TextStyle(color = onSurfaceVariantColor))
            val totalValueLayoutResult = textMeasurer.measure(totalValueText, style = TextStyle(color = onSurfaceVariantColor))
            val verticalSpacing = 4.dp.toPx()
            val backgroundPadding = 8.dp.toPx()
            val combinedTextHeight = totalLabelLayoutResult.size.height + verticalSpacing + totalValueLayoutResult.size.height
            val bgWidth = max(totalLabelLayoutResult.size.width, totalValueLayoutResult.size.width).toFloat() + backgroundPadding * 2
            val bgHeight = combinedTextHeight + backgroundPadding * 2
            val bgX = pieCenter.x - bgWidth / 2
            val bgY = pieCenter.y - bgHeight / 2

            drawRoundRect(
                color = primaryContainerColor.copy(alpha = 0.95f),
                topLeft = Offset(bgX, bgY),
                size = Size(bgWidth, bgHeight),
                cornerRadius = CornerRadius(8.dp.toPx())
            )
            drawText(
                totalLabelLayoutResult,
                topLeft = Offset(pieCenter.x - totalLabelLayoutResult.size.width / 2f, bgY + backgroundPadding)
            )
            drawText(
                totalValueLayoutResult,
                topLeft = Offset(pieCenter.x - totalValueLayoutResult.size.width / 2f, bgY + backgroundPadding + totalLabelLayoutResult.size.height + verticalSpacing)
            )
        }
    }
}

data class PieChartData(
    val color: Color = getRandomColor(),
    val value: Double,
    val description: String,
    val isTapped: Boolean = false
)

internal fun getRandomColor(): Color {
    return Color(
        red = Random.nextFloat(),
        blue = Random.nextFloat(),
        green = Random.nextFloat()
    )
}
