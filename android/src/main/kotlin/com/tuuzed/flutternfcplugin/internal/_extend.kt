@file:JvmName("-ExtendKt")

package com.tuuzed.flutternfcplugin.internal

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
