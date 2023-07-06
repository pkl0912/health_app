package com.example.personalproject

import android.annotation.SuppressLint
import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


data class DataModel (var location:LocationState, var current:Weather) {
    data class Weather(
        var temp_c: Double? = null,
        var wind_mph: Double? = null,
        var condition: Condition)
    data class Condition(var text: String ?= null)
    data class LocationState(var lat: Double? = null, var lon: Double? = null)
}
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)
        val userId = arguments?.getString(MainActivity.EXT_ID)
        val editView = view.findViewById<EditText>(R.id.editTextTextPersonName4)
        val btn = view.findViewById<Button>(R.id.button2)
        val textView = view.findViewById<TextView>(R.id.textView8)
        val imageView = view.findViewById<ImageView>(R.id.imageView)
        imageView.setImageResource(R.drawable.weather)

        val client = OkHttpClient()
        val host = "https://api.weatherapi.com/v1/current.json"

        btn.setOnClickListener {
            val api_key=""
            val find_city=editView.text
            val path = "?key=" + api_key + "&q=" + find_city
            val req = Request.Builder().url(host+path).build()
            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use{
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")
                        val data = response.body!!.string()
                        val dat_parse = Gson().fromJson(data, DataModel::class.java)
//                        val find_lat= dat_parse.location.lat!!
//                        val find_lon= dat_parse.location.lon!!
                        val condition_find=dat_parse.current.condition.text
                        val wind_mph=dat_parse.current.wind_mph
                        val temp_c=dat_parse.current.temp_c

                        if(condition_find.toString() =="Clear"){
                            CoroutineScope(Dispatchers.Main).launch {
                                textView.text = "Try walking 10000 steps!"
                                imageView.setImageResource(R.drawable.clear)

                            }
                        }else{
                            CoroutineScope(Dispatchers.Main).launch {
                                textView.text = "Try doing stretching"
                                imageView.setImageResource(R.drawable.clear)
                            }

                        }
                    }
//                        CoroutineScope(Dispatchers.Main).launch {
//                            var concat: String =
//                                "Current " + find_city.toString() + " City weather is .."+
//                                        condition_find.toString() + " and wind_mph & temperature is " +
//                                        wind_mph.toString() + ", " + temp_c.toString()
//                            Toast.makeText(activity, concat, Toast.LENGTH_LONG).show()
//                        }

                }
            })
        }






        return view
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(userId: String, userPwd: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(MainActivity.EXT_ID, userId)
                    putString(MainActivity.EXT_PWD, userPwd)
                }
            }
    }
}