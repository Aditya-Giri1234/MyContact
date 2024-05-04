package com.aditya.mycontact.ui_layer.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aditya.mycontact.data_layer.model.Contact
import com.aditya.mycontact.data_layer.model.Resources
import com.aditya.mycontact.data_layer.services.AddContactRepo
import kotlinx.coroutines.launch

class ContactViewModel(val app: Application) : AndroidViewModel(app) {

    private val repository = AddContactRepo(app)


    //region:: Save data to room database
    private val _saveState = MutableLiveData<Resources<Boolean>>()
    val saveState: LiveData<Resources<Boolean>> get() = _saveState

    fun saveData(contact: Contact) = viewModelScope.launch {
        runCatching {
            _saveState.postValue(Resources.Loading())
            repository.saveData(contact)
        }.onSuccess {
            if (it == 0L) {
                _saveState.postValue(Resources.Error("Data not inserted !"))
            } else {
                _saveState.postValue(Resources.Success(true))
            }
        }.onFailure {
            _saveState.postValue(Resources.Error(it.message.toString()))
        }
    }
    //endregion

    //region:: Fetch data from data base
    private val contactList: LiveData<List<Contact>> = repository.fetchContact()
    fun fetchContact() = contactList
    //endregion


    //region:: Delete Contact

    private val _deleteContact = MutableLiveData<Resources<Boolean>>()
    val deleteContact: LiveData<Resources<Boolean>> get() = _deleteContact

    fun deleteContact(contact: Contact) = viewModelScope.launch {
        runCatching {
            _deleteContact.postValue(Resources.Loading())
            repository.deleteContact(contact)
        }.onSuccess {
            if (it == 0) {
                _deleteContact.postValue(Resources.Error("Data not deleted !"))
            } else {
                _deleteContact.postValue(Resources.Success(true))
            }
        }.onFailure {
            _deleteContact.postValue(Resources.Error(it.message.toString()))
        }
    }

    //endregion


    //region:: Delete Contact

    private val _idContact = MutableLiveData<Resources<Contact>>()
    val idContact: LiveData<Resources<Contact>> get() = _idContact

    fun fetchContactById(id:String) = viewModelScope.launch {
        runCatching {
            _idContact.postValue(Resources.Loading())
            repository.fetchContactById(id)
        }.onSuccess {
            _idContact.postValue(Resources.Success(it))
        }.onFailure {
            _idContact.postValue(Resources.Error(it.message.toString()))
        }
    }

    //endregion


}