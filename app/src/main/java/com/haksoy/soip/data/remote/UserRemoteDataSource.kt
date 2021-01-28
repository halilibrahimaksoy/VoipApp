package com.haksoy.soip.data.remote

import UserList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserRemoteDataSource(apiClient: ApiClient) :
    UserDataSource {

    private var call: Call<UserList>?=null
    private val service = ApiClient.build()

    override fun retrieveUsers(count:Int, callback: OperationCallback<UserList>) {
        call = service?.getUsers(count)
        call?.enqueue(object : Callback<UserList>{
            override fun onFailure(call: Call<UserList>, t: Throwable) {
               callback.onError(t.message)
            }

            override fun onResponse(call: Call<UserList>, response: Response<UserList>) {
               response.body()?.let {
                   if(response.isSuccessful)
                       callback.onSuccess(it.results)
                   else
                       callback.onError("Error")
               }
            }

        })
    }

    override fun cancel() {
        TODO("Not yet implemented")
    }

}

interface UserDataSource {

    fun retrieveUsers(count:Int, callback: OperationCallback<UserList>)
    fun cancel()
}