package com.aditya.mycontact.ui_layer.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aditya.mycontact.MainActivity
import com.aditya.mycontact.R
import com.aditya.mycontact.data_layer.model.Contact
import com.aditya.mycontact.data_layer.model.Resources
import com.aditya.mycontact.data_layer.util.Helper
import com.aditya.mycontact.data_layer.util.Helper.hideKeyboard
import com.aditya.mycontact.databinding.FragmentContactListBinding
import com.aditya.mycontact.ui_layer.adapter.ContactListAdapter
import com.aditya.mycontact.ui_layer.view_model.ContactViewModel
import kotlin.math.abs
import kotlin.math.roundToInt


class ContactListFragment : Fragment() {

    private var _binding: FragmentContactListBinding? = null
    private val binding: FragmentContactListBinding get() = _binding!!

    private val contactList = ArrayList<Contact>()
    private var currentCallData: Contact? = null

    private val Int.dp
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            toFloat(), resources.displayMetrics
        ).roundToInt()

    private val contactListAdapter: ContactListAdapter by lazy {
        ContactListAdapter() {
            val action: NavDirections =
                ContactListFragmentDirections.actionContactListFragment2ToAddContactFragment2(it.id)
            myNavController.navigate(action)
        }
    }
    private val viewModel: ContactViewModel by lazy {
        (requireActivity() as MainActivity).contactModel
    }

    private val myNavController: NavController by lazy {
        (requireActivity() as MainActivity).navController
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                makeCall(currentCallData!!)
            } else {
                Helper.customToast(
                    requireContext(),
                    "Please give phone call permission for making a call , you can give permission via app setting !",
                    Toast.LENGTH_LONG
                )
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
        _binding = FragmentContactListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initUi()
        subscribeToObserver()

    }

    private fun subscribeToObserver() {
        viewModel.fetchContact().observe(viewLifecycleOwner) {
            Log.e("Data", "Contact List :-> $it")
            contactList.clear()
            showRecycleView(it.size)
            if (it.isNotEmpty()) {
                contactList.addAll(it)
                contactListAdapter.submitList(ArrayList<Contact>(it.reversed()))
            }else{
                contactListAdapter.submitList(ArrayList())
            }
        }
        viewModel.deleteContact.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resources.Success -> {
                    Helper.customToast(requireContext(), "Data is deleted !", Toast.LENGTH_SHORT)

                }

                is Resources.Loading -> {
                    Helper.customToast(
                        requireContext(),
                        "Data is deleting ... ",
                        Toast.LENGTH_SHORT
                    )
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

    private fun showRecycleView(size: Int) {
        binding.apply {
            svContact.isGone = size <= 7
            rvContact.isGone=size==0
            linearNoContact.isGone=size!=0
        }
    }

    private fun initUi() {
        binding.apply {
            fabBtn.setOnClickListener {
                val action: NavDirections =
                    ContactListFragmentDirections.actionContactListFragment2ToAddContactFragment2(
                        null
                    )
                myNavController.navigate(action)
            }
            rvContact.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = contactListAdapter
                addItemDecoration(
                    DividerItemDecoration(
                        requireContext(),
                        LinearLayoutManager.VERTICAL
                    )
                )

                setItemTouchListener()

            }
        }



        setListener()
    }

    private fun setItemTouchListener() {
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val height = (displayMetrics.heightPixels / displayMetrics.density).toInt().dp
        val width = (displayMetrics.widthPixels / displayMetrics.density).toInt().dp

        val deleteIcon = resources.getDrawable(R.drawable.delete, null)
        val callIcon = resources.getDrawable(R.drawable.call, null)

        val deleteColor = resources.getColor(android.R.color.holo_red_light)
        val callColor = resources.getColor(android.R.color.holo_green_light)


        val swipeHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            //more code here
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.LEFT) {
                    performCall(contactList[viewHolder.adapterPosition])
                } else {
                    performDelete(viewHolder.adapterPosition)
                }
            }
            

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {


                //clear view if dx is zero
                if (dX == 0f) {
                    clearView(recyclerView, viewHolder)
                    return
                }

                //1. Background color based upon direction swiped
                when {
                    abs(dX) < width / 3 -> canvas.drawColor(Color.GRAY)
                    dX > width / 3 -> canvas.drawColor(deleteColor)
                    else -> canvas.drawColor(callColor)
                }

                //2. Printing the icons
                val iconMargin = resources.getDimension(com.intuit.sdp.R.dimen._16sdp)
                    .roundToInt()
                deleteIcon.bounds = Rect(
                    iconMargin,
                    viewHolder.itemView.top + iconMargin + 8.dp,
                    iconMargin + deleteIcon.intrinsicWidth,
                    viewHolder.itemView.top + deleteIcon.intrinsicHeight
                            + iconMargin + 8.dp
                )
                // Calculate positions for the right icon based on dX
                val iconTop = viewHolder.itemView.top + (viewHolder.itemView.height - callIcon.intrinsicHeight) / 2
                val iconBottom = iconTop + callIcon.intrinsicHeight
                val callIconLeft = (viewHolder.itemView.right - iconMargin - callIcon.intrinsicWidth - dX).coerceAtLeast(viewHolder.itemView.left.toFloat())
                val callIconRight = (viewHolder.itemView.right - iconMargin - dX).coerceAtMost(viewHolder.itemView.right.toFloat())
                callIcon.setBounds(callIconLeft.toInt(), iconTop, callIconRight.toInt(), iconBottom)


                //3. Drawing icon based upon direction swiped
                if (dX > 0) deleteIcon.draw(canvas) else callIcon.draw(canvas)


                super.onChildDraw(
                    canvas,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        })

        swipeHelper.attachToRecyclerView(binding.rvContact)
    }

    private fun performCall(contact: Contact) {
        currentCallData = contact
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            makeCall(contact)
        } else {
            permissionLauncher.launch(
                Manifest.permission.CALL_PHONE
            )
        }
    }

    private fun makeCall(contact: Contact) {
        Intent(
            Intent.ACTION_CALL,
            Uri.parse("tel:${contact.phoneNumber}")
        ).also(::startActivity)
    }


    private fun performDelete(position: Int) {
        val contact = contactList[position]
        viewModel.deleteContact(contact)
        contactList.remove(contact)
    }

    private fun setListener() {
        binding.svContact.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.svContact.hideKeyboard()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == null) {
                    contactListAdapter.submitList(contactList)
                } else {
                    filter(newText.trim())
                }

                return true
            }

        })
    }

    private fun filter(text: String) {

        val tempList = ArrayList<Contact>()

        contactList.forEach {
            if (it.name.contains(text) || it.email.contains(text) || it.phoneNumber.contains(text)) {
                tempList.add(it)
            }
        }
        contactListAdapter.submitList(tempList)

    }

}