package com.plaud.flutter_alipay_plugin

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import com.alipay.sdk.app.PayTask
import com.alipay.sdk.app.AlipayApi
import com.alipay.sdk.app.debug.AlipayDebugOptions
import com.alipay.sdk.app.EnvUtils
import android.util.Log
import org.json.JSONObject

import java.util.concurrent.Executors

class FlutterAlipayPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private var activity: Activity? = null
    private val executor = Executors.newSingleThreadExecutor()

    private var appId: String = ""

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
                val isSandbox = call.argument<Boolean>("isSandbox") ?: false
                val appId = call.argument<String>("appId") ?: ""

                if (isSandbox) {
                    EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX)

                    val alipayPayLifeCycle = object : AlipayDebugOptions.AlipayPayLifeCycle {
                        override fun onPayEnd(endPayParams: JSONObject) {
//                            Log.i("alipay", "pay local onPayEnd endPayParams=$endPayParams")
                        }
                    }
                    val alipayDebugOptions = AlipayDebugOptions.Builder()
                        .setAlipayPayLifeCycle(alipayPayLifeCycle)
                        .build()
                    AlipayApi.setAlipayDebugOptions(alipayDebugOptions)
                }

                if (appId.isNotEmpty()) {
                    this.appId = appId

                    AlipayApi.registerApp(context, appId)
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

                executor.execute {
                    try {
                        val payTask = PayTask(activity)
                        val resultMap = payTask.payV2(orderInfo, isShowPayLoading)

                        activity?.runOnUiThread {
                            val response = mutableMapOf<String, Any>()
                            val resultStatus = resultMap["resultStatus"] as? String ?: ""
                            val payResult = resultMap["result"] as? String ?: ""
                            val memo = resultMap["memo"] as? String ?: ""

                            response["resultStatus"] = resultStatus
                            response["result"] = payResult
                            response["memo"] = memo
                            response["success"] = resultStatus == "9000"

                            result.success(response)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        activity?.runOnUiThread {
                            val response = mutableMapOf<String, Any>()
                            response["resultStatus"] = "4000"
                            response["result"] = ""
                            response["memo"] = "custom Payment failed: ${e.message}"
                            response["success"] = false
                            result.success(response)
                        }
                    }
                }
            }

            "isAlipayInstalled" -> {
                result.success(true)
            }

            else -> result.notImplemented()
        }
    }

    // private fun isAlipayAppInstalled(): Boolean {
    //     return try {
    //         val packageManager = context.packageManager
    //         packageManager.getPackageInfo("com.eg.android.AlipayGphone", PackageManager.GET_ACTIVITIES)
    //         true
    //     } catch (e: Exception) {
    //         false
    //     }
    // }

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