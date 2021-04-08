package io.github.tuuzed.flutter_nfc_plugin

import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

/** FlutterNfcPlugin */
class FlutterNfcPlugin : FlutterPlugin, ActivityAware {

    companion object {
        private const val CHANNEL_PREFIX = "io.github.tuuzed.flutter_nfc_plugin"

        /** 方法通道 */
        private const val METHOD_CHANNEL_NAME = "$CHANNEL_PREFIX/MethodChannel"

        /** 事件通道 */
        private const val EVENT_CHANNEL_NAME = "$CHANNEL_PREFIX/EventChannel"
    }

    private var activityPluginBinding: ActivityPluginBinding? = null

    private lateinit var methodChannel: MethodChannel
    private lateinit var eventChannel: EventChannel

    override fun onAttachedToEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        val handler = PluginHandler(binding.applicationContext) { activityPluginBinding?.activity }

        methodChannel = MethodChannel(binding.binaryMessenger, METHOD_CHANNEL_NAME)
        methodChannel.setMethodCallHandler(handler)

        eventChannel = EventChannel(binding.binaryMessenger, EVENT_CHANNEL_NAME)
        eventChannel.setStreamHandler(handler)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        methodChannel.setMethodCallHandler(null)
        eventChannel.setStreamHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        this.activityPluginBinding = binding
    }

    override fun onDetachedFromActivityForConfigChanges() {
        this.activityPluginBinding = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        this.activityPluginBinding = binding
    }

    override fun onDetachedFromActivity() {
        this.activityPluginBinding = null
    }


}
