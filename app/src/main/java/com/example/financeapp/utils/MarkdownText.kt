package com.example.financeapp.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

@Composable
fun MarkdownText(markdownText: String) {
    Text(text = parseMarkdown(markdownText))
}

fun parseMarkdown(markdownText: String): AnnotatedString {
    return buildAnnotatedString {
        val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
        var lastIndex = 0

        boldRegex.findAll(markdownText).forEach { match ->
            append(markdownText.substring(lastIndex, match.range.first))

            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(match.groups[1]?.value ?: "")
            }

            lastIndex = match.range.last + 1
        }

        if (lastIndex < markdownText.length) {
            append(markdownText.substring(lastIndex))
        }
    }
}