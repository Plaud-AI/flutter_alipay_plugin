/*
 * Copyright (c) 2025 PlaudAI. All rights reserved.
 * Author: Neo.Wang@plaud.ai
 */

import Flutter
import UIKit

public class FlutterAlipayPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "flutter_alipay_plugin", binaryMessenger: registrar.messenger())
    let instance = FlutterAlipayPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "getPlatformVersion":
      result("iOS " + UIDevice.current.systemVersion)
    case "initAlipay":
      result(notSupportedPlatformError())
    case "pay":
      result(notSupportedPlatformError())
    case "queryOrder":
      result(notSupportedPlatformError())
    case "isAlipayInstalled":
      result(notSupportedPlatformError())
    default:
      result(FlutterMethodNotImplemented)
    }
  }

  private func notSupportedPlatformError() -> FlutterError {
    return FlutterError(code: "UNSUPPORTED_PLATFORM", 
                         message: "Alipay payment is not supported on iOS platform", 
                         details: nil)
  }
}
