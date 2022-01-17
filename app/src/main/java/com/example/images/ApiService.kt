package com.example.images

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/comments")
    fun getComments(): Call<MutableList<CommentsModel>>

    @GET("/comments")
    fun getCommentsWithPaging(@Query("_start") offset: Int, @Query("_limit") limit: Int): Call<MutableList<CommentsModel>>
}