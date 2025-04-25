package com.example.hiemapp.ui.auth // <- Đảm bảo đúng package

import android.util.Log // Sử dụng Log thay println
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest // <<< THÊM IMPORT NÀY
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val TAG = "AuthViewModel" // Tag cho logging

    // --- State cho Input Fields ---
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    // --- State cho UI Loading ---
    private val _isLoadingEmail = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoadingEmail.asStateFlow()

    private val _isGoogleLoading = MutableStateFlow(false)
    val isGoogleLoading: StateFlow<Boolean> = _isGoogleLoading.asStateFlow()

    // --- State cho Lỗi ---
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // --- State cho Sự kiện thành công ---
    private val _loginSuccess = MutableSharedFlow<Boolean>(replay = 0)
    val loginSuccess: SharedFlow<Boolean> = _loginSuccess.asSharedFlow()

    private val _registerSuccess = MutableSharedFlow<Boolean>(replay = 0)
    val registerSuccess: SharedFlow<Boolean> = _registerSuccess.asSharedFlow()


    // --- Hàm xử lý thay đổi input ---
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        clearError()
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        clearError()
    }

    // --- Hàm xử lý Đăng nhập Email/Password ---
    fun loginUser() {
        val currentEmail = _email.value.trim()
        val currentPassword = _password.value.trim()

        if (currentEmail.isEmpty() || currentPassword.isEmpty()) {
            _error.value = "Vui lòng nhập email và mật khẩu."
            return
        }

        _isLoadingEmail.value = true
        clearError()

        viewModelScope.launch {
            try {
                Log.d(TAG, "Attempting email/password login for: $currentEmail")
                auth.signInWithEmailAndPassword(currentEmail, currentPassword).await()
                Log.i(TAG, "Email/password login SUCCESS for: $currentEmail")
                _loginSuccess.emit(true)
                clearInputs()
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                Log.w(TAG, "Login failed: Invalid credentials for $currentEmail")
                _error.value = "Email hoặc mật khẩu không đúng."
            } catch (e: Exception) {
                Log.e(TAG, "Login failed for $currentEmail", e)
                _error.value = "Đăng nhập thất bại: ${e.localizedMessage}"
            } finally {
                _isLoadingEmail.value = false
            }
        }
    }

    // --- Hàm xử lý Đăng ký Email/Password (ĐÃ CẬP NHẬT) ---
    fun registerUser(name: String) { // <<< NHẬN THÊM THAM SỐ 'name'
        val currentEmail = _email.value.trim()
        val currentPassword = _password.value.trim()
        val currentName = name.trim() // <<< Lấy và trim 'name'

        // <<< THÊM KIỂM TRA CHO 'name'
        if (currentName.isEmpty()) {
            _error.value = "Vui lòng nhập tên người dùng."
            return
        }
        if (currentEmail.isEmpty() || currentPassword.isEmpty()) {
            _error.value = "Vui lòng nhập email và mật khẩu."
            return
        }
        if (currentPassword.length < 6) {
            _error.value = "Mật khẩu phải có ít nhất 6 ký tự."
            return
        }

        _isLoadingEmail.value = true
        clearError()

        viewModelScope.launch {
            try {
                Log.d(TAG, "Attempting registration for: $currentEmail, Name: $currentName")
                // Bước 1: Tạo user bằng email và password
                val authResult = auth.createUserWithEmailAndPassword(currentEmail, currentPassword).await()
                Log.i(TAG, "User created successfully for: $currentEmail")

                // Bước 2: Cập nhật profile với Tên người dùng (QUAN TRỌNG)
                authResult.user?.let { user ->
                    Log.d(TAG, "Updating profile for user: ${user.uid} with name: $currentName")
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(currentName)
                        // .setPhotoUri(...) // Có thể cập nhật ảnh sau này nếu cần
                        .build()
                    user.updateProfile(profileUpdates).await() // Chờ cập nhật hoàn tất
                    Log.i(TAG, "User profile updated successfully for: ${user.uid}")
                } ?: Log.w(TAG, "User was null after creation for $currentEmail, profile not updated.")

                // Bước 3: Thông báo đăng ký thành công
                _registerSuccess.emit(true)
                clearInputs() // Xóa email và password khỏi state sau khi thành công
                // Không cần xóa name vì nó đang được quản lý cục bộ ở UI

            } catch (e: FirebaseAuthWeakPasswordException) {
                Log.w(TAG, "Registration failed: Weak password for $currentEmail")
                _error.value = "Mật khẩu quá yếu."
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                Log.w(TAG, "Registration failed: Invalid email format for $currentEmail")
                _error.value = "Địa chỉ email không hợp lệ."
            } catch (e: FirebaseAuthUserCollisionException) {
                Log.w(TAG, "Registration failed: Email already in use for $currentEmail")
                _error.value = "Email này đã được sử dụng."
            } catch (e: Exception) {
                Log.e(TAG, "Registration failed for $currentEmail", e)
                _error.value = "Đăng ký thất bại: ${e.localizedMessage}"
            } finally {
                _isLoadingEmail.value = false
            }
        }
    }

    // --- Hàm xử lý Đăng nhập bằng Google Credential ---
    fun signInWithGoogleCredential(account: GoogleSignInAccount) {
        _isGoogleLoading.value = true
        clearError()

        viewModelScope.launch {
            try {
                Log.d(TAG, "Attempting Google Sign In for: ${account.email}")
                val idToken = account.idToken ?: run {
                    Log.e(TAG, "Google ID Token is null for account: ${account.email}")
                    throw IllegalStateException("Google ID Token is null")
                }
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
                Log.i(TAG, "Google Sign In SUCCESS for: ${account.email}")
                // Khi đăng nhập bằng Google, tên người dùng (displayName) thường tự động được cập nhật từ tài khoản Google
                _loginSuccess.emit(true)
            } catch (e: Exception) {
                Log.e(TAG, "Google Sign In failed for: ${account.email}", e)
                _error.value = "Đăng nhập Google thất bại: ${e.localizedMessage}"
            } finally {
                _isGoogleLoading.value = false
            }
        }
    }

    // --- Hàm để set lỗi từ bên ngoài ---
    fun setExternalError(errorMessage: String?) {
        Log.w(TAG, "External error set: $errorMessage")
        _error.value = errorMessage
    }

    // --- Hàm tiện ích ---
    private fun clearInputs() {
        _email.value = ""
        _password.value = ""
        // Không cần xóa name ở đây nếu UI tự quản lý
    }

    fun clearError() {
        if (_error.value != null) {
            Log.d(TAG, "Clearing error state.")
        }
        _error.value = null
    }
}