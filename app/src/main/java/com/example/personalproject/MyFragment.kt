package com.example.personalproject

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.personalproject.MainActivity.Companion.EXT_ID
import com.example.personalproject.MainActivity.Companion.EXT_PWD
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


data class Add(var name: String ?= null, var kg: String ?= null, var sex: String ?= null )
data class AddResponse(val body: String)
data class GetUserInfo(val name: String?=null)
data class GetUserInfoResponse(
    val name: String,
    val kg: String,
    val sex: String
)

data class ResponseWrapper(
    val statusCode: Int,
    val body: String
)


class MyFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var nameTextView: TextView
    private lateinit var kgTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var saveButton: Button

    private var isEditMode: Boolean = false

    private fun addUser(name: String, kg: String, sex: String) {
        val client = OkHttpClient()

        // create Add object with the current data
        val addData = Add(name, kg, sex)
        val json = Gson().toJson(addData)

        val url = "https://.amazonaws.com/default/userinfo/" // TODO: Replace with your actual add url
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

                    val addResponse = Gson().fromJson(str, AddResponse::class.java)

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
    private fun enableTextViews() {
        nameTextView.visibility = View.VISIBLE
        kgTextView.visibility = View.VISIBLE
        genderTextView.visibility = View.VISIBLE

        val nameEditText = view?.findViewById<EditText>(R.id.editTextTextPersonName)
        val kgEditText = view?.findViewById<EditText>(R.id.editTextTextPersonName2)
        val genderEditText = view?.findViewById<EditText>(R.id.editTextTextPersonName3)

        nameTextView.text = nameEditText?.text.toString()
        kgTextView.text = kgEditText?.text.toString()
        genderTextView.text = genderEditText?.text.toString()

        nameEditText?.visibility = View.GONE
        kgEditText?.visibility = View.GONE
        genderEditText?.visibility = View.GONE
    }
    private fun enableEditTexts() {
        nameTextView.visibility = View.GONE
        kgTextView.visibility = View.GONE
        genderTextView.visibility = View.GONE

        val nameEditText = view?.findViewById<EditText>(R.id.editTextTextPersonName)
        val kgEditText = view?.findViewById<EditText>(R.id.editTextTextPersonName2)
        val genderEditText = view?.findViewById<EditText>(R.id.editTextTextPersonName3)

        nameEditText?.setText(nameTextView.text)
        kgEditText?.setText(kgTextView.text)
        genderEditText?.setText(genderTextView.text)

        nameEditText?.visibility = View.VISIBLE
        kgEditText?.visibility = View.VISIBLE
        genderEditText?.visibility = View.VISIBLE
    }
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
        val view = inflater.inflate(R.layout.fragment_my, container, false)
        val userId = arguments?.getString(EXT_ID)


        nameTextView = view.findViewById(R.id.textViewName)
        kgTextView = view.findViewById(R.id.textViewKG)
        genderTextView = view.findViewById(R.id.textViewGender)
        saveButton = view.findViewById(R.id.button)


        val client = OkHttpClient()
        val json = Gson().toJson(GetUserInfo(userId))
        val host = "https://.amazonaws.com/default/getuserinfo/"
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val req = Request.Builder().url(host ).post(json.toString().toRequestBody(mediaType)).build()
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {


                e.printStackTrace()

            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val str = response.body!!.string()
                    val responseWrapper = Gson().fromJson(str, ResponseWrapper::class.java)
                    val data = Gson().fromJson(responseWrapper.body, GetUserInfoResponse::class.java)

                    CoroutineScope(Dispatchers.Main).launch {

                        nameTextView.text = userId
                        kgTextView.text = data.kg
                        genderTextView.text = data.sex
                    }


                }

            }
        })
        saveButton.setOnClickListener {
            if (isEditMode) {

                enableTextViews()
                saveButton.text = "edit"
                //db에 추가하는 코드 짜기
                addUser(nameTextView.text.toString(), kgTextView.text.toString(), genderTextView.text.toString())



            } else {
                enableEditTexts()
                saveButton.text = "save"
            }
            isEditMode = !isEditMode
        }

        return view
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(userId: String, userPwd: String) =
            MyFragment().apply {
                arguments = Bundle().apply {
                    putString(EXT_ID, userId)
                    putString(EXT_PWD, userPwd)
                }
            }
    }
}