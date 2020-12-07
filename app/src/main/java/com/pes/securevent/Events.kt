package com.pes.securevent

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_events.recyclerView
import org.json.JSONException

class Events : Fragment() {
    private var requestQueue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = "https://securevent.herokuapp.com/events"
        requestQueue = Volley.newRequestQueue(activity?.applicationContext)
        val arrayList = ArrayList<Model>()
        println("MATRIX : : : ")
        val request = JsonArrayRequest(Request.Method.GET, url, null, { response ->
           try {
               for (i in 0 until response.length()) {
                   val event = response.getJSONObject(i)
                   arrayList.add(
                           Model(
                                   event.getString("name"),
                                   event.getString("_id"),
                                   R.drawable.icon,
                                   event.getString("id_room"),
                                   event.getString("date"),
                                   event.getString("hourIni"),
                                   event.getString("minPrice"),
                                   event.getString("maxPrice")
                           )
                   )

               }

               val myAdapter = (activity?.applicationContext?.let { MyAdapter(arrayList, it) })

               recyclerView.layoutManager = LinearLayoutManager(activity)
               recyclerView.adapter = myAdapter


           } catch (e: JSONException) {
               e.printStackTrace()
           }
        }, { error -> error.printStackTrace() })

        requestQueue?.add(request)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    class MyDrawerLayout : DrawerLayout {
        constructor(context: Context?) : super(context!!) {}
        constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
        constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle) {}

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            var widthMeasureSpec = widthMeasureSpec
            var heightMeasureSpec = heightMeasureSpec
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY)
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY)
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}