package pl.michal_cyran.mobileremote

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import pl.michal_cyran.mobileremote.di.appModule

class MobileRemoteApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MobileRemoteApp)
            androidLogger()

            modules(appModule)
        }
    }
}