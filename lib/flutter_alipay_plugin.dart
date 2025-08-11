/*
 * Copyright (c) 2025 PlaudAI. All rights reserved.
 * Author: Neo.Wang@plaud.ai
 */

import 'flutter_alipay_plugin_platform_interface.dart';

class FlutterAlipayPlugin {
  /// Get platform version
  Future<String?> getPlatformVersion() {
    return FlutterAlipayPluginPlatform.instance.getPlatformVersion();
  }

  /// Initialize Alipay payment
  Future<bool> initAlipay({
    required String appId,
    required String privateKey,
    String? publicKey,
  }) {
    return FlutterAlipayPluginPlatform.instance.initAlipay(
      appId: appId,
      privateKey: privateKey,
      publicKey: publicKey,
    );
  }

  /// Initiate Alipay payment
  Future<Map<String, dynamic>> pay({
    required String orderInfo,
    bool isShowPayLoading = true,
  }) {
    return FlutterAlipayPluginPlatform.instance.pay(
      orderInfo: orderInfo,
      isShowPayLoading: isShowPayLoading,
    );
  }

  /// Check if Alipay is installed
  Future<bool> isAlipayInstalled() {
    return FlutterAlipayPluginPlatform.instance.isAlipayInstalled();
  }
}
