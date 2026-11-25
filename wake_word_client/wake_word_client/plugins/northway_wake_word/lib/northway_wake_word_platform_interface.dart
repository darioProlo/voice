import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'northway_wake_word_method_channel.dart';

abstract class NorthwayWakeWordPlatform extends PlatformInterface {
  /// Constructs a NorthwayWakeWordPlatform.
  NorthwayWakeWordPlatform() : super(token: _token);

  static final Object _token = Object();

  static NorthwayWakeWordPlatform _instance = MethodChannelNorthwayWakeWord();

  /// The default instance of [NorthwayWakeWordPlatform] to use.
  ///
  /// Defaults to [MethodChannelNorthwayWakeWord].
  static NorthwayWakeWordPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [NorthwayWakeWordPlatform] when
  /// they register themselves.
  static set instance(NorthwayWakeWordPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
