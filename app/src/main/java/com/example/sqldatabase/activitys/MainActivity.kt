package com.example.sqldatabase.activitys

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sqldatabase.R
import com.example.sqldatabase.database.ProductDb
import com.example.sqldatabase.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageUri: Uri
    private lateinit var productDb: ProductDb
    private var imageByteArray: ByteArray? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var captureImageLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        productDb = ProductDb(this)

        selectWeight()
        initClickListener()
        imageCapture()

    }

    private fun imageCapture(){
//      ----------------image pick from gallery--------------------
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){
            if (it.resultCode == RESULT_OK){
                imageUri = it.data?.data!!
                binding.addImageView.setImageURI(imageUri)
                val stream = contentResolver.openInputStream(imageUri)
                imageByteArray = stream?.readBytes()
            }
        }
//      ----------------image pick from camera--------------------
        captureImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == RESULT_OK){
                val bitmapImage = it.data?.extras?.get("data") as Bitmap
                binding.addImageView.setImageBitmap(bitmapImage)
                val stream = ByteArrayOutputStream()
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                imageByteArray = stream.toByteArray()
            }
        }
    }

    private fun initClickListener() {
        binding.btnAddImage.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
        binding.btnShowData.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.btnAddImage.id -> {
                showImagePickerDialog()
            }
            binding.btnSubmit.id -> {
                saveProduct()
            }
            binding.btnShowData.id -> {
                val intent = Intent(this, ShowProductDataActivity::class.java)
                startActivity(intent)
            }
        }
    }

    //   ------------ spinner weight ------------
    private fun selectWeight() {
        val productSize = arrayOf(
            "Weight","1 Kg", "2 Kg", "3 Kg", "4 Kg",
            "5 Kg", "6 Kg", "7 Kg", "8 Kg", "9 Kg", "10 Kg"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, productSize)
        binding.productSize.adapter = adapter

    }
// ----------------Image add dialog----------------
    private fun showImagePickerDialog() {
        val options = arrayOf("Camera", "Gallery")
        AlertDialog.Builder(this)
            .setTitle("Select Image From")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun openGallery() {
        val iGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(iGallery)
    }

    private fun openCamera() {
        val iCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        captureImageLauncher.launch(iCamera)
    }

//    -------------- save product ------------------
    private fun saveProduct() {
        val productName = binding.productTitle.text.toString()
        val productPrice = binding.productPrice.text.toString()
        val productSize = binding.productSize.selectedItem.toString()
        val productDescription = binding.productDesc.text.toString()

        if (imageByteArray != null){
            val product = productDb.addProduct(imageByteArray!!, productName, productPrice, productSize, productDescription)
            if (product){
                Toast.makeText(this, "Product Added", Toast.LENGTH_SHORT).show()
                Log.d("TestTAG", "Successfully added")
                resetForm()
            } else {
                Log.d("TestTAG", "Failed to add product")
                Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d("TestTAG", "Failed to image load")
            Toast.makeText(this, "Failed to image load", Toast.LENGTH_SHORT).show()
        }
    }

//    ------------- reset form -----------------
    private fun resetForm(){
        binding.productTitle.text?.clear()
        binding.productPrice.text?.clear()
        binding.productDesc.text?.clear()
        binding.addImageView.setImageResource(R.drawable.add_photo)
        binding.productSize.setSelection(0)
        imageByteArray = null
        binding.productTitle.requestFocus()
    }


}