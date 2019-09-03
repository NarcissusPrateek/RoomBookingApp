package com.nineleaps.conferenceroombooking.Helper

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nineleaps.conferenceroombooking.Models.ConferenceList
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.utils.Constants

class ConferenceRecyclerAdapter(
    private val conferencceList: List<ConferenceList>,
    val listener: EditRoomDetails,
    val deleteListner: DeleteClickListner,
    val blockListner: BlockClickListner,
    private val mMoreListener: MoreAminitiesListner

) :
    androidx.recyclerview.widget.RecyclerView.Adapter<ConferenceRecyclerAdapter.ViewHolder>() {

    companion object{
        var mMoreAminitiesListener: MoreAminitiesListner? = null

    }
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
        mMoreAminitiesListener = mMoreListener

        holder.conferenceName.text =
            conferencceList[position].roomName
           holder.roomCapacity.text =  "${conferencceList[position].capacity} people"
        if (conferencceList[position].permission!! == true) {
            holder.permissionTextView.visibility = View.VISIBLE
        } else {
            holder.permissionTextView.visibility = View.GONE
        }

        val amenities = conferencceList[position].amenities!!.values.toMutableList()
        for (i in amenities.indices) {
            if (i > 3) {
                SetDrawable.setDrawable("More", holder.amenity3)
                holder.amenity3.text = "More"
                holder.amenity3.setTextColor(Color.parseColor("#0072BC"))
                holder.amenity3.visibility = View.VISIBLE
            } else if (i == 3) {
                SetDrawable.setDrawable(amenities[3], holder.amenity3)
                holder.amenity3.text = conferencceList[position].amenities!!.getValue(3)
                holder.amenity3.setTextColor(Color.parseColor("#4F4F4F"))
                holder.amenity3.visibility = View.VISIBLE
            }
            if (i == 0) {
                SetDrawable.setDrawable(amenities[0], holder.amenity0)
                holder.amenity0.visibility = View.VISIBLE
            } else if (i == 1) {
                SetDrawable.setDrawable(amenities[1], holder.amenity1)
                holder.amenity1.visibility = View.VISIBLE
            } else if (i == 2) {
                SetDrawable.setDrawable(amenities[2], holder.amenity2)
                holder.amenity2.visibility = View.VISIBLE
            }
        }
        holder.editRoom.setOnClickListener {
            listener.editRoom(position)
        }
        holder.deleteRoom.setOnClickListener {
            deleteListner.deleteRoom(position)
        }

        holder.blockRoomTextView.setOnClickListener {
            blockListner.blockRoom(position)
        }

        holder.amenity3.setOnClickListener {
            if (holder.amenity3.text == "More") {
                mMoreListener.moreAmenities(position)
            }
        }
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val conferenceName: TextView = itemView.findViewById(R.id.room_name_show)
        val permissionTextView: TextView = itemView.findViewById(R.id.permission_text_view)
        var amenity0: TextView = itemView.findViewById(R.id.dashboard_ani_1)
        var amenity1: TextView = itemView.findViewById(R.id.dashboard_ani_2)
        var amenity2: TextView = itemView.findViewById(R.id.dashboard_ani_3)
        var amenity3: TextView = itemView.findViewById(R.id.dashboard_ani_4)
        var deleteRoom: TextView = itemView.findViewById(R.id.delete_room_text_view)
        var editRoom: TextView = itemView.findViewById(R.id.edit_room_text_view)
        var roomCapacity: TextView = itemView.findViewById(R.id.room_capacity_text_view)
        var blockRoomTextView: TextView = itemView.findViewById(R.id.block_room_text_view)
        var conferencelist: ConferenceList? = null
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

    interface DeleteClickListner {
        fun deleteRoom(position: Int)
    }

    interface BlockClickListner {
        fun blockRoom(position: Int)
    }

    interface MoreAminitiesListner {
        fun moreAmenities(position: Int)
    }
}