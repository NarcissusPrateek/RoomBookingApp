package com.nineleaps.conferenceroombooking.Helper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nineleaps.conferenceroombooking.GetAllAmenities
import com.nineleaps.conferenceroombooking.R


class SelectAmenities(var mAmenities: List<GetAllAmenities>, var listener: ItemClickListener) :
    androidx.recyclerview.widget.RecyclerView.Adapter<SelectAmenities.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_amenities_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mAmenities.size
    }

    companion object {
        var mClickListener: ItemClickListener? = null
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTextView.text = mAmenities[position].amenityName
        holder.itemView.setOnClickListener {
            listener.onBtnClick(mAmenities[position].amenityName, mAmenities[position].amenityId!!.toInt())
        }
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.add_amenities_checkbox)


    }

    interface ItemClickListener {
        fun onBtnClick(name: String?, id: Int?)
    }

    fun filterList(filterList: ArrayList<GetAllAmenities>) {
        this.mAmenities = filterList
        notifyDataSetChanged()
    }
}