package com.awesomeproject

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.google.firebase.messaging.FirebaseMessaging

class NotificationModule(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    companion object {
        private const val NAME = "Notification"
        var notificationToken: String? = null

        const val PUSH_MESSAGES = "messages"
        const val PUSH_SUBSCRIPTION = "subscription"

        const val ACTION_DELETED = "deleted"
        const val ACTION_RECEIVED = "received"
        const val ACTION_OPENED = "opened"

        // to standardise between Android & iOS
        private val STATUSES = hashMapOf(
            "denied" to 1,
            "authorized" to 2
        )
        private val ACTIONS = hashMapOf(
            ACTION_DELETED to ACTION_DELETED,
            ACTION_RECEIVED to ACTION_RECEIVED,
            ACTION_OPENED to ACTION_OPENED
        )
        private val TYPES = hashMapOf(PUSH_MESSAGES to PUSH_MESSAGES, PUSH_SUBSCRIPTION to PUSH_SUBSCRIPTION)
    }

    private lateinit var notificationManagerCompat: NotificationManagerCompat
    override fun initialize() {
        super.initialize()
        notificationManagerCompat = NotificationManagerCompat.from(reactContext)
    }

    override fun getName(): String {
        return NAME
    }

    override fun getConstants(): MutableMap<String, Any> {
        val constants = HashMap<String, Any>()
        constants["statuses"] = STATUSES
        constants["actions"] = ACTIONS
        constants["types"] = TYPES
        return constants
    }

    @ReactMethod
    fun isNotificationPermissionGranted(promise: Promise) {
        val status = when (notificationManagerCompat.areNotificationsEnabled()) {
            true -> STATUSES["authorized"]
            false -> STATUSES["denied"]
        }
        promise.resolve(status)
    }

    @ReactMethod
    fun requestPermission() {
        val intent = Intent().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, reactContext.packageName)
            } else {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.parse("package:".plus(reactContext.packageName))
            }
        }
        reactContext.currentActivity?.startActivity(intent)
    }

    @ReactMethod
    fun getNotificationToken(promise: Promise) {
        if (notificationToken.isNullOrBlank()) {
            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                promise.resolve(it)
            }
        } else {
            promise.resolve(notificationToken)
        }
    }
}
