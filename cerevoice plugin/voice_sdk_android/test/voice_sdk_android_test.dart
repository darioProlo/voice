import 'package:flutter_test/flutter_test.dart';
import 'package:voice_sdk_android/voice_sdk_android.dart';
import 'package:voice_sdk_android/voice_sdk_android_platform_interface.dart';
import 'package:voice_sdk_android/voice_sdk_android_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockVoiceSdkAndroidPlatform
    with MockPlatformInterfaceMixin
    implements VoiceSdkAndroidPlatform {

  @override
  Future<String?> getAudioFromText() => Future.value('42');
}

void main() {
  final VoiceSdkAndroidPlatform initialPlatform = VoiceSdkAndroidPlatform.instance;

  test('$MethodChannelVoiceSdkAndroid is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelVoiceSdkAndroid>());
  });

  test('getPlatformVersion', () async {
    VoiceSdkAndroid voiceSdkAndroidPlugin = VoiceSdkAndroid();
    MockVoiceSdkAndroidPlatform fakePlatform = MockVoiceSdkAndroidPlatform();
    VoiceSdkAndroidPlatform.instance = fakePlatform;

    expect(await voiceSdkAndroidPlugin.getAudioFromText(), '42');
  });
}
