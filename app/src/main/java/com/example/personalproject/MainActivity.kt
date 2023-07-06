package com.example.personalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class MainActivity : AppCompatActivity() {
    companion object{
        const val EXT_ID = "extra_user_Id"
        const val EXT_PWD = "extra_user_pwd"
    }

    data class Register(var id: String ?= null,var pwd: String ?= null )
    data class ResponseBody(val success: String)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val loginbtn = findViewById<Button>(R.id.btn_login)


        val client = OkHttpClient()
        val host = "https://amazonaws.com/default/userslogin/"
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val et1 = findViewById<EditText>(R.id.editId)
        val et2 = findViewById<EditText>(R.id.editPwd)

        loginbtn.setOnClickListener{


            val userId = et1.text.trim().toString()
            val userPwd = et2.text.trim().toString()

            val json = Gson().toJson(Register(userId, userPwd))
            Log.d("request json", json.toString())

            val req = Request.Builder().url(host).post(json.toString().toRequestBody(mediaType)).build()

            client.newCall(req).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {


                    e.printStackTrace()

                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")
                        val str = response.body!!.string()
                        val data = Gson().fromJson(str, ResponseBody::class.java)

                        Log.d("data", data.toString())

                        if(data.success=="true" ){



                            val intent = Intent(this@MainActivity, HomeActivity::class.java).apply{
                                putExtra(EXT_ID, userId)
                                putExtra(EXT_PWD, userPwd)
                            }
                            startActivity(intent)
                        }else{

                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(applicationContext, "fail", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                }
            })

        }

    }
}