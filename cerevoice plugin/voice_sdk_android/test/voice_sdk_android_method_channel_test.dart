import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:voice_sdk_android/voice_sdk_android_method_channel.dart';

void main() {
  MethodChannelVoiceSdkAndroid platform = MethodChannelVoiceSdkAndroid();
  const MethodChannel channel = MethodChannel('voice_sdk_android');

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
    expect(await platform.getAudioFromText(), '42');
  });
}
