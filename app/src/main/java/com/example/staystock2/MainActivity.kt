package com.example.staystock2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.*
import org.json.JSONObject
import java.io.IOException



class MainActivity : AppCompatActivity() {

    var accessToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getAuthorization()
    }

    private fun getAuthorization() {

        val clientID = getString(R.string.CLIENT_ID)
        val clientSecret = getString(R.string.CLIENT_SECRET)

        val client = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("grant_type", "client_credentials")
            .add("scope", "product.compact")
            .build()

        val credentials = Credentials.basic(clientID, clientSecret)
        val request = Request.Builder()
            .url("https://api.kroger.com/v1/connect/oauth2/token")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("Authorization", credentials)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle error
                Log.d("Autho Error", "Error" )
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle response
                Log.d("Autho OnResponse", "success")
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    accessToken = JSONObject(responseBody).getString("access_token")
                    Log.d("Autho Token", "$accessToken")
                    getFoodInfo()
                } else {
                    Log.d("Autho Response Error", "error")
                }

            }
        })


    }


   private fun getFoodInfo(){
        val foodURL = "https://api.kroger.com/v1/products?filter.term=milk&filter.item=3"
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(foodURL)
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $accessToken")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                Log.d("Autho Food", "failure")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    // Parse the JSON response
                    Log.d("Autho Food Response success", "$responseBody")
                } else {
                    // Handle error
                    Log.d("Autho Food Response Error", "error")
                }
            }
        })
    }
}