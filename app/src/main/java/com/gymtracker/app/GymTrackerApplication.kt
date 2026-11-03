package com.gymtracker.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.gymtracker.app.core.di.AppContainer

class GymTrackerApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        container = runCatching { AppContainer(this) }
            .getOrElse { error ->
                Log.e("GymControl", "No se pudo crear el contenedor de dependencias", error)
                AppContainer(this)
            }

        // Al pasar a segundo plano forzamos el guardado del estado activo.
        // Así un cierre inesperado no deja datos a medias.
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityStopped(activity: Activity) {
                container.flushPersistence()
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
            override fun onActivityStarted(activity: Activity) = Unit
            override fun onActivityResumed(activity: Activity) = Unit
            override fun onActivityPaused(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
            override fun onActivityDestroyed(activity: Activity) = Unit
        })
    }
}
