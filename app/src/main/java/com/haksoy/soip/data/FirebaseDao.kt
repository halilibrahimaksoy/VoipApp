package com.haksoy.soip.data

import User
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.haksoy.soip.data.entiries.Location
import com.haksoy.soip.utlis.Constants
import com.haksoy.soip.utlis.Resource
import com.haksoy.soip.utlis.observeOnce

private const val TAG = "SoIP:FirebaseDao"

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

    fun forgetPassword(email: String): LiveData<Resource<Exception>> {
        var result = MutableLiveData<Resource<Exception>>()
        auth.sendPasswordResetEmail(email)
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

    fun updateUserProfile(user: User): MutableLiveData<Resource<Exception>> {
        var result = MutableLiveData<Resource<Exception>>()
        if (!user.profileImage.toString()
                        .contains(Constants.firebaseStoregeURL, false)
        ) {// for understanding to is image changed ?
            uploadProfileImage(getCurrentUserUid(), user.profileImage.toString()).observeOnce {
                if (it.status == Resource.Status.SUCCESS) {
                    user.profileImage = it.data
                    updateUser(
                            user
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
                    user
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
            user: User
    ): MutableLiveData<Resource<Exception>> {
        var result = MutableLiveData<Resource<Exception>>()
        cloudFirestoreDB.collection(Constants.User).document(user.uid!!).set(user)
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
                    if (snapshot != null && snapshot.data != null)
                        location.value = snapshot.toObject(User::class.java)!!.location
                }
        return location
    }

//    fun getNearlyUsers(location: Location): List<User> {
//
//        var user: User
//        val users: MutableList<User> = mutableListOf()
//        for (i in 1..20) {
//            user = User(
//                UUID.randomUUID().toString(),
//                "email_$i",
//                "Name_$i",
//                "user info user info user info user info user info user info user info user info ",
//                Constants.randomImageUrl,
//                null,
//                Location(
//                    UUID.randomUUID().toString(),
//                    location.latitude - Constants.nerlyLimit / 2 + Random.nextDouble(Constants.nerlyLimit),
//                    location.longitude - Constants.nerlyLimit / 2 + Random.nextDouble(Constants.nerlyLimit),
//                    true,
//                    Date()
//                )
//            )
//            users.add(user)
//        }
//        return users
//    }

    fun getNearlyUsers(location: Location): MutableLiveData<Resource<List<User>>> {
        var result = MutableLiveData<Resource<List<User>>>()
        Log.i(TAG, "getNearlyUsers filter -> location.latitude >= ${location.latitude - Constants.nerlyLimit}")
        Log.i(TAG, "getNearlyUsers filter -> location.latitude <= ${location.latitude + Constants.nerlyLimit}")
        Log.i(TAG, "getNearlyUsers filter -> location.longitude >= ${location.longitude - Constants.nerlyLimit}")
        Log.i(TAG, "getNearlyUsers filter -> location.longitude <= ${location.longitude + Constants.nerlyLimit}")

        cloudFirestoreDB.collection(Constants.User).whereGreaterThanOrEqualTo("location.latitude", location.latitude - Constants.nerlyLimit)
                .whereLessThanOrEqualTo("location.latitude", location.latitude + Constants.nerlyLimit)
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        cloudFirestoreDB.collection(Constants.User)
                                .whereGreaterThanOrEqualTo("location.longitude", location.longitude - Constants.nerlyLimit)
                                .whereLessThanOrEqualTo("location.longitude", location.longitude + Constants.nerlyLimit)
                                .get().addOnCompleteListener { it1 ->
                                    if (it1.isSuccessful) {
                                        val latitudeList = it.result.toObjects(User::class.java)
                                        Log.i(TAG, "Lat List")
                                        for (user in latitudeList) {
                                            Log.i(TAG, user.toString())
                                        }
                                        val longitudeList = it1.result.toObjects(User::class.java)
                                        Log.i(TAG, "Long List")
                                        for (user in longitudeList) {
                                            Log.i(TAG, user.toString())
                                        }
                                        val lasResult = latitudeList.intersect(longitudeList)
                                        result.value = Resource.success(lasResult.toMutableList())
                                        Log.i(TAG, "Result List")
                                        for (user in lasResult) {
                                            Log.i(TAG, user.toString())
                                        }
                                    } else
                                        result.value = Resource.error(it.exception!!.localizedMessage)
                                }

                    } else
                        result.value = Resource.error(it.exception!!.localizedMessage)
                }
        return result
    }

//    fun getNearlyUsers(location: Location): MutableLiveData<Resource<List<User>>> {
//        var result = MutableLiveData<Resource<List<User>>>()
//        Log.i(TAG, "getNearlyUsers filter -> location.latitude >= ${location.latitude - Constants.nerlyLimit}")
//        Log.i(TAG, "getNearlyUsers filter -> location.latitude <= ${location.latitude + Constants.nerlyLimit}")
//        Log.i(TAG, "getNearlyUsers filter -> location.longitude >= ${location.longitude - Constants.nerlyLimit}")
//        Log.i(TAG, "getNearlyUsers filter -> location.longitude <= ${location.longitude + Constants.nerlyLimit}")
//
//        cloudFirestoreDB.collection(Constants.User)
////                .whereGreaterThanOrEqualTo("location.latitude", location.latitude - Constants.nerlyLimit)
////                .whereLessThanOrEqualTo("location.latitude", location.latitude + Constants.nerlyLimit)
//                .get().addOnCompleteListener {
//                    if (it.isSuccessful) {
//                        result.value = Resource.success(it.result.toObjects(User::class.java))
//                    } else
//                        result.value = Resource.error(it.exception!!.localizedMessage)
//                }
//        return result
//    }

    fun isUserDataExist(uid: String): MutableLiveData<Resource<Boolean>> {
        var result = MutableLiveData<Resource<Boolean>>()
        cloudFirestoreDB.collection(Constants.User).document(uid)
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (it.result.toObject(User::class.java) != null) {
                            result.value = Resource.success(true)
                        } else {
                            result.value = Resource.success(false)
                        }
                    } else
                        result.value = Resource.error(it.exception!!.localizedMessage)
                }
        return result
    }
}