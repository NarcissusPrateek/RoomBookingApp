package com.nineleaps.conferenceroombooking.Helper

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nineleaps.conferenceroombooking.Models.ConferenceList
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.utils.Constants

class ConferenceRecyclerAdapter(private val conferencceList: List<ConferenceList>, val listener: EditRoomDetails, val deleteListner:DeleteClickListner) :
    androidx.recyclerview.widget.RecyclerView.Adapter<ConferenceRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.conference_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return conferencceList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.conferencelist = conferencceList[position]
        holder.conferenceName.text = conferencceList[position].roomName + " [${conferencceList[position].capacity} people]"
     //   holder.roomCapacity.text = " [${conferencceList[position].capacity} people]"
        if(conferencceList[position].permission!! == 1) {
            holder.permissionTextView.visibility = View.VISIBLE
        } else {
            holder.permissionTextView.visibility = View.GONE
        }
        if(conferencceList[position].amenities!!.isNotEmpty()) {
            for(i in conferencceList[position].amenities!!.indices) {
                when(i) {
                    0 -> {
                        setDrawable(conferencceList[position].amenities!![i], holder.amenity1)
                        holder.amenity1.visibility = View.VISIBLE

                    }
                    1-> {
                        setDrawable(conferencceList[position].amenities!![i], holder.amenity2)
                        holder.amenity2.visibility = View.VISIBLE
                    }
                    2-> {
                        setDrawable(conferencceList[position].amenities!![i], holder.amenity3)
                        holder.amenity3.visibility = View.VISIBLE
                    }
                    3-> {
                        setDrawable(conferencceList[position].amenities!![i], holder.amenity4)
                        holder.amenity4.visibility = View.VISIBLE
                    }
                    4 -> {
                        setDrawable(conferencceList[position].amenities!![i], holder.amenity5)
                        holder.amenity5.visibility = View.VISIBLE
                    }
                }
            }
        }
        holder.editRoom.setOnClickListener {
                listener.editRoom(position)
        }
        holder.deleteRoom.setOnClickListener {
            deleteListner.deleteRoom(position)
        }
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val conferenceName: TextView = itemView.findViewById(R.id.room_name_show)
        val permissionTextView: TextView = itemView.findViewById(R.id.permission_text_view)
        var amenity1: TextView = itemView.findViewById(R.id.dashboard_ani_1)
        var amenity2: TextView = itemView.findViewById(R.id.dashboard_ani_2)
        var amenity3: TextView = itemView.findViewById(R.id.dashboard_ani_3)
        var amenity4: TextView = itemView.findViewById(R.id.dashboard_ani_4)
        var amenity5: TextView = itemView.findViewById(R.id.dashboard_ani_5)
        var deleteRoom: TextView = itemView.findViewById(R.id.delete_room_text_view)
        var editRoom: TextView = itemView.findViewById(R.id.edit_room_text_view)
       // var roomCapacity: TextView = itemView.findViewById(R.id.room_capacity_text_view)
        var conferencelist: ConferenceList? = null
    }

    private fun setDrawable(amitie: String, targetTextView: TextView) {
        when(amitie) {
            Constants.PROJECTOR -> {
                targetTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_projector, 0, 0, 0)
            }
            Constants.WHITEBOARD_MARKER -> {
                targetTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_white_board2, 0, 0, 0)
            }
            Constants.MONITOR -> {
                targetTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_live_tv_black_24dp, 0, 0, 0)
            }
            Constants.SPEAKER -> {
                targetTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_speaker, 0, 0, 0)
            }
            Constants.EXTENSION_BOARD -> {
                targetTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_extension_board, 0, 0, 0)
            }
        }
        targetTextView.text = amitie
    }

    /**
     * click listener on right drawable
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun TextView.onRightDrawableClicked(onClicked: (view: TextView) -> Unit) {
        this.setOnTouchListener { v, event ->
            var hasConsumed = false
            if (v is TextView && event.x >= v.width - v.totalPaddingRight) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasConsumed = true
            }
            hasConsumed
        }
    }
    interface EditRoomDetails {
        fun editRoom(position: Int)
    }

    interface DeleteClickListner{
        fun deleteRoom(position: Int)
    }

}