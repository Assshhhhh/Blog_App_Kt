package com.example.blogappkt.models

data class UserData(
    val name: String = "",
    val email: String = "",
    val profileImage: String = ""
){
    constructor(): this("", "", "")
}
