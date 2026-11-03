package com.gymtracker.app.data.local

import android.content.Context
import android.util.AtomicFile
import android.util.Log
import com.gymtracker.app.domain.model.CustomPlan
import com.gymtracker.app.domain.model.Exercise
import com.gymtracker.app.domain.model.ExerciseLog
import com.gymtracker.app.domain.model.MuscleGroup
import com.gymtracker.app.domain.model.SetType
import com.gymtracker.app.domain.model.WorkoutSession
import com.gymtracker.app.domain.model.WorkoutSet
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDateTime
import java.util.UUID

/**
 * Estado completo que se persiste en disco. Guardar todo en un único documento
 * permite escribir de forma atómica y recuperar un estado coherente tras un cierre
 * inesperado de la aplicación.
 */
data class WorkoutState(
    val workouts: List<WorkoutSession> = emptyList(),
    val activeWorkout: WorkoutSession? = null,
    val customPlans: List<CustomPlan> = emptyList(),
    val activePlanId: String? = null
)

/**
 * Almacenamiento local basado en un fichero JSON escrito con [AtomicFile].
 *
 * Si el fichero está corrupto o pertenece a una versión incompatible, se archiva
 * y se devuelve un estado vacío en lugar de propagar la excepción. De esta forma
 * un dato dañado nunca impide que la app vuelva a abrirse.
 */
class WorkoutStateStore(context: Context) {

    private val atomicFile = AtomicFile(File(context.filesDir, FILE_NAME))

    fun load(): WorkoutState {
        val backupFile = File(atomicFile.baseFile.path + ".bak")
        if (!atomicFile.baseFile.exists() && !backupFile.exists()) return WorkoutState()

        return runCatching {
            val text = atomicFile.openRead().bufferedReader(Charsets.UTF_8).use { it.readText() }
            if (text.isBlank()) {
                WorkoutState()
            } else {
                parseState(JSONObject(text))
            }
        }.getOrElse { error ->
            Log.e(TAG, "No se pudo leer el estado local. Se iniciará un estado limpio.", error)
            archiveCorruptFile()
            WorkoutState()
        }
    }

    @Synchronized
    fun save(state: WorkoutState) {
        val bytes = stateToJson(state).toString().toByteArray(Charsets.UTF_8)
        val output = atomicFile.startWrite()
        try {
            output.write(bytes)
            output.flush()
            atomicFile.finishWrite(output)
        } catch (error: Exception) {
            atomicFile.failWrite(output)
            throw error
        }
    }

    private fun archiveCorruptFile() {
        runCatching {
            val original = atomicFile.baseFile
            val backup = File(original.parentFile, "gymcontrol_state_corrupt.json")
            if (backup.exists()) backup.delete()
            original.renameTo(backup)
        }.onFailure { Log.w(TAG, "No se pudo archivar el estado corrupto", it) }
    }

    // ---------------------------------------------------------------------
    // Serialización
    // ---------------------------------------------------------------------

    private fun stateToJson(state: WorkoutState): JSONObject = JSONObject().apply {
        put("version", SCHEMA_VERSION)
        put("workouts", JSONArray(state.workouts.map { it.toJson() }))
        put("activeWorkout", state.activeWorkout?.toJson() ?: JSONObject.NULL)
        put("customPlans", JSONArray(state.customPlans.map { it.toJson() }))
        put("activePlanId", state.activePlanId ?: JSONObject.NULL)
    }

    private fun parseState(root: JSONObject): WorkoutState = WorkoutState(
        workouts = root.optJSONArray("workouts").mapObjects { it.toWorkoutSession() },
        activeWorkout = root.optJSONObject("activeWorkout")?.let { runCatching { it.toWorkoutSession() }.getOrNull() },
        customPlans = root.optJSONArray("customPlans").mapObjects { it.toCustomPlan() },
        activePlanId = root.optString("activePlanId").takeIf { it.isNotBlank() && it != "null" }
    )

    private fun WorkoutSession.toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("title", title)
        put("startedAt", startedAt.toString())
        put("durationSeconds", durationSeconds)
        put("exercises", JSONArray(exercises.map { it.toJson() }))
        put("isCompleted", isCompleted)
        put("notes", notes)
    }

    private fun JSONObject.toWorkoutSession(): WorkoutSession = WorkoutSession(
        id = optString("id").ifBlank { UUID.randomUUID().toString() },
        title = optString("title", "Entrenamiento"),
        startedAt = runCatching { LocalDateTime.parse(optString("startedAt")) }.getOrDefault(LocalDateTime.now()),
        durationSeconds = optLong("durationSeconds", 0L),
        exercises = optJSONArray("exercises").mapObjects { it.toExerciseLog() },
        isCompleted = optBoolean("isCompleted", false),
        notes = optString("notes", "")
    )

    private fun ExerciseLog.toJson(): JSONObject = JSONObject().apply {
        put("exercise", exercise.toJson())
        put("sets", JSONArray(sets.map { it.toJson() }))
    }

    private fun JSONObject.toExerciseLog(): ExerciseLog = ExerciseLog(
        exercise = optJSONObject("exercise")?.toExercise()
            ?: Exercise(
                id = "unknown",
                name = optString("exerciseName", "Ejercicio"),
                muscleGroup = MuscleGroup.CORE
            ),
        sets = optJSONArray("sets").mapObjects { it.toWorkoutSet() }
    )

    private fun Exercise.toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("muscleGroup", muscleGroup.name)
        put("equipment", equipment)
        put("defaultRestSeconds", defaultRestSeconds)
        put("description", description)
        put("tips", JSONArray(tips))
    }

    private fun JSONObject.toExercise(): Exercise = Exercise(
        id = optString("id").ifBlank { "unknown" },
        name = optString("name", "Ejercicio"),
        muscleGroup = runCatching { MuscleGroup.valueOf(optString("muscleGroup")) }
            .getOrDefault(MuscleGroup.CORE),
        equipment = optString("equipment", "Peso Corporal"),
        defaultRestSeconds = optInt("defaultRestSeconds", 60),
        description = optString("description", ""),
        tips = optJSONArray("tips").toStringList()
    )

    private fun WorkoutSet.toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("setNumber", setNumber)
        put("weightKg", weightKg)
        put("reps", reps)
        put("isCompleted", isCompleted)
        put("type", type.name)
    }

    private fun JSONObject.toWorkoutSet(): WorkoutSet = WorkoutSet(
        id = optString("id").ifBlank { UUID.randomUUID().toString() },
        setNumber = optInt("setNumber", 1).coerceAtLeast(1),
        weightKg = optDouble("weightKg", 0.0).coerceAtLeast(0.0),
        reps = optInt("reps", 1).coerceAtLeast(1),
        isCompleted = optBoolean("isCompleted", false),
        type = runCatching { SetType.valueOf(optString("type")) }.getOrDefault(SetType.NORMAL)
    )

    private fun CustomPlan.toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("exerciseIds", JSONArray(exerciseIds))
        put("createdAt", createdAt)
    }

    private fun JSONObject.toCustomPlan(): CustomPlan = CustomPlan(
        id = optString("id").ifBlank { UUID.randomUUID().toString() },
        name = optString("name", "Mi plan"),
        exerciseIds = optJSONArray("exerciseIds").toStringList(),
        createdAt = optLong("createdAt", System.currentTimeMillis())
    )

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return buildList {
            for (index in 0 until length()) {
                val value = optString(index, "")
                if (value.isNotBlank()) add(value)
            }
        }
    }

    private fun <T> JSONArray?.mapObjects(transform: (JSONObject) -> T): List<T> {
        if (this == null) return emptyList()
        val result = mutableListOf<T>()
        for (index in 0 until length()) {
            val item = optJSONObject(index) ?: continue
            runCatching { transform(item) }.getOrNull()?.let(result::add)
        }
        return result
    }

    companion object {
        private const val TAG = "GymControlStorage"
        private const val FILE_NAME = "gymcontrol_state.json"
        private const val SCHEMA_VERSION = 1
    }
}
