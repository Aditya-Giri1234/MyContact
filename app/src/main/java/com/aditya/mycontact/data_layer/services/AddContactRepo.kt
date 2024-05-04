package com.aditya.mycontact.data_layer.services

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.RoomDatabase
import com.aditya.mycontact.data_layer.db.ContactDatabase
import com.aditya.mycontact.data_layer.db.DbBuilder
import com.aditya.mycontact.data_layer.model.Contact

class AddContactRepo(private val context: Context) {
    private val database: ContactDatabase = DbBuilder.getDatabase(context)


    suspend fun saveData(contact: Contact)= database.contactDao().upsert(contact = contact)

    fun fetchContact()=database.contactDao().getAllContact()

    suspend fun deleteContact(contact: Contact)=database.contactDao().deleteContact(contact)

    suspend fun fetchContactById(id:String)=database.contactDao().getContactById(id)

}