@file:JvmName("-ExtendKt")

package com.github.tuuzed.flutter_nfc_plugin.internal

import android.util.Log
import java.io.Closeable
import java.io.IOException

internal fun log(tag: String, msg: String, tr: Throwable? = null) = Log.v(tag, msg, tr)

fun close(closeable: Closeable?) {
    if (closeable != null) {
        try {
            closeable.close()
        } catch (e: IOException) { // pass
        }
    }
}