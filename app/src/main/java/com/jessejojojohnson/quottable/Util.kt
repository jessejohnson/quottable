package com.jessejojojohnson.quottable

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

fun hasPermissions(context: Context?, vararg permissions: String?): Boolean{
    for (permission in permissions){
        if (context?.let {
                ActivityCompat.checkSelfPermission(it, permission!!)
        } != PackageManager.PERMISSION_GRANTED){
            return false
        }
    }
    return true
}

fun checkPermissionsAndRequest(context: Activity?, PERMISSIONS: Array<String>, requestCode: Int): Boolean {
    return if (hasPermissions(context, *PERMISSIONS)){
        hasPermissions(context, *PERMISSIONS)
    } else {
        ActivityCompat.requestPermissions(context!!, PERMISSIONS, requestCode)
        hasPermissions(context, *PERMISSIONS)
    }
}