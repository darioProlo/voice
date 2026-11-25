package com.example.northway_wake_word

import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import com.example.northway_wake_word.tray.TrayActivity
import com.example.northway_wake_word.tray.TrayConfig
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.spokestack.spokestack.util.EventTracer
import io.spokestack.tray.SpokestackTrayListener

/** NorthwayWakeWordPlugin */
class NorthwayWakeWordPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {

    private val ok = false
    private val message = "This is a message from spokestack config success"

    private lateinit var channel: MethodChannel


    inner class SpokeStack : SpokestackTrayListener {
        fun getTrayConfig(): TrayConfig {
            return TrayConfig.Builder()
                // substitute your own credentials
                .credentials(
                    "f0bc990c-e9db-4a0c-a2b1-6a6395a3d97e",
                    "5BD5483F573D691A15CFA493C1782F451D4BD666E39A9E7B2EBE287E6A72C6B6"
                )
                .wakewordModelURL("https://d3dmqd7cy685il.cloudfront.net/model/wake/spokestack")
                .nluURL("https://s.spokestack.io/u/7fYxV")
                .logLevel(EventTracer.Level.PERF.value())
                .withListener(this)
                .greeting(message)
                .build()
        }

         fun getTrayListener(): SpokestackTrayListener {
            TODO("Not yet implemented")
        }


    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "northway_wake_word")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {

        SpokeStack().getTrayConfig()


        if (call.method == "getPlatformVersion") {
            result.success(message)
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        TODO("Not yet implemented")
    }

    override fun onDetachedFromActivityForConfigChanges() {
        TODO("Not yet implemented")
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        TODO("Not yet implemented")
    }

    override fun onDetachedFromActivity() {
        TODO("Not yet implemented")
    }

}
