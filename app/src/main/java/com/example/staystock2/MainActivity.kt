package com.example.staystock2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.*
import org.json.JSONObject
import java.io.IOException



class MainActivity : AppCompatActivity() {

//    below is for recyclerview

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchText: String
    private val productList = mutableListOf<Product>()

//    below is for api call

    var accessToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize the recyclerview with the productadapter
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ProductAdapter(productList)

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

    private fun getFoodInfoAsy(){
        val  userProductItemQuery = "milk"
        val foodURL = "https://api.kroger.com/v1/products?filter.term=$userProductItemQuery&filter.item=10"
        val client = AsyncHttpClient()

        client[foodURL, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Headers,
                json: JsonHttpResponseHandler.JSON
            ) {
                Log.d("Autho Food JSON", "query response successful $json")

                val dataObject = json.jsonObject.getJSONObject("data")


            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("Autho Food Error", errorResponse)
            }
        }]

    }


   private fun getFoodInfo(){
        val  userProductItemQuery = "milk"
        val foodURL = "https://api.kroger.com/v1/products?filter.term=$userProductItemQuery&filter.item=10"
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
                    // Parse the response to JSON Object
                    Log.d("Autho Food Response success", "$responseBody")
                    val jsonObject = JSONObject(responseBody)
                    Log.d("Autho Food Json", "$jsonObject")

                    //Get Data
                    //Update the onResponse method in getFoodInfo()
                    // to populate the productList with the data from the API and update the RecyclerView:
                    val data = jsonObject.getJSONArray("data")

                    for (i in 0 until data.length()) {
                        val item = data.getJSONObject(i)

                        val productName = item.optString("description")
                        val brandName = item.optString("brand")
                        val productSize = item.getJSONArray("items").getJSONObject(0).optString("size") // Use first item for size
                        val categoryId = item.optString("categoryId")
                        val category = "Category" // Replace this with an actual category lookup based on categoryId

                        val imageJSONObject = item.getJSONArray("images").getJSONObject(0)
                        val imageSizes = imageJSONObject.getJSONArray("sizes")
                        val imageInfo = imageSizes.getJSONObject(1)
                        val imageUrl = imageInfo.optString("url")

                        productList.add(Product(productName, brandName, productSize, imageUrl, category, imageUrl))
                    }

                    runOnUiThread {
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
//                    Log.d("Autho Food data", "$data")
//
//                    //Get info from first item out of 10
//                    val firstItem = data.getJSONObject(0);
//                    Log.d("Autho Food First Item", "$firstItem")
//
//                    //Get productName
//                    val productName = firstItem.optString("description").toString()
//                    Log.d("Autho Food Description", "$productName")
//
//                    //Get BrandName
//                    val brandName = firstItem.optString("brand").toString()
//                    Log.d("Autho Food Description", "$brandName")
//
//                    //Price is missing in jsonobject
//                    val price = firstItem.getJSONArray("items")
//                    Log.d("Autho Price", "$price")
//
//                    //image
//                    val imageJSONObject = firstItem.getJSONArray("images").getJSONObject(0)
//                    val imageSizes = imageJSONObject.getJSONArray("sizes")
//                    val imageInfo = imageSizes.getJSONObject(1)
//                    val imgURL = imageInfo.optString("url")
//                    Log.d("Autho Image JSON Object", "$imageJSONObject")
//                    Log.d("Autho Image Sizes", "$imageSizes")
//                    Log.d("Autho Image Thumbnail Info", "$imageInfo")
//                    Log.d("Autho Image url", "$imgURL")
//
//                } else {
//                    // Handle error
//                    Log.d("Autho Food Response Error", "error")
                }
            }
        })
    }
}