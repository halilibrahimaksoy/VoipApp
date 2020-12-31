package com.haksoy.voipapp.ui.profile

import User
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.haksoy.voipapp.utlis.Constants
import com.haksoy.voipapp.utlis.Resource

class UserProfileViewModel : ViewModel() {
    val cloudFirestoreDB = Firebase.firestore
    val storageFirebase = Firebase.storage

    val currentUser = MutableLiveData<User>()

    fun fetchUserDate(uid: String) {
        val docRef = cloudFirestoreDB.collection(Constants.User).document(uid)
        docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
            }

            if (snapshot != null && snapshot.exists()) {
                currentUser.postValue(snapshot.toObject(User::class.java))
            } else {
            }
        }
    }

    fun updateUserProfile(
        uid: String,
        email: String,
        newImageUri: Uri?=null,
        name: String,
        info: String
    ): MutableLiveData<Resource<Exception>> {
        var result = MutableLiveData<Resource<Exception>>()

        if (newImageUri != null) {
            uploadProfileImage(uid, newImageUri.toString()).observeForever(Observer {
                if (it.status == Resource.Status.SUCCESS) {
                    updateUser(uid, email, name, info, it.data).observeForever(Observer {
                        if (it.status == Resource.Status.SUCCESS) {
                            result.value = Resource.success(null)
                        } else if (it.status == Resource.Status.ERROR) {
                            result.value = Resource.error(it.message!!)
                        }
                    })
                } else if (it.status == Resource.Status.ERROR) {
                    result.value = Resource.error(it.message!!)
                }
            })
        } else {
            updateUser(uid, email, name, info).observeForever(Observer {
                if (it.status == Resource.Status.SUCCESS) {
                    result.value = Resource.success(null)
                } else if (it.status == Resource.Status.ERROR) {
                    result.value = Resource.error(it.message!!)
                }
            })
        }
        return result
    }

    private fun updateUser(
        uid: String,
        email: String,
        name: String,
        info: String,
        imageUri: String? = null
    ): MutableLiveData<Resource<Exception>> {
        val newUser = User(uid, email, name, info, imageUri)
        var result = MutableLiveData<Resource<Exception>>()
        cloudFirestoreDB.collection(Constants.User).document(newUser.uid!!).set(newUser)
            .addOnSuccessListener {
                result.value = Resource.success(null)
            }
            .addOnFailureListener {
                result.value = Resource.error(it.localizedMessage, it)
            }

        return result
    }

    private fun uploadProfileImage(
        uid: String,
        imageUri: String
    ): MutableLiveData<Resource<String>> {
        var result = MutableLiveData<Resource<String>>()
        val updateRef = storageFirebase.reference.child(Constants.User_Profile_Image).child(uid)
        val uploadTask = updateRef.putFile(Uri.parse(imageUri))

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    result.value = Resource.error(it.localizedMessage)
                }
            }
            updateRef.downloadUrl
        }.addOnSuccessListener {
            result.value = Resource.success(it.toString())
        }.addOnFailureListener {
            result.value = Resource.error(it.localizedMessage)
        }

        return result
    }

}