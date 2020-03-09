package com.breadwallet.presenter.activities


import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.webkit.*
import android.webkit.WebView
import com.breadwallet.R
import com.breadwallet.presenter.activities.util.BRActivity
import com.breadwallet.presenter.customviews.BRDialogView.BROnClickListener
import com.breadwallet.tools.animation.BRDialog
import com.breadwallet.wallet.WalletsMaster
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName


data class MyClass(@SerializedName("s1") val s1: Int)

//val myClass: MyClass = Gson().fromJson(data, MyClass::class.java)
//val outputJson: String = Gson().toJson(myClass)

/**
 * Created by afresina on 2/26/20.
 * Copyright 2020 CGift. All rights reserved.
 */
class RedeemWebViewActivity() : BRActivity() {

    private var mWebView: WebView? = null
    private var mWebAppLoaded = false
    private var isReady = false
    private final var cGiftScript = "window.cgift = {platform: 'android',sendMessage: function(name, body){android.sendMessage(name, JSON.stringify(body));}};"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_view_layout)

        if (mWebView == null) {
            mWebView = findViewById<WebView>(R.id.webView)

            mWebView!!.settings.javaScriptEnabled = true
            mWebView!!.settings.domStorageEnabled = true
            mWebView!!.settings.allowFileAccessFromFileURLs = true
            mWebView!!.settings.cacheMode = WebSettings.LOAD_NO_CACHE

            mWebView?.addJavascriptInterface(CGiftJavascriptInterface(this), "android")
            WebView.setWebContentsDebuggingEnabled(true)
            mWebView?.setWebViewClient(object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    mWebAppLoaded = true
                    runOnUiThread {
                        mWebView!!.evaluateJavascript(cGiftScript) {
                            print(it)
                        }
                        mWebView!!.evaluateJavascript("cgiftReady();") {
                            print(it)
                        }
                    }
                }
                override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                    view.loadUrl(url)
                    return true
                }
                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
//                    val builder: AlertDialog.Builder = AlertDialog.Builder(this@RedeemWebViewActivity)
//                    val alertDialog: AlertDialog = builder.create()
//                    var message = "SSL Certificate error."
//                    when (error?.primaryError) {
//                        SslError.SSL_UNTRUSTED -> message = "The certificate authority is not trusted."
//                        SslError.SSL_EXPIRED -> message = "The certificate has expired."
//                        SslError.SSL_IDMISMATCH -> message = "The certificate Hostname mismatch."
//                        SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
//                    }
//                    message += " Do you want to continue anyway?"
//                    alertDialog.setTitle("SSL Certificate Error")
//                    alertDialog.setMessage(message)
//                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", DialogInterface.OnClickListener { dialog, which ->
//                        // Ignore SSL certificate errors
//                        handler?.proceed()
//                    })
//                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", DialogInterface.OnClickListener { dialog, which -> handler?.cancel() })
//                    alertDialog.show()
                }
            })
            mWebView?.setWebChromeClient(CGiftWebViewClient())
        }
    }

    override fun onStart() {
        super.onStart()
        val url: String = "https://api.cgift.io/webview/gift_card_redemptions/new?card_number=${RedeemActivity.rcr.cardNumber}&pin=${RedeemActivity.rcr.pin}&locale=en"
        mWebView?.loadUrl(url)//"file:///android_asset/index.html" )
    }

    private fun showErrorDialog(message: String) {
        BRDialog.showCustomDialog(this,
                "Redeem Card",
                message,
                this.getString(R.string.AccessibilityLabels_close),
                null,
                BROnClickListener {
                    brDialogView -> brDialogView.dismiss()
                },
                null,
                null,
                0)
    }

//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        // Check if the key event was the Back button and if there's history
//        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView?.canGoBack()!!) {
//            //mWebView?.goBack()
//            return true
//        }
//        // If it wasn't the Back key or there's no web page history, bubble up to the default
//        // system behavior (probably exit the activity)
//        return super.onKeyDown(keyCode, event)
//    }

    fun sendMessage(name: String, body: String) {
        val condition = "window.cgift && window.cgift.receiveMessage"
        val statement = "window.cgift.receiveMessage(\"${name}\", ${body});"
        val javascript = "if (${condition}) { ${statement} }"
        println("javascript send: ${javascript}")
        runOnUiThread {
            mWebView!!.evaluateJavascript(javascript) {
                println("javascript response: ${it}")
            }
        }
    }

    fun handleReadyMessage() {
        this.isReady = true
        sendAllWalletAddressMessages()
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
        val jsonObject = GsonBuilder().create().fromJson(body.toString(), Map::class.java)
        //println("handleSuccessMessage ${jsonObject["detail"].toString()} ${jsonObject["title"].toString()}")
        var title = "Redeem Card"
        var detail = this.getString(R.string.RedeemGiftCard_successMessage)
        if (jsonObject["detail"].toString().isNotEmpty() && jsonObject["title"].toString().isNotEmpty()) {
            title = jsonObject.get("title").toString()
            detail = jsonObject.get("detail").toString()
        }
        BRDialog.showCustomDialog(this, title, detail, this.getString(R.string.AccessibilityLabels_close), null, BROnClickListener { brDialogView ->
            brDialogView.dismiss()
            this.onBackPressed()
            val intent = Intent(this@RedeemWebViewActivity, HomeActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
        }, null, null, 0)
    }

    fun handleFailureMessage(body: Any) {
        val jsonObject = GsonBuilder().create().fromJson(body.toString(), Map::class.java)
        //println("handleSuccessMessage ${jsonObject["detail"].toString()} ${jsonObject["title"].toString()}")
        var title = "Redeem Card"
        var detail = "Card redeem failed - please try again later."
        if (jsonObject["error"].toString().isNotEmpty()) {
            detail = jsonObject.get("error").toString()
        }
        BRDialog.showCustomDialog(this, title, detail, this.getString(R.string.AccessibilityLabels_close), null, BROnClickListener { brDialogView ->
            brDialogView.dismiss()
            this.onBackPressed()
            val intent = Intent(this@RedeemWebViewActivity, HomeActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
        }, null, null, 0)
    }

    private fun sendWalletAddressMessage(currency: String, walletAddress: String) {
        sendMessage("walletAddress", "{\"currency\":\"${currency}\", \"walletAddress\":\"${walletAddress}\"}")
    }

    private fun sendAllWalletAddressMessages() {
        sendBtcWalletAddressMessage()
        sendEthWalletAddressMessage()
    }

    private fun sendBtcWalletAddressMessage() {
        val walletAddress = WalletsMaster.getInstance().getWalletByIso(this, "BTC").getAddress(this) ?: return
        sendWalletAddressMessage("BTC", walletAddress)
    }

    private fun sendEthWalletAddressMessage() {
        val walletAddress = WalletsMaster.getInstance().getWalletByIso(this, "ETH").getAddress(this) ?: return
        sendWalletAddressMessage("ETH", walletAddress)
    }

    companion object {
        //var mWebView: WebView? = null
    }

}

/** Instantiate the interface and set the context  */
class CGiftJavascriptInterface(private val mContext: RedeemWebViewActivity) {
    @JavascriptInterface
    fun sendMessage(message: String, body: String) {
        //val jsonObject = GsonBuilder().create().fromJson(body, Object::class.java)
        //print("sendMessage message: $message $jsonObject")
        when (message) {
            "ready" -> {
                mContext.handleReadyMessage()
            }
            "back" -> {
                mContext.handleBackMessage()
            }
            "forward" -> {
                mContext.handleForwardMessage(body)
            }
            "dismiss" -> {
                mContext.handleDismissMessage()
            }
            "success" -> {
                mContext.handleSuccessMessage(body)
            }
            "failure" -> {
                mContext.handleFailureMessage(body)
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
