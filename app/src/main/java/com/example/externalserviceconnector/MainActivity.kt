package com.example.externalserviceconnector

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private var boundServiceMessenger: Messenger? = null
    private var isBound = false
    private val messenger = Messenger(ClientHandler(this))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intentForMcuService = Intent()
        intentForMcuService.component = ComponentName("com.example.messengerservicebase", "com.example.messengerservicebase.ImageService")
        bindService(intentForMcuService, serviceConnection, BIND_AUTO_CREATE)
    }

    fun onBindClick(view: View) {
        val message = Message.obtain(null, 1).apply {
            replyTo = messenger
            data = Bundle().apply {
                putString(
                        "link",
                        "https://img.icons8.com/ios/452/service.png" // Заглушка
                )
            }
        }
        boundServiceMessenger?.send(message)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            boundServiceMessenger = Messenger(service)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            boundServiceMessenger = null
            isBound = false
        }
    }
}

private class ClientHandler(val context: MainActivity) : Handler() {
    override fun handleMessage(message: Message) {
        when (message.what) {
            2 -> {
                context.findViewById<TextView>(R.id.textLoad).text =
                        message.data.getString(
                                "response",
                                "nope"
                        )
            }
        }
    }

}