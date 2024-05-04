package com.aditya.mycontact.data_layer.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aditya.mycontact.ui_layer.ui.MyDialog
import kotlin.math.roundToInt


object Helper {

    private var toast: Toast? = null



    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(this.windowToken, 0)
    }


    fun showDialog(activity: Activity, bitmap: Bitmap) {
        val dialog = MyDialog(bitmap)
        dialog.show((activity as AppCompatActivity).supportFragmentManager, "MyDialog")
    }

    fun ImageView.getBitmapByDrawable(): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun customToast(context: Context, msg: String, duration: Int) {
        toast?.cancel()
        toast = Toast.makeText(context,msg,duration)
        toast?.show()
    }

    fun getInitial(name: String): String {
        val str: Array<String> = name.trim().split(" ").toTypedArray()
        return if (str.size >= 2) {
            "${str[0][0]}${str[1][0]}"
        } else {
            "${str[0][0]}"
        }
    }
}