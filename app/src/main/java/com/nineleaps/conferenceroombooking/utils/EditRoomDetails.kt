package com.nineleaps.conferenceroombooking.utils

import com.nineleaps.conferenceroombooking.Models.ConferenceList
import java.io.Serializable

data class EditRoomDetails (
    var mRoomDetail: ConferenceList? = null
): Serializable