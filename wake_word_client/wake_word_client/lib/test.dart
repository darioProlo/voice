import 'package:flutter/services.dart';
import 'package:flutter/src/foundation/key.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/material.dart';

import 'package:northway_wake_word/northway_wake_word.dart' as wakeWord;

class Test extends StatefulWidget {
  Test({Key? key}) : super(key: key);

  String? message;

  @override
  State<Test> createState() => _TestState();
}

class _TestState extends State<Test> {
  Future<void> getTheMessage() async {
    try {
      widget.message = await wakeWord.NorthwayWakeWord().getPlatformVersion();
    } on PlatformException {
      widget.message = 'Excepcion';
    }
    setState(() {
      widget.message;
    });
    if (!mounted) return;
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          GestureDetector(
            onTap: () {
              getTheMessage();
            },
            child: const Text('TAP HERE', style: TextStyle(color: Colors.green, fontSize: 30)),
          ),
          Text(widget.message ?? ''),
        ],
      ),
    );
  }
}
