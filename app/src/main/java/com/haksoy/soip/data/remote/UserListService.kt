package com.haksoy.soip.data.remote

import UserList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UserListService {
    @GET("api/")
    fun getUsers(@Query("results") results: Int, @Query("nat") nat: String = "tr"): Call<UserList>
}