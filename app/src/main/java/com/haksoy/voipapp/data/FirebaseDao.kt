package com.haksoy.voipapp.data

import User
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.haksoy.voipapp.data.entiries.Location
import com.haksoy.voipapp.utlis.Constants
import com.haksoy.voipapp.utlis.Resource
import com.haksoy.voipapp.utlis.observeOnce
import java.util.*
import kotlin.random.Random

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
    private val cloudFirestoreDB = Firebase.firestore
    private val storageFirebase = Firebase.storage
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

    fun createNewUser(
        uid: String,
        email: String
    ): MutableLiveData<Resource<Exception>> {
        val newUser = User(uid, email)
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

    fun getCurrentUserUid(): String {
        return auth.currentUser!!.uid
    }

    fun getCurrentUserEmail(): String {
        return auth.currentUser!!.email.toString()
    }

    fun addLocation(location: Location) {
        cloudFirestoreDB.collection(Constants.User).document(getCurrentUserUid())
            .update(Constants.location, location)
    }

    fun fetchUserDate(uid: String): LiveData<Resource<User>> {
        val docRef = cloudFirestoreDB.collection(Constants.User).document(uid)
        var result = MutableLiveData<Resource<User>>()
        docRef.get().addOnCompleteListener {
            if (it.isSuccessful) {
                result.value = Resource.success(it.result.toObject(User::class.java))
            } else
                result.value = Resource.error(it.exception!!.localizedMessage)
        }
        return result
    }

    fun updateUserProfile(
        newImageUri: Uri? = null,
        name: String,
        info: String
    ): MutableLiveData<Resource<Exception>> {
        var result = MutableLiveData<Resource<Exception>>()

        if (newImageUri != null) {
            uploadProfileImage(getCurrentUserUid(), newImageUri.toString()).observeOnce {
                if (it.status == Resource.Status.SUCCESS) {
                    updateUser(
                        getCurrentUserUid(),
                        getCurrentUserEmail(),
                        name,
                        info,
                        it.data
                    ).observeOnce { it1 ->
                        if (it1.status == Resource.Status.SUCCESS) {
                            result.value = Resource.success(null)
                        } else if (it1.status == Resource.Status.ERROR) {
                            result.value = Resource.error(it1.message!!)
                        }
                    }
                } else if (it.status == Resource.Status.ERROR) {
                    result.value = Resource.error(it.message!!)
                }
            }
        } else {
            updateUser(
                getCurrentUserUid(),
                getCurrentUserEmail(),
                name,
                info
            ).observeOnce {
                if (it.status == Resource.Status.SUCCESS) {
                    result.value = Resource.success(null)
                } else if (it.status == Resource.Status.ERROR) {
                    result.value = Resource.error(it.message!!)
                }
            }
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

        uploadTask.continueWithTask { task ->
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


    fun getLocation(uid: String): MutableLiveData<Location> {
        val location = MutableLiveData<Location>()
        val docRef = cloudFirestoreDB.collection(Constants.User).document(uid)
            .addSnapshotListener { snapshot, error ->
                if(snapshot!=null && snapshot.data!=null)
                location.value = snapshot.toObject(User::class.java)!!.location
            }
        return location
    }

    fun getNearlyUsers(location: Location): List<User> {

        var user: User
        val users: MutableList<User> = mutableListOf()
        for (i in 1..20) {
            user = User(
                UUID.randomUUID().toString(),
                "email_$i",
                "Name_$i",
                "user info user info user info user info user info user info user info user info ",
                Constants.randomImageUrl,
                null,
                Location(
                    UUID.randomUUID().toString(),
                    location.latitude - Constants.nerlyLimit / 2 + Random.nextDouble(Constants.nerlyLimit),
                    location.longitude - Constants.nerlyLimit / 2 + Random.nextDouble(Constants.nerlyLimit),
                    true,
                    Date()
                )
            )
            users.add(user)
        }
        return users
    }
}