package com.nineleaps.conferenceroombooking.Helper

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.model.RoomDetails


@Suppress("NAME_SHADOWING")
class RoomAdapter(
    private val roomDetailsList: ArrayList<RoomDetails>,
    val mContext: Context,
    val listener: ItemClickListener
) : androidx.recyclerview.widget.RecyclerView.Adapter<RoomAdapter.ViewHolder>() {

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

        val amiList = roomDetailsList[position].amenities
        if(roomDetailsList[position].permission == 1) {
            holder.permissionTextView.visibility = View.VISIBLE
        }
        for (i in amiList!!.indices) {
            when (i) {
                0 -> {
                    setDrawable(roomDetailsList[position].amenities!![i], holder.amenity1)
                    holder.amenity1.visibility = View.VISIBLE
                }
                1 -> {
                    setDrawable(roomDetailsList[position].amenities!![i], holder.amenity2)
                    holder.amenity2.visibility = View.VISIBLE
                }
                2 -> {
                    setDrawable(roomDetailsList[position].amenities!![i], holder.amenity3)
                    holder.amenity3.visibility = View.VISIBLE
                }
                3 -> {
                    setDrawable(roomDetailsList[position].amenities!![i], holder.amenity4)
                    holder.amenity4.visibility = View.VISIBLE
                }
                4 -> {
                    setDrawable(roomDetailsList[position].amenities!![i], holder.amenity5)
                    holder.amenity5.visibility = View.VISIBLE
                }
            }
        }
        if (roomDetailsList[position].status == "Unavailable") {
            holder.roomNameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_unavailable, 0, 0, 0)
            holder.roomNameTextView.setTextColor(mContext.getResources().getColor(R.color.aminities))
            holder.permissionTextView.setTextColor(mContext.getResources().getColor(R.color.aminities))
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
        var permissionTextView: TextView = itemView.findViewById(R.id.permission_required_text_view)
        val mainCard: CardView = itemView.findViewById(R.id.main_card)
        var amenity1: TextView = itemView.findViewById(R.id.ami_room1)
        var amenity2: TextView = itemView.findViewById(R.id.ami_room2)
        var amenity3: TextView = itemView.findViewById(R.id.ami_room3)
        var amenity4: TextView = itemView.findViewById(R.id.ami_room4)
        var amenity5: TextView = itemView.findViewById(R.id.ami_room5)
        var roomDetails: RoomDetails? = null
    }

    /**
     * set data to the fields of view
     */
    @SuppressLint("SetTextI18n")
    private fun setDataToFields(holder: ViewHolder, position: Int) {
        holder.roomDetails = roomDetailsList[position]
        holder.buildingNameTextView.text = roomDetailsList[position].buildingName + ", " + roomDetailsList[position].place
        holder.roomNameTextView.text = roomDetailsList[position].roomName + " [${roomDetailsList[position].capacity} people]"
    }

    /**
     * an Interface which needs to be implemented whenever the adapter is attached to the recyclerview
     */
    interface ItemClickListener {
        fun onItemClick(roomId: Int?, buidingId: Int?, roomName: String?, buildingName: String?)
    }

}
