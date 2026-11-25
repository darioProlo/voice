
import 'northway_wake_word_platform_interface.dart';

class NorthwayWakeWord {
  Future<String?> getPlatformVersion() {
    return NorthwayWakeWordPlatform.instance.getPlatformVersion();
  }
}
