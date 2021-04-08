import 'dart:async';

import 'package:flutter/services.dart';

import './models.dart';

export './models.dart';

/// NFC插件,支持ISO14443A规格标签读写
class FlutterNfcPlugin {
  static const _CHANNEL_PREFIX = "io.github.tuuzed.flutter_nfc_plugin";
  static const _METHOD_CHANNEL_NAME = "$_CHANNEL_PREFIX/MethodChannel";
  static const _EVENT_CHANNEL_NAME = "$_CHANNEL_PREFIX/EventChannel";

  static const FLAG_READER_NFC_A = 0x1;
  static const FLAG_READER_NFC_B = 0x2;
  static const FLAG_READER_NFC_F = 0x4;
  static const FLAG_READER_NFC_V = 0x8;
  static const FLAG_READER_NFC_BARCODE = 0x10;
  static const FLAG_READER_SKIP_NDEF_CHECK = 0x80;
  static const FLAG_READER_NO_PLATFORM_SOUNDS = 0x100;

  static const DEFAULT_KEY = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";

  static const MethodChannel _methodChannel =
      const MethodChannel(_METHOD_CHANNEL_NAME);
  static const EventChannel _eventChannel =
      const EventChannel(_EVENT_CHANNEL_NAME);

  static Future<bool> enableReaderMode({int flags}) async {
    return await _methodChannel.invokeMethod("enableReaderMode", {
      "flags": flags,
    });
  }

  static Future<bool> disableReaderMode() async {
    return await _methodChannel.invokeMethod("disableReaderMode");
  }

  static Future<bool> cancel() async {
    return await _methodChannel.invokeMethod("cancel");
  }

  static Future<bool> readTag({List<ReadTagArg> args}) async {
    return await _methodChannel.invokeMethod(
      "readTag",
      args.map((it) => it.toMap()).toList(),
    );
  }

  static Future<bool> writeTag({List<WriteTagArg> args}) async {
    return await _methodChannel.invokeMethod(
      "writeTag",
      args.map((it) => it.toMap()).toList(),
    );
  }

  static Stream<TagResult> onTagDiscovered() {
    return _eventChannel
        .receiveBroadcastStream()
        .map((it) => TagResult.formMap(it));
  }
}
