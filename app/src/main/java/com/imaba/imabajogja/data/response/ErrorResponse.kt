package com.imaba.imabajogja.data.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("errors")
    val errors: Map<String, List<String>>?  // Menyimpan semua field yang error
)