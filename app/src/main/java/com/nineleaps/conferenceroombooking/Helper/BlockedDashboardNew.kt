package com.nineleaps.conferenceroombooking.Helper

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nineleaps.conferenceroombooking.Blocked
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.utils.FormatDate
import com.nineleaps.conferenceroombooking.utils.FormatTimeAccordingToZone


class BlockedDashboardNew(private val blockedList: List<Blocked>, val mContext: Context, val listener: UnblockRoomListener) : androidx.recyclerview.widget.RecyclerView.Adapter<BlockedDashboardNew.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.block_dashboard_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setDataToFields(holder, position)
        holder.unblock.setOnClickListener {
            unBlockRoom(blockedList[position].bookingId!!)
        }
    }
    override fun getItemCount(): Int {
        return blockedList.size
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val conferenceName : TextView = itemView.findViewById(R.id.block_conferenceRoomName)
        val purpose : TextView = itemView.findViewById(R.id.block_purpose)
        val fromtime : TextView = itemView.findViewById(R.id.block_from_time)
        val date : TextView= itemView.findViewById(R.id.block_date)
        val unblock :TextView = itemView.findViewById(R.id.unblock)
        var blocked: Blocked? = null
    }

    @SuppressLint("SetTextI18n")
    fun setDataToFields(holder: ViewHolder, position: Int) {
        val startTimeInUTC = blockedList[position].fromTime!!.split("T")[0] + " " + blockedList[position].fromTime!!.split("T")[1]
        val endTimeInUTC = blockedList[position].fromTime!!.split("T")[0] + " " + blockedList[position].toTime!!.split("T")[1]
        val startTimeInLocalTimeFormat = FormatTimeAccordingToZone.formatDateAsIndianStandardTime(startTimeInUTC)
        val endTimeInLocalTimeFormat = FormatTimeAccordingToZone.formatDateAsIndianStandardTime(endTimeInUTC)
        holder.blocked = blockedList[position]
        holder.conferenceName.text = blockedList[position].roomName + ", " + blockedList[position].buildingName
        holder.purpose.text = blockedList[position].purpose
        holder.date.text = startTimeInLocalTimeFormat.split(" ")[0]
        holder.fromtime.text = FormatDate.changeFormateFromDateTimeToTime(startTimeInLocalTimeFormat) + " - " + FormatDate.changeFormateFromDateTimeToTime(endTimeInLocalTimeFormat)
    }

    private fun unBlockRoom(bookingId: Int){
        listener.onClickOfUnblock(bookingId)
    }

    interface UnblockRoomListener {
        fun onClickOfUnblock(bookingId: Int)
    }


}