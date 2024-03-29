package com.github.tuuzed.flutter_nfc_plugin.internal

import android.app.Activity
import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Handler
import android.os.Looper
import androidx.annotation.Keep
import androidx.annotation.UiThread
import com.github.tuuzed.flutter_nfc_plugin.internal.tech.ISO14443A
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class NfcPluginHandler(
    applicationContext: Context,
    private val activityProvider: () -> Activity?
) : MethodChannel.MethodCallHandler, EventChannel.StreamHandler, NfcAdapter.ReaderCallback {

    companion object {
        private const val TAG = "NfcPlugin"
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private val nfcAdapter = NfcAdapter.getDefaultAdapter(applicationContext)

    private inline fun runOnUiThread(crossinline block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            mainHandler.post { block() }
        }
    }

    private var args = Args()
    private var sink: EventChannel.EventSink? = null

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        log(TAG, "call: ${call.method}, args: ${call.arguments}")
        when (call.method) {
            "enableReaderMode" -> enableReaderMode(call, result)
            "disableReaderMode" -> disableReaderMode(result)
            "readTag" -> readTag(call, result)
            "writeTag" -> writeTag(call, result)
            "cancel" -> cancel(result)
            else -> result.notImplemented()
        }
    }

    override fun onListen(args: Any?, sink: EventChannel.EventSink?) {
        this.sink = sink
    }

    override fun onCancel(args: Any?) {
        sink = null
    }

    override fun onTagDiscovered(tag: Tag?) {
        tag ?: return
        log(TAG, "tag: $tag, args: $args")
        runOnUiThread { doHandleTag(tag) }
    }

    @UiThread
    private fun doHandleTag(tag: Tag) {
        val foundTag = mapOf(
            "type" to TagResultType.foundTag.name,
            "hexId" to HexStringUtils.bytesToHexString(tag.id),
            "techList" to tag.techList.joinToString(","),
            "success" to true
        )
        log(TAG, "toFlutter: $foundTag")
        sink?.success(foundTag)
        if (args.cancel) return
        if (args.readTagArgs.isNotEmpty()) {
            val dataList = mutableListOf<Map<String, *>>()
            for (arg in args.readTagArgs) {
                val key = HexStringUtils.hexStringToBytes(arg.key)
                val rst = when (arg.keyType) {
                    KeyType.keyA -> ISO14443A.readByKeyA(tag, arg.sector, arg.block, key)
                    KeyType.keyB -> ISO14443A.readByKeyB(tag, arg.sector, arg.block, key)
                }
                if (rst != null) {
                    dataList.add(
                        mapOf(
                            "sector" to arg.sector,
                            "block" to arg.block,
                            "hexData" to HexStringUtils.bytesToHexString(rst)
                        )
                    )
                } else {
                    sink?.success(
                        mapOf(
                            "type" to TagResultType.readTag.name,
                            "hexId" to HexStringUtils.bytesToHexString(tag.id),
                            "techList" to tag.techList.joinToString(","),
                            "success" to false
                        )
                    )
                    return
                }
            }
            sink?.success(
                mapOf(
                    "type" to TagResultType.readTag.name,
                    "hexId" to HexStringUtils.bytesToHexString(tag.id),
                    "techList" to tag.techList.joinToString(","),
                    "success" to true,
                    "dataList" to dataList
                )
            )
            return
        }
        if (args.writeTagArgs.isNotEmpty()) {
            for (arg in args.writeTagArgs) {
                val key = HexStringUtils.hexStringToBytes(arg.key)
                val data = HexStringUtils.hexStringToBytes(arg.data)
                val rst = when (arg.keyType) {
                    KeyType.keyA -> ISO14443A.writeByKeyA(tag, arg.sector, arg.block, key, data)
                    KeyType.keyB -> ISO14443A.writeByKeyB(tag, arg.sector, arg.block, key, data)
                }
                if (rst == null || !rst) {
                    sink?.success(
                        mapOf(
                            "type" to TagResultType.writeTag.name,
                            "hexId" to HexStringUtils.bytesToHexString(tag.id),
                            "techList" to tag.techList.joinToString(","),
                            "success" to false
                        )
                    )
                    return
                }
            }
            sink?.success(
                mapOf(
                    "type" to TagResultType.writeTag.name,
                    "hexId" to HexStringUtils.bytesToHexString(tag.id),
                    "techList" to tag.techList.joinToString(","),
                    "success" to true
                )
            )
            return
        }
    }

    private fun enableReaderMode(call: MethodCall, result: MethodChannel.Result) {
        val flags = call.argument<Int>("flags")
        if (flags == null) {
            log(TAG, "flags == null")
            result.success(false)
            return
        }
        val activity = activityProvider()
        if (activity == null) {
            log(TAG, "activity == null")
            result.success(false)
            return
        }
        nfcAdapter.enableReaderMode(activity, this, flags, null)
        result.success(true)
    }

    private fun disableReaderMode(result: MethodChannel.Result) {
        val activity = activityProvider()
        if (activity == null) {
            log(TAG, "activity == null")
            result.success(false)
            return
        }
        nfcAdapter.disableReaderMode(activity)
        result.success(true)
    }

    private fun readTag(call: MethodCall, result: MethodChannel.Result) {
        val readTagArgs = call.arguments<List<Map<String, *>>>().map {
            val sector = it["sector"] as Int?
            val block = it["block"] as Int?
            val keyType = (it["keyType"] as String?)?.let { kt -> KeyType.valueOf(kt) }
            val hexKey = it["hexKey"] as String?
            if (sector == null) {
                log(TAG, "sector == null")
                result.success(false)
                return
            }
            if (block == null) {
                log(TAG, "block == null")
                result.success(false)
                return
            }
            if (keyType == null) {
                log(TAG, "keyType == null")
                result.success(false)
                return
            }
            if (hexKey == null) {
                log(TAG, "hexKey == null")
                result.success(false)
                return
            }
            ReadTagArg(sector, block, keyType, hexKey)
        }
        runOnUiThread {
            args.readTagArgs = readTagArgs
            args.writeTagArgs = emptyList()
            args.cancel = false
            result.success(true)
        }
    }

    private fun writeTag(call: MethodCall, result: MethodChannel.Result) {
        val writeTagArgs = call.arguments<List<Map<String, *>>>().map {
            val sector = it["sector"] as Int?
            val block = it["block"] as Int?
            val keyType = (it["keyType"] as String?)?.let { kt -> KeyType.valueOf(kt) }
            val hexKey = it["hexKey"] as String?
            val hexData = it["hexData"] as String?
            if (sector == null) {
                log(TAG, "sector == null")
                result.success(false)
                return
            }
            if (block == null) {
                log(TAG, "block == null")
                result.success(false)
                return
            }
            if (keyType == null) {
                log(TAG, "keyType == null")
                result.success(false)
                return
            }
            if (hexKey == null) {
                log(TAG, "hexKey == null")
                result.success(false)
                return
            }
            if (hexData == null) {
                log(TAG, "hexData == null")
                result.success(false)
                return
            }
            WriteTagArg(sector, block, hexData, keyType, hexKey)
        }
        runOnUiThread {
            args.readTagArgs = emptyList()
            args.writeTagArgs = writeTagArgs
            args.cancel = false
            result.success(true)
        }
    }

    private fun cancel(result: MethodChannel.Result) {
        runOnUiThread {
            args.readTagArgs = emptyList()
            args.writeTagArgs = emptyList()
            args.cancel = true
            result.success(true)
        }
    }

    @Keep
    private data class Args(
        var cancel: Boolean = true,
        var readTagArgs: List<ReadTagArg> = emptyList(),
        var writeTagArgs: List<WriteTagArg> = emptyList()
    )

    @Keep
    private data class ReadTagArg(
        var sector: Int,
        var block: Int,
        var keyType: KeyType,
        var key: String
    )

    @Keep
    private data class WriteTagArg(
        var sector: Int,
        var block: Int,
        var data: String,
        var keyType: KeyType,
        var key: String
    )

    @Suppress("EnumEntryName")
    @Keep
    private enum class TagResultType { foundTag, readTag, writeTag }

    @Suppress("EnumEntryName")
    @Keep
    private enum class KeyType { keyA, keyB }


}