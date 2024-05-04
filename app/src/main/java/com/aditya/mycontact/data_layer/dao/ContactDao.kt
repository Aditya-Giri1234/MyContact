package com.aditya.mycontact.data_layer.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.aditya.mycontact.data_layer.model.Contact
import com.aditya.mycontact.data_layer.util.Constant


@Dao
interface ContactDao {

    @Upsert
    suspend fun upsert(contact: Contact): Long

    @Query(" Select * from ${Constant.CONTACT}")
    fun getAllContact(): LiveData<List<Contact>>

    @Query("SELECT * FROM ${Constant.CONTACT} WHERE id = :id")
    suspend fun getContactById(id: String): Contact

    @Delete
    suspend fun deleteContact(contact:Contact):Int

}