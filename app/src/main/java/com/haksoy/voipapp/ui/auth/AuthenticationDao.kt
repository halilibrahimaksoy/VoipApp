package com.haksoy.voipapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.haksoy.voipapp.utlis.Resource
import java.lang.Exception

class AuthenticationDao {

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
                result.value = Resource.error(it.localizedMessage,it)
            }
        return result
    }
}