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
import com.example.hiemapp.ui.parent.ParentHomeScreen // Import ParentHomeScreen

// Định nghĩa các đường dẫn (routes) để tránh lỗi chính tả
object Routes {
    const val LOGIN = "login_screen" // Đặt tên rõ ràng hơn
    const val REGISTER = "register_screen"
    const val PARENT_HOME = "parent_home_screen" // Sửa lại tên
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
        if (result.resultCode == android.app.Activity.RESULT_OK) {
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

    val launchGoogleSignIn: () -> Unit = {
        authViewModel.clearError() // Xóa lỗi cũ trước khi bắt đầu
        Log.d("AppNavigation", "Launching Google Sign In Intent...")

        if (googleSignInClient != null) {
            val signInIntent = googleSignInClient.signInIntent
            if (signInIntent != null) {
                googleSignInLauncher.launch(signInIntent)
            } else {
                Log.e("AppNavigation", "signInIntent is null! Check Google Sign-In configuration.")
                authViewModel.setExternalError("Không thể tạo Intent đăng nhập Google. Vui lòng kiểm tra cấu hình.")
            }
        } else {
            Log.e("AppNavigation", "googleSignInClient is null! Check Web Client ID.")
            authViewModel.setExternalError("Chưa cấu hình Google Sign-In. Vui lòng kiểm tra Web Client ID.")
        }
    }

    // --- Xác định màn hình bắt đầu dựa trên trạng thái đăng nhập ---
    val startDestination = if (auth.currentUser != null) {
        Log.d("AppNavigation", "User already logged in: ${auth.currentUser?.email}. Starting at PARENT_HOME.")
        Routes.PARENT_HOME
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
                    Log.d("AppNavigation", "Login Successful, navigating to PARENT_HOME")
                    // Đi đến màn hình chính, xóa hết backstack cũ liên quan đến auth
                    navController.navigate(Routes.PARENT_HOME) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        // launchSingleTop = true // Không cần thiết khi đang popUpTo
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
                    // Hoặc bạn có thể tự động đăng nhập và chuyển đến PARENT_HOME nếu muốn
                }
            )
        }

        // Định nghĩa màn hình chính của ứng dụng (Parent Home)
        composable(Routes.PARENT_HOME) {
            ParentHomeScreen(
                onNavigateToLogin = {
                    // Điều hướng về màn hình đăng nhập và xóa mọi thứ khỏi back stack
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

// --- Hàm Helper để tạo GoogleSignInClient ---
private fun getGoogleSignInClient(context: Context): GoogleSignInClient? {
    Log.d("AppNavigation", "Creating GoogleSignInClient...")
    // Dòng này là nơi có thể xảy ra lỗi NPE nếu context null hoặc R.string không đúng
    val webClientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
        Log.e("AppNavigation", "Error getting default_web_client_id from resources", e)
        // Xử lý lỗi: Hiển thị thông báo lỗi thân thiện thay vì crash
        // throw IllegalStateException("Could not retrieve default_web_client_id. Check strings.xml and context.", e)
        return null // Hoặc trả về null và xử lý ở nơi gọi
    }

    if (webClientId == null || webClientId.isEmpty() || !webClientId.endsWith(".apps.googleusercontent.com")) {
        Log.e("AppNavigation", "Invalid default_web_client_id: $webClientId. Check strings.xml.")
        // Có thể throw lỗi ở đây, hoặc xử lý ở nơi gọi
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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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