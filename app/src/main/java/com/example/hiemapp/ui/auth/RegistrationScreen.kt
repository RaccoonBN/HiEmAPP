package com.example.hiemapp.ui.auth

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun RegistrationScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    // States
    val email by authViewModel.email.collectAsState()
    val password by authViewModel.password.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val error by authViewModel.error.collectAsState()

    // Local states
    var name by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    // Validation
    val passwordsMatch = password == confirmPassword
    val isPasswordValid = password.length >= 6
    val isRegisterButtonEnabled = !isLoading &&
            name.isNotBlank() &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            confirmPassword.isNotBlank() &&
            isPasswordValid &&
            passwordsMatch

    // UI helpers
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Success handling
    LaunchedEffect(authViewModel.registerSuccess) {
        authViewModel.registerSuccess.collect { success ->
            if (success) {
                keyboardController?.hide()
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Đăng ký thành công!",
                        duration = SnackbarDuration.Short
                    )
                    onRegisterSuccess()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Tạo tài khoản",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(
                            color = primaryColor.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_account_circle),
                        contentDescription = "Đăng ký",
                        tint = primaryColor,
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Form
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = surfaceColor,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Name field
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Tên người dùng", style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = outlineColor
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        enabled = !isLoading,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = outlineColor,
                            focusedLabelColor = primaryColor,
                            unfocusedLabelColor = outlineColor,
                            cursorColor = primaryColor
                        )
                    )

                    // Email field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { authViewModel.onEmailChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Email", style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
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
                        isError = error?.contains("email", ignoreCase = true) == true,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = outlineColor,
                            focusedLabelColor = primaryColor,
                            unfocusedLabelColor = outlineColor,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            cursorColor = primaryColor
                        )
                    )

                    // Password field
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            authViewModel.onPasswordChange(it)
                            if (error != null && (error!!.contains("mật khẩu", ignoreCase = true) || error!!.contains("password", ignoreCase = true))) {
                                authViewModel.clearError()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Mật khẩu", style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
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
                                    contentDescription = null,
                                    tint = outlineColor
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
                            imeAction = ImeAction.Next
                        ),
                        isError = (!isPasswordValid && password.isNotEmpty()) ||
                                error?.contains("mật khẩu", ignoreCase = true) == true ||
                                error?.contains("password", ignoreCase = true) == true,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = outlineColor,
                            focusedLabelColor = primaryColor,
                            unfocusedLabelColor = outlineColor,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            cursorColor = primaryColor
                        )
                    )

                    if (!isPasswordValid && password.isNotEmpty()) {
                        Text(
                            text = "Mật khẩu phải có ít nhất 6 ký tự",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    // Confirm password field
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            if (error != null && error!!.contains("khớp", ignoreCase = true)) {
                                authViewModel.clearError()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Xác nhận mật khẩu", style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = outlineColor
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility
                                    else Icons.Filled.VisibilityOff,
                                    contentDescription = null,
                                    tint = outlineColor
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        enabled = !isLoading,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }
                        ),
                        isError = !passwordsMatch && confirmPassword.isNotEmpty(),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = outlineColor,
                            focusedLabelColor = primaryColor,
                            unfocusedLabelColor = outlineColor,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            cursorColor = primaryColor
                        )
                    )

                    if (!passwordsMatch && confirmPassword.isNotEmpty()) {
                        Text(
                            text = "Mật khẩu không khớp",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    // General error message
                    error?.let { errorMessage ->
                        if (!(errorMessage.contains("email", ignoreCase = true) ||
                                    errorMessage.contains("mật khẩu", ignoreCase = true) ||
                                    errorMessage.contains("password", ignoreCase = true) ||
                                    errorMessage.contains("khớp", ignoreCase = true))) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Register button
                Button(
                    onClick = { authViewModel.registerUser(name) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = isRegisterButtonEnabled,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Tạo tài khoản",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // Login prompt
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        "Đã có tài khoản?",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = outlineColor
                        )
                    )
                    TextButton(onClick = onNavigateToLogin) {
                        Text(
                            "Đăng nhập ngay",
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
}

@Preview (showBackground = true, name = "Registration Screen Modern Preview")
@Composable
fun RegistrationScreenModernPreview() {
    val previewViewModel: AuthViewModel = viewModel()
    HiEmAppTheme {
        RegistrationScreen(
            authViewModel = previewViewModel,
            onNavigateToLogin = {},
            onRegisterSuccess = {}
        )
    }
}