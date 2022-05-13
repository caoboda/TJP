package com.me.webviewlib.bean

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast

class AndroidtoJs(private val context: Context) {
    /**
     * @JavascriptInterface注解，在Android <=4.1.2 (API 16)，WebView使用WebKit浏览器引擎，并未正确限制addJavascriptInterface的使用方法，
     * 在应用权限范围内，攻击者可以通过Java反射机制实现任意命令执行。在Android >＝4.2 (API 17)，WebView使用Chromium浏览器引擎，
     * 并且限制了Javascript对Java对象方法的调用权限，只有声明了@JavascriptInterace注解的方法才能被Web页面调用。
     */
    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    fun test(msg:String) {
        Log.e("AndroidtoJs","JS调用了Android的test方法  msg=$msg")
        Toast.makeText(context,"JS调用了Android的test方法  msg=$msg", Toast.LENGTH_LONG).show()

    }

}