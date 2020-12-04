package com.pes.securevent

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter


class SalaAdapter(context: Context, matrixList: List<Sala>) :
    BaseAdapter() {
    var context: Context
    lateinit var salaList: List<Sala>
    override fun getCount(): Int {
        return salaList.size
    }

    override fun getItem(i: Int): Any {
        return salaList[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View {
        return View.inflate(context, R.layout.grid_item, null)
    }

    init {
        this.context = context
        this.salaList = matrixList
    }
}