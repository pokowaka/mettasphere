package com.pokowaka.mettasphere.data

data class Preset(
    val id: Int? = null,
    val name: String,
    val visual: String,
    val delaySeconds: Int,
    val intervalMinutes: Int,
    val totalMinutes: Double,
    val startSound: String,
    val intervalSound: String,
    val endSound: String,
    val showDetailedReflection: Boolean = true
)

data class Reflection(
    val id: Int? = null,
    val timestamp: Long, // unix milliseconds
    val mettaRadiance: Int, // 1-10
    val hindrances: List<String>, // ids of active Nivarana
    val recognitionLevel: Int, // 1-5
    val releaseType: String, // 'suppression' | 'analytical' | 'passive' | '6r'
    val relaxationLevel: Int, // 1-4
    val smileQuality: Int, // 1-4
    val smileDuration: String, // 'vanished' | 'stayed'
    val physicalSensations: List<String>,
    val mentalStates: List<String>
)
