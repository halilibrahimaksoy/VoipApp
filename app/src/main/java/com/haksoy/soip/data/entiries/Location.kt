package com.haksoy.soip.data.entiries

import java.io.Serializable
import java.util.*

data class Location(
    val id: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val foreground: Boolean = true,
    val date: Date = Date()
) : Serializable