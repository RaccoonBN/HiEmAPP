package com.example.hiemapp.data.remote // Thay đổi nếu bạn đặt nó ở thư mục khác

import com.cloudinary.Cloudinary
import java.util.HashMap

object CloudinaryConfig {
    fun getConfig(): HashMap<String, Any> {
        return hashMapOf(
            "cloud_name" to "dyf91xhcr",
            "api_key" to "327687394212666",
            "api_secret" to "cNpo1Z2F6xto3EwvTlpasKoxO-Y"
        )
    }
}

object CloudinaryManager {
    private var cloudinary: Cloudinary? = null

    fun getCloudinary(): Cloudinary {
        if (cloudinary == null) {
            cloudinary = Cloudinary(CloudinaryConfig.getConfig())
        }
        return cloudinary!!
    }
}