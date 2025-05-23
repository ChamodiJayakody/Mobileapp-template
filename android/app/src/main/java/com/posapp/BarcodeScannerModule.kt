package com.posapp

import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat

class BarcodeScannerModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    // Remove the default constructor - ReactApplicationContext cannot be null
    // private val reactContext: ReactApplicationContext = reactContext

    private val SCAN_ACTION = "com.honeywell.action.BARCODE_DECODING_BROADCAST"
    private val DECODING_DATA = "com.honeywell.decode.data"
    private val DECODING_SOURCE = "com.honeywell.decode.source"

    private var receiver: BroadcastReceiver? = null

    override fun getName(): String {
        return "BarcodeScannerModule"
    }

    @ReactMethod
    fun startBarcodeScanner() {
        // Unregister existing receiver if any
        receiver?.let {
            reactApplicationContext.unregisterReceiver(it)
        }

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == SCAN_ACTION) {
                    val barcode = intent.getStringExtra(DECODING_DATA)
                    val source = intent.getStringExtra(DECODING_SOURCE)
                    
                    val params = Arguments.createMap().apply {
                        putString("barcode", barcode)
                        putString("source", source)
                    }

                    reactApplicationContext
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                        .emit("onBarcodeScanned", params)
                }
            }
        }

        // Register the receiver
        ContextCompat.registerReceiver(
            reactApplicationContext,
            receiver,
            IntentFilter(SCAN_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    @ReactMethod
    fun stopBarcodeScanner() {
        receiver?.let {
            reactApplicationContext.unregisterReceiver(it)
            receiver = null
        }
    }

    @ReactMethod
    fun addListener(eventName: String) {
        // Keep: Required for RN built-in Event Emitter Calls
    }

    @ReactMethod
    fun removeListeners(count: Int) {
        // Keep: Required for RN built-in Event Emitter Calls
    }

    override fun onCatalystInstanceDestroy() {
        super.onCatalystInstanceDestroy()
        stopBarcodeScanner()
    }
}