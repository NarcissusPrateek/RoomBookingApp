package com.nineleaps.conferenceroombooking.Helper

import android.widget.TextView
import com.nineleaps.conferenceroombooking.R

class SetDrawable {
    companion object{
        fun setDrawable(amitie: String, targetTextView: TextView) {
            when (amitie) {
                "Projector" -> {
                    targetTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_projector, 0, 0, 0)
                }
                "WhiteBoard-Marker" -> {
                    targetTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_white_board2, 0, 0, 0)
                }
                "Monitor" -> {
                    targetTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_live_tv_black_24dp, 0, 0, 0)
                }
                "Speaker" -> {
                    targetTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_speaker, 0, 0, 0)
                }
                "Extension Board" -> {
                    targetTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_extension_board, 0, 0, 0)
                }
                "TV", "tv" -> {
                    targetTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_tv_black_24dp, 0, 0, 0)
                }
                "More" -> {
                    targetTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unfold_more_black_24dp, 0, 0, 0)
                }
                else -> {
                    targetTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_devices_other_black_24dp, 0, 0, 0)
                }
            }
            targetTextView.text = amitie
        }
    }
}