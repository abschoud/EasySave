package com.example.financeapp.ui.components.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.db.transaction.Transactionlist
import com.example.financeapp.ui.theme.ColorPalette
import com.example.financeapp.utils.AddType
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TransactionItem(
    item: Transactionlist,
    onDelete: (Int) -> Unit,
    onEdit: (Transactionlist) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val itemColor = if (item.transactionType != AddType.INCOME) Color(ColorPalette.getOutgoingColor(item.transactionName)) else Color(ColorPalette.getIncomeColor(item.transactionName))

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
                text = SimpleDateFormat("HH:mm:aa, dd/MM", Locale.ENGLISH).format(item.transactionCreatedAt),
                fontSize = 10.sp,
                color = itemColor.copy(alpha = 0.7f)
            )
            Text(
                text = item.transactionName,
                fontSize = 16.sp,
                color = itemColor
            )
        }

        Text(
            text = "£${String.format("%.2f", item.transactionValue)}",
            fontSize = 16.sp,
            color = itemColor
        )

        IconButton(onClick = { showMenu = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Options",
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
                    onDelete(item.transactionId)
                },
            )
        }
    }
}