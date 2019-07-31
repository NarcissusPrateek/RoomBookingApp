package com.nineleaps.conferenceroombooking.manageBuildings.adapter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.model.Building


class BuildingDashboardAdapter(var mContext: Context, private val mBuildingList: List<Building>, private val btnListener: BtnClickListener, private val editListener: EditClickListener, private val deleteListener:DeleteClickListner) :
    androidx.recyclerview.widget.RecyclerView.Adapter<BuildingDashboardAdapter.ViewHolder>() {


    /**
     * an interface object delacration
     */
    companion object {
        var mClickListener: BtnClickListener? = null
    }

    /**
     * attach view to the recyclerview
     */
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.building_dashboard_list, parent, false)
        return ViewHolder(view)
    }

    /**
     * return the number of item present in the list
     */
    override fun getItemCount(): Int {
        return mBuildingList.size
    }

    /**
     * bind data to the view
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mClickListener = btnListener

        /**
         * set data into various fields of recylcerview card
         */
        holder.building = mBuildingList[position]
        holder.txvBuilding.text = mBuildingList[position].buildingName
        holder.txvBuildingPlace.text = mBuildingList[position].buildingPlace
        val id = mBuildingList[position].buildingId
        val buildingName = mBuildingList[position].buildingName

        /**
         * call the interface method on click of item in recyclerview
         */
        holder.edit.setOnClickListener {
            editListener.onEditBtnClick(position)
        }
        holder.viewRooms.setOnClickListener {
            mClickListener?.onBtnClick(id, buildingName)
        }
        holder.delete.setOnClickListener {
            Log.i("66666","Enter")
            deleteListener.onDeleteClick(position)
        }
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val txvBuilding: TextView = itemView.findViewById(R.id.building_name)
        val txvBuildingPlace: TextView = itemView.findViewById(R.id.building_place)
        val edit: TextView = itemView.findViewById(R.id.edit_room_details)
        val delete: TextView = itemView.findViewById(R.id.delete_building_text_view)
        val viewRooms: TextView = itemView.findViewById(R.id.view_room_text_view)
        var building: Building? = null
    }

    /**
     * an Interface which needs to be implemented whenever the adapter is attached to the recyclerview
     */
    interface BtnClickListener {
        fun onBtnClick(buildingId: String?, buildingName: String?)
    }

    interface EditClickListener {
        fun onEditBtnClick(position: Int)
    }

    interface  DeleteClickListner{
        fun onDeleteClick(position: Int)
    }
}