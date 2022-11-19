package com.example.mad_prac9_20012011107

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsManager
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.SMSView
import com.example.mad_prac9_20012011107.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val SMS_PERMISSION_CODE = 110
    private lateinit var binder: ActivityMainBinding
    private lateinit var al: ArrayList<SMSView>
    private lateinit var lv: ListView
    private lateinit var smsrecevier: smsboradcastrecevier

    private fun requestSMSPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_SMS
            )
        ) {
        }
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS
            ),
            SMS_PERMISSION_CODE
        )
    }

    private fun loadSMSInbox() {
        if (!checkRequestPermission()) return
        val uriSMS = Uri.parse("content://sms/inbox")
        val c = contentResolver.query(uriSMS, null, null, null, null)
        al.clear()
        while (c!!.moveToNext()) {
            al.add(SMSView(c.getString(2), c.getString(12)))
        }
        lv.adapter = SMSViewAdapter(this, al)
    }

    private fun checkRequestPermission(): Boolean {
        return isSMSReadPermission && isSMSWritePermission
    }

    private val isSMSReadPermission: Boolean
        get() = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
    private val isSMSWritePermission: Boolean
        get() = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)
        al = ArrayList()
        lv = binder.listview1
        if (checkRequestPermission()) {
            loadSMSInbox()
        } else {
            requestSMSPermission()
        }
        smsrecevier = smsboradcastrecevier()
        registerReceiver(smsrecevier, IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
        smsrecevier.listner = ListnerImplement()
        binder.sendButton.setOnClickListener {
            val phone = binder.phoneno.text.toString()
            val msg = binder.message.text.toString()
            sendsms(phone,msg)
            val builder: androidx.appcompat.app.AlertDialog.Builder =
                androidx.appcompat.app.AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Sent SMS")
            builder.setMessage("SMS is sent.\nPhone No : $phone \n\n Message : $msg")
            builder.setCancelable(true)
            builder.setPositiveButton("OK", null);
            builder.show()
        }
    }
    fun sendsms(sPhoneNo: String?, sMsg: String?) {
        if (!checkRequestPermission()) {
            return
            //toast msg
        } else {
            requestSMSPermission()
        }
        val smsmanager = SmsManager.getDefault()
        if (smsmanager != null) {
            smsmanager.sendTextMessage(sPhoneNo, null, sMsg, null, null)
            //toast msg for ack meg sent
        }

    }
    inner class ListnerImplement: smsboradcastrecevier.Lister {
        override fun onTextReceived(sPhoneNo: String?, sMsg: String?) {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("new sms recevied")
            builder.setMessage("$sPhoneNo\n$sMsg")
            builder.setCancelable(true)
            builder.show()
            loadSMSInbox()
        }

    }
    override fun onDestroy() {

        super.onDestroy()
        unregisterReceiver(smsrecevier)
    }
}
