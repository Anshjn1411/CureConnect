package com.project.cureconnect.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.project.cureconnect.R

val comic = FontFamily(
    Font(R.font.comicneuebold )
)
// Set of Material typography styles to start with
val Typography = Typography(

    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
            bodyLarge = TextStyle(
            fontFamily = comic,  // Use your custom font
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp
),
titleLarge = TextStyle(
fontFamily = comic,
fontWeight = FontWeight.Bold,
fontSize = 100.sp,  // Match your original Text size
color = Color.White
)

)