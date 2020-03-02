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

/**
 * Created by afresina on 2/26/20.
 * Copyright 2020 CGift. All rights reserved.
 */
class RedeemWebViewActivity() : BRActivity() {

    //private var mWebView: WebView? = null
    private var mWebAppLoaded = false

    private final var cGiftScript =
            """
                window.cgift = {
                    sendMessage: function(name, message) {
                        android.postMessage(name, message);
                    }
                };
            """.trimIndent()

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
                    mWebView!!.evaluateJavascript("window.androidFunc !== undefined") { value ->
                        if (java.lang.Boolean.parseBoolean(value)) {
                            mWebView!!.evaluateJavascript("window.androidFunc.receiveMessage('POST!', 'werwerwerwerwerwer')") {
                                // ignore for now
                            }
                        }
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

    companion object {
        var mWebView: WebView? = null
        fun sendMessage(name: String, body: String) {
            mWebView!!.evaluateJavascript("this.recieveMessage('" + name + "', 'testtrrrrrrrrr from android')") {
                // ignore for now
            }
        }
    }

}

/** Instantiate the interface and set the context  */
class CGiftJavascriptInterface(private val mContext: Context) {

    @JavascriptInterface
    fun sendMessage(message: String, body: String) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
        when (message) {
            "ready" -> {
                print("x == 1")
                RedeemWebViewActivity.sendMessage(message, body)
            }
            "back" -> RedeemWebViewActivity.sendMessage(message, body)
            "forward" -> RedeemWebViewActivity.sendMessage(message, body)
            else -> {
                //print("x is neither 1 nor 2")
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
