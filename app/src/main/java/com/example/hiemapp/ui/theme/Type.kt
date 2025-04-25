package com.example.hiemapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
// import androidx.compose.ui.text.font.FontFamily // Không cần import cụ thể nếu ReadexPro cùng package
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
// Import FontFamily mới nếu nó ở file khác, ví dụ: Font.kt
// import com.example.hiemapp.ui.theme.ReadexPro // Bỏ comment nếu ReadexPro ở file Font.kt

// Cập nhật Typography để sử dụng font ReadexPro
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = ReadexPro, // <<< THAY ĐỔI FONT
        fontWeight = FontWeight.Normal, // Readex Pro thường đẹp hơn ở Normal/Light
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.Normal, // Tiêu đề chính có thể dùng Normal hoặc Medium
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.SemiBold, // Headline có thể đậm hơn
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.SemiBold, // Title Large dùng SemiBold
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.Medium, // Title Medium dùng Medium
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.Normal, // Body dùng Regular (Normal)
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.Light, // Body Small có thể dùng Light
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.Medium, // Label dùng Medium
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = ReadexPro,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)