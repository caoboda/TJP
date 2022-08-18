package com.ajl.tjp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.baichuan.android.trade.AlibcTrade
import com.alibaba.baichuan.android.trade.model.AlibcShowParams
import com.alibaba.baichuan.android.trade.model.OpenType
import com.alibaba.baichuan.trade.biz.AlibcTradeCallback
import com.alibaba.baichuan.trade.biz.applink.adapter.AlibcFailModeType
import com.alibaba.baichuan.trade.biz.context.AlibcTradeResult
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams
import com.alibaba.baichuan.trade.biz.login.AlibcLogin
import com.alibaba.baichuan.trade.biz.login.AlibcLoginCallback
import com.kepler.jd.Listener.ActionCallBck
import com.kepler.jd.Listener.LoginListener
import com.kepler.jd.Listener.OpenAppAction
import com.kepler.jd.login.KeplerApiManager
import com.kepler.jd.sdk.bean.KelperTask
import com.me.webviewlib.WebViewActivity
import com.me.webviewlib.simple.androidandjs.AndroidCallJsActivity
import com.me.webviewlib.simple.androidandjs.JsCallAndroidActivity
import com.xunmeng.duoduojinbao.JinbaoUtil


class MainActivity : AppCompatActivity() {
    private var TAG =javaClass.simpleName
    private lateinit var turnJdBtn : Button
    private lateinit var turnTbBtn : Button
    private lateinit var turnPddBtn : Button
    private lateinit var turnDouYinBtn : Button
    private lateinit var turnWeiPinHuiBtn : Button
    private lateinit var turnWeb : Button
    private lateinit var turnInterActiveJsBtn : Button
    private lateinit var jsCallAndroidBtn : Button
    private  var mKelperTask: KelperTask?=null
  //  private val jdurl="https://u.jd.com/Zw0123O"
    private val jdurl="https://u.jd.com/ZM0bgTy"
    private val tburl="https://detail.tmall.com/item.htm?id=607595719976&ali_trackid=2:mm_10011550_0_0:1650536026_070_338022869&union_lens=lensId:OPT@1650536020@b6eb4c50-b33a-45d1-8571-eadb74ac328c_607595719976@1;recoveryid:201_33.5.39.50_785894_1650535977053;prepvid:201_33.5.39.50_785894_1650535977053&spm=a3126.8759693/d.zhtj.31&pvid=b6eb4c50-b33a-45d1-8571-eadb74ac328c&scm=1007.15880.171602.0&bxsign=tbkSDOx6al/2JVbTt/uKAyTjz9TyCKauHvdz16hVBQJTmP9uI%20AgummmVOIgTOgJ4s1rhpuzptX2VoCP5SiErpnEgfaQmjAqLEZP%20qF%20yF6Zk0="
    private val pddurl="https://p.pinduoduo.com/yG6IlO09"
    private val douyinurl="https://v.douyin.com/YTQXjDE/"
    private val weipinhuiurl="https://t.vip.com/tP2qJyK4GY8?chanTag=wpid&aq=1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        turnJdBtn=findViewById(R.id.turnJdBtn)
        turnTbBtn=findViewById(R.id.turnTbBtn)
        turnPddBtn=findViewById(R.id.turnPddBtn)
        turnDouYinBtn=findViewById(R.id.turnDouYinBtn)
        turnWeiPinHuiBtn=findViewById(R.id.turnWeiPinHuiBtn)
        turnWeb=findViewById(R.id.turnWeb)
        turnInterActiveJsBtn=findViewById(R.id.turnInterActiveJsBtn)
        jsCallAndroidBtn=findViewById(R.id.jsCallAndroidBtn)
        toJdApp()
        toTbApp()
        toPddApp()
        toDouYin()
        toWeiPinHui()
        toWeb()
        androidToJs()
        jsCallAndroid()
    }

    private fun toWeiPinHui() {
        turnWeiPinHuiBtn.setOnClickListener {
            val b = checkAppInstalled(MainActivity@this, "com.achievo.vipshop")
            if (b) {
                val intent = Intent()
                intent.data = Uri.parse(weipinhuiurl)
                startActivity(intent)
            } else {
                Toast.makeText(MainActivity@this, "请安装唯品会App", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toDouYin() {
        turnDouYinBtn.setOnClickListener {
            val b = checkAppInstalled(MainActivity@this, "com.ss.android.ugc.aweme")
            if (b) {
                val intent = Intent()
                intent.data = Uri.parse(douyinurl)
                startActivity(intent)
            } else {
                Toast.makeText(MainActivity@this, "请安装抖音", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun androidToJs() {
        turnInterActiveJsBtn.setOnClickListener {
            val intent =Intent(this, AndroidCallJsActivity::class.java).apply {
              //  putExtra("url","file:///android_asset/jsInteractiveAndroid.html")
            }
            startActivity(intent)
        }
    }

    private fun jsCallAndroid() {
        jsCallAndroidBtn.setOnClickListener {
            val intent =Intent(this, JsCallAndroidActivity::class.java).apply {
                //  putExtra("url","file:///android_asset/jsInteractiveAndroid.html")
            }
            startActivity(intent)
        }
    }



    private fun toWeb() {
        turnWeb.setOnClickListener {
            val intent =Intent(this,WebViewActivity::class.java).apply {
                putExtra("url","https://www.baidu.com")
            }
            startActivity(intent)
        }

    }


    private fun toJdApp() {
        turnJdBtn.setOnClickListener {
            //  验证登录态（验证是否登录）
            KeplerApiManager.getWebViewService().checkLoginState(object : ActionCallBck {
                override fun onDateCall(key: Int, info: String): Boolean {
                    Log.e(TAG,"已登录")
                    mKelperTask= KeplerApiManager.getWebViewService().openJDUrlPage(jdurl, "customerInfo",this@MainActivity,mOpenAppAction,5000)
                    return false
                }

                override fun onErrCall(key: Int, error: String): Boolean {
                    Log.e(TAG,"未登录")
                    KeplerApiManager.getWebViewService().login(this@MainActivity, mLoginListener)
                    return false
                }
            })
        }
    }

    private fun toTbApp() {
        turnTbBtn.apply {
            setOnClickListener {
                login()
            }
        }
    }

    private fun toPddApp() {
        turnPddBtn.apply {
            setOnClickListener {
                val b = checkAppInstalled(context, "com.xunmeng.pinduoduo")
                if (b) {
                    //url,backUrl
                    JinbaoUtil.openPdd(pddurl, "ddtzopen://test")
                } else {
                    Toast.makeText(context, "请安装拼多多", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private var mOpenAppAction = OpenAppAction { status ->
        when (status) {
            OpenAppAction.OpenAppAction_start -> { //开始状态未必一定执行，
                //  dialogShow()
            }
            OpenAppAction.OpenAppAction_result_NoJDAPP -> { //沒有安裝京東app
                //  dialogShow()
                //表示的就是MainActivity这个类对象本来，这种写法一般用在内部类里，因为在外部类中直接可以用关键字this表示本类，而内部类中直接写this的话表示的是内部类本身，想表示外部类的话就得加上外部类的类名.this。
                Toast.makeText(this@MainActivity,"請先安裝京東app", Toast.LENGTH_LONG).show()
            }
            else -> {
                mKelperTask = null
                // dialogDiss()
            }
        }
    }

    //直接授权登录
    val mLoginListener: LoginListener = object : LoginListener {

        override fun authSuccess() {
            //授权登录成功回调方法
            Log.e(TAG, "authSuccess")
            mKelperTask= KeplerApiManager.getWebViewService().openJDUrlPage(jdurl, "customerInfo",this@MainActivity,mOpenAppAction,5000)
        }

        override fun authFailed(errorCode: Int) {
            //授权登录失败回调方法
            Log.e(TAG, " errorCode= $errorCode")
        }
    }


    private fun login() {
        AlibcLogin.getInstance().showLogin(object : AlibcLoginCallback {

            override fun onSuccess(loginResult:Int ,openId:String ,userNick:String ) {
                // 参数说明：
                // loginResult(0--登录初始化成功；1--登录初始化完成；2--登录成功)
                // openId：用户id
                // userNick: 用户昵称
                Log.e(TAG, "获取淘宝用户信息: " + AlibcLogin.getInstance().session)
                // 以显示传入url的方式打开页面（第二个参数是套件名称）
                openTbUrl()
            }

            override fun onFailure(code: Int, msg:String) {
                // code：错误码  msg： 错误信息
                Log.e(TAG,"tb login failure code=${code}  msg=${msg} " + AlibcLogin.getInstance().session)
            }
        })
    }

    private fun openTbUrl() {
        // 页面实例 相比旧版本，新版本不再支持AlibcPage，存在普通url的情况直接调用openByUrl即可；以下Page实例支持openByBizCode的方式调用。
      //  val page: AlibcBasePage = AlibcDetailPage(itemId)
       // val page: AlibcBasePage = AlibcShopPage(shopId)
        //展示参数配置
        //// showParam各参数介绍
        //1、OpenType（页面打开方式）： 枚举值（Auto和Native），Native表示唤端，Auto表示不做设置
        //2、clientType表示唤端类型：taobao---唤起淘宝客户端；tmall---唤起天猫客户端
        //3、BACK_URL（小把手）：唤端返回的scheme
        //(如果不传默认将不展示小把手；如果想展示小把手，可以自己传入自定义的scheme，
        //或者传入百川提供的默认scheme："alisdk://")
        //4、AlibcFailModeType（唤端失败模式）： 枚举值如下
        //  AlibcNativeFailModeNONE：不做处理；
        //  AlibcNativeFailModeJumpBROWER：跳转浏览器；
        //  AlibcNativeFailModeJumpDOWNLOAD：跳转下载页；
        //  AlibcNativeFailModeJumpH5：应用内webview打开）
        //（注：AlibcNativeFailModeJumpBROWER不推荐使用）
        val showParams = AlibcShowParams()
        showParams.openType = OpenType.Native
        showParams.clientType = "taobao"
        showParams.backUrl = "scheme：\"alisdk://\""
        showParams.nativeOpenFailedMode = AlibcFailModeType.AlibcNativeFailModeJumpH5
   /*   taokeParams（淘客）参数配置：配置aid或pid的方式分佣
        参数说明：
        pid
        unionId
        subPid
        adzoneId
        extraParams
        （注：1、如果走adzoneId的方式分佣打点，需要在extraParams中显式传入taokeAppkey，否则打点失败；
        2、如果是打开店铺页面(shop)，需要在extraParams中显式传入sellerId，否则同步打点转链失败）*/

        //推广位示例：pid=mm_111111111_2222222_33333333（数字仅做举例，具体每个推广位的pid以实际创建后生成的为准）
        //其中111111111为账户id，通常也叫member id；2222222为媒体id，通常也叫site id；33333333为具体推广位id，通常也叫adzone id。
        // 整体三段式，叫做pid。这部分若您是开发者型的淘宝客，需要调用联盟API的，需要重点关注下。
        val taokeParams = AlibcTaokeParams("", "", "")
    //    taokeParams.setPid("mm_112883640_11584347_72287650277")
     //   taokeParams.setAdzoneid("72287650277")
        //adzoneid是需要taokeAppkey参数才可以转链成功&店铺页面需要卖家id（sellerId），具体设置方式如下：
     //   taokeParams.extraParams["taokeAppkey"] = "xxxxx"
     //   taokeParams.extraParams["sellerId"] = "xxxxx"
         //自定义参数
        val trackParams: Map<String, String> = HashMap()

        AlibcTrade.openByUrl(this@MainActivity, "", tburl, null,
            WebViewClient(), WebChromeClient(), showParams,taokeParams, trackParams, object : AlibcTradeCallback{

                override fun onTradeSuccess(tradeResult: AlibcTradeResult?) {
                    Log.e(TAG, "request success")
                }

                override fun onFailure(code: Int, msg: String) {
                    Log.e(TAG, " code=$code, msg=$msg ")
                    if (code == -1) {
                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }




}