package com.pes.securevent

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row.view.*

class MyAdapter(val arrayList: ArrayList<Model>, val context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

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

            var ETitle : String = model.title
            var EId : String = model._id

            var EImage : Int = model.image

            var ELoc : String = model.loc

            var EDate : String = model.date

            var EHour: String = model.hour
            var EPriceMin : String = model.minPrice
            var EPriceMax : String = model.maxPrice


            val esdeveniment = Intent(context, Esdeveniment::class.java)

            esdeveniment.putExtra("ETitle", ETitle)
            esdeveniment.putExtra("EDesc", EId)
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


