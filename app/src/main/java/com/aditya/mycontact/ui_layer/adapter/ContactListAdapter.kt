package com.aditya.mycontact.ui_layer.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aditya.mycontact.data_layer.model.Contact
import com.aditya.mycontact.data_layer.util.Helper
import com.aditya.mycontact.databinding.SampleContactItemBinding

class ContactListAdapter(val onClick:(contact:Contact)->Unit) : RecyclerView.Adapter<ContactListAdapter.ViewHolder>() {

    private val differ = AsyncListDiffer(this, callback)

    companion object {
        val callback = object : DiffUtil.ItemCallback<Contact>() {
            override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem == newItem
            }

        }
    }

    fun submitList(list: ArrayList<Contact>) {
        differ.submitList(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: SampleContactItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Contact) {
            binding.apply {
                data.let {
                    if (it.image == null) {
                        ivProfile.isGone = true
                        tvShortName.isGone = false
                        tvShortName.text = Helper.getInitial(it.name)
                    } else {
                        ivProfile.isGone = false
                        tvShortName.isGone = true
                        ivProfile.setImageBitmap(
                            BitmapFactory.decodeByteArray(
                                it.image,
                                0,
                                it.image.size
                            )
                        )
                    }
                    tvProfileName.text=it.name
                    tvPhoneNumber.text=it.phoneNumber
                }
                root.setOnClickListener {
                    onClick(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            SampleContactItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }
}