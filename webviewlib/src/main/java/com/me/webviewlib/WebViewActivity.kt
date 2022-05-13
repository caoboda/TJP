package com.me.webviewlib

import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.me.webviewlib.bean.AndroidtoJs
import com.me.webviewlib.databinding.ActivityWebviewBinding

/**
 *  WebView怎么选择： https://blog.csdn.net/weixin_33958366/article/details/91371957
 */
class WebViewActivity: BaseActivity<ActivityWebviewBinding>() {
    private var url: String? = ""
    private lateinit var mWebView:WebView
  /*  private var _mBinding:ActivityWebviewBinding?=null
    val mBinding: ActivityWebviewBinding
          get() =_mBinding!!
*/
    override fun initView() {
     //   _mBinding = ActivityWebviewBinding.inflate(layoutInflater)
        mWebView=mBinding.webView
        mWebView.settings.apply {
            useWideViewPort = true // 缩放至屏幕大小
            loadWithOverviewMode = true // 缩放至屏幕大小
            javaScriptEnabled=true// WebView允许js执行
            //缩放操作
            setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
            builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
            displayZoomControls = false //隐藏原生的缩放控件
            javaScriptCanOpenWindowsAutomatically = true  // 设置允许JS弹窗
            savePassword = false//密码明文存储漏洞
            mWebView.removeJavascriptInterface("searchBoxJavaBridge_")//searchBoxJavaBridge_接口引起远程代码执行漏洞,Android 3.0以下，Android系统会默认通过searchBoxJavaBridge_的Js接口给 WebView 添加一个JS映射对象：searchBoxJavaBridge_对象, 该接口可能被利用，实现远程任意代码。
        }
    }


    override fun initData() {
        url=intent.getStringExtra("url")
        //Activity与html通信步骤
        //1、webview的方法addJavascriptInterface，可以向html注入一个java对象，这样，在js中就可以调用java方法了
        //1.首先需要一个java对象JsCallManager
        //2.webview中如何使用
        // webView.addJavascriptInterface(new JSCallManager(jsonData), "jSinterface");
        //webView.setWebChromeClient(new WebChromeClient());
        //3.js中如何使用
        //  var data = window.jSCallManagerObject.showInfoFromJs();
        url?.let {
            mWebView.loadUrl(it)
           when (it.startsWith("file")){
                true ->{
                    //在js中调用本地java方法
                    mWebView.addJavascriptInterface(AndroidtoJs(this),"jSinterface")
                    mWebView.webChromeClient= WebChromeClient()
                }
                else -> {
                    mWebView.webViewClient = UIWebViewClient()
                    mWebView.webChromeClient =UIWebChromeClient()
                }
            }
        }


    }

    //处理各种通知 & 请求事件
    inner class UIWebViewClient : WebViewClient(){
        //打开网页时不调用系统浏览器， 而是在本WebView中显示；在网页上的所有加载都经过这个方法,这个函数我们可以做很多操作。
        // shouldOverrideUrlLoading接口，并非阻止WebView loadUrl时调用系统浏览器。
        // 若想让WebView loadUrl时，不会调用系统浏览器，需要设置自定的WebViewClient
        //该接口，主要是给WebView提供时机，让其选择是否对UrlLoading进行拦截。
        //关于该接口的返回值，True（拦截WebView加载Url），False（允许WebView加载Url）
        //以下为官网关于该接口返回值的解释：If a WebViewClient is provided, returning true causes the current WebView to abort loading the URL, while returning false causes the WebView to continue loading the URL as usual.
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            Log.e(classSimpleName,"shouldOverrideUrlLoading request")
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            Log.e(classSimpleName,"shouldOverrideUrlLoading url "+Uri.parse(url).host)
            //拦截https://www.baidu.com
            if(Uri.parse(url).host?.contains("baidu.com") == true){
                return true
            }
          //  view?.loadUrl(url!!)
            return false
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            Log.e(classSimpleName,"onPageStarted")
           mBinding.progressBar.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.e(classSimpleName,"onPageFinished")
            mBinding.progressBar.visibility = View.GONE
        }

        // shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) is called when a new page is about to be opened
        //whereas shouldInterceptRequest is called each time a resource is loaded like a css file, a js file etc.
        //　shouldOverrideUrlLoading
        //shouldOverrideUrlLoading被调用新页面时被打开,而shouldInterceptRequest被调用每次加载资源像一个css文件,js文件等。
        //https://cloud.tencent.com/developer/ask/61353
        override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
            val url=request?.url.toString()
            // Log.e(classSimpleName, "shouldInterceptRequest $url")
            /*     return if (url.contains(".js")) {
                     getWebResourceResponseFromString()
                 } else {
                     super.shouldInterceptRequest(view, url)
                 }*/
            return  super.shouldInterceptRequest(view, url)
        }
    }



    //辅助 WebView 处理 Javascript 的对话框,网站图标,网站标题,网页加载进度等等。
    inner class UIWebChromeClient : WebChromeClient() {

       //获取Web页中的标题
        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            Log.e(classSimpleName, "onReceivedTitle >>> title:${title}")
            title?.apply {
                    this@WebViewActivity.title = this
                //    mBinding.tvTitle.text = this
                }
        }

       //获得网页的加载进度并显示
       override fun onProgressChanged(view: WebView?, newProgress: Int) {
           super.onProgressChanged(view, newProgress)
           Log.e(classSimpleName, "onProgressChanged >>> newProgress=${newProgress}")
           mBinding.progressBar.progress=newProgress
       }
    }

    override fun initListener() {

    }


    //Back按键控制网页后退,多级网页回退而不是退出浏览器
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode ==KeyEvent.KEYCODE_BACK && mWebView.canGoBack()){
            mWebView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        //WebView避免内存泄露
        mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
        mWebView.clearHistory()
        (mWebView.parent as ViewGroup).removeView(mWebView)
        mWebView.destroy()
        mWebView == null
    }
    

}