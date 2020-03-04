package com.breadwallet.presenter.activities

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.KeyEvent
import android.webkit.*
import android.widget.Toast
import com.breadwallet.R


import com.breadwallet.presenter.activities.util.BRActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class MyClass(@SerializedName("s1") val s1: Int)

//val myClass: MyClass = Gson().fromJson(data, MyClass::class.java)
//val outputJson: String = Gson().toJson(myClass)

/**
 * Created by afresina on 2/26/20.
 * Copyright 2020 CGift. All rights reserved.
 */
class RedeemWebViewActivity() : BRActivity() {

    //private var mWebView: WebView? = null
    private var mWebAppLoaded = false

    private final var cGiftScript = "window.cgift = {platform: 'android',sendMessage: function(name, body){android.sendMessage(name, JSON.stringify(body));}};"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_view_layout)

        if (mWebView == null) {
            mWebView = findViewById<WebView>(R.id.webView)
            //val mWebSettings = mWebView?.getSettings()
            //mWebSettings?.javaScriptEnabled = true
            mWebView!!.settings.javaScriptEnabled = true
            mWebView!!.settings.domStorageEnabled = true
            mWebView!!.settings.allowFileAccessFromFileURLs = true
            mWebView!!.settings.cacheMode = WebSettings.LOAD_NO_CACHE

            mWebView?.addJavascriptInterface(CGiftJavascriptInterface(this), "android")
            WebView.setWebContentsDebuggingEnabled(true)
            //mWebView?.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                    //or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            mWebView?.setWebViewClient(object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    mWebAppLoaded = true
                    mWebView!!.evaluateJavascript(cGiftScript) {
                        print(it)
                    }
                    mWebView!!.evaluateJavascript("cgiftReady();") {
                        print(it)
                    }
                }
                override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                    view.loadUrl(url)
                    return true
                }
                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this@RedeemWebViewActivity)
                    val alertDialog: AlertDialog = builder.create()
                    var message = "SSL Certificate error."
                    when (error?.primaryError) {
                        SslError.SSL_UNTRUSTED -> message = "The certificate authority is not trusted."
                        SslError.SSL_EXPIRED -> message = "The certificate has expired."
                        SslError.SSL_IDMISMATCH -> message = "The certificate Hostname mismatch."
                        SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
                    }
                    message += " Do you want to continue anyway?"
                    alertDialog.setTitle("SSL Certificate Error")
                    alertDialog.setMessage(message)
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", DialogInterface.OnClickListener { dialog, which ->
                        // Ignore SSL certificate errors
                        handler?.proceed()
                    })
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", DialogInterface.OnClickListener { dialog, which -> handler?.cancel() })
                    alertDialog.show()
                }
            })
            mWebView?.setWebChromeClient(CGiftWebViewClient())
        }
    }

    override fun onStart() {
        super.onStart()
        mWebView?.loadUrl("file:///android_asset/index.html" ) //("https://www.google.com")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView?.canGoBack()!!) {
            //mWebView?.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }

    fun handleReadyMessage() {
        //isReady = true
        //sendAllWalletAddressMessages()
    }

    fun handleBackMessage() {
        //navigationController?.popViewController(animated: true)
    }

    fun handleForwardMessage(body: Any) {
//        guard let props = body as? [String: String] else {
//            print("Message params are invalid.")
//            return
//        }
//
//        guard let urlString = props["url"] else {
//            print("Message must have a `url` property.")
//            return
//        }
//
//        guard let url = URL(string: urlString) else {
//            print("Message `url` is invalid: \(urlString)")
//            return
//        }
//
//        let vc = CGRedeemWorkflowViewController(destination: url)
//        vc.redeemWorkflowDelegate = redeemWorkflowDelegate
//        navigationController?.pushViewController(vc, animated: true)
    }

    fun handleDismissMessage() {
        //redeemWorkflowDelegate?.redeemGiftCardDidDismiss()
    }

    fun handleSuccessMessage(body: Any) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@RedeemWebViewActivity)
        val alertDialog: AlertDialog = builder.create()
        var message = "Success from Window.CGift!!"
        alertDialog.setTitle("Success")
        alertDialog.setMessage(message)
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", DialogInterface.OnClickListener { dialog, which ->
            // Ignore SSL certificate errors
        })
        alertDialog.show()
//        guard let props = body as? [String: String] else {
//            print("Message params are invalid.")
//            return
//        }
//
//        guard let title = props["title"] else {
//            print("Message must have a `title` property.")
//            return
//        }
//
//        guard let detail = props["detail"] else {
//            print("Message must have a `detail` property.")
//            return
//        }
//
//        redeemWorkflowDelegate?.redeemGiftCardDidSucceed(title: title, detail: detail)
    }

    fun handleFailureMessage(body: Any) {
//        guard let props = body as? [String: String] else {
//            print("Message params are invalid.")
//            return
//        }
//
//        guard let error = props["error"] else {
//            print("Message must have an `error` property.")
//            return
//        }
//
//        redeemWorkflowDelegate?.redeemGiftCardDidFail(error: error)
    }

    companion object {
        var mWebView: WebView? = null
        fun sendMessage(name: String, body: String) {
            mWebView!!.evaluateJavascript("window.cgift !== undefined") { value ->
                if (java.lang.Boolean.parseBoolean(value)) {
                    mWebView!!.evaluateJavascript("window.cgift.receiveMessage('werwerwerwerwerwer!', '')") {
                        print(it)
                    }
                }
            }
        }
    }

}

/** Instantiate the interface and set the context  */
class CGiftJavascriptInterface(private val mContext: RedeemWebViewActivity) {
    @JavascriptInterface
    fun sendMessage(message: String, body: String) {
        val jsonObject = GsonBuilder().create().fromJson(body, Object::class.java)
        print("sendMessage message: $message $jsonObject")
        when (message) {
            "ready" -> {
                mContext.handleReadyMessage()
            }
            "back" -> {
                mContext.handleBackMessage()
            }
            "forward" -> {
                mContext.handleForwardMessage("")
            }
            "dismiss" -> {
                mContext.handleDismissMessage()
            }
            "success" -> {
                mContext.handleSuccessMessage("")
            }
            "failure" -> {
                mContext.handleFailureMessage("")
            }
            else -> {
                print("Unsupported message: ")
            }
        }
    }
}

private class CGiftWebViewClient : WebChromeClient() {
    override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }

    override fun onJsBeforeUnload(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
        return super.onJsBeforeUnload(view, url, message, result)
    }

    override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
        return super.onJsAlert(view, url, message, result)
    }
}
