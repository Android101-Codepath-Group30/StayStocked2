package com.example.staystock2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import android.view.Gravity
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration


class MainActivity : AppCompatActivity() {
    lateinit var builder: AlertDialog.Builder

    private lateinit var productAdapter: ProductAdapter
    private val addedProductNames = mutableListOf<String>()

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
        builder = AlertDialog.Builder(this)

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

        // Set up the tooltip for the added items
        val tooltipText = addedProductNames.joinToString(separator = "\n")
        val tooltipTextView = TextView(this).apply {
            text = tooltipText
            setPadding(
                resources.getDimensionPixelOffset(R.dimen.spacing_small),
                resources.getDimensionPixelOffset(R.dimen.spacing_small),
                resources.getDimensionPixelOffset(R.dimen.spacing_small),
                resources.getDimensionPixelOffset(R.dimen.spacing_small)
            )
            setBackgroundResource(R.drawable.tooltip_background) // Create a custom background for the tooltip
        }

        val tooltip = PopupWindow(tooltipTextView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            isOutsideTouchable = true
            isFocusable = true
        }

       // Show the tooltip when the list button is clicked
        findViewById<View>(R.id.list_tooltip).setOnClickListener {
            tooltip.showAsDropDown(it, 0, 0, Gravity.TOP or Gravity.START)
        }

        // Set up the adapter for the recyclerview
        recyclerView.adapter = ProductAdapter(this, productList) { productName ->
            addedProductNames.add(productName)
            Toast.makeText(this, "$productName added to list", Toast.LENGTH_SHORT).show()
        }

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


                    this@MainActivity.runOnUiThread {
                        val productAdapter = ProductAdapter(this@MainActivity, productList) { productName ->
                            addedProductNames.add(productName)
                            Toast.makeText(this@MainActivity, "$productName added to list", Toast.LENGTH_SHORT).show()
                        }
                        recyclerView.adapter = productAdapter
                        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                        productAdapter.notifyDataSetChanged()
                    }

                }
            }
        })
    }

    private fun updateUserProductItemQuery( searchTerm: String = "food") {
        //For radio buttons
        val filterBy = if (radioBrand.isChecked) "brand" else "category"

        // Clear the productList before making a new API call
        productList.clear()

        getFoodInfo(searchTerm, filterBy)
    }
}
