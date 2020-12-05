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
            if (seatList[i].occupied == 1)
                return View.inflate(context, R.layout.seat_item_occ, null)
            return View.inflate(context, R.layout.seat_item, null)
        }

        init {
            this.context = context
            this.seatList = seatList
        }
}