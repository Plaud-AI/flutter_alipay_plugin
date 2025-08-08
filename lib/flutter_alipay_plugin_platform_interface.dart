/*
 * Copyright (c) 2025 PlaudAI. All rights reserved.
 * Author: Neo.Wang@plaud.ai
 */

import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_alipay_plugin_method_channel.dart';

abstract class FlutterAlipayPluginPlatform extends PlatformInterface {
  /// Constructs a FlutterAlipayPluginPlatform.
  FlutterAlipayPluginPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterAlipayPluginPlatform _instance = MethodChannelFlutterAlipayPlugin();

  /// The default instance of [FlutterAlipayPluginPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterAlipayPlugin].
  static FlutterAlipayPluginPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterAlipayPluginPlatform] when
  /// they register themselves.
  static set instance(FlutterAlipayPluginPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  /// Initialize Alipay payment
  Future<bool> initAlipay({
    required String appId,
    required String privateKey,
    String? publicKey,
  }) {
    throw UnimplementedError('initAlipay() has not been implemented.');
  }

  /// Initiate Alipay payment
  Future<Map<String, dynamic>> pay({
    required String orderInfo,
    bool isShowPayLoading = true,
  }) {
    throw UnimplementedError('pay() has not been implemented.');
  }

  /// Query Alipay payment result
  Future<Map<String, dynamic>> queryOrder({
    required String orderId,
  }) {
    throw UnimplementedError('queryOrder() has not been implemented.');
  }

  /// Check if Alipay is installed
  Future<bool> isAlipayInstalled() {
    throw UnimplementedError('isAlipayInstalled() has not been implemented.');
  }
}
