package com.imaba.imabajogja.data.model



data class UserModel(
    val email: String,
    val token: String,
    val role: String,
    val isLogin: Boolean = false,
)

