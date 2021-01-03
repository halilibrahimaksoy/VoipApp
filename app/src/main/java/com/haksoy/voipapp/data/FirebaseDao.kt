package com.haksoy.voipapp.data

import User
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.haksoy.voipapp.data.entiries.Location
import com.haksoy.voipapp.utlis.Constants
import com.haksoy.voipapp.utlis.Resource

class FirebaseDao {
    companion object {
        @Volatile
        private var INSTANCE: FirebaseDao? = null

        fun getInstance(): FirebaseDao {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FirebaseDao().also { INSTANCE = it }
            }
        }
    }

    val auth = Firebase.auth
    val cloudFirestoreDB = Firebase.firestore
    fun createAccount(email: String, password: String): LiveData<Resource<Exception>> {
        val auth = Firebase.auth
        var result = MutableLiveData<Resource<Exception>>()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                result.value = Resource.success(null)
            }
            .addOnFailureListener {
                result.value = Resource.error(it.localizedMessage, it)
            }
        return result
    }

    fun signIn(email: String, password: String): LiveData<Resource<Exception>> {

        val auth = Firebase.auth
        var result = MutableLiveData<Resource<Exception>>()

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                result.value = Resource.success(null)
            }
            .addOnFailureListener {
                result.value = Resource.error(it.localizedMessage, it)
            }
        return result
    }

    fun getUid(): String {
        return auth.currentUser!!.uid
    }

    fun getEmail(): String {
        return auth.currentUser!!.email.toString()
    }

    fun addLocation(location: Location){
        println("qwdfadf")
        cloudFirestoreDB.collection(Constants.User).document(getUid()).update("locaiton",location)
    }
}