package com.aditya.mycontact.data_layer.util

object Constant {

    //region:: Table name

    const val CONTACT="Contact"
    //endregion


    //region:: Column name

    enum class ContactColumnName(val columnName:String){
        Id("id"),
        Name("name"),
        PhoneNumber("phoneNumber" ) ,
        Email("email")
    }

    //endregion

    //region:: Database name

    const val databaseName="contact.db"

    //endregion




}