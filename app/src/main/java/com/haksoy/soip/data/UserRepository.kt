package com.haksoy.soip.data

import UserList
import com.haksoy.soip.data.remote.ApiClient
import com.haksoy.soip.data.remote.OperationCallback
import com.haksoy.soip.data.remote.UserDataSource
import com.haksoy.soip.data.remote.UserRemoteDataSource

class UserRepository() {
    val userDataSource: UserDataSource = UserRemoteDataSource(ApiClient)
    fun fetchUsers(count: Int, callback: OperationCallback<UserList>) {
        userDataSource.retrieveUsers(count, callback)
    }

    fun cancel() {
        userDataSource.cancel()
    }
}