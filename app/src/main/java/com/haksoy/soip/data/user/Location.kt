package com.haksoy.soip.data.user

import java.io.Serializable
import java.util.*

data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) : Serializable