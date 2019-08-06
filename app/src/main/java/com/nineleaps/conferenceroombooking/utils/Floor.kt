package com.nineleaps.conferenceroombooking.utils

class Floor{

    companion object{
        fun FloorToString(floor:Int):String{
            when(floor) {
                1->return "st Floor"
                2->return "nd Floor"
                3->return "rd Floor"
                else->return "th Floor"
            }
        }
    }

}