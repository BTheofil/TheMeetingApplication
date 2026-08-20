package hu.tb.design_system.modifier

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun Modifier.screenPadding() = this.padding(vertical = 16.dp, horizontal = 8.dp)