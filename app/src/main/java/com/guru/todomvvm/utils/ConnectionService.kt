package com.guru.todomvvm.utils

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.guru.todomvvm.R
import com.guru.todomvvm.ui.todoList.TodoListActivity


class ConnectionService : Service() {
    var manager: NotificationManager? = null
    private val channelId = "Notification from Service"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(
                        channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT
                )
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                    channel
            )
        }
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Let it continue running until it is stopped.
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        val receiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (CONNECTIVITY_CHANGE_ACTION == action) {
                    //check internet connection
                    if (!ConnectionHelper.isConnectedOrConnecting(context)) {
                        if (context != null) {
                            var show = false
                            if (ConnectionHelper.lastNoConnectionTs == -1L) { //first time
                                show = true
                                ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis()
                            } else {
                                if (System.currentTimeMillis() - ConnectionHelper.lastNoConnectionTs > 1000) {
                                    show = true
                                    ConnectionHelper.lastNoConnectionTs = System.currentTimeMillis()
                                }
                            }
                            if (show && ConnectionHelper.isOnline) {
                                ConnectionHelper.isOnline = false
                                Log.i("NETWORK123", "Connection lost")
                                notify("App is Offline")
                                //manager.cancelAll();
                            }
                        }
                    } else {
                        Log.i("NETWORK123", "Connected")
                        notify("App is Online")
                        // Perform your actions here
                        ConnectionHelper.isOnline = true
                    }
                }
            }
        }
        registerReceiver(receiver, filter)
        return START_STICKY
    }

    fun notify(title: String) {
        val notificationIntent = Intent(this, TodoListActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
                this,
                0, notificationIntent, 0
        )
        val notification: Notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        const val CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    }
}