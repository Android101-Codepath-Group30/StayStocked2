package com.example.staystock2

import android.annotation.SuppressLint
import android.graphics.Color

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

    private val addedProductNames = mutableListOf<String>()

    private lateinit var tooltip: PopupWindow
    private lateinit var tooltipTextView: TextView

    // below is for recyclerview

    private lateinit var recyclerView: RecyclerView
    private val productList = mutableListOf<Product>()

    // below is for search
    private lateinit var searchBar: EditText

    // below is for api call

    var accessToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        builder = AlertDialog.Builder(this)


        // initialize the search bar and radio buttons
        searchBar = findViewById(R.id.search_bar)

        ///Gaby search bar
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString()
                updateUserProductItemQuery(searchText)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        
        // initialize the recyclerview with the productadapter
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = productAdapter
        recyclerView.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))



        // Set up the tooltip for the added items
        tooltipTextView = createTooltipTextView()
        tooltip = createTooltip()


        // Show the tooltip when the list button is clicked

        val tooltip = PopupWindow(tooltipTextView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            isOutsideTouchable = true
            isFocusable = true
        }

       // Show the tooltip when the list button is clicked

        findViewById<View>(R.id.list_tooltip).setOnClickListener {
            showTooltip(it)
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


    


   private fun getFoodInfo(searchTerm: String){
        val foodURL = "https://api.kroger.com/v1/products?filter.term=$searchTerm&filter.limit=50"

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

            @SuppressLint("NotifyDataSetChanged")
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

                        productAdapter.updateData(productList)
                        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                        recyclerView.adapter?.notifyDataSetChanged()
                    }

                   
                }
            }
        })
    }



    private fun updateUserProductItemQuery( searchTerm: String = "food") {
        
        // Clear the productList before making a new API call
        productList.clear()

        getFoodInfo(searchTerm)
    }

    private fun createTooltip(): PopupWindow {
        return PopupWindow(tooltipTextView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            isOutsideTouchable = true
            isFocusable = true
        }
    }

    private fun createTooltipTextView(): TextView {
        val tooltipText = addedProductNames.joinToString(separator = "\n")
        return TextView(this).apply {
            text = tooltipText
            setPadding(
                resources.getDimensionPixelOffset(R.dimen.spacing_small),
                resources.getDimensionPixelOffset(R.dimen.spacing_small),
                resources.getDimensionPixelOffset(R.dimen.spacing_small),
                resources.getDimensionPixelOffset(R.dimen.spacing_small)
            )
            setBackgroundResource(R.drawable.tooltip_background) // Create a custom background for the tooltip
        }
    }

    private fun showTooltip(anchorView: View) {
        val tooltipText = if (addedProductNames.isEmpty()) {
            "No items added" // Show a default message if the list is empty
        } else {
            addedProductNames.joinToString(separator = "\n")
        }

        tooltipTextView.apply {
            text = tooltipText // update the tooltip text
            setTextColor(Color.BLACK) // set the text color to black
            setBackgroundColor(Color.WHITE) // set the background color to white
        }

        tooltip.showAsDropDown(anchorView, 0, 0, Gravity.TOP or Gravity.START)
    }

    private val productAdapter = ProductAdapter(this, mutableListOf()) { productName ->
        addedProductNames.add(productName)
        Toast.makeText(this, "$productName added to list", Toast.LENGTH_SHORT).show()
        showTooltip(findViewById(R.id.list_tooltip))
    }
}
