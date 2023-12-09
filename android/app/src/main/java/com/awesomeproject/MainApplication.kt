package com.awesomeproject

import android.app.Application
import com.braze.BrazeActivityLifecycleCallbackListener
import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.soloader.SoLoader
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.load

class MainApplication : Application(), ReactApplication {
    private val mReactNativeHost: ReactNativeHost = object : DefaultReactNativeHost(this) {
        override fun getUseDeveloperSupport(): Boolean {
            return BuildConfig.DEBUG
        }

        override fun getPackages(): List<ReactPackage> {
            // Packages that cannot be autolinked yet can be added manually here, for example:
            // packages.add(new MyReactNativePackage());
            val packages = PackageList(this).packages
            packages.add(NotificationModulePackage())
            return packages
        }

        override fun getJSMainModuleName(): String {
            return "index"
        }

        override val isNewArchEnabled: Boolean
            get() = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
        override val isHermesEnabled: Boolean
            get() = BuildConfig.IS_HERMES_ENABLED

//        protected val packages: List<Any>
//            protected get() =// Packages that cannot be autolinked yet can be added manually here, for example:
//                // packages.add(new MyReactNativePackage());
//                PackageList(this).getPackages()
//        protected val jSMainModuleName: String
//            protected get() = "index"
//        protected val isNewArchEnabled: Boolean
//            protected get() = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
//        protected val isHermesEnabled: Boolean
//            protected get() = BuildConfig.IS_HERMES_ENABLED
    }

    override fun getReactNativeHost(): ReactNativeHost {
        return mReactNativeHost
    }

    override fun onCreate() {
        super.onCreate()
        SoLoader.init(this,  /* native exopackage */false)
        registerActivityLifecycleCallbacks(BrazeActivityLifecycleCallbackListener())
        if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
            // If you opted-in for the New Architecture, we load the native entry point for this app.
            load()
        }
        ReactNativeFlipper.initializeFlipper(this, reactNativeHost.getReactInstanceManager())
    }
}