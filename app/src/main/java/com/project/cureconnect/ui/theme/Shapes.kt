package com.project.cureconnect.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp


val CureConnectShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Custom shapes for specific components
val CardShape = RoundedCornerShape(16.dp)
val ButtonShape = RoundedCornerShape(12.dp)
val ChipShape = RoundedCornerShape(20.dp)
val SearchBarShape = RoundedCornerShape(24.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
val DialogShape = RoundedCornerShape(20.dp)

// Elevation values
object Elevations {
    val none = 0.dp
    val small = 2.dp
    val medium = 4.dp
    val large = 8.dp
    val extraLarge = 16.dp
}