import 'package:flutter_test/flutter_test.dart';
import 'package:northway_wake_word/northway_wake_word.dart';
import 'package:northway_wake_word/northway_wake_word_platform_interface.dart';
import 'package:northway_wake_word/northway_wake_word_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockNorthwayWakeWordPlatform
    with MockPlatformInterfaceMixin
    implements NorthwayWakeWordPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final NorthwayWakeWordPlatform initialPlatform = NorthwayWakeWordPlatform.instance;

  test('$MethodChannelNorthwayWakeWord is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelNorthwayWakeWord>());
  });

  test('getPlatformVersion', () async {
    NorthwayWakeWord northwayWakeWordPlugin = NorthwayWakeWord();
    MockNorthwayWakeWordPlatform fakePlatform = MockNorthwayWakeWordPlatform();
    NorthwayWakeWordPlatform.instance = fakePlatform;

    expect(await northwayWakeWordPlugin.getPlatformVersion(), '42');
  });
}
