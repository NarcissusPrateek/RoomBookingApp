package com.nineleaps.conferenceroombooking.Helper


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.model.EmployeeList


class SelectMembers(private var mEmployeeList: List<EmployeeList>, var listener: ItemClickListener) :
    androidx.recyclerview.widget.RecyclerView.Adapter<SelectMembers.ViewHolder>() {


    /**
     * an interface object delacration
     */
    companion object {
        var mClickListener: ItemClickListener? = null
    }

    /**
     * attach view to the recyclerview
     */
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        mClickListener = listener
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_member_items, parent, false)
        return ViewHolder(view)
    }

    /**
     * return the number of item present in the list
     */
    override fun getItemCount(): Int {
        return mEmployeeList.size
    }

    /**
     * bind data to the view
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.nameTextView.text = mEmployeeList[position].name
        holder.emailTextView.text = mEmployeeList[position].email
        /**
         * call the interface method on click of item in recyclerview
         */

        holder.itemView.setOnClickListener {
            //mClickListener!!.onBtnClick(mEmployeeList[position].name, mEmployeeList[position].email)
            mClickListener!!.onBtnClick(mEmployeeList[position].name, mEmployeeList[position].email)
        }
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.add_member_checkbox)
        val emailTextView: TextView = itemView.findViewById(R.id.add_member_email)

    }

    /**
     * an Interface which needs to be implemented whenever the adapter is attached to the recyclerview
     */
    interface ItemClickListener {
        fun onBtnClick(name: String?, email: String?)
    }

    fun filterList(filteredNames: ArrayList<EmployeeList>) {
        this.mEmployeeList = filteredNames
        notifyDataSetChanged()
    }
}