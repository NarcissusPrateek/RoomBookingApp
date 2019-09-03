package com.nineleaps.conferenceroombooking.utils

class Floor{

    companion object{
        fun floorToString(floor:Int):String{
            return when(floor) {
                1-> "st Floor"
                2-> "nd Floor"
                3-> "rd Floor"
                else-> "th Floor"
            }
        }
    }

}