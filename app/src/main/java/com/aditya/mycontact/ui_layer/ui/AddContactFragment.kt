package com.aditya.mycontact.ui_layer.ui

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.navArgs
import com.aditya.mycontact.MainActivity
import com.aditya.mycontact.R
import com.aditya.mycontact.data_layer.model.Contact
import com.aditya.mycontact.data_layer.model.Resources
import com.aditya.mycontact.data_layer.util.Helper
import com.aditya.mycontact.data_layer.util.Helper.getBitmapByDrawable
import com.aditya.mycontact.data_layer.util.Helper.hideKeyboard
import com.aditya.mycontact.databinding.FragmentAddContactBinding
import com.aditya.mycontact.ui_layer.view_model.ContactViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.UUID


class AddContactFragment : Fragment() {

    private var _binding: FragmentAddContactBinding? = null
    private val binding: FragmentAddContactBinding get() = _binding!!

    private var contact:Contact?=null


    private val args: AddContactFragmentArgs by navArgs()


    private val myNavController: NavController by lazy {
        (requireActivity() as MainActivity).navController
    }
    private val viewModel: ContactViewModel by lazy {
        (requireActivity() as MainActivity).contactModel
    }


    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!
                    setImageOnProfileView(fileUri)

                }

                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }

            }

        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddContactBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObserver()
        initUi()
    }

    private fun subscribeToObserver() {
        viewModel.saveState.observe(viewLifecycleOwner) { response ->

            when (response) {
                is Resources.Success -> {
                    Helper.customToast(requireContext(), "Data is saved !", Toast.LENGTH_SHORT)

                    myNavController.popBackStack()

                }

                is Resources.Loading -> {
                    Helper.customToast(requireContext(), "Data is saving ... ", Toast.LENGTH_SHORT)
                }

                is Resources.Error -> {
                    Helper.customToast(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    )
                }
            }

        }
        viewModel.idContact.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resources.Success -> {
                    Helper.customToast(requireContext(), "Data is loaded !", Toast.LENGTH_SHORT)

                    lifecycleScope.launch {
                        response.data?.let { data ->
                            contact=data
                            binding.apply {
                                tiEtName.setText(data.name)
                                tiEtEmail.setText(data.email)
                                tiEtPhone.setText(data.phoneNumber)
                                data.image?.let {
                                    linearProfileView.isGone=false
                                    ivAddImage.isGone=true
                                    ivProfileView.setImageBitmap(
                                            BitmapFactory.decodeByteArray(
                                                it,
                                                0,
                                                it.size
                                            )
                                    )
                                }

                            }

                        }
                    }

                }

                is Resources.Loading -> {
                    Helper.customToast(requireContext(), "Data is Loading ... ", Toast.LENGTH_SHORT)
                }

                is Resources.Error -> {
                    Helper.customToast(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    )
                }
            }
        }

        viewModel.deleteContact.observe(viewLifecycleOwner){response ->
            when (response) {
                is Resources.Success -> {
                    Helper.customToast(requireContext(), "Data is deleted !", Toast.LENGTH_SHORT)

                    myNavController.popBackStack()

                }

                is Resources.Loading -> {
                    Helper.customToast(requireContext(), "Data is deleting ... ", Toast.LENGTH_SHORT)
                }

                is Resources.Error -> {
                    Helper.customToast(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    )
                }
            }
        }
    }

    private fun initUi() {

        binding.apply {
            args.id?.let {
                tvAddContact.text = "Update Contact"
                btnDelete.isGone = false
                viewModel.fetchContactById(it)
            }
        }
        setListener()
    }


    private fun setListener() {
        binding.run {
            ivProfileView.setOnClickListener {
                Helper.showDialog(requireActivity(), ivProfileView.getBitmapByDrawable())
            }
            icEditProfilePic.setOnClickListener {
                ImagePicker.with(this@AddContactFragment)
                    .crop()
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
            }
            ivAddImage.setOnClickListener {
                ImagePicker.with(this@AddContactFragment)
                    .crop()
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
            }

            icCheck.setOnClickListener {
                it.hideKeyboard()
                saveData()
            }

            icClose.setOnClickListener {
                myNavController.popBackStack()
            }

            btnDelete.setOnClickListener {
                contact?.let {
                    viewModel.deleteContact(it)
                }
            }

            setTextChangeListener(tiEtName, tiEtPhone, tiEtEmail)
        }
    }


    private fun setTextChangeListener(vararg view: TextInputEditText) {
        for (it in view) {
            it.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (allEditTextFilled()) {
                        binding.icCheck.apply {
                            setColorFilter(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.green
                                )
                            )
                            isClickable = true
                            isEnabled = true
                        }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })
        }
    }

    private fun setImageOnProfileView(fileUri: Uri) {
        binding.apply {
            ivAddImage.isGone = true
            linearProfileView.isGone = false
            ivProfileView.setImageURI(fileUri)
        }
    }

    private fun saveData() {
        if (validate()) {
            // here write to code save data to room data base
            val contact = Contact(
                UUID.randomUUID().toString(),
                binding.tiEtName.text.toString(),
                getByteArrayFromImage(),
                binding.tiEtPhone.text.toString(),
                binding.tiEtEmail.text.toString()
            )
            lifecycleScope.launch {
                viewModel.saveData(contact)
            }

        }
    }


    private fun validate(): Boolean {
        binding.run {

            resetAllPreviousError()


            if (!Patterns.PHONE.matcher(tiEtPhone.text!!).matches()) {
                tilPhone.error = "Please enter a valid phone number , Ex. 7895604568 !"
                return false
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(tiEtEmail.text!!).matches()) {
                tilEmail.error = "Please enter a valid email  , Ex. abc@gmail.com !"
                return false
            }
            return true
        }
    }

    private fun resetAllPreviousError() {
        binding.apply {
            tilEmail.error = null
            tilPhone.error = null
        }
    }

    private fun allEditTextFilled(): Boolean {
        return (binding.tiEtName.text?.isNotEmpty()
            ?: false) && (binding.tiEtEmail.text?.isNotEmpty()
            ?: false) && (binding.tiEtPhone.text?.isNotEmpty() ?: false)
    }

    private fun getByteArrayFromImage(): ByteArray? {
        if (binding.linearProfileView.isGone)
            return null
        val bitmap = (binding.ivProfileView.drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

}