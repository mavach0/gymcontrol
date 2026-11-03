package com.gymtracker.app.core.di

import android.content.Context
import com.gymtracker.app.data.local.UserPreferences
import com.gymtracker.app.data.local.WorkoutStateStore
import com.gymtracker.app.data.repository.LocalWorkoutRepository
import com.gymtracker.app.domain.repository.WorkoutRepository

/**
 * Contenedor de dependencias de la aplicación (service locator ligero).
 *
 * La construcción está blindada: si el almacenamiento local fallara por cualquier
 * motivo, se arranca con un repositorio en memoria para que la app nunca se quede
 * en pantalla en blanco ni entre en un bucle de cierre.
 */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val userPreferences: UserPreferences = UserPreferences(appContext)

    val workoutRepository: WorkoutRepository = runCatching {
        LocalWorkoutRepository(WorkoutStateStore(appContext))
    }.getOrElse { error ->
        android.util.Log.e("GymControl", "Fallo al abrir el almacenamiento local", error)
        LocalWorkoutRepository(store = null)
    }

    fun flushPersistence() {
        (workoutRepository as? LocalWorkoutRepository)?.flush()
    }
}
