package com.haksoy.voipapp.data

import UserList
import com.haksoy.voipapp.data.remote.ApiClient
import com.haksoy.voipapp.data.remote.OperationCallback
import com.haksoy.voipapp.data.remote.UserDataSource
import com.haksoy.voipapp.data.remote.UserRemoteDataSource

class UserRepository() {
    val userDataSource: UserDataSource = UserRemoteDataSource(ApiClient)
    fun fetchUsers(count: Int, callback: OperationCallback<UserList>) {
        userDataSource.retrieveUsers(count, callback)
    }

    fun cancel() {
        userDataSource.cancel()
    }
}