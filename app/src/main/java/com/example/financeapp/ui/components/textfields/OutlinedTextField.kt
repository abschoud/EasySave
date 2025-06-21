package com.example.financeapp.ui.components.textfields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedTextFieldDefaults

class CurrencyVisualTransformation(private val currencySymbol: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val transformedText = AnnotatedString(currencySymbol + text.text)
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return offset + currencySymbol.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                return (offset - currencySymbol.length).coerceAtLeast(0)
            }
        }

        return TransformedText(transformedText, offsetMapping)
    }
}

@Composable
fun OutlinedTextField(
    keyboardController: SoftwareKeyboardController?,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = CurrencyVisualTransformation("£")
) {
    androidx.compose.material3.OutlinedTextField(
        value = value,
        onValueChange = { newValueState ->
            val newText = newValueState.text

            if (newText.isEmpty()) {
                onValueChange(newValueState)
                return@OutlinedTextField
            }

            val decimalRegex = Regex("^\\d*\\.?\\d{0,2}$")

            if (decimalRegex.matches(newText)) {
                onValueChange(newValueState)
            }
        },
        label = label,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedLabelColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        shape = RoundedCornerShape(8.dp),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation
    )
}
