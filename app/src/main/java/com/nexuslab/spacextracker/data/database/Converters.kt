package com.nexuslab.spacextracker.data.database

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class Converters {
    
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { json.encodeToString(it) }
    }
    
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { 
            try {
                json.decodeFromString<List<String>>(it)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    @TypeConverter
    fun fromMap(value: Map<String, Any>?): String? {
        return value?.let { json.encodeToString(it) }
    }
    
    @TypeConverter
    fun toMap(value: String?): Map<String, Any>? {
        return value?.let { 
            try {
                json.decodeFromString<Map<String, Any>>(it)
            } catch (e: Exception) {
                emptyMap()
            }
        }
    }
}