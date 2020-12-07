package com.pes.securevent

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_esdeveniment.*

class MyEsdeveniment : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myesdeveniment)

        val actionBar : ActionBar? = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        val intent = intent
        val ETitle = intent.getStringExtra("ETitle")
        val EId = intent.getStringExtra("EId")
        val EImageView = intent.getIntExtra("EImage", 0)
        val ELoc = intent.getStringExtra("ELoc")
        val EDate = intent.getStringExtra("EDate")
        val EHour = intent.getStringExtra("EHour")
        val EPriceMin = intent.getStringExtra("EPriceMin")
        val EPriceMax = intent.getStringExtra("EPriceMax")

        actionBar.setTitle(ETitle)
        titleE.text = ETitle
        IDE.text = EId
        imageE.setImageResource(EImageView)
        LocE.text= ELoc
        DateE.text = EDate
        HourE.text = EHour
        MinPriceE.text = EPriceMin
        MaxPriceE.text = EPriceMax

    }





}