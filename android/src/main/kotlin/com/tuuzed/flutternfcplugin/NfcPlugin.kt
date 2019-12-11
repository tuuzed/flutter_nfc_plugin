package com.tuuzed.flutternfcplugin

import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry

class NfcPlugin {

    companion object {

        private const val CHANNEL_PREFIX = "com.tuuzed.flutternfcplugin"
        /** 方法通道 */
        private const val METHOD_CHANNEL_NAME = "$CHANNEL_PREFIX/MethodChannel"
        /** 事件通道 */
        private const val EVENT_CHANNEL_NAME = "$CHANNEL_PREFIX/EventChannel"

        @JvmStatic
        fun registerWith(registrar: PluginRegistry.Registrar) {

            val methodChannel = MethodChannel(registrar.messenger(), METHOD_CHANNEL_NAME)
            val eventChannel = EventChannel(registrar.messenger(), EVENT_CHANNEL_NAME)

            val handler = PluginHandlerImpl(registrar)

            methodChannel.setMethodCallHandler(handler)
            eventChannel.setStreamHandler(handler)

        }

    }


}