package com.example.images

data class CommentsModel(
    val postId: Int? = null,
    val id: Int? = null,
    val email: String,
    val name: String,
    val body: String
)