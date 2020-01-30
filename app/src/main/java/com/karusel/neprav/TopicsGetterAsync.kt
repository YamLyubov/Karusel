package com.karusel.neprav

import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

//не знаю работает или нет
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class TopicsGetterAsync(private var activity: KaruselActivity) : AsyncTask<String, String, String>() {

    override fun onPreExecute() {
        super.onPreExecute()
        Toast.makeText(activity?.applicationContext, "BEGINNING", Toast.LENGTH_SHORT).show()
    }
    override fun doInBackground(vararg url: String?): String? {
        var text: String
        val connection = URL(url[0]).openConnection() as HttpURLConnection
        
        try {
            connection.connect()
            text = connection.inputStream.use { it.reader().use{reader ->  reader.readText()}}
            TimeUnit.SECONDS.sleep(3)
        } finally {
            connection.disconnect()
        }
        return  text
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        Log.d("SAMPLETAG", "Конец")
        Toast.makeText(activity?.applicationContext, "ENDING", Toast.LENGTH_SHORT).show()
        handleJson(result)
    }

    private fun handleJson(result: String): Topics {
        val gson = GsonBuilder().create()
        return gson.fromJson(result, Topics::class.java)

    }

}
