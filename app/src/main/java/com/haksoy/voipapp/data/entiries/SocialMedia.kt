package com.haksoy.voipapp.data.entiries

import org.parceler.Parcel
import java.io.Serializable

@Parcel
data class SocialMedia(
    var instagram: String? = null,
    var facebook: String? = null,
    var twitter: String? = null
) : Serializable