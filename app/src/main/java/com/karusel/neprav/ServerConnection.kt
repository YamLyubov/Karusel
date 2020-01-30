package com.karusel.neprav

import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONException
import java.io.IOException

class ServerConnection(val activity: KaruselActivity){

    val client = OkHttpClient()

    fun getTopics() {
        val url = "http://192.168.6.103:5000/"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity.run {
                    runOnUiThread {
                        //null
                        Toast.makeText(this.applicationContext, e.message, Toast.LENGTH_LONG).show()

                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println("Connected $body")
                try {
                    val gson = GsonBuilder().create()
                    val new_topics = gson.fromJson(body, Topics::class.java)
                    activity.run {
                        runOnUiThread {
                            Toast.makeText(this.applicationContext, "Got it!", Toast.LENGTH_LONG).show()

                            //null
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }
}