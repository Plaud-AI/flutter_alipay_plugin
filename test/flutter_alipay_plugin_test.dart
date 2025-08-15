/*
 * Copyright (c) 2025 PlaudAI. All rights reserved.
 * Author: Neo.Wang@plaud.ai
 */

import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_alipay_plugin/flutter_alipay_plugin.dart';
import 'package:flutter_alipay_plugin/flutter_alipay_plugin_platform_interface.dart';
import 'package:flutter_alipay_plugin/flutter_alipay_plugin_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFlutterAlipayPluginPlatform
    with MockPlatformInterfaceMixin
    implements FlutterAlipayPluginPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<bool> initAlipay({
    required String appId,
    bool? isSandbox,
  }) =>
      Future.value(true);

  @override
  Future<Map<String, dynamic>> pay({
    required String orderInfo,
    bool isShowPayLoading = true,
  }) =>
      Future.value({'success': true});

  @override
  Future<bool> isAlipayInstalled() => Future.value(true);
}

void main() {
  final FlutterAlipayPluginPlatform initialPlatform =
      FlutterAlipayPluginPlatform.instance;

  test('$MethodChannelFlutterAlipayPlugin is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutterAlipayPlugin>());
  });

  test('getPlatformVersion', () async {
    FlutterAlipayPlugin flutterAlipayPlugin = FlutterAlipayPlugin();
    MockFlutterAlipayPluginPlatform fakePlatform =
        MockFlutterAlipayPluginPlatform();
    FlutterAlipayPluginPlatform.instance = fakePlatform;

    expect(await flutterAlipayPlugin.getPlatformVersion(), '42');
  });

  test('initAlipay', () async {
    FlutterAlipayPlugin flutterAlipayPlugin = FlutterAlipayPlugin();
    MockFlutterAlipayPluginPlatform fakePlatform =
        MockFlutterAlipayPluginPlatform();
    FlutterAlipayPluginPlatform.instance = fakePlatform;

    expect(
        await flutterAlipayPlugin.initAlipay(appId: 'test', privateKey: 'test'),
        true);
  });

  test('pay', () async {
    FlutterAlipayPlugin flutterAlipayPlugin = FlutterAlipayPlugin();
    MockFlutterAlipayPluginPlatform fakePlatform =
        MockFlutterAlipayPluginPlatform();
    FlutterAlipayPluginPlatform.instance = fakePlatform;

    expect(await flutterAlipayPlugin.pay(orderInfo: 'test_order'),
        {'success': true});
  });

  test('isAlipayInstalled', () async {
    FlutterAlipayPlugin flutterAlipayPlugin = FlutterAlipayPlugin();
    MockFlutterAlipayPluginPlatform fakePlatform =
        MockFlutterAlipayPluginPlatform();
    FlutterAlipayPluginPlatform.instance = fakePlatform;

    expect(await flutterAlipayPlugin.isAlipayInstalled(), true);
  });
}
