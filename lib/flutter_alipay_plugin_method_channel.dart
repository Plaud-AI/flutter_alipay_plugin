/*
 * Copyright (c) 2025 PlaudAI. All rights reserved.
 * Author: Neo.Wang@plaud.ai
 */

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_alipay_plugin_platform_interface.dart';

/// An implementation of [FlutterAlipayPluginPlatform] that uses method channels.
class MethodChannelFlutterAlipayPlugin extends FlutterAlipayPluginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_alipay_plugin');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<bool> initAlipay({
    required String appId,
    required String privateKey,
    String? publicKey,
    bool? isSandbox,
  }) async {
    final result = await methodChannel.invokeMethod<bool>('initAlipay', {
      'appId': appId,
      'privateKey': privateKey,
      'publicKey': publicKey,
      'isSandbox': isSandbox,
    });
    return result ?? false;
  }

  @override
  Future<Map<String, dynamic>> pay({
    required String orderInfo,
    bool isShowPayLoading = true,
  }) async {
    final result = await methodChannel.invokeMethod<Map<dynamic, dynamic>>('pay', {
      'orderInfo': orderInfo,
      'isShowPayLoading': isShowPayLoading,
    });
    return Map<String, dynamic>.from(result ?? {});
  }

  @override
  Future<bool> isAlipayInstalled() async {
    final result = await methodChannel.invokeMethod<bool>('isAlipayInstalled');
    return result ?? false;
  }
}
