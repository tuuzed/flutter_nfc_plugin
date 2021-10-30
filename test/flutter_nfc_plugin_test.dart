import 'package:flutter/services.dart';
import 'package:flutter_nfc_plugin/flutter_nfc_plugin.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  const MethodChannel channel = MethodChannel('flutter_nfc_plugin');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return 'FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('FlutterNfcPlugin', () async {
    expect(FlutterNfcPlugin.DEFAULT_KEY, 'FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF');
  });
}
