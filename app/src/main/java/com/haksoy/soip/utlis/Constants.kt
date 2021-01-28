package com.haksoy.soip.utlis

import java.util.regex.Pattern

object Constants {
    const val User_Profile_Image = "USER_PROFILE_IMAGE"
    const val nerlyLimit: Double = 0.1

    //    val randomImageUrl = "https://loremflickr.com/320/240"
    const val randomImageUrl =
        "https://firebasestorage.googleapis.com/v0/b/voipapp-dbc05.appspot.com/o/USER_PROFILE_IMAGE%2Fdefault_image.jpg?alt=media&token=966a0ad1-3753-4c0f-a829-82281d1481cc"
    const val firebaseStoregeURL = "https://firebasestorage.googleapis.com/"
    const val UserProfileFragmentReason = "UserProfileFragmentReason"
    const val UserProfileFragmentSelectedUser = "UserProfileFragmentSelectedUser"

    //    val randomImageUrl = "https://firebasestorage.googleapis.com/v0/b/voipapp-dbc05.appspot.com/o/USER_PROFILE_IMAGE%2Fw5VU6dSV4Jc56oiyb7LSH2vzwX92?alt=media&token=a0c50e33-2175-4d5e-badd-e7a58eac0446"
    const val UserListFragmentTag = "UserListFragment"
    const val UserProfileFragmentTag = "UserProfileFragmentTag"
    const val NearlyUserList = "NearlyUserList"
    const val SelectedUserUid = "SelectedUserUid"


    const val REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 34
    const val REQUEST_BACKGROUND_LOCATION_PERMISSIONS_REQUEST_CODE = 56

    const val MIN_PASSWORD_CHARACTER_COUNT = 6

    //User Fileds
    const val User = "USER"
    const val uid = "uid"
    const val email = "email"
    const val name = "name"
    const val info = "info"
    const val profileImage = "profileImage"
    const val gender = "gender"
    const val location = "location"
    const val nat = "nat"

    val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
}