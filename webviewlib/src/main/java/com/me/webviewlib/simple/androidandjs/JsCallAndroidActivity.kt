package com.me.webviewlib.simple.androidandjs

import android.net.Uri
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.me.webviewlib.BaseActivity
import com.me.webviewlib.bean.AndroidtoJs
import com.me.webviewlib.databinding.ActivityWebviewSimple2Binding
/**
对于JS调用Android代码的方法有3种：
    1、通过WebView的addJavascriptInterface（）进行对象映射  优点：使用简单  缺点：存在严重的漏洞问题(js执行任意代码漏洞)
    2、通过 WebViewClient 的shouldOverrideUrlLoading ()方法回调拦截 url  优点：不存在方式1的漏洞； 缺点：JS获取Android方法的返回值复杂。
    3、通过 WebChromeClient 的onJsAlert()、onJsConfirm()、onJsPrompt（）方法回调拦截JS对话框alert()、confirm()、prompt（） 消息
 */
class JsCallAndroidActivity : BaseActivity<ActivityWebviewSimple2Binding>() {

    override fun initView() {
        mBinding.webView.settings.apply {
            javaScriptEnabled = true// WebView允许js执行
            javaScriptCanOpenWindowsAutomatically = true  // 设置允许JS弹窗
            savePassword = false//密码明文存储漏洞
            mBinding.webView.removeJavascriptInterface("searchBoxJavaBridge_")//searchBoxJavaBridge_接口引起远程代码执行漏洞,Android 3.0以下，Android系统会默认通过searchBoxJavaBridge_的Js接口给 WebView 添加一个JS映射对象：searchBoxJavaBridge_对象, 该接口可能被利用，实现远程任意代码。
        }
       // 方式1：通过 WebView的addJavascriptInterface（）进行对象映射
        //步骤1：定义一个与JS对象映射关系的Android类：AndroidtoJs
        //步骤2：将需要调用的JS代码以.html格式放到src/main/assets文件夹里
        //步骤3：在Android里通过WebView设置Android类与JS代码的映射
        // 格式规定为:file:///android_asset/文件名.html
        mBinding.webView.addJavascriptInterface(AndroidtoJs(this),"jsObj")
        mBinding.webView.loadUrl("file:///android_asset/jsCallAndroid.html")

    /*   方式2：通过 WebViewClient 的方法shouldOverrideUrlLoading ()回调拦截 url
        具体原理：
        Android通过 WebViewClient 的回调方法shouldOverrideUrlLoading ()拦截 url
        解析该 url 的协议
        如果检测到是预先约定好的协议，就调用相应方法
        即JS需要调用Android的方法

        具体使用：
        步骤1：在JS约定所需要的Url协议
        JS代码：jsCallAndroid.html
        以.html格式放到src/main/assets文件夹里*/
        //当该JS通过Android的mWebView.loadUrl("file:///android_asset/jsCallAndroid.html")加载后，就会回调shouldOverrideUrlLoading （）
    /*   优点：不存在方式1的漏洞；
         缺点：JS获取Android方法的返回值复杂。
        如果JS想要得到Android方法的返回值，只能通过 WebView 的 loadUrl （）去执行 JS 方法把返回值传递回去，相关的代码如下：
      // Android：MainActivity.java
        mWebView.loadUrl("javascript:returnResult(" + result + ")");
      // JS：js.html
        function returnResult(result){
            alert("result is" + result);
        }*/
        mBinding.webView.webViewClient = UiWebViewClient()
       // 方式3：通过 WebChromeClient 的onJsAlert()、onJsConfirm()、onJsPrompt（）方法回调拦截JS对话框alert()、confirm()、prompt（） 消息
      /*  方式3的原理：Android通过 WebChromeClient 的onJsAlert()、onJsConfirm()、onJsPrompt（）方法回调分别拦截JS对话框
        （即上述三个方法），得到他们的消息内容，然后解析即可。
        下面的例子将用拦截 JS的输入框（即prompt（）方法）说明 （AndroidCallJsActivity.kt里面有拦截alert()的方法）
        常用的拦截是：拦截 JS的输入框（即prompt（）方法）
        因为只有prompt（）可以返回任意类型的值，操作最全面方便、更加灵活；而alert（）对话框没有返回值；confirm（）对话框只能返回两种状态（确定 / 取消）两个值*/
        //步骤1：加载JS代码，如下：  jsCallAndroid.html
        // 以.html格式放到src/main/assets文件夹里
        mBinding.webView.webChromeClient = UiWebChromeClient()
    }

    override fun initData() {

    }

    override fun initListener() {

    }

    inner class UiWebViewClient :WebViewClient(){

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            // 步骤2：根据协议的参数，判断是否是所需要的url
            // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
            //假定传入进来的 url = "js://webview?arg1=111&amp;arg2=222"（同时也是约定好的需要拦截的）
             val uri:Uri  = Uri.parse(url)
            // 如果url的协议 = 预先约定的 js 协议
            // 就解析往下解析参数
            if ( uri.scheme.equals("js")) {
                // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                // 所以拦截url,下面JS开始调用Android需要的方法
                if (uri.authority.equals("webview")) {
                    //  步骤3：
                    // 执行JS所需要调用的逻辑(js调安卓方式2)
                    Log.e(classSimpleName,"js在此可以调用了Android的方法了")
                    Toast.makeText(this@JsCallAndroidActivity,"js在此可以调用了Android的方法了",Toast.LENGTH_LONG).show()
                    // 可以在协议上带有参数并传递到Android上
                    val params:MutableMap<String,String> = mutableMapOf()
                    val collection: Set<String> = uri?.queryParameterNames
                    Log.e(classSimpleName,"collection= $collection")
                }
                return true
            }
            return super.shouldOverrideUrlLoading(view, url)

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
            AlertDialog.Builder(this@JsCallAndroidActivity).apply {
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
        // 拦截输入框(原理同方式2)
        // 参数message:代表promt（）的内容（不是url）
        // 参数result:代表输入框的返回值
        override fun onJsPrompt(
            view: WebView?,
            url: String?,
            message: String?,
            defaultValue: String?,
            result: JsPromptResult?
        ): Boolean {
            // 根据协议的参数，判断是否是所需要的url(原理同方式2)
            // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
            //假定传入进来的 url = "js://webview?arg1=3333&arg2=4444"（同时也是约定好的需要拦截的）
            Log.e(classSimpleName,"url= $url message= $message ")
            val uri = Uri.parse(message);
            // 如果url的协议 = 预先约定的 js 协议
            // 就解析往下解析参数
            if (uri.scheme.equals("js")) {
                // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                // 所以拦截url,下面JS开始调用Android需要的方法
                if (uri.authority.equals("webview")) {
                    // 执行JS所需要调用的逻辑
                    Log.e(classSimpleName,"js在此可以调用了Android的方法了")
                    Toast.makeText(this@JsCallAndroidActivity,"js在此可以调用了Android的方法了",Toast.LENGTH_LONG).show()
                    // 可以在协议上带有参数并传递到Android上
                    val params:MutableMap<String,String> = mutableMapOf()
                    val collection: Set<String> = uri?.queryParameterNames as Set<String>
                    Log.e(classSimpleName,"collection= $collection")
                    //参数result:代表消息框的返回值(输入值)
                    result?.confirm("js调用了Android的方法成功啦")
                }
                return true
            }
            return super.onJsPrompt(view, url, message, defaultValue, result)
        }

    }
}