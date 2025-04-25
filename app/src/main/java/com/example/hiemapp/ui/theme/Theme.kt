package com.example.hiemapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Định nghĩa bảng màu SÁNG (Light Theme)
private val LightColorScheme = lightColorScheme(
    primary = Thistle,                 // Màu chính: Tím nhạt
    onPrimary = DarkerText,            // Chữ/icon trên nền Tím nhạt: Đen/Xám đậm
    primaryContainer = ThistleContainer, // Nền phụ cho thành phần chính: Tím rất nhạt
    onPrimaryContainer = DarkerText,   // Chữ/icon trên nền Tím rất nhạt

    secondary = LightPink,             // Màu phụ: Hồng pastel
    onSecondary = DarkerText,          // Chữ/icon trên nền Hồng pastel
    secondaryContainer = LightPinkContainer,// Nền phụ cho thành phần phụ: Hồng rất nhạt
    onSecondaryContainer = DarkerText, // Chữ/icon trên nền Hồng rất nhạt

    tertiary = PaleGreen,              // Màu nhấn thứ 3: Xanh mint
    onTertiary = DarkerText,           // Chữ/icon trên nền Xanh mint
    tertiaryContainer = PaleGreenContainer,// Nền phụ cho nhấn 3: Xanh rất nhạt
    onTertiaryContainer = DarkerText,  // Chữ/icon trên nền Xanh rất nhạt

    background = White,                // Nền chính của ứng dụng: Trắng
    onBackground = DarkerText,         // Chữ/icon trên nền Trắng

    surface = White,                   // Nền cho các component (Card, Dialog,...): Trắng
    onSurface = DarkerText,            // Chữ/icon trên nền component

    surfaceVariant = LemonChiffon,        // Nền biến thể (có thể dùng cho viền input, nền chip): Vàng nhạt
    onSurfaceVariant = DarkerText,     // Chữ/icon trên nền biến thể

    outline = Thistle,                 // Màu viền (có thể dùng Thistle hoặc LightGrey)
    inverseOnSurface = White,          // Chữ/icon trên nền đảo ngược (Snackbar tối)
    inverseSurface = DarkerText,       // Nền đảo ngược (Snackbar tối)
    inversePrimary = PaleGreen,        // Màu chính trên nền đảo ngược

    error = ErrorRed,                  // Màu báo lỗi
    onError = OnErrorRed,              // Chữ/icon trên nền lỗi
    errorContainer = LightPinkContainer,// Nền cho thông báo lỗi (dùng màu hồng rất nhạt)
    onErrorContainer = ErrorRed        // Chữ/icon trên nền thông báo lỗi
)

// Định nghĩa bảng màu TỐI (Dark Theme) - Cần điều chỉnh nếu muốn hỗ trợ Dark Mode tốt
// Tạm thời có thể dùng màu tối hơn hoặc giữ nguyên Light để tập trung vào Light trước
private val DarkColorScheme = darkColorScheme( // Ví dụ đơn giản, cần tinh chỉnh nhiều
    primary = Thistle,                 // Có thể dùng màu sáng hơn hoặc đậm hơn cho Dark Mode
    onPrimary = Black,
    primaryContainer = Color(0xFF4A148C), // Ví dụ: Tím đậm
    onPrimaryContainer = ThistleContainer,

    secondary = LightPink,
    onSecondary = Black,
    secondaryContainer = Color(0xFF880E4F), // Ví dụ: Hồng đậm
    onSecondaryContainer = LightPinkContainer,

    tertiary = PaleGreen,
    onTertiary = Black,
    tertiaryContainer = Color(0xFF1B5E20), // Ví dụ: Xanh đậm
    onTertiaryContainer = PaleGreenContainer,

    background = Color(0xFF121212),      // Nền tối phổ biến
    onBackground = White,

    surface = Color(0xFF1E1E1E),      // Nền component tối hơn chút
    onSurface = White,

    surfaceVariant = Color(0xFF303030),
    onSurfaceVariant = Color(0xFFCCCCCC),

    outline = Thistle,
    inverseOnSurface = Black,
    inverseSurface = White,
    inversePrimary = Thistle,

    error = Color(0xFFCF6679),          // Màu đỏ lỗi cho nền tối
    onError = Black,
    errorContainer = Color(0xFFB00020),
    onErrorContainer = Color(0xFFFCD8DF)
)


@Composable
fun HiEmAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color hiện không cần thiết vì chúng ta tự định nghĩa màu
    dynamicColor: Boolean = false, // Tắt Dynamic Color để dùng bảng màu cố định
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        // DarkColorScheme // Bật nếu muốn test Dark Mode
        LightColorScheme // Tạm thời luôn dùng Light để dễ test
    } else {
        LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Đặt màu thanh trạng thái trùng màu nền hoặc màu khác
            window.statusBarColor = colorScheme.background.toArgb()
            // Đặt icon trên thanh trạng thái là màu tối (vì nền sáng)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme, // Áp dụng bảng màu đã chọn
        typography = Typography,   // Áp dụng font chữ đã định nghĩa (từ Type.kt)
        content = content
    )
}