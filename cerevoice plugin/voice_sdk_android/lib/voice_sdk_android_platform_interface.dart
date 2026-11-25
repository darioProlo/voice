import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'voice_sdk_android_method_channel.dart';

abstract class VoiceSdkAndroidPlatform extends PlatformInterface {
  /// Constructs a VoiceSdkAndroidPlatform.
  VoiceSdkAndroidPlatform() : super(token: _token);

  static final Object _token = Object();

  static VoiceSdkAndroidPlatform _instance = MethodChannelVoiceSdkAndroid();

  /// The default instance of [VoiceSdkAndroidPlatform] to use.
  ///
  /// Defaults to [MethodChannelVoiceSdkAndroid].
  static VoiceSdkAndroidPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [VoiceSdkAndroidPlatform] when
  /// they register themselves.
  static set instance(VoiceSdkAndroidPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getAudioFromText(String textToAudio) {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
