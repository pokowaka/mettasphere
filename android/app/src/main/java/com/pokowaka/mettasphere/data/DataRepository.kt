package com.pokowaka.mettasphere.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

interface DataRepository {
    val presets: Flow<List<Preset>>
    val reflections: Flow<List<Reflection>>

    suspend fun addPreset(preset: Preset)
    suspend fun updatePreset(preset: Preset)
    suspend fun deletePreset(id: Int)
    suspend fun addReflection(reflection: Reflection)
}

class DefaultDataRepository(context: Context) : DataRepository {
    private val dbHelper = DbHelper(context.applicationContext)

    private val _presets = MutableStateFlow<List<Preset>>(emptyList())
    override val presets: Flow<List<Preset>> = _presets.asStateFlow()

    private val _reflections = MutableStateFlow<List<Reflection>>(emptyList())
    override val reflections: Flow<List<Reflection>> = _reflections.asStateFlow()

    init {
        // Initial load
        refreshPresets()
        refreshReflections()
    }

    private fun refreshPresets() {
        val list = mutableListOf<Preset>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DbHelper.TABLE_PRESETS,
            null, null, null, null, null,
            "${DbHelper.COL_PRESET_ID} ASC"
        )
        with(cursor) {
            while (moveToNext()) {
                list.add(
                    Preset(
                        id = getInt(getColumnIndexOrThrow(DbHelper.COL_PRESET_ID)),
                        name = getString(getColumnIndexOrThrow(DbHelper.COL_PRESET_NAME)),
                        visual = getString(getColumnIndexOrThrow(DbHelper.COL_PRESET_VISUAL)),
                        delaySeconds = getInt(getColumnIndexOrThrow(DbHelper.COL_PRESET_DELAY)),
                        intervalMinutes = getInt(getColumnIndexOrThrow(DbHelper.COL_PRESET_INTERVAL)),
                        totalMinutes = getDouble(getColumnIndexOrThrow(DbHelper.COL_PRESET_DURATION)),
                        startSound = getString(getColumnIndexOrThrow(DbHelper.COL_PRESET_START_SOUND)),
                        intervalSound = getString(getColumnIndexOrThrow(DbHelper.COL_PRESET_INTERVAL_SOUND)),
                        endSound = getString(getColumnIndexOrThrow(DbHelper.COL_PRESET_END_SOUND)),
                        showDetailedReflection = getInt(getColumnIndexOrThrow(DbHelper.COL_PRESET_SHOW_DETAILED)) == 1
                    )
                )
            }
            close()
        }
        _presets.value = list
    }

    private fun refreshReflections() {
        val list = mutableListOf<Reflection>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DbHelper.TABLE_REFLECTIONS,
            null, null, null, null, null,
            "${DbHelper.COL_REF_TIMESTAMP} DESC"
        )
        with(cursor) {
            while (moveToNext()) {
                val hindrancesStr = getString(getColumnIndexOrThrow(DbHelper.COL_REF_HINDRANCES))
                val physicalStr = getString(getColumnIndexOrThrow(DbHelper.COL_REF_PHYSICAL))
                val mentalStr = getString(getColumnIndexOrThrow(DbHelper.COL_REF_MENTAL))

                list.add(
                    Reflection(
                        id = getInt(getColumnIndexOrThrow(DbHelper.COL_REF_ID)),
                        timestamp = getLong(getColumnIndexOrThrow(DbHelper.COL_REF_TIMESTAMP)),
                        mettaRadiance = getInt(getColumnIndexOrThrow(DbHelper.COL_REF_RADIANCE)),
                        hindrances = if (hindrancesStr.isEmpty()) emptyList() else hindrancesStr.split(","),
                        recognitionLevel = getInt(getColumnIndexOrThrow(DbHelper.COL_REF_RECOGNITION)),
                        releaseType = getString(getColumnIndexOrThrow(DbHelper.COL_REF_RELEASE)),
                        relaxationLevel = getInt(getColumnIndexOrThrow(DbHelper.COL_REF_RELAXATION)),
                        smileQuality = getInt(getColumnIndexOrThrow(DbHelper.COL_REF_SMILE_QUALITY)),
                        flowLevel = getInt(getColumnIndexOrThrow(DbHelper.COL_REF_FLOW_LEVEL)),
                        smileDuration = getString(getColumnIndexOrThrow(DbHelper.COL_REF_SMILE_DURATION)),
                        physicalSensations = if (physicalStr.isEmpty()) emptyList() else physicalStr.split(","),
                        mentalStates = if (mentalStr.isEmpty()) emptyList() else mentalStr.split(",")
                    )
                )
            }
            close()
        }
        _reflections.value = list
    }

    override suspend fun addPreset(preset: Preset) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DbHelper.COL_PRESET_NAME, preset.name)
            put(DbHelper.COL_PRESET_VISUAL, preset.visual)
            put(DbHelper.COL_PRESET_DELAY, preset.delaySeconds)
            put(DbHelper.COL_PRESET_INTERVAL, preset.intervalMinutes)
            put(DbHelper.COL_PRESET_DURATION, preset.totalMinutes)
            put(DbHelper.COL_PRESET_START_SOUND, preset.startSound)
            put(DbHelper.COL_PRESET_INTERVAL_SOUND, preset.intervalSound)
            put(DbHelper.COL_PRESET_END_SOUND, preset.endSound)
            put(DbHelper.COL_PRESET_SHOW_DETAILED, if (preset.showDetailedReflection) 1 else 0)
        }
        db.insert(DbHelper.TABLE_PRESETS, null, values)
        refreshPresets()
    }

    override suspend fun updatePreset(preset: Preset) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DbHelper.COL_PRESET_NAME, preset.name)
            put(DbHelper.COL_PRESET_VISUAL, preset.visual)
            put(DbHelper.COL_PRESET_DELAY, preset.delaySeconds)
            put(DbHelper.COL_PRESET_INTERVAL, preset.intervalMinutes)
            put(DbHelper.COL_PRESET_DURATION, preset.totalMinutes)
            put(DbHelper.COL_PRESET_START_SOUND, preset.startSound)
            put(DbHelper.COL_PRESET_INTERVAL_SOUND, preset.intervalSound)
            put(DbHelper.COL_PRESET_END_SOUND, preset.endSound)
            put(DbHelper.COL_PRESET_SHOW_DETAILED, if (preset.showDetailedReflection) 1 else 0)
        }
        db.update(
            DbHelper.TABLE_PRESETS,
            values,
            "${DbHelper.COL_PRESET_ID} = ?",
            arrayOf(preset.id.toString())
        )
        refreshPresets()
    }

    override suspend fun deletePreset(id: Int) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        db.delete(
            DbHelper.TABLE_PRESETS,
            "${DbHelper.COL_PRESET_ID} = ?",
            arrayOf(id.toString())
        )
        refreshPresets()
    }

    override suspend fun addReflection(reflection: Reflection) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DbHelper.COL_REF_TIMESTAMP, reflection.timestamp)
            put(DbHelper.COL_REF_RADIANCE, reflection.mettaRadiance)
            put(DbHelper.COL_REF_HINDRANCES, reflection.hindrances.joinToString(","))
            put(DbHelper.COL_REF_RECOGNITION, reflection.recognitionLevel)
            put(DbHelper.COL_REF_RELEASE, reflection.releaseType)
            put(DbHelper.COL_REF_RELAXATION, reflection.relaxationLevel)
            put(DbHelper.COL_REF_SMILE_QUALITY, reflection.smileQuality)
            put(DbHelper.COL_REF_FLOW_LEVEL, reflection.flowLevel)
            put(DbHelper.COL_REF_SMILE_DURATION, reflection.smileDuration)
            put(DbHelper.COL_REF_PHYSICAL, reflection.physicalSensations.joinToString(","))
            put(DbHelper.COL_REF_MENTAL, reflection.mentalStates.joinToString(","))
        }
        db.insert(DbHelper.TABLE_REFLECTIONS, null, values)
        refreshReflections()
    }
}
