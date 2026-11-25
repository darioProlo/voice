import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'voice_sdk_android_platform_interface.dart';

/// An implementation of [VoiceSdkAndroidPlatform] that uses method channels.
class MethodChannelVoiceSdkAndroid extends VoiceSdkAndroidPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('voice_sdk_android');

  @override
  Future<String?> getAudioFromText(String textToAudio) async {
    final audio = await methodChannel.invokeMethod<String>('messageSent', textToAudio);
    return audio;
  }
}
