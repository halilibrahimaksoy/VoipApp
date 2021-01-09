package com.haksoy.voipapp.data.entiries

import org.parceler.Parcel
import java.io.Serializable
import java.util.*

@Parcel
data class Location(
    val id: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val foreground: Boolean = true,
    val date: Date = Date()
) : Serializable