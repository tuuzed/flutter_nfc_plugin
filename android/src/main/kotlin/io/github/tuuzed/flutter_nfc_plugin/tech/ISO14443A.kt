package io.github.tuuzed.flutter_nfc_plugin.tech

import android.nfc.Tag
import android.nfc.tech.MifareClassic
import io.github.tuuzed.flutter_nfc_plugin.internal.close
import io.github.tuuzed.flutter_nfc_plugin.internal.log
import java.io.IOException


object ISO14443A {

    private const val TAG = "NfcPlugin"

    fun readByKeyA(tag: Tag, sector: Int, block: Int, accessKey: ByteArray): ByteArray? {
        val mc = MifareClassic.get(tag)
        log(TAG, "readByKeyA: mc=$mc")
        mc ?: return null
        return try {
            mc.connect()
            val auth = mc.authenticateSectorWithKeyA(sector, accessKey) //auth
            if (auth) { //the last block of the sector is used for KeyA and KeyB cannot be overwritted
                mc.readBlock(sector * 4 + block)
            } else {
                log(TAG, "auth fail")
                null
            }
        } catch (e: IOException) {
            log(TAG, e.toString(), e)
            null
        } finally {
            close(mc)
        }
    }

    fun writeByKeyA(tag: Tag, sector: Int, block: Int, accessKey: ByteArray, data16bytes: ByteArray): Boolean? {
        val mc = MifareClassic.get(tag)
        log(TAG, "writeByKeyA: mc=$mc")
        mc ?: return null
        return try {
            mc.connect()
            val auth = mc.authenticateSectorWithKeyA(sector, accessKey) //auth
            if (auth) {
                mc.writeBlock(sector * 4 + block, data16bytes) //write
                true
            } else {
                false
            }
        } catch (e: IOException) {
            log(TAG, e.toString(), e)
            false
        } finally {
            close(mc)
        }
    }

    fun readByKeyB(tag: Tag, sector: Int, block: Int, accessKey: ByteArray): ByteArray? {
        val mc = MifareClassic.get(tag)
        log(TAG, "readByKeyB: mc=$mc")
        mc ?: return null
        return try {
            mc.connect()
            val auth = mc.authenticateSectorWithKeyB(sector, accessKey) //auth
            if (auth) { //the last block of the sector is used for KeyA and KeyB cannot be overwritted
                mc.readBlock(sector * 4 + block)
            } else {
                null
            }
        } catch (e: IOException) {
            log(TAG, e.toString(), e)
            null
        } finally {
            close(mc)
        }
    }

    fun writeByKeyB(tag: Tag, sector: Int, block: Int, accessKey: ByteArray, data16bytes: ByteArray): Boolean? {
        val mc = MifareClassic.get(tag)
        log(TAG, "writeByKeyB: mc=$mc")
        mc ?: return null
        return try {
            mc.connect()
            val auth = mc.authenticateSectorWithKeyB(sector, accessKey) //auth
            if (auth) {
                mc.writeBlock(sector * 4 + block, data16bytes) //write
                true
            } else {
                false
            }
        } catch (e: IOException) {
            log(TAG, e.toString(), e)
            false
        } finally {
            close(mc)
        }
    }


}