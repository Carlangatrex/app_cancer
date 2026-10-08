package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RefugioDao {
    @Query("SELECT * FROM companion_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<CompanionProfileEntity?>

    @Query("SELECT * FROM companion_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfileOnce(): CompanionProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: CompanionProfileEntity)

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC, id ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE isBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarkedMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("UPDATE chat_messages SET isBookmarked = :bookmarked WHERE id = :messageId")
    suspend fun setBookmark(messageId: Long, bookmarked: Boolean)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()

    @Query("SELECT * FROM caregiver_checkins ORDER BY timestamp DESC")
    fun getAllCheckIns(): Flow<List<CaregiverCheckInEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: CaregiverCheckInEntity): Long

    @Query("DELETE FROM caregiver_checkins WHERE id = :id")
    suspend fun deleteCheckIn(id: Long)
}
