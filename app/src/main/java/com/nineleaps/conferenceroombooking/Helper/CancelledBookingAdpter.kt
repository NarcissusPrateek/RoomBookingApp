package com.nineleaps.conferenceroombooking.Helper


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.model.Dashboard
import com.nineleaps.conferenceroombooking.utils.FormatDate
import com.nineleaps.conferenceroombooking.utils.FormatTimeAccordingToZone
import java.text.SimpleDateFormat

@Suppress("NAME_SHADOWING")
class CancelledBookingAdpter(
    private val dashboardItemList: ArrayList<Dashboard>,
    val mContext: Context,
    private val mShowMembers: ShowMembersListener

) : androidx.recyclerview.widget.RecyclerView.Adapter<CancelledBookingAdpter.ViewHolder>() {

    /**
     * a variable which will hold the 'Instance' of interface
     */
    companion object {
        var mShowMembersListener: ShowMembersListener? = null
    }

    /**
     * this override function will set a view for the recyclerview items
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_cancelled_bookings_list_items, parent, false)
        return ViewHolder(view)
    }

    /**
     * bind data with the view
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mShowMembersListener = mShowMembers

        val fromTime = dashboardItemList[position].fromTime
        val toTime = dashboardItemList[position].toTime
        val fromDate = fromTime!!.split("T")
        val toDate = toTime!!.split("T")

        setDataToFields(holder, position)

        val localStartTime = FormatTimeAccordingToZone.formatDateAsIndianStandardTime("${fromDate[0]} ${fromDate[1]}")
        val localEndTime = FormatTimeAccordingToZone.formatDateAsIndianStandardTime("${fromDate[0]} ${toDate[1]}")
        holder.dateTextView.text = formatDate(localStartTime.split(" ")[0])
        holder.fromTimeTextView.text = FormatDate.changeFormateFromDateTimeToTime(localStartTime) + " - " + FormatDate.changeFormateFromDateTimeToTime(localEndTime)
    }


    /**
     * formate date
     */
    @SuppressLint("SimpleDateFormat")
    private fun formatDate(date: String): String {
        val simpleDateFormatInput = SimpleDateFormat("yyyy-MM-dd")
        val simpleDateFormatOutput = SimpleDateFormat("dd MMMM yyyy")
        return simpleDateFormatOutput.format(simpleDateFormatInput.parse(date.split("T")[0]))
    }


    /**
     * it will return number of items contains in recyclerview view
     */
    override fun getItemCount(): Int {
        return dashboardItemList.size
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var buildingNameTextView: TextView = itemView.findViewById(R.id.cancelled_building_name)
        var fromTimeTextView: TextView = itemView.findViewById(R.id.cancelled_time)
        var dateTextView: TextView = itemView.findViewById(R.id.cancelled_date)
        var purposeTextView: TextView = itemView.findViewById(R.id.cancelled_purpose)
        var showButton: ImageView = itemView.findViewById(R.id.cancelled_btnshow)
        var dashboard: Dashboard? = null
    }

    /**
     * set data to the fields of view
     */
    @SuppressLint("SetTextI18n")
    private fun setDataToFields(holder: ViewHolder, position: Int) {
        holder.dashboard = dashboardItemList[position]
        holder.buildingNameTextView.text = "${dashboardItemList[position].roomName}, ${dashboardItemList[position].buildingName}"
        holder.purposeTextView.text = dashboardItemList[position].purpose
        holder.showButton.setOnClickListener {
            setMeetingMembers(position)
        }
    }

    /**
     * send employee List to the activity using interface which will display the list of employee names in the alert dialog
     */
    private fun setMeetingMembers(position: Int) {
        if (dashboardItemList[position].name != null)
            mShowMembers.showMembers(dashboardItemList[position].name!!, position)
        else
            mShowMembers.showMembers(emptyList(),position)    }

    /**
     * An interface which will be implemented by UserDashboardBookingActivity activity to pass employeeList to the activity
     */
    interface ShowMembersListener {
        fun showMembers(mEmployeeList: List<String>, position: Int)
    }
}
