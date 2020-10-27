package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row.view.*

class MyAdapter(val arrayList: ArrayList<Model>, val context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bindItems(model:Model){

            itemView.titleTv.text = model.title
            itemView.descriptionTv.text = model.des
            itemView.imageIv.setImageResource(model.image)
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
            var EDesc : String = model.des

            var EImage : Int = model.image

            var ELocalitzacio : String = model.localitzacio

            var EData : String = model.data

            var EHora: String = model.hora
            var EPreu : String = model.preu


            val intent = Intent(context, Esdeveniment::class.java)

            intent.putExtra("ETitle", ETitle)
            intent.putExtra("EDesc", EDesc)
            intent.putExtra("EImage", EImage)
            intent.putExtra("ELocalitzacio", ELocalitzacio)
            intent.putExtra("EData", EData)
            intent.putExtra("EHora", EHora)
            intent.putExtra("EPreu", EPreu)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}


