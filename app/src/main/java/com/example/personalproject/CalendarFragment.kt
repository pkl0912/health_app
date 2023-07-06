package com.example.personalproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

data class AddCal(var date: String ?= null, var memo: String ?= null)
data class AddCalResponse(val body: String)
data class GetCal(val date: String?=null)
data class GetCalResponse(
    val date: String,
    val memo: String
)

data class ResponseWrapper2(
    val statusCode: Int,
    val body: String
)
class CalendarFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var calendarView: CalendarView
    private var selectedDate: String? = null
    private var isMemoVisible: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        var contextEditText = view.findViewById<EditText>(R.id.contextEditText)
        val saveButton = view.findViewById<Button>(R.id.saveBtn)
        contextEditText.visibility = View.INVISIBLE
        saveButton.visibility = View.INVISIBLE

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val clickedDate = "${dayOfMonth}/${month + 1}/${year}"
            if (clickedDate == selectedDate && isMemoVisible) {
                contextEditText.visibility = View.INVISIBLE
                saveButton.visibility = View.INVISIBLE
                isMemoVisible = false
            } else {
                selectedDate = clickedDate
                CoroutineScope(Dispatchers.IO).launch {
                    val memo = getMemoForDate(selectedDate)
                    withContext(Dispatchers.Main) {
                        if(memo != "not found"){
                            contextEditText.setText(memo)
                            contextEditText.visibility = View.VISIBLE
                            saveButton.visibility = View.INVISIBLE
                            isMemoVisible = true
                        } else {
                            contextEditText.setText("")
                            contextEditText.visibility = View.VISIBLE
                            saveButton.visibility = View.VISIBLE
                            isMemoVisible = true
                        }
                    }
                }
            }
        }

        saveButton.setOnClickListener {
            val memo = contextEditText.text.toString()
            saveMemoForDate(selectedDate, memo)
            Toast.makeText(requireContext(), "Memo saved for $selectedDate", Toast.LENGTH_SHORT).show()
        }
        return view
    }

    private suspend fun getMemoForDate(date: String?): String {
        var memoText = ""
        withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val json = Gson().toJson(GetCal(date))
            val host = "https://.amazonaws.com/default/getcalendar/"
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val req = Request.Builder().url(host ).post(json.toString().toRequestBody(mediaType)).build()
            client.newCall(req).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val str = response.body!!.string()
                val responseWrapper = Gson().fromJson(str, ResponseWrapper2::class.java)
                val data = Gson().fromJson(responseWrapper.body, GetCalResponse::class.java)
                memoText = data.memo
            }
        }
        return memoText
    }

    private fun saveMemoForDate(date: String?, memo: String) {
        val client = OkHttpClient()

        // create Add object with the current data
        val addData = AddCal(date, memo)
        val json = Gson().toJson(addData)

        val url = "https://79ylc5i8b9.execute-api.ap-northeast-1.amazonaws.com/default/addcalender/" // TODO: Replace with your actual add url
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val req = Request.Builder().url(url).post(json.toString().toRequestBody(mediaType)).build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val str = response.body!!.string()

                    val addResponse = Gson().fromJson(str, AddCalResponse::class.java)

                    CoroutineScope(Dispatchers.Main).launch {
                        if (addResponse.body == "success") {
                            Toast.makeText(activity, "Added successfully", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(activity, "Failed to add", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }
}






//companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment CalendarFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            CalendarFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
//}