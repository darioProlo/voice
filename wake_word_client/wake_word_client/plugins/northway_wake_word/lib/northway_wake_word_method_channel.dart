import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'northway_wake_word_platform_interface.dart';

/// An implementation of [NorthwayWakeWordPlatform] that uses method channels.
class MethodChannelNorthwayWakeWord extends NorthwayWakeWordPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('northway_wake_word');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
