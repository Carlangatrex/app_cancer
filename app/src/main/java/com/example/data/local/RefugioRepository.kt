package com.example.data.local

import kotlinx.coroutines.flow.Flow

class RefugioRepository(private val dao: RefugioDao) {
    val profileFlow: Flow<CompanionProfileEntity?> = dao.getProfile()
    val messagesFlow: Flow<List<ChatMessageEntity>> = dao.getAllMessages()
    val bookmarkedMessagesFlow: Flow<List<ChatMessageEntity>> = dao.getBookmarkedMessages()
    val checkInsFlow: Flow<List<CaregiverCheckInEntity>> = dao.getAllCheckIns()

    suspend fun getProfileOnce(): CompanionProfileEntity? = dao.getProfileOnce()

    suspend fun saveProfile(profile: CompanionProfileEntity) {
        dao.upsertProfile(profile.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun addMessage(message: ChatMessageEntity): Long {
        return dao.insertMessage(message)
    }

    suspend fun toggleBookmark(messageId: Long, currentBookmarked: Boolean) {
        dao.setBookmark(messageId, !currentBookmarked)
    }

    suspend fun clearConversation() {
        dao.clearAllMessages()
    }

    suspend fun addCheckIn(checkIn: CaregiverCheckInEntity): Long {
        return dao.insertCheckIn(checkIn)
    }

    suspend fun deleteCheckIn(id: Long) {
        dao.deleteCheckIn(id)
    }
}
