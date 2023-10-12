// Copyright 2022 The casbin Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.casdoor_flutter_sdk

import android.content.Context
import android.os.Build
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

class CasdoorFlutterSdkPlugin(private var context: Context? = null, private var channel: MethodChannel? = null): MethodCallHandler, FlutterPlugin {

    companion object {
        val callbacks = mutableMapOf<String, Result>()

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val plugin = CasdoorFlutterSdkPlugin()
            plugin.initInstance(registrar.messenger(), registrar.context())
        }
    }

    private fun initInstance(messenger: BinaryMessenger, context: Context) {
        this.context = context
        channel = MethodChannel(messenger, "casdoor_flutter_sdk")
        channel?.setMethodCallHandler(this)
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        initInstance(binding.binaryMessenger, binding.applicationContext)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        context = null
        channel = null
    }

    override fun onMethodCall(call: MethodCall, resultCallback: Result) {
        when (call.method) {
            "authenticate" -> {
                val url = call.argument<String>("url")
                val callbackUrlScheme = call.argument<String>("callbackUrlScheme")!!
                val preferEphemeral = call.argument<Boolean>("preferEphemeral") ?: false

                callbacks[callbackUrlScheme] = resultCallback

                val webView = findViewById<WebView>(R.id.webView)
                webView.settings.javaScriptEnabled = true


                // Set a WebViewClient to handle URL loading within the WebView.
                webView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        // Handle URL loading here. You can check the URL and take appropriate actions.
                        return false // Return false to allow loading the URL in the WebView.
                    }
                }

                // Set a WebChromeClient to handle JavaScript alerts, dialogs, etc.
                webView.webChromeClient = object : WebChromeClient() {
                    // Implement methods as needed.
                }

                webView.loadUrl(url)

                // You can add the WebView to your layout or display it in a dialog as needed.
                // For example, if you want to display it in a dialog:
                // val dialog = Dialog(context)
                // dialog.setContentView(webView)
                // dialog.show()
            }
            "cleanUpDanglingCalls" -> {
                // Implement your clean-up logic here.
                resultCallback.success(null)
            }
            "getPlatformVersion" -> {
                resultCallback.success("Android ${Build.VERSION.RELEASE}")
            }
            else -> resultCallback.notImplemented()
        }
    }
}
