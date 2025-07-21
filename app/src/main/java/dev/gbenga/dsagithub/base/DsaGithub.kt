package dev.gbenga.dsagithub.base

import android.app.Application
import dev.gbenga.dsagithub.data.database.databaseModule
import dev.gbenga.dsagithub.features.featureModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DsaGithub : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@DsaGithub)
            modules(appModules, databaseModule, featureModule)
        }
    }
}