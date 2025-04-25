package com.example.hiemapp // <- Đảm bảo đúng package gốc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.hiemapp.navigation.AppNavigation // <- Import AppNavigation
import com.example.hiemapp.ui.theme.HiEmAppTheme // <- Import Theme của bạn

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HiEmAppTheme { // Sử dụng Theme của bạn
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation() // Gọi Composable điều hướng chính
                }
            }
        }
    }
}