package com.haksoy.voipapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.haksoy.voipapp.utlis.Resource

class AuthenticationDao {

    fun createAccount(email: String, password: String): LiveData<Resource<FirebaseAuth>> {
        val auth = Firebase.auth
        var result = MutableLiveData<Resource<FirebaseAuth>>()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                result.value = Resource.success(auth)
            }
            .addOnFailureListener {
                result.value = Resource.error(it.localizedMessage, auth)
            }
        return result
    }

    fun signIn(email: String, password: String): LiveData<Resource<FirebaseAuth>> {

        val auth = Firebase.auth
        var result = MutableLiveData<Resource<FirebaseAuth>>()

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                result.value = Resource.success(auth)
            }
            .addOnFailureListener {
                result.value = Resource.error(it.localizedMessage)
            }
        return result
    }
}