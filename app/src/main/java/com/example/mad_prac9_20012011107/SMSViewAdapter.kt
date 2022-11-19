package com.example.mad_prac9_20012011107

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.SMSView
import com.example.mad_prac9_20012011107.databinding.SmsItemViewBinding

class SMSViewAdapter(context: Context, private val array:ArrayList<SMSView>):
        ArrayAdapter<SMSView>(context,array.size, array) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val currentsms: SMSView? = getItem(position)
        val binding = SmsItemViewBinding.inflate(LayoutInflater.from(context))
        binding.textview.text = currentsms!!.phoneno
        binding.textview2.text = currentsms.msg
        return binding.root
    }
}



