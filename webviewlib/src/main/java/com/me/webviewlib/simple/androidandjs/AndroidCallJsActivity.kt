package com.me.webviewlib.simple.androidandjs

import android.os.Build
import android.util.Log
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import com.me.webviewlib.BaseActivity
import com.me.webviewlib.databinding.ActivityWebviewSimpleBinding

/**
 * Android与JS通过WebView互相调用方法，实际上是：
    1、Android去调用JS的代码
    2、JS去调用Android的代码
    二者沟通的桥梁是WebView
对于Android调用JS代码的方法有2种：
    1、通过WebView的loadUrl（）  优点：方便简洁  缺点：效率低，获取返回值麻烦   一般在性能要求低，不需要获取返回值时使用。
    2、通过WebView的evaluateJavascript（） 优点：效率高、因为该方法的执行不会使页面刷新，而第一种方法（loadUrl ）则会执行页面刷新。 缺点：向下兼容性差（4.4及以上才能使用）

    所以我们要处理兼容性问题，在4.4以下使用第一种方式，4.4以上使用第二种方式
对于JS调用Android代码的方法有3种：
    1、通过WebView的addJavascriptInterface（）进行对象映射
    2、通过 WebViewClient 的shouldOverrideUrlLoading ()方法回调拦截 url
    3、通过 WebChromeClient 的onJsAlert()、onJsConfirm()、onJsPrompt（）方法回调拦截JS对话框alert()、confirm()、prompt（） 消息
 */


class AndroidCallJsActivity : BaseActivity<ActivityWebviewSimpleBinding>() {

    override fun initView() {
        mBinding.webView.settings.apply {
            javaScriptEnabled = true// WebView允许js执行
            javaScriptCanOpenWindowsAutomatically = true  // 设置允许JS弹窗
            savePassword = false//密码明文存储漏洞
            mBinding.webView.removeJavascriptInterface("searchBoxJavaBridge_")//searchBoxJavaBridge_接口引起远程代码执行漏洞,Android 3.0以下，Android系统会默认通过searchBoxJavaBridge_的Js接口给 WebView 添加一个JS映射对象：searchBoxJavaBridge_对象, 该接口可能被利用，实现远程任意代码。
        }
        // 先载入JS代码
        // 格式规定为:file:///android_asset/文件名.html
        mBinding.webView.loadUrl("file:///android_asset/androidCallJs.html")
        // 由于设置了弹窗检验调用结果,所以需要支持js对话框
        // webview只是载体，内容的渲染需要使用webviewChromClient类去实现
        // 通过设置WebChromeClient对象处理JavaScript的对话框
        //设置响应js 的Alert()函数
        mBinding.webView.webChromeClient =UiWebChromeClient()


    }

    override fun initData() {

    }

    override fun initListener() {
        //特别注意：JS代码调用一定要在 onPageFinished（） 回调之后才能调用，否则不会调用。
        mBinding.webView.post {
            mBinding.androidCallJsBtn.setOnClickListener {
                // 注意调用的JS方法名要对应上
                // 调用javascript的callJS(msg)方法
                var msg="我是从Android传过去的参数666666"
                // Android版本变量
                val version:Int = Build.VERSION.SDK_INT
                // 因为该方法在 Android 4.4 版本才可使用，所以使用时需进行版本判断
                if (version < 18) {
                    mBinding.webView.loadUrl("javascript:callJS('${msg}')")
                } else {
                    mBinding.webView.evaluateJavascript("javascript:callJS('${msg}')"){value->
                        //此处为 js 返回的结果
                        Log.e(classSimpleName,"value= $value")
                    }
                }
            }
        }
    }


    //辅助 WebView 处理 Javascript 的对话框,网站图标,网站标题,网页加载进度等等。
    inner class UiWebChromeClient: WebChromeClient(){
        //拦截js的alert对话框
        override fun onJsAlert(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?
        ): Boolean {
            Log.e(classSimpleName,"url= $url message= $message ")
            //弹窗口消费
            AlertDialog.Builder(this@AndroidCallJsActivity).apply {
                setTitle("Alert")
                setMessage(message)
                setPositiveButton(android.R.string.ok){ _, _ ->
                   result?.confirm()
                }
                setCancelable(false)
                create().show()
            }
            return true//消费
        }

        //拦截js的confirm对话框
        override fun onJsConfirm(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?
        ): Boolean {
            return super.onJsConfirm(view, url, message, result)
        }

        //拦截js的输入框
        override fun onJsPrompt(
            view: WebView?,
            url: String?,
            message: String?,
            defaultValue: String?,
            result: JsPromptResult?
        ): Boolean {
            return super.onJsPrompt(view, url, message, defaultValue, result)
        }

    }

}