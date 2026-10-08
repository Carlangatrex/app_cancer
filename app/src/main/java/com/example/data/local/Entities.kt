package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "companion_profile")
data class CompanionProfileEntity(
    @PrimaryKey val id: Int = 1,
    val avatarName: String = "Alma",
    val caregiverName: String = "",
    val trenchContext: String = "Acompañando en sala de quimio y trámites",
    val onboardingCompleted: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "CAREGIVER" or "COMPANION"
    val content: String,
    val emotionValidated: String? = null,
    val microActionPrompt: String? = null,
    val isCrisisAlert: Boolean = false,
    val isBookmarked: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "caregiver_checkins")
data class CaregiverCheckInEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val physicalState: String,
    val emotionalState: String,
    val trenchEffort: String,
    val personalNote: String = "",
    val companionValidation: String,
    val timestamp: Long = System.currentTimeMillis()
)
