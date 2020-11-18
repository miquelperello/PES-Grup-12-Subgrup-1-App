package com.pes.securevent

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pes.securevent.MainActivity.Companion.LlistaEvents
import com.pes.securevent.MainActivity.Companion.UsuariActiu
import kotlinx.android.synthetic.main.fragment_events.*
import kotlinx.android.synthetic.main.fragment_my_events.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyEvents.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyEvents : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }



    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(com.pes.securevent.R.layout.fragment_my_events, container, false)
        // Inflate the layout for this fragment
        if (UsuariActiu){
            val arrayList = ArrayList<Model>()
            for (i in LlistaEvents){
                arrayList.add(Model("holaa", "aasasas", 0, "asas", "1212", "12", "11", "11"))
            }
            var recyclerViewEE : RecyclerView = view.findViewById(com.pes.securevent.R.id.recyclerViewE)


            recyclerViewEE.layoutManager = LinearLayoutManager(activity)
            var myAdapter_ =  MyAdapter(arrayList, requireContext())
            recyclerViewEE.adapter = myAdapter_
                    


        }
        else{}

        return inflater.inflate(com.pes.securevent.R.layout.fragment_my_events, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyEvents.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MyEvents().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}