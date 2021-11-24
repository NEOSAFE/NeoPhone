package com.neosafe.neophone

import android.app.Application
import com.neosafe.commons.extensions.checkUseEnglish

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        checkUseEnglish()
    }
}
