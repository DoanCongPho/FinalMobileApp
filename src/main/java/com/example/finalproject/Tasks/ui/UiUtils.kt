package com.example.dailyview.Tasks.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun getDrawableId(name: String): Int {
    val context = androidx.compose.ui.platform.LocalContext.current
    return remember(name) {
        context.resources.getIdentifier(name, "drawable", context.packageName)
    }
}
