import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_nfc_plugin/flutter_nfc_plugin.dart';

void log(String msg) => debugPrint(msg);

void main() => runApp(const App());

class App extends StatefulWidget {
  const App({Key? key}) : super(key: key);

  @override
  _AppState createState() => _AppState();
}

class _AppState extends State<App> {
  StreamSubscription<TagResult>? _streamSubscription;

  @override
  void initState() {
    super.initState();
    _streamSubscription =
        FlutterNfcPlugin.onTagDiscovered().listen((onData) async {
      log("onTagDiscovered: type= ${onData.type}");
      log("onTagDiscovered: success= ${onData.success}");
      log("onTagDiscovered: hexId= ${onData.hexId}");
      log("onTagDiscovered: techList= ${onData.techList}");
      for (var it in onData.dataList) {
        log("onTagDiscovered: dataList] sector=${it.sector},block=${it.block},hexData=${it.hexData}");
      }
      if (onData.success) {
        switch (onData.type) {
          case TagResultType.none:
            break;
          case TagResultType.foundTag:
            break;
          case TagResultType.readTag:
            await FlutterNfcPlugin.cancel();
            break;
          case TagResultType.writeTag:
            await FlutterNfcPlugin.cancel();
            break;
        }
      }
    });
  }

  @override
  void dispose() {
    _streamSubscription?.cancel();
    super.dispose();
  }

  void _enable() async {
    var flags = FlutterNfcPlugin.FLAG_READER_NFC_A |
        FlutterNfcPlugin.FLAG_READER_NFC_B |
        FlutterNfcPlugin.FLAG_READER_NFC_V |
        FlutterNfcPlugin.FLAG_READER_NFC_F |
        FlutterNfcPlugin.FLAG_READER_NFC_BARCODE;
    var rst = await FlutterNfcPlugin.enableReaderMode(flags: flags);
    log("_enable: rst=$rst");
  }

  void _disable() async {
    var rst = await FlutterNfcPlugin.disableReaderMode();
    log("_disable: rst=$rst");
  }

  void _readTag() async {
    var args = <ReadTagArg>[];
    for (int i = 0; i < 16; i++) {
      for (int j = 0; j < 4; j++) {
        args.add(ReadTagArg(sector: i, block: j));
      }
    }
    var rst = await FlutterNfcPlugin.readTag(args: args);
    log("_readTag: rst=$rst");
  }

  void _writeTag() async {
    var rst = await FlutterNfcPlugin.writeTag(args: [
      WriteTagArg(
          sector: 1, block: 0, hexData: "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"),
      WriteTagArg(
          sector: 1, block: 1, hexData: "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB"),
      WriteTagArg(
          sector: 1, block: 2, hexData: "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"),
    ]);
    log("_writeTag: rst=$rst");
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('NFC Plugin app'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: <Widget>[
              ElevatedButton(
                child: const Text("Enable"),
                onPressed: () => _enable(),
              ),
              ElevatedButton(
                child: const Text("Disable"),
                onPressed: () => _disable(),
              ),
              ElevatedButton(
                child: const Text("ReadTag"),
                onPressed: () => _readTag(),
              ),
              ElevatedButton(
                child: const Text("WriteTag"),
                onPressed: () => _writeTag(),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
