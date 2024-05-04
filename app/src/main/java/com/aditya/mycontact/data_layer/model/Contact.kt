package com.aditya.mycontact.data_layer.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aditya.mycontact.data_layer.util.Constant


@Entity(
    tableName = Constant.CONTACT
)
data class Contact(
    @PrimaryKey(false)
    val id:String,
    val name:String,
    val image:ByteArray?=null,
    val phoneNumber:String,
    val email:String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Contact

        if (id != other.id) return false
        if (name != other.name) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false
        if (phoneNumber != other.phoneNumber) return false
        return email == other.email
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (image?.contentHashCode() ?: 0)
        result = 31 * result + (phoneNumber?.hashCode() ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        return result
    }
}
