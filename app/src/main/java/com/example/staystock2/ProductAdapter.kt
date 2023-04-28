package com.example.staystock2

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductAdapter(private val context: Context, private val products: MutableList<Product>, private val onAddProductClick: (productName: String) -> Unit) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.product_image)
        val productName: TextView = view.findViewById(R.id.product_name)
        val productBrand: TextView = view.findViewById(R.id.product_brand)
        val productSize: TextView = view.findViewById(R.id.product_size)
        val category: TextView = view.findViewById(R.id.category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        val cleanedProductName = product.name.replace(Regex("[^A-Za-z0-9 ]"), "")
        holder.productName.text = cleanedProductName
        holder.category.text = product.category
        holder.productBrand.text = product.brand
        holder.productSize.text = product.size
        Glide.with(holder.itemView.context).load(product.imageUrl).into(holder.productImage)

        holder.itemView.findViewById<Button>(R.id.add_to_list_button).setOnClickListener {
            onAddToList(product.name)
        }
    }

    private fun onAddToList(name: String) {
        val message = context.getString(R.string.added_to_list, name)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        onAddProductClick(name) // Add this line to call the lambda function passed to the adapter
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun updateData(newData: List<Product>) {
        Log.d("AdapterUpdate", "Updating adapter data")
        products.clear()
        products.addAll(newData)
        notifyDataSetChanged()
    }
}