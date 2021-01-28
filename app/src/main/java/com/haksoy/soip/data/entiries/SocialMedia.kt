package com.haksoy.soip.data.entiries

import java.io.Serializable

data class SocialMedia(
    var instagram: String? = null,
    var facebook: String? = null,
    var twitter: String? = null
) : Serializable