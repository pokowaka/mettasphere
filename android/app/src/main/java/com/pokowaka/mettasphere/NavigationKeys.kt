package com.pokowaka.mettasphere

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Start : NavKey
@Serializable data class Timer(val presetId: Int) : NavKey
@Serializable data class Reflect(val presetId: Int? = null) : NavKey
@Serializable data class PresetForm(val presetId: Int? = null) : NavKey
@Serializable data object Progress : NavKey
@Serializable data object History : NavKey
