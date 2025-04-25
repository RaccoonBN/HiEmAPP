package com.example.hiemapp.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hiemapp.R
import com.example.hiemapp.ui.theme.HiEmAppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    onGoogleSignInClicked: () -> Unit
) {
    // States
    val email by authViewModel.email.collectAsState()
    val password by authViewModel.password.collectAsState()
    val isLoadingEmail by authViewModel.isLoading.collectAsState()
    val isGoogleLoading by authViewModel.isGoogleLoading.collectAsState()
    val error by authViewModel.error.collectAsState()
    val isLoading = isLoadingEmail || isGoogleLoading
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Colors
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)

    // Side Effects
    LaunchedEffect(authViewModel.loginSuccess) {
        authViewModel.loginSuccess.collect { success ->
            if (success) {
                keyboardController?.hide()
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Đăng nhập thành công!",
                        duration = SnackbarDuration.Short
                    )
                    onLoginSuccess()
                }
            }
        }
    }

    // UI
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Logo with subtle animation
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            color = primaryColor.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image( // Sử dụng Image thay vì Icon để hiển thị logo
                        painter = painterResource(id = R.drawable.hiemlogo),
                        contentDescription = "HiEm Logo",
                        modifier = Modifier.size(64.dp)
                    )
                }

                Text(
                    text = "HiEm",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryColor
                    )
                )

                Text(
                    text = "Chạm nhẹ cảm xúc, nâng bước yêu thương",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = onSurfaceColor.copy(alpha = 0.7f)
                    ),
                    textAlign = TextAlign.Center,
                )
            }

            // Login Form
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = surfaceColor,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { authViewModel.onEmailChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            "Email",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = "Email", // Thêm contentDescription
                            tint = outlineColor
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = outlineColor,
                        focusedLabelColor = primaryColor,
                        unfocusedLabelColor = outlineColor,
                        cursorColor = primaryColor
                    )
                )

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { authViewModel.onPasswordChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            "Mật khẩu",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Mật khẩu", // Thêm contentDescription
                            tint = outlineColor
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",                                tint = outlineColor
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            authViewModel.loginUser()
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = outlineColor,
                        focusedLabelColor = primaryColor,
                        unfocusedLabelColor = outlineColor,
                        cursorColor = primaryColor
                    )
                )

                // Error Message
                // Sửa lỗi ở đây: Thêm nhánh else
                val errorMessage = error
                if (errorMessage != null && errorMessage.isNotBlank()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Spacer(modifier = Modifier.height(0.dp)) // Để giữ khoảng cách nếu không có lỗi
                }


                // Login Button
                Button(
                    onClick = {
                        keyboardController?.hide()
                        authViewModel.loginUser()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = Color.White
                    )
                ) {
                    if (isLoadingEmail) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Đăng nhập",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            // Social Login Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Divider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = outlineColor,
                        thickness = 1.dp
                    )
                    Text(
                        text = "hoặc",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = outlineColor
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = outlineColor,
                        thickness = 1.dp
                    )
                }

                // Google Button
                OutlinedButton(
                    onClick = {
                        keyboardController?.hide()
                        onGoogleSignInClicked()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, outlineColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = onSurfaceColor
                    )
                ) {
                    if (isGoogleLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = primaryColor,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_google_logo),
                                contentDescription = "Google", // Thêm contentDescription
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Tiếp tục với Google",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }

            // Register Prompt
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Chưa có tài khoản?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = outlineColor
                    )
                )
                TextButton(
                    onClick = onNavigateToRegister,
                    enabled = !isLoading
                ) {
                    Text(
                        "Đăng ký ngay",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = primaryColor
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Login Screen Modern Preview")
@Composable
fun LoginScreenModernPreview() {
    val previewViewModel: AuthViewModel = viewModel()
    HiEmAppTheme {
        LoginScreen(
            authViewModel = previewViewModel,
            onNavigateToRegister = {},
            onLoginSuccess = {},
            onGoogleSignInClicked = {}
        )
    }
}