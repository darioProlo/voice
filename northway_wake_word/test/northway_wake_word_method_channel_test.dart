import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:northway_wake_word/northway_wake_word_method_channel.dart';

void main() {
  MethodChannelNorthwayWakeWord platform = MethodChannelNorthwayWakeWord();
  const MethodChannel channel = MethodChannel('northway_wake_word');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
