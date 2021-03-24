package com.haksoy.soip.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.haksoy.soip.data.user.User
import com.haksoy.soip.data.user.UserDao
import com.haksoy.soip.data.user.UserTypeConverters
import com.haksoy.soip.utlis.Constants


@Database(entities = [User::class], version = 1)
@TypeConverters(UserTypeConverters::class)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        // For Singleton instantiation
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            return INSTANCE
                    ?: synchronized(this) {
                        INSTANCE
                                ?: buildDatabase(
                                        context
                                )
                                        .also { INSTANCE = it }
                    }
        }

        private fun buildDatabase(context: Context): UserDatabase {
            return Room.databaseBuilder(
                    context,
                    UserDatabase::class.java,
                    Constants.USER_DATABASE
            ).build()
        }
    }
}