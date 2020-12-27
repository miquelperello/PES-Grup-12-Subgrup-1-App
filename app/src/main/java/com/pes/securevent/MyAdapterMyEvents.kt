package com.pes.securevent

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row.view.*

class MyAdapterMyEvents(val arrayList: ArrayList<Model>, val context: Context, val past: Boolean) : RecyclerView.Adapter<MyAdapterMyEvents.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bindItems(model:Model){

            itemView.titleE.text = model.title
            itemView.descE.text = model._id
            itemView.imageE.setImageResource(model.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(arrayList[position])

        holder.itemView.setOnClickListener{
            val model = arrayList.get(position)

            val ETitle : String = model.title
            val EId : String = model._id

            val EImage : Int = model.image

            val ELoc : String = model.loc

            val EDate : String = model.date

            val EHour: String = model.hour
            val EPriceMin : String = model.minPrice
            val EPriceMax : String = model.maxPrice


            val esdeveniment : Intent = if(past)
                Intent(context, MyPastEvent::class.java)
            else
                Intent(context, MyEsdeveniment::class.java)

            esdeveniment.putExtra("ETitle", ETitle)
            esdeveniment.putExtra("EId", EId)
            esdeveniment.putExtra("EImage", EImage)
            esdeveniment.putExtra("ELoc", ELoc)
            esdeveniment.putExtra("EDate", EDate)
            esdeveniment.putExtra("EHour", EHour)
            esdeveniment.putExtra("EPriceMin", EPriceMin)
            esdeveniment.putExtra("EPriceMax", EPriceMax)

            context.startActivity(esdeveniment)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}


