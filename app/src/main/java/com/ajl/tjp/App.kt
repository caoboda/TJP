package com.ajl.tjp

import android.app.Application
import android.util.Log
import com.alibaba.baichuan.android.trade.AlibcTradeSDK
import com.alibaba.baichuan.android.trade.callback.AlibcTradeInitCallback
import com.kepler.jd.Listener.AsyncInitListener
import com.kepler.jd.login.KeplerApiManager
import com.xunmeng.duoduojinbao.JinbaoUtil


class App :Application() {
    private val appKey= "aaf044d5196c2a4eedd03f5f6f4c529b"
    private val keySecret= "e3630216901d44f98675b0587b96715e"

    override fun onCreate() {
        super.onCreate()

      //jd sdk init
        KeplerApiManager.asyncInitSdk(this@App, appKey, keySecret,
            object : AsyncInitListener {

                override fun onSuccess() {
                    Log.e("Applications kepler", "Kepler asyncInitSdk onSuccess ")
                }

                override fun onFailure() {
                    Log.e("Applications kepler","Kepler asyncInitSdk 授权失败，请检查lib 工程资源引用；包名,签名证书是否和注册一致")
                }
            })

        //taobao sdk init

        AlibcTradeSDK.asyncInit(this, object : AlibcTradeInitCallback{
            override fun onSuccess() {
                Log.e("Applications Alibc","阿里百川sdk初始化成功")
            }

            override fun onFailure(code: Int, msg: String?) {
                Log.e("Applications Alibc","阿里百川sdk初始化失败 code= $code")
            }
        })

        //pdd sdk init
        JinbaoUtil.init(this) { b ->
            if (b) {
                Log.e("Applications Jinbao", "pdd sdk初始化成功 b= $b")
            } else {
                Log.e("Applications Jinbao", "pdd sdk初始化失败 b= $b")
            }
        }

    }
}