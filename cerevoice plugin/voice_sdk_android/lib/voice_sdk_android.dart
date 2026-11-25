
import 'voice_sdk_android_platform_interface.dart';

class VoiceSdkAndroid {
  Future<String?> getAudioFromText(String textToAudio) {
    return VoiceSdkAndroidPlatform.instance.getAudioFromText(textToAudio);
  }
}
