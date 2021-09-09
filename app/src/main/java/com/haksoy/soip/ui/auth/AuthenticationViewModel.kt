package com.haksoy.soip.ui.auth

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.haksoy.soip.data.FirebaseDao
import com.haksoy.soip.utlis.Resource

private const val TAG = "SoIP:AuthenticationViewModel"

class AuthenticationViewModel : ViewModel() {
    val firebaseDao = FirebaseDao.getInstance()

    val verificationId = MutableLiveData<String>()
    private var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                firebaseDao.signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }
            }

            override fun onCodeSent(
                _verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent:$verificationId")

                verificationId.postValue(_verificationId)
//            storedVerificationId = verificationId
//            resendToken = token
            }
        }

    fun verifyPhoneNumber(activity: Activity,phoneNumber: String) {
        firebaseDao.verifyPhoneNumber(activity,phoneNumber, callbacks)
    }

    fun signInWithPhoneAuthCredential(verificationId:String,otp: String): LiveData<Resource<Exception>> {
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        return firebaseDao.signInWithPhoneAuthCredential(credential)
    }


}