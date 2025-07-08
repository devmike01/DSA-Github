package dev.gbenga.dsagithub.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

fun String.initial(): String{
    return this[0].uppercase()
}
