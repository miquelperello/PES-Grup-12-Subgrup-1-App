package com.pes.securevent

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row.view.*

class MyAdapter(val arrayList: ArrayList<Model>, val context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bindItems(model:Model){

            itemView.titleE.text = model.title
            itemView.descE.text = model._id

            if(model.image.isEmpty())
                Picasso.get().load(R.drawable.icon).into(itemView.imageE)
            else
                Picasso.get().load(model.image).into(itemView.imageE)
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

            val EImage : String = model.image

            val ELoc : String = model.loc

            val EDate : String = model.date

            val EHour: String = model.hour

            val EHourEnd: String = model.hourEnd

            val EPriceMin : String = model.minPrice

            val EPriceMax : String = model.maxPrice

            val Elogo : String = model.image


            val esdeveniment = Intent(context, Esdeveniment::class.java)

            esdeveniment.putExtra("ETitle", ETitle)
            esdeveniment.putExtra("EId", EId)
            esdeveniment.putExtra("EImage", EImage)
            esdeveniment.putExtra("ELoc", ELoc)
            esdeveniment.putExtra("EDate", EDate)
            esdeveniment.putExtra("EHourEnd", EHourEnd)
            esdeveniment.putExtra("EHour", EHour)
            esdeveniment.putExtra("EPriceMin", EPriceMin)
            esdeveniment.putExtra("EPriceMax", EPriceMax)
            esdeveniment.putExtra("Elogo", Elogo)
            esdeveniment.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(esdeveniment)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}


