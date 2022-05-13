package com.me.webviewlib

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import java.lang.Exception
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VB: ViewBinding>:AppCompatActivity() {
    lateinit var mBinding : VB
    var classSimpleName: String =javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(reflectToGetRootView())
        initView()
        initData()
        initListener()
    }



    abstract fun initView()

    abstract fun initData()

    abstract fun initListener()

    private fun reflectToGetRootView(): View {
        val type = javaClass.genericSuperclass as ParameterizedType
        val clazz = type.actualTypeArguments[0] as Class<*>
        if (clazz != ViewBinding::class.java && ViewBinding::class.java.isAssignableFrom(clazz)) {
            try {
                val method = clazz.getDeclaredMethod("inflate", LayoutInflater::class.java)
                method.isAccessible = true
                //ActivityMainBinding.inflate(layoutInflater)
                mBinding = method.invoke(null, layoutInflater) as VB
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        return mBinding.root
    }


}