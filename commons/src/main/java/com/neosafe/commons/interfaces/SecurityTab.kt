package com.neosafe.commons.interfaces

import androidx.biometric.auth.AuthPromptHost
import com.neosafe.commons.views.MyScrollView

interface SecurityTab {
    fun initTab(requiredHash: String, listener: HashListener, scrollView: MyScrollView, biometricPromptHost: AuthPromptHost)

    fun visibilityChanged(isVisible: Boolean)
}
