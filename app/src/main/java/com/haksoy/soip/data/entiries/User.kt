package com.haksoy.soip.data.entiries

import java.io.Serializable

data class User(
    var uid: String? = null,
    var email: String? = null,
    var name: String? = null,
    var info: String? = null,
    var profileImage: String? = null,
    var gender: String? = null,
    var location: Location = Location(),
    var nat: String? = null,
    var socialMedia: SocialMedia = SocialMedia(),
    var token:String?=null
) : Serializable