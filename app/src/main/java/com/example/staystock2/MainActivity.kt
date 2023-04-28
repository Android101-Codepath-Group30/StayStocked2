package com.example.staystock2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
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
    private val productList = mutableListOf<Product>()

    // below is for search
    private lateinit var searchBar: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioBrand: RadioButton
    private lateinit var radioCategory: RadioButton

//    below is for api call

    var accessToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize the search bar and radio buttons
        searchBar = findViewById(R.id.search_bar)
        radioBrand = findViewById(R.id.radioBrand)
        radioCategory = findViewById(R.id.radioCategory)

        // Set up the listeners for search bar -Shi
       /* searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                updateUserProductItemQuery()
                true
            } else {
                false
            }
        }
*/

        ///Gaby search bar
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString()
                updateUserProductItemQuery(searchText)
            }

            override fun afterTextChanged(s: Editable?) {}
        })




        // Set up the listeners for radio buttons
        radioBrand.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateUserProductItemQuery()
            }
        }

        radioCategory.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                updateUserProductItemQuery()
            }
        }

       // initialize the recyclerview with the productadapter
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)



        // Get the authorization and update the product list for the first time
        getAuthorization()
        updateUserProductItemQuery()
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
                Log.d("Autho Error", "Error")
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle response
                Log.d("Autho OnResponse", "success")
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    accessToken = JSONObject(responseBody).getString("access_token")
                    Log.d("Autho Token", "$accessToken")

                    // Call updateUserProductItemQuery() here
                    runOnUiThread {
                        updateUserProductItemQuery()
                    }
                } else {
                    Log.d("Autho Response Error", "error")
                }
            }
        })
    }

    private fun getFoodInfo(searchTerm: String, filterBy: String){
//        val  userProductItemQuery = "food"
        val foodURL = "https://api.kroger.com/v1/products?filter.term=$searchTerm&filter.limit=30"
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

                    val length = data.length()
                    Log.d("Food", "$length")

                    //Parse data, Create Product class, and add to productList
                    for (i in 0 until data.length()) {
                        val item = data.getJSONObject(i)
                        val productId = item.optString("productId")
                        val productName = item.optString("description")
                        Log.d("ProductName", "Product Name: $productName")
                        val brandName = item.optString("brand")
                        val productSize = item.getJSONArray("items").getJSONObject(0).optString("size") // Use first item for size
                        val categoryId = item.optString("countryOrigin")
                        val category = categoryId // Replace this with an actual category lookup based on categoryId

                        val imageJSONObject = item.getJSONArray("images").getJSONObject(0)
                        val imageSizes = imageJSONObject.getJSONArray("sizes")
                        val imageInfo = imageSizes.getJSONObject(1)
                        val imageUrl = imageInfo.optString("url")

                        productList.add(Product(productId, productName, brandName, category, productSize, imageUrl))
                    }

                    //Shi's recycler view call
                   /* runOnUiThread {
                        recyclerView.adapter?.notifyDataSetChanged()
                    }*/

                    runOnUiThread {
                        val adapter = ProductAdapter(productList)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                        adapter.notifyDataSetChanged()
                    }


                }
            }
        })
    }

    private fun updateUserProductItemQuery( searchTerm: String = "food") {
        //For radio buttons
        val filterBy = if (radioBrand.isChecked) "brand" else "category"

        Log.d("Food search term","$searchTerm")
        getFoodInfo(searchTerm, filterBy)
    }
}
