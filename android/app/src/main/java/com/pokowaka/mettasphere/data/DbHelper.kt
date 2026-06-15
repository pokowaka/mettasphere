package com.pokowaka.mettasphere.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "mettasphere.db"
        const val DATABASE_VERSION = 7

        // Presets Table
        const val TABLE_PRESETS = "presets"
        const val COL_PRESET_ID = "id"
        const val COL_PRESET_NAME = "name"
        const val COL_PRESET_VISUAL = "visual"
        const val COL_PRESET_DELAY = "delay_seconds"
        const val COL_PRESET_INTERVAL = "interval_minutes"
        const val COL_PRESET_DURATION = "total_minutes"
        const val COL_PRESET_START_SOUND = "start_sound"
        const val COL_PRESET_INTERVAL_SOUND = "interval_sound"
        const val COL_PRESET_END_SOUND = "end_sound"
        const val COL_PRESET_SHOW_DETAILED = "show_detailed_reflection"

        // Reflections Table
        const val TABLE_REFLECTIONS = "reflections"
        const val COL_REF_ID = "id"
        const val COL_REF_TIMESTAMP = "timestamp"
        const val COL_REF_RADIANCE = "metta_radiance"
        const val COL_REF_HINDRANCES = "hindrances" // Comma-separated
        const val COL_REF_RECOGNITION = "recognition_level"
        const val COL_REF_RELEASE = "release_type"
        const val COL_REF_RELAXATION = "relaxation_level"
        const val COL_REF_SMILE_QUALITY = "smile_quality"
        const val COL_REF_FLOW_LEVEL = "flow_level"
        const val COL_REF_SMILE_DURATION = "smile_duration"
        const val COL_REF_PHYSICAL = "physical_sensations" // Comma-separated
        const val COL_REF_MENTAL = "mental_states" // Comma-separated
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createPresetsTable = """
            CREATE TABLE $TABLE_PRESETS (
                $COL_PRESET_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PRESET_NAME TEXT NOT NULL,
                $COL_PRESET_VISUAL TEXT NOT NULL,
                $COL_PRESET_DELAY INTEGER NOT NULL,
                $COL_PRESET_INTERVAL INTEGER NOT NULL,
                $COL_PRESET_DURATION REAL NOT NULL,
                $COL_PRESET_START_SOUND TEXT NOT NULL,
                $COL_PRESET_INTERVAL_SOUND TEXT NOT NULL,
                $COL_PRESET_END_SOUND TEXT NOT NULL,
                $COL_PRESET_SHOW_DETAILED INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent()

        val createReflectionsTable = """
            CREATE TABLE $TABLE_REFLECTIONS (
                $COL_REF_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_REF_TIMESTAMP INTEGER NOT NULL,
                $COL_REF_RADIANCE INTEGER NOT NULL,
                $COL_REF_HINDRANCES TEXT NOT NULL,
                $COL_REF_RECOGNITION INTEGER NOT NULL,
                $COL_REF_RELEASE TEXT NOT NULL,
                $COL_REF_RELAXATION INTEGER NOT NULL,
                $COL_REF_SMILE_QUALITY INTEGER NOT NULL,
                $COL_REF_FLOW_LEVEL INTEGER NOT NULL DEFAULT 1,
                $COL_REF_SMILE_DURATION TEXT NOT NULL,
                $COL_REF_PHYSICAL TEXT NOT NULL,
                $COL_REF_MENTAL TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createPresetsTable)
        db.execSQL(createReflectionsTable)

        // Seed presets
        seedPresets(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_PRESETS ADD COLUMN $COL_PRESET_SHOW_DETAILED INTEGER NOT NULL DEFAULT 1")
        }
        if (oldVersion < 7) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_PRESETS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_REFLECTIONS")
            onCreate(db)
        }
    }

    private fun seedPresets(db: SQLiteDatabase) {
        val presets = listOf(
            ContentValues().apply {
                put(COL_PRESET_NAME, "Learning the 6Rs (15m)")
                put(COL_PRESET_VISUAL, "https://images.unsplash.com/photo-1441974231531-c6227db76b6e")
                put(COL_PRESET_DELAY, 0)
                put(COL_PRESET_INTERVAL, 5)
                put(COL_PRESET_DURATION, 15.0)
                put(COL_PRESET_START_SOUND, "Singing Bowl")
                put(COL_PRESET_INTERVAL_SOUND, "Woodblock")
                put(COL_PRESET_END_SOUND, "Gong")
                put(COL_PRESET_SHOW_DETAILED, 1) // Beginner reflection enabled
            },
            ContentValues().apply {
                put(COL_PRESET_NAME, "Learning the 6Rs (30m)")
                put(COL_PRESET_VISUAL, "https://images.unsplash.com/photo-1500674425917-06385469493a")
                put(COL_PRESET_DELAY, 0)
                put(COL_PRESET_INTERVAL, 10)
                put(COL_PRESET_DURATION, 30.0)
                put(COL_PRESET_START_SOUND, "Singing Bowl")
                put(COL_PRESET_INTERVAL_SOUND, "Woodblock")
                put(COL_PRESET_END_SOUND, "Gong")
                put(COL_PRESET_SHOW_DETAILED, 1) // Beginner reflection enabled
            },
            ContentValues().apply {
                put(COL_PRESET_NAME, "Morning Metta (30m)")
                put(COL_PRESET_VISUAL, "https://images.unsplash.com/photo-1506744038136-46273834b3fb")
                put(COL_PRESET_DELAY, 0)
                put(COL_PRESET_INTERVAL, 10)
                put(COL_PRESET_DURATION, 30.0)
                put(COL_PRESET_START_SOUND, "Singing Bowl")
                put(COL_PRESET_INTERVAL_SOUND, "Woodblock")
                put(COL_PRESET_END_SOUND, "Gong")
                put(COL_PRESET_SHOW_DETAILED, 0) // Short questionnaire
            },
            ContentValues().apply {
                put(COL_PRESET_NAME, "Deep Presence (60m)")
                put(COL_PRESET_VISUAL, "https://images.unsplash.com/photo-1441974231531-c6227db76b6e")
                put(COL_PRESET_DELAY, 0)
                put(COL_PRESET_INTERVAL, 30)
                put(COL_PRESET_DURATION, 60.0)
                put(COL_PRESET_START_SOUND, "Singing Bowl")
                put(COL_PRESET_INTERVAL_SOUND, "Woodblock")
                put(COL_PRESET_END_SOUND, "Gong")
                put(COL_PRESET_SHOW_DETAILED, 0) // Short questionnaire
            }
        )

        for (p in presets) {
            db.insert(TABLE_PRESETS, null, p)
        }
    }
}
