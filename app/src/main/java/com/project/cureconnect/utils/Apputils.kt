package com.project.ecommerceLocal.utils


import android.content.Context
import android.os.Message
import android.widget.Toast


object Apputils {
    fun showToast(context: Context, message: String){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show()
    }
}