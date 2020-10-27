package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import kotlinx.android.synthetic.main.activity_esdeveniment.*

class Esdeveniment : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_esdeveniment)

        val actionBar : ActionBar? = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.setDisplayShowHomeEnabled(true)

        var intent = intent
        val ETitle = intent.getStringExtra("ETitle")
        val EDesc = intent.getStringExtra("EDesc")
        val EImageView = intent.getIntExtra("EImage", 0)

        actionBar.setTitle(ETitle)
        titleTv.text = ETitle
        descriptionTv.text = EDesc
        imageIv.setImageResource(EImageView)
    }
}