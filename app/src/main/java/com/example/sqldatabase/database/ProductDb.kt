package com.example.sqldatabase.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.sqldatabase.database.Database.Companion.PRODUCT_DESCRIPTION
import com.example.sqldatabase.database.Database.Companion.PRODUCT_ID
import com.example.sqldatabase.database.Database.Companion.PRODUCT_IMAGE
import com.example.sqldatabase.database.Database.Companion.PRODUCT_NAME
import com.example.sqldatabase.database.Database.Companion.PRODUCT_PRICE
import com.example.sqldatabase.database.Database.Companion.PRODUCT_WEIGHT
import com.example.sqldatabase.database.Database.Companion.TABLE_PRODUCTS
import com.example.sqldatabase.models.ProductData

class ProductDb(context: Context) :
    SQLiteOpenHelper(context, TABLE_PRODUCTS, null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE $TABLE_PRODUCTS ("
                    + "$PRODUCT_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "$PRODUCT_NAME TEXT, "
                    + "$PRODUCT_PRICE TEXT, "
                    + "$PRODUCT_WEIGHT TEXT, "
                    + "$PRODUCT_DESCRIPTION TEXT, "
                    + "$PRODUCT_IMAGE BLOB)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        onCreate(db)
    }

//    -------------- insert the product--------------------
    fun addProduct(image: ByteArray, title: String, price: String, weight: String, description: String): Boolean{

        val db: SQLiteDatabase = this.writableDatabase
        val values = ContentValues()
        values.put(PRODUCT_NAME, title)
        values.put(PRODUCT_PRICE, price)
        values.put(PRODUCT_WEIGHT, weight)
        values.put(PRODUCT_DESCRIPTION, description)
        values.put(PRODUCT_IMAGE, image)

        val result = db.insert(TABLE_PRODUCTS, null, values)
        db.close()
        return result != -1L
    }

//    -----------------show the data--------------------
@SuppressLint("Range", "Recycle")
fun fetchProduct(): ArrayList<ProductData>{
        val productList = ArrayList<ProductData>()
        val db: SQLiteDatabase = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PRODUCTS", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(PRODUCT_ID))
                val image = cursor.getBlob(cursor.getColumnIndex(PRODUCT_IMAGE))
                val name = cursor.getString(cursor.getColumnIndex(PRODUCT_NAME))
                val price = cursor.getString(cursor.getColumnIndex(PRODUCT_PRICE))
                val weight = cursor.getString(cursor.getColumnIndex(PRODUCT_WEIGHT))
                val description = cursor.getString(cursor.getColumnIndex(PRODUCT_DESCRIPTION))

                val product = ProductData(id, image, name, price, weight, description)
                productList.add(product)
            } while (cursor.moveToNext())
        }
        return productList
    }

//    --------------------delete the data--------------------
    fun deleteProduct(id: Int){
        val db: SQLiteDatabase = this.writableDatabase
        db.delete(TABLE_PRODUCTS, "$PRODUCT_ID = ?", arrayOf(id.toString()))
        db.close()
    }

// ---------------------Restore a product----------------------
    fun restoreProduct(product: ProductData): Boolean {
        val db: SQLiteDatabase = this.writableDatabase
        val values = ContentValues()
        values.put(PRODUCT_ID, product.id)
        values.put(PRODUCT_NAME, product.name)
        values.put(PRODUCT_PRICE, product.price)
        values.put(PRODUCT_WEIGHT, product.weight)
        values.put(PRODUCT_DESCRIPTION, product.description)
        values.put(PRODUCT_IMAGE, product.image)

        val result = db.insert(TABLE_PRODUCTS, null, values)
        db.close()
        return result != -1L
    }

//    ------------------update a product--------------------
    fun updateProduct(product: ProductData): Boolean{
        val db:SQLiteDatabase = this.writableDatabase
        val values = ContentValues()
        values.put(PRODUCT_NAME, product.name)
        values.put(PRODUCT_PRICE, product.price)
        values.put(PRODUCT_DESCRIPTION, product.description)

        val result = db.update(TABLE_PRODUCTS, values, "$PRODUCT_ID = ?", arrayOf(product.id.toString()))
        db.close()

    return result.toLong() != -1L
    }
}