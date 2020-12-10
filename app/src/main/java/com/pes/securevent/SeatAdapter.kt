package com.pes.securevent

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter


class SeatAdapter(context: Context, seatList: List<Seat>) :

    BaseAdapter() {

        var context: Context
        var seatList: List<Seat>

        override fun getCount(): Int {
            return seatList.size
        }

        override fun getItem(i: Int): Any {
            return seatList[i]
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        @SuppressLint("ViewHolder")
        override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
            if (seatList[i].occupied == 'F')
                return View.inflate(context, R.layout.seat_item_occ, null)
            else if (seatList[i].occupied == 'T')
                return View.inflate(context, R.layout.seat_item_free, null)
            else if (seatList[i].occupied == 'U')
                return View.inflate(context, R.layout.seat_item_user, null)
            return View.inflate(context, R.layout.seat_item_covid, null)
        }

        init {
            this.context = context
            this.seatList = seatList
        }
}