package com.nineleaps.conferenceroombooking.utils

import android.app.AlertDialog
import android.content.Context
import com.nineleaps.conferenceroombooking.R
import com.nineleaps.conferenceroombooking.model.Dashboard

class ShowAlertDialogForEmployeeList {
    companion object{
        fun showEmployeeList(mEmployeeList:List<String>,position:Int,finalList: ArrayList<Dashboard>,activity: Context){
            val arrayListOfNames = ArrayList<String>()

            if (mEmployeeList.isEmpty()) {
                arrayListOfNames.add(finalList[position].organizer + activity.getString(R.string.organizer))

            } else {
                arrayListOfNames.add(finalList[position].organizer + activity.getString(R.string.organizer))

                for (item in mEmployeeList) {
                    arrayListOfNames.add(item)
                }
            }
            val listItems = arrayOfNulls<String>(arrayListOfNames.size)
            arrayListOfNames.toArray(listItems)
            val builder = AlertDialog.Builder(activity)
            builder.setItems(
                listItems
            ) { _, _ -> }
            val mDialog = builder.create()
            mDialog.show()
        }
    }
}