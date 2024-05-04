package com.aditya.mycontact.data_layer.db

import android.content.Context
import androidx.room.Room
import com.aditya.mycontact.data_layer.util.Constant

object DbBuilder {
    private var INSTANCE:ContactDatabase?=null

    fun getDatabase(context: Context) : ContactDatabase{
        if (INSTANCE==null){
            INSTANCE=
                Room.databaseBuilder(context,ContactDatabase::class.java, Constant.databaseName).fallbackToDestructiveMigration().build()
        }

        return INSTANCE!!
    }
}