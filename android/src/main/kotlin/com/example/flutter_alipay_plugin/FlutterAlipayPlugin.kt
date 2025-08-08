/*
 * Copyright (c) 2025 PlaudAI. All rights reserved.
 * Author: Neo.Wang@plaud.ai
 */

package com.example.flutter_alipay_plugin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import com.alipay.sdk.app.PayTask
import com.alipay.sdk.app.H5PayCallback
import com.alipay.sdk.util.H5PayResultModel
import java.util.concurrent.Executors
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/** FlutterAlipayPlugin - Alipay payment integration for Flutter */
class FlutterAlipayPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var context: Context
  private var activity: Activity? = null
  private val executor = Executors.newSingleThreadExecutor()
  
  // Alipay SDK configuration
  private var appId: String = ""
  private var privateKey: String = ""
  private var publicKey: String = ""

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_alipay_plugin")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when (call.method) {
      "getPlatformVersion" -> {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
      }
      "initAlipay" -> {
        val appId = call.argument<String>("appId") ?: ""
        val privateKey = call.argument<String>("privateKey") ?: ""
        val publicKey = call.argument<String>("publicKey") ?: ""
        
        if (appId.isNotEmpty() && privateKey.isNotEmpty()) {
          this.appId = appId
          this.privateKey = privateKey
          this.publicKey = publicKey
          result.success(true)
        } else {
          result.success(false)
        }
      }
      "pay" -> {
        val orderInfo = call.argument<String>("orderInfo") ?: ""
        val isShowPayLoading = call.argument<Boolean>("isShowPayLoading") ?: true
        
        if (orderInfo.isEmpty()) {
          result.error("INVALID_ORDER_INFO", "Order info cannot be empty", null)
          return
        }
        
        if (activity == null) {
          result.error("NO_ACTIVITY", "Activity is not available", null)
          return
        }
        
        // Use real Alipay SDK for payment
        executor.execute {
          try {
            val payTask = PayTask(activity)
            val resultMap = payTask.payV2(orderInfo, isShowPayLoading)
            
            activity?.runOnUiThread {
              val response = mutableMapOf<String, Any>()
              
              // Parse Alipay SDK result
              val resultStatus = resultMap["resultStatus"] as? String ?: ""
              val result = resultMap["result"] as? String ?: ""
              val memo = resultMap["memo"] as? String ?: ""
              
              response["resultStatus"] = resultStatus
              response["result"] = result
              response["memo"] = memo
              response["success"] = resultStatus == "9000"
              
              result.success(response)
            }
          } catch (e: Exception) {
            activity?.runOnUiThread {
              val response = mutableMapOf<String, Any>()
              response["resultStatus"] = "4000"
              response["result"] = ""
              response["memo"] = "Payment failed: ${e.message}"
              response["success"] = false
              result.success(response)
            }
          }
        }
      }
      "queryOrder" -> {
        val orderId = call.argument<String>("orderId") ?: ""
        
        if (orderId.isEmpty()) {
          result.error("INVALID_ORDER_ID", "Order ID cannot be empty", null)
          return
        }
        
        // Use Alipay SDK to query order status
        executor.execute {
          try {
            // Note: Alipay SDK doesn't provide direct order query API
            // This should be implemented on server side
            // For now, we'll simulate the query process
            val payTask = PayTask(activity)
            
            // In real implementation, you would need to:
            // 1. Call your server API to query order status
            // 2. Server should call Alipay's trade.query API
            // 3. Return the result to client
            
            activity?.runOnUiThread {
              val response = mutableMapOf<String, Any>()
              response["success"] = false
              response["message"] = "Order query requires server-side implementation. Please implement order query on your server using Alipay's trade.query API."
              response["orderId"] = orderId
              response["status"] = "UNKNOWN"
              result.success(response)
            }
          } catch (e: Exception) {
            activity?.runOnUiThread {
              val response = mutableMapOf<String, Any>()
              response["success"] = false
              response["message"] = "Query failed: ${e.message}"
              response["orderId"] = orderId
              response["status"] = "ERROR"
              result.success(response)
            }
          }
        }
      }
      "isAlipayInstalled" -> {
        val isInstalled = isAlipayAppInstalled()
        result.success(isInstalled)
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  private fun isAlipayAppInstalled(): Boolean {
    return try {
      val packageManager = context.packageManager
      packageManager.getPackageInfo("com.eg.android.AlipayGphone", PackageManager.GET_ACTIVITIES)
      true
    } catch (e: Exception) {
      false
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
  }

  override fun onDetachedFromActivityForConfigChanges() {
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activity = binding.activity
  }

  override fun onDetachedFromActivity() {
    activity = null
  }
}
