package com.ajl.tjp

import android.content.Context
import android.content.pm.PackageManager


 fun checkAppInstalled(context: Context, pkgName: String?): Boolean {
    if (pkgName == null || pkgName.isEmpty()) {
        return false
    }
    val packageManager: PackageManager = context.packageManager
    val info = packageManager.getInstalledPackages(0)
    if (info == null || info.isEmpty()) return false
    for (i in info.indices) {
        if (pkgName == info[i].packageName) {
            return true
        }
    }
    return false
}



