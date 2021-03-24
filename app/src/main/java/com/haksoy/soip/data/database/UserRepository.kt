package com.haksoy.soip.data.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.haksoy.soip.data.FirebaseDao
import com.haksoy.soip.data.user.User
import com.haksoy.soip.utlis.observeOnce
import java.util.concurrent.ExecutorService

class UserRepository private constructor(
    userDatabase: UserDatabase,
    private val executor: ExecutorService
) {

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(context: Context, executor: ExecutorService): UserRepository {
            return INSTANCE
                ?: synchronized(this) {
                    INSTANCE
                        ?: UserRepository(
                            UserDatabase.getInstance(context),
                            executor
                        )
                            .also { INSTANCE = it }
                }
        }
    }
    private val userDao = userDatabase.userDao()

    fun getUser(uid: String): LiveData<User> = userDao.getUserData(uid)

    fun addUser(user: User) {
        executor.execute {
            userDao.addUser(user)
        }
    }
}