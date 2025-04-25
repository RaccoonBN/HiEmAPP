package com.example.hiemapp.ui.theme

import androidx.compose.ui.graphics.Color

// --- Các màu cơ bản ---
val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)

// --- Bảng màu HiEm ---
// Vàng nhạt (cho body/nền phụ)
val LemonChiffon = Color(0xFFFFF9C4)  // Chỉnh lại sáng và tươi sáng hơn
// Xanh lá nhạt (cho mặt nạ/primary)
val Thistle = Color(0xFF81C784)      // Tím nhạt -> Xanh lá nhẹ, dễ chịu hơn
// Hồng pastel (má, sọc đuôi/secondary)
val LightPink = Color(0xFFF8BBD0)    // Hồng sáng nhẹ nhàng, dễ thương hơn
// Xanh mint (chữ Hi/tertiary)
val PaleGreen = Color(0xFFB2FF59)    // Xanh mint tươi sáng hơn
// Vàng cam nhạt (chữ m/container?)
val Peachpuff = Color(0xFFFFE082)    // Vàng cam dịu nhẹ hơn

// --- Có thể cần thêm các màu bổ trợ ---
val DarkerText = Color(0xFF616161) // Màu chữ tối hơn Black một chút, đỡ gắt
val LightGrey = Color(0xFFFAFAFA) // Màu xám rất nhạt cho viền hoặc nền nhẹ
val ErrorRed = Color(0xFFE57373)  // Màu đỏ nhẹ nhàng hơn cho lỗi
val OnErrorRed = Color(0xFFFFFFFF) // Chữ trên nền đỏ lỗi

// --- Màu dùng cho các Container (Nền cho các thành phần chứa màu chính/phụ) ---
// Chọn các màu rất nhạt từ bảng màu hoặc biến thể nhạt hơn
val ThistleContainer = Color(0xFFF1F8E9) // Ví dụ: Xanh lá nhạt, nhẹ nhàng
val LightPinkContainer = Color(0xFFF8BBD0) // Ví dụ: Hồng pastel nhẹ nhàng
val PaleGreenContainer = Color(0xFFE8F5E9) // Ví dụ: Xanh lá rất nhạt
val PeachpuffContainer = Color(0xFFFFF9C4) // Ví dụ: Vàng cam nhẹ
