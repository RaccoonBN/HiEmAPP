package com.example.hiemapp.ui.parent

import com.example.hiemapp.R

// Dữ liệu tạm thời cho hồ sơ trẻ
data class ChildProfile(val id: Int, val name: String, val gender: Gender, val age: Int, val avatar: String? = null)

enum class Gender {
    MALE,
    FEMALE
}

object DefaultAvatars {
    val MALE = R.drawable.img_boy // Sử dụng val thay vì const val
    val FEMALE = R.drawable.img_girl // Sử dụng val thay vì const val
    //val DEFAULT = R.drawable.ic_account_circle
}

fun getDefaultAvatar(gender: Gender): Int {
    return when (gender) {
        Gender.MALE -> DefaultAvatars.MALE
        Gender.FEMALE -> DefaultAvatars.FEMALE
    }
}