package com.example.sqldatabase.activitys

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sqldatabase.adapters.CustomProductShow
import com.example.sqldatabase.database.ProductDb
import com.example.sqldatabase.databinding.ActivityShowProductDataBinding
import com.example.sqldatabase.databinding.UpdateDataDialogBinding
import com.example.sqldatabase.models.ProductData
import com.google.android.material.snackbar.Snackbar

class ShowProductDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowProductDataBinding
    private lateinit var productDb: ProductDb
    private lateinit var productAdapter: CustomProductShow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityShowProductDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productDb = ProductDb(this)

        setUpRecyclerView()
        setUpSwipeToDelete()
    }

    private fun setUpRecyclerView() {
        val productList = productDb.fetchProduct()
        productAdapter = CustomProductShow(this, productList)
        binding.RvShowData.layoutManager = LinearLayoutManager(this)
        binding.RvShowData.adapter = productAdapter
    }

    private fun setUpSwipeToDelete(){
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when(direction){
                    ItemTouchHelper.LEFT -> confirmUpdateDataDialog(position)
                    ItemTouchHelper.RIGHT -> confirmDeleteDataDialog(position)
                }
            }
        })
            .attachToRecyclerView(binding.RvShowData)
    }

    private fun confirmDeleteDataDialog(position: Int){
        AlertDialog.Builder(this)
            .setTitle("Delete Data")
            .setMessage("Are you sure you want to delete this data?")
            .setPositiveButton("Delete") { _, _ ->
                Log.d("ShowDataTAG", "delete data")
                deleteData(position)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                Log.d("ShowDataTAG", "cancel")
                productAdapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteData(position: Int){
        val product = productDb.fetchProduct()[position]
        productDb.deleteProduct(product.id)
        productAdapter.removeItem(position)
        Snackbar.make(binding.root, "Data Deleted", Snackbar.LENGTH_LONG)
            .setAction("Restore"){
                productDb.restoreProduct(product)
                productAdapter.restoreItem(product, position)
            }
            .show()
        setUpRecyclerView()
    }

    private fun confirmUpdateDataDialog(position: Int){
        val updateDialog = UpdateDataDialogBinding.inflate(layoutInflater)
        val product = productDb.fetchProduct()[position]
        updateDialog.updateTitle.setText(productDb.fetchProduct()[position].name)
        updateDialog.updatePrice.setText(productDb.fetchProduct()[position].price)
        updateDialog.updateDesc.setText(productDb.fetchProduct()[position].description)

        AlertDialog.Builder(this)
            .setTitle("Update Data")
            .setMessage("Are you sure you want to udpdate this data?")
            .setView(updateDialog.root)
            .setPositiveButton("Update") { _, _ ->
                val name = updateDialog.updateTitle.text.toString()
                val price = updateDialog.updatePrice.text.toString()
                val desc = updateDialog.updateDesc.text.toString()
                val updateProduct = ProductData(product.id, product.image, name, price, product.weight, desc)
                val isUpdated = productDb.updateProduct(updateProduct)
                if (isUpdated) {
                    productAdapter.notifyItemChanged(position)
                    Log.d("UpdateTAG", "Data Updated")
                    Toast.makeText(this, "Data Updated", Toast.LENGTH_SHORT).show()
                } else{
                    Log.d("UpdateTAG", "Failed to Update Data")
                    Toast.makeText(this, "Failed to Update Data", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}