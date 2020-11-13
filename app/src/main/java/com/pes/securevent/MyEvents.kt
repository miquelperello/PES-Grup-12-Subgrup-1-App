package com.pes.securevent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pes.securevent.MainActivity.Companion.LlistaEvents
import com.pes.securevent.MainActivity.Companion.UsuariActiu
import kotlinx.android.synthetic.main.fragment_events.*
import kotlinx.android.synthetic.main.fragment_my_events.*
import kotlinx.android.synthetic.main.fragment_my_events.recyclerViewE
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

        if (UsuariActiu){
            val arrayList = ArrayList<Model>()
            for (i in LlistaEvents){
            arrayList.add(Model(i, "", 0, "", "", "", "", "" ))
            }
            val myAdapter = (getActivity()?.getApplicationContext()?.let { MyAdapter(arrayList, it) })
            //var recyclerViewE = view?.findViewById(R.id.recyclerViewE) as RecyclerView
            val recyclerViewE : RecyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerViewE)

            recyclerViewE.layoutManager = LinearLayoutManager(activity)
            recyclerViewE.adapter = myAdapter
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false)
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