package com.example.financeapp.ui.components.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.db.budget.Budgetlist
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun BudgetItem(
    item: Budgetlist,
    onDelete: () -> Unit,
    onEdit: (Budgetlist) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val spendingPercentage = if (item.budgetLimit > 0) {
        (item.budgetSpent / item.budgetLimit).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }
    val progressColor = when {
        spendingPercentage < 0.5f -> Color(0xFF26CB26)
        spendingPercentage < 0.8f -> Color(0xFFECDA3A)
        else -> Color(0xFFE16238)
    }
    val itemColor = Color(item.budgetColor)
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(itemColor.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.budgetName,
                fontSize = 16.sp,
                color = itemColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (item.budgetName == "Savings") {
                    "Goal: £${String.format("%.2f", item.budgetLimit)}"
                } else {
                    "Limit: £${String.format("%.2f", item.budgetLimit)}"
                },
                fontSize = 12.sp,
                color = itemColor.copy(alpha = 0.7f)
            )
            Text(
                text = if (item.budgetName == "Savings") {
                    "Saved: £${String.format("%.2f", item.budgetSpent)}"
                } else {
                    "Spent: £${String.format("%.2f", item.budgetSpent)}"
                },
                fontSize = 12.sp,
                color = itemColor.copy(alpha = 0.7f)
            )
            Text(
                text = if (item.budgetRemaining < 0) {
                    "Remaining: -£${String.format("%.2f", -item.budgetRemaining)}"
                } else {
                    "Remaining: £${String.format("%.2f", item.budgetRemaining)}"
                },
                fontSize = 12.sp,
                color = itemColor.copy(alpha = 0.7f)
            )

            if (item.budgetType == "Custom" && item.startDate != null && item.endDate != null) {
                Text(
                    text = "End Date: ${dateFormatter.format(item.endDate)}",
                    fontSize = 12.sp,
                    color = itemColor.copy(alpha = 0.7f)
                )
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(70.dp)
        ) {
            CircularProgressIndicator(
                progress = { spendingPercentage },
                modifier = Modifier.size(70.dp),
                color = if (item.budgetName.equals("Savings", ignoreCase = true)) {
                    Color(0xFF26CB26)
                } else {
                    progressColor
                },
                strokeWidth = 8.dp,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
            )

            Text(
                text = "${(spendingPercentage * 100).roundToInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = itemColor
            )
        }

        Spacer(modifier = Modifier.weight(0.1f))

        IconButton(onClick = { showMenu = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Budget Options",
                tint = itemColor
            )
        }

        DropdownMenu(
            containerColor = MaterialTheme.colorScheme.onTertiary,
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            offset = DpOffset(x = 200.dp, y = (-20).dp)
        ) {
            DropdownMenuItem(
                text = { Text("Edit",
                    color = MaterialTheme.colorScheme.onSurfaceVariant) },
                onClick = {
                    showMenu = false
                    onEdit(item)
                }
            )
            DropdownMenuItem(
                text = { Text("Delete",
                    color = MaterialTheme.colorScheme.onSurfaceVariant) },
                onClick = {
                    showMenu = false
                    onDelete()
                },
            )
        }
    }
}