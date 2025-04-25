package com.example.hiemapp.navigation // <- Đảm bảo đúng package

import android.app.Activity.RESULT_OK
import android.content.Context
import android.util.Log // Thêm Log để debug nếu cần
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.example.hiemapp.R // <- Import R để lấy string resource
import com.example.hiemapp.ui.auth.AuthViewModel // <- Import ViewModel
import com.example.hiemapp.ui.auth.LoginScreen // <- Import LoginScreen
import com.example.hiemapp.ui.auth.RegistrationScreen // <- Import RegistrationScreen

// Định nghĩa các đường dẫn (routes) để tránh lỗi chính tả
object Routes {
    const val LOGIN = "login_screen" // Đặt tên rõ ràng hơn
    const val REGISTER = "register_screen"
    const val MAIN_APP = "main_app_screen"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Khởi tạo AuthViewModel sử dụng Hilt hoặc viewModel() mặc định
    val authViewModel: AuthViewModel = viewModel()
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current // Lấy Context hiện tại trong Composable

    // --- Cấu hình và Launcher cho Google Sign In ---
    val googleSignInClient = remember(context) { // Chạy lại nếu context thay đổi (hiếm)
        getGoogleSignInClient(context)
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Xử lý kết quả trả về từ Google Sign In Activity
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Lấy tài khoản Google thành công
                val account = task.getResult(ApiException::class.java)!! // Nếu OK thì account không null
                Log.d("AppNavigation", "Google Sign In Success - Got account: ${account.email}")
                // Gọi ViewModel để xác thực với Firebase bằng tài khoản Google
                authViewModel.signInWithGoogleCredential(account)
            } catch (e: ApiException) {
                // Xử lý lỗi từ Google Sign In API
                Log.e("AppNavigation", "Google Sign In failed with ApiException: ${e.statusCode}", e)
                authViewModel.setExternalError("Đăng nhập Google thất bại (Lỗi API: ${e.statusCode}).")
            } catch (e: Exception) {
                // Xử lý các lỗi khác (ví dụ: account.idToken null)
                Log.e("AppNavigation", "Google Sign In failed with Exception", e)
                authViewModel.setExternalError("Lỗi không xác định khi đăng nhập Google: ${e.message}")
            }
        } else {
            // Người dùng có thể đã hủy hoặc có lỗi khác không phải RESULT_OK
            Log.w("AppNavigation", "Google Sign In canceled or failed (resultCode: ${result.resultCode})")
            authViewModel.clearError() // Xóa lỗi cũ nếu có, không nên hiển thị lỗi mới khi người dùng hủy
        }
    }

    // Hàm lambda để khởi chạy luồng Google Sign In
    val launchGoogleSignIn: () -> Unit = {
        authViewModel.clearError() // Xóa lỗi cũ trước khi bắt đầu
        Log.d("AppNavigation", "Launching Google Sign In Intent...")
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    // --- Xác định màn hình bắt đầu dựa trên trạng thái đăng nhập ---
    val startDestination = if (auth.currentUser != null) {
        Log.d("AppNavigation", "User already logged in: ${auth.currentUser?.email}. Starting at MAIN_APP.")
        Routes.MAIN_APP
    } else {
        Log.d("AppNavigation", "User not logged in. Starting at LOGIN.")
        Routes.LOGIN
    }

    // --- Thiết lập NavHost để quản lý điều hướng ---
    NavHost(navController = navController, startDestination = startDestination) {

        // Định nghĩa màn hình Đăng nhập
        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    authViewModel.clearError() // Xóa lỗi trước khi chuyển màn hình
                    navController.navigate(Routes.REGISTER)
                },
                onLoginSuccess = {
                    Log.d("AppNavigation", "Login Successful, navigating to MAIN_APP")
                    // Đi đến màn hình chính, xóa hết backstack cũ liên quan đến auth
                    navController.navigate(Routes.MAIN_APP) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onGoogleSignInClicked = launchGoogleSignIn // Truyền hàm để gọi Google Sign In
            )
        }

        // Định nghĩa màn hình Đăng ký
        composable(Routes.REGISTER) {
            RegistrationScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    authViewModel.clearError()
                    navController.popBackStack() // Quay lại màn hình trước đó (Login)
                },
                onRegisterSuccess = {
                    Log.d("AppNavigation", "Registration Successful, navigating back to Login")
                    authViewModel.clearError()
                    // Sau khi đăng ký thành công, quay lại Login để người dùng đăng nhập
                    navController.popBackStack()
                    // Hoặc bạn có thể tự động đăng nhập và chuyển đến MAIN_APP nếu muốn
                }
            )
        }

        // Định nghĩa màn hình Chính của ứng dụng
        composable(Routes.MAIN_APP) {
            // Thay thế bằng màn hình chính thực sự của bạn sau này
            MainAppPlaceholderScreen(
                navController = navController,
                onSignOut = {
                    Log.d("AppNavigation", "Signing out...")
                    authViewModel.clearError() // Xóa trạng thái lỗi cũ
                    auth.signOut() // Đăng xuất khỏi Firebase Auth
                    // Quan trọng: Đăng xuất khỏi Google Sign In để lần sau hiện cửa sổ chọn tài khoản
                    googleSignInClient.signOut().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("AppNavigation", "Google Sign Out successful.")
                        } else {
                            Log.w("AppNavigation", "Google Sign Out failed.")
                        }
                        // Luôn điều hướng về Login sau khi đăng xuất
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.MAIN_APP) { inclusive = true } // Xóa màn hình chính khỏi backstack
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}

// --- Hàm Helper để tạo GoogleSignInClient ---
private fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    Log.d("AppNavigation", "Creating GoogleSignInClient...")
    // Dòng này là nơi có thể xảy ra lỗi NPE nếu context null hoặc R.string không đúng
    val webClientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
        Log.e("AppNavigation", "Error getting default_web_client_id from resources", e)
        // Xử lý lỗi: Có thể throw lỗi rõ ràng hơn hoặc trả về giá trị mặc định không hợp lệ để gây lỗi sau này
        // Hoặc bạn có thể hardcode tạm thời ID ở đây để test, nhưng KHÔNG NÊN làm vậy trong code production
        throw IllegalStateException("Could not retrieve default_web_client_id. Check strings.xml and context.", e)
        // "" // Trả về chuỗi rỗng để tránh NPE ngay lập tức, nhưng sẽ gây lỗi khi tạo GSO
    }

    if (webClientId.isEmpty() || !webClientId.endsWith(".apps.googleusercontent.com")) {
        Log.e("AppNavigation", "Invalid default_web_client_id: $webClientId. Check strings.xml.")
        // Có thể throw lỗi ở đây
        throw IllegalArgumentException("Invalid default_web_client_id configured in strings.xml.")
    }

    Log.d("AppNavigation", "Using Web Client ID: $webClientId")
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(webClientId) // Yêu cầu ID Token để xác thực với Firebase
        .requestEmail()             // Yêu cầu email người dùng
        .build()
    return GoogleSignIn.getClient(context, gso)
}


// --- Placeholder cho Màn hình Chính ---
@Composable
fun MainAppPlaceholderScreen(navController: NavHostController, onSignOut: () -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Màn hình chính HiEm", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        user?.email?.let { email ->
            Text("Email: $email")
        }
        user?.displayName?.let { name ->
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tên: $name") // Hiển thị tên từ Firebase User Profile
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onSignOut) {
            Text("Đăng xuất")
        }
        // TODO: Thêm các nút điều hướng đến các chức năng chính của HiEm tại đây
    }
}