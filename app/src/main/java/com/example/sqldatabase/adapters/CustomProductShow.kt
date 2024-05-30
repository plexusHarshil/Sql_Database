package com.example.sqldatabase.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sqldatabase.databinding.CustomShowDataBinding
import com.example.sqldatabase.models.ProductData

class CustomProductShow(private val context: Context, private val product: ArrayList<ProductData>):
    RecyclerView.Adapter<CustomProductShow.ViewHolder>() {

    class ViewHolder(val binding: CustomShowDataBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CustomShowDataBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = product[position]
        holder.binding.product = product

        val bitmap = BitmapFactory.decodeByteArray(product.image, 0, product.image.size)
        holder.binding.TvImageView.setImageBitmap(bitmap)
    }

    override fun getItemCount(): Int {
        return product.size
    }

    fun removeItem(position: Int){
        product.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(productData: ProductData, position: Int){
        product.add(position, productData)
        notifyItemInserted(position)
    }

}