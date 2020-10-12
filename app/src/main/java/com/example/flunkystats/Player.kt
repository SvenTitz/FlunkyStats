package com.example.flunkystats

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Player(
    var name: String? = "",
    var team: String? = ""
)