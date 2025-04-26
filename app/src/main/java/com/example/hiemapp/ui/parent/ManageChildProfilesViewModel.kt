package com.example.hiemapp.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.toMutableStateList
import com.example.hiemapp.ui.parent.ChildProfile
import com.example.hiemapp.ui.parent.Gender
import com.example.hiemapp.R
import com.example.hiemapp.ui.parent.DefaultAvatars
import com.example.hiemapp.ui.parent.getDefaultAvatar


class ManageChildProfilesViewModel : ViewModel() {

    private val _childProfiles = MutableStateFlow<List<ChildProfile>>(emptyList())
    val childProfiles: StateFlow<List<ChildProfile>> = _childProfiles.asStateFlow()

    private var nextId = 1 // Biến để theo dõi ID tiếp theo cho hồ sơ mới

    // Hàm để thêm hồ sơ trẻ (ĐÃ SỬA)
    fun addChildProfile(name: String, gender: Gender, age: Int, avatar: String? = null) {
        val newProfile = ChildProfile(nextId++, name, gender, age, avatar)
        _childProfiles.value = _childProfiles.value + newProfile
    }

    // Hàm để chỉnh sửa hồ sơ trẻ
    fun editChildProfile(profile: ChildProfile, newName: String,newGender: Gender, newAge: Int,newAvatar: String?) {
        _childProfiles.value = _childProfiles.value.map {
            if (it.id == profile.id) {
                it.copy(name = newName,gender = newGender,age = newAge, avatar = newAvatar)
            } else {
                it
            }
        }
    }

    // Hàm để xóa hồ sơ trẻ
    fun deleteChildProfile(profile: ChildProfile) {
        _childProfiles.value = _childProfiles.value.filter { it.id != profile.id }
    }
}