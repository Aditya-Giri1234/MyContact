package com.aditya.mycontact.data_layer.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aditya.mycontact.data_layer.dao.ContactDao
import com.aditya.mycontact.data_layer.model.Contact
import com.aditya.mycontact.data_layer.util.Constant


@Database(entities = [Contact::class], version = 2, exportSchema = false)
abstract class ContactDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}