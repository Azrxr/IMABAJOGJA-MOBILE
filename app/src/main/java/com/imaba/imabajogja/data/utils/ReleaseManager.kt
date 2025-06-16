package com.imaba.imabajogja.data.utils

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

object ReleaseManager {

    fun checkForAppUpdate(context: Context, onUpdateAvailable: () -> Unit) {
        val appUpdateManager = AppUpdateManagerFactory.create(context)

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                Log.d("ReleaseManager", "Update tersedia dari Play Store")
                onUpdateAvailable()
            } else {
                Log.d("ReleaseManager", "Tidak ada update")
            }
        }

        appUpdateInfoTask.addOnFailureListener {
            Log.e("ReleaseManager", "Gagal cek update: ${it.localizedMessage}")
        }
    }

    fun getLocalVersionName(context: Context): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "1.0.0"
        } catch (e: PackageManager.NameNotFoundException) {
            "1.0.0"
        }
    }
}