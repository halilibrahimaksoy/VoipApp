package com.haksoy.soip.data.user

import androidx.room.TypeConverter

class UserTypeConverters {
    @TypeConverter
    fun fromSocialMedia(socialMedia: SocialMedia): String {
        return socialMedia.toString();
    }

    @TypeConverter
    fun toSocialMedia(input: String): SocialMedia {
        return SocialMedia.toSocialMedia(input)
    }

    @TypeConverter
    fun fromLocation(location: Location): String {
        return "${location.latitude}:${location.longitude}"
    }

    @TypeConverter
    fun toLocation(string: String): Location {
        return Location(string.split(":")[0].toDouble(), string.split(":")[1].toDouble())
    }
}