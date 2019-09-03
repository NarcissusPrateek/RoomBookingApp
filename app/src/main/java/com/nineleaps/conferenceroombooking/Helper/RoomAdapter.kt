package com.nineleaps.conferenceroombooking.Helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.model.RoomDetails


@Suppress("NAME_SHADOWING")
class RoomAdapter(
    private var roomDetailsList: ArrayList<RoomDetails>,
    val mContext: Context,
    val listener: ItemClickListener,
    private val mMoreListener: MoreAminitiesListner

) : androidx.recyclerview.widget.RecyclerView.Adapter<RoomAdapter.ViewHolder>() {
    companion object {
        var mMoreAminitiesListener: MoreAminitiesListner? = null

    }

    /**
     * this override function will set a view for the recyclerview items
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.conference_room_list, parent, false)
        return ViewHolder(view)
    }

    /**
     * bind data with the view
     */
    @SuppressLint("SetTextI18n", "NewApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mMoreAminitiesListener = mMoreListener

        val amenities = roomDetailsList[position].amenities!!.values.toMutableList()
        if (amenities.isEmpty()) {
            holder.amenity0.text = mContext.getString(R.string.no_aminities)
            holder.amenity0.visibility = View.VISIBLE
        } else
            for (i in amenities.indices) {
                if (i > 3) {
                    setDrawable("More", holder.amenity3)
                    holder.amenity3.text = "More"
                    holder.amenity3.setTextColor(Color.parseColor("#0072BC"))
                    holder.amenity3.visibility = View.VISIBLE
                } else if (i == 3) {
                    setDrawable(amenities[3], holder.amenity3)
                    holder.amenity3.text = roomDetailsList[position].amenities!!.getValue(3)
                    holder.amenity3.setTextColor(Color.parseColor("#4F4F4F"))
                    holder.amenity3.visibility = View.VISIBLE
                }
                if (i == 0) {
                    setDrawable(amenities[0], holder.amenity0)
                    holder.amenity0.visibility = View.VISIBLE
                } else if (i == 1) {
                    setDrawable(amenities[1], holder.amenity1)
                    holder.amenity1.visibility = View.VISIBLE
                } else if (i == 2) {
                    setDrawable(amenities[2], holder.amenity2)
                    holder.amenity2.visibility = View.VISIBLE
                }
            }
        if (roomDetailsList[position].status == "Unavailable") {
            holder.roomNameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unavailable, 0, 0, 0)
            holder.roomNameTextView.setTextColor(mContext.getResources().getColor(R.color.aminities))
            // holder.permissionTextView.setTextColor(mContext.getResources().getColor(R.color.aminities))
            holder.buildingNameTextView.setTextColor(mContext.getResources().getColor(R.color.aminities))
            holder.mainCard.elevation = 0.75F
        }
        setDataToFields(holder, position)
        holder.itemView.setOnClickListener {
            if (roomDetailsList[position].status == "Available") {
                listener.onItemClick(
                    roomDetailsList[position].roomId,
                    roomDetailsList[position].buildingId,
                    roomDetailsList[position].roomName,
                    roomDetailsList[position].buildingName
                )
            }
        }
        holder.amenity3.setOnClickListener {
            if (holder.amenity3.text == "More") {
                mMoreListener.moreAmenities(position)
            }
        }
    }

    private fun setDrawable(amitie: String, targetTextView: TextView) {
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


    /**
     * it will return number of items contains in recyclerview view
     */
    override fun getItemCount(): Int {
        return roomDetailsList.size
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var buildingNameTextView: TextView = itemView.findViewById(R.id.building_name)
        var roomNameTextView: TextView = itemView.findViewById(R.id.room_name)
        //   var permissionTextView: TextView = itemView.findViewById(R.id.permission_required_text_view)
        val mainCard: CardView = itemView.findViewById(R.id.main_card)
        var amenity0: TextView = itemView.findViewById(R.id.ami_room1)
        var amenity1: TextView = itemView.findViewById(R.id.ami_room2)
        var amenity2: TextView = itemView.findViewById(R.id.ami_room3)
        var amenity3: TextView = itemView.findViewById(R.id.ami_room4)

        var roomDetails: RoomDetails? = null
    }

    /**
     * set data to the fields of view
     */
    @SuppressLint("SetTextI18n")
    private fun setDataToFields(holder: ViewHolder, position: Int) {
        holder.roomDetails = roomDetailsList[position]
        holder.buildingNameTextView.text =
            roomDetailsList[position].buildingName + ", " + roomDetailsList[position].place
        holder.roomNameTextView.text =
            roomDetailsList[position].roomName + " [${roomDetailsList[position].capacity} people]"
    }

    /**
     * an Interface which needs to be implemented whenever the adapter is attached to the recyclerview
     */
    interface ItemClickListener {
        fun onItemClick(roomId: Int?, buidingId: Int?, roomName: String?, buildingName: String?)
    }

    fun filterList(filteredNames: ArrayList<RoomDetails>) {
        this.roomDetailsList = filteredNames
        notifyDataSetChanged()
    }

    interface MoreAminitiesListner {
        fun moreAmenities(position: Int)
    }
}
