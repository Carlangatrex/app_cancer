package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CaregiverCheckInEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.CompanionProfileEntity
import com.example.data.local.RefugioDatabase
import com.example.data.local.RefugioRepository
import com.example.data.remote.CompanionPromptEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class RefugioTab {
    CONTENCION,
    MI_ESTADO,
    MICRO_PAUSAS,
    CRISIS_Y_LIMITES
}

class RefugioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RefugioRepository

    val profileState: StateFlow<CompanionProfileEntity>
    val messagesState: StateFlow<List<ChatMessageEntity>>
    val bookmarkedMessagesState: StateFlow<List<ChatMessageEntity>>
    val checkInsState: StateFlow<List<CaregiverCheckInEntity>>

    private val _selectedTab = MutableStateFlow(RefugioTab.CONTENCION)
    val selectedTab: StateFlow<RefugioTab> = _selectedTab.asStateFlow()

    private val _isSendingMessage = MutableStateFlow(false)
    val isSendingMessage: StateFlow<Boolean> = _isSendingMessage.asStateFlow()

    private val _showAvatarSheet = MutableStateFlow(false)
    val showAvatarSheet: StateFlow<Boolean> = _showAvatarSheet.asStateFlow()

    private val _showCrisisBanner = MutableStateFlow(false)
    val showCrisisBanner: StateFlow<Boolean> = _showCrisisBanner.asStateFlow()

    private val _activeExercise = MutableStateFlow(CaregiverCatalog.microExercises.first())
    val activeExercise: StateFlow<MicroRegulationExercise> = _activeExercise.asStateFlow()

    private val _latestCheckInFeedback = MutableStateFlow<CaregiverCheckInEntity?>(null)
    val latestCheckInFeedback: StateFlow<CaregiverCheckInEntity?> = _latestCheckInFeedback.asStateFlow()

    init {
        val dao = RefugioDatabase.getInstance(application).refugioDao()
        repository = RefugioRepository(dao)

        profileState = repository.profileFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = CompanionProfileEntity()
            )
            .let { flow ->
                // Map null to default profile
                val mapped = MutableStateFlow(CompanionProfileEntity())
                viewModelScope.launch {
                    flow.collect { entity ->
                        if (entity != null) {
                            mapped.value = entity
                        }
                    }
                }
                mapped.asStateFlow()
            }

        messagesState = repository.messagesFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        bookmarkedMessagesState = repository.bookmarkedMessagesFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        checkInsState = repository.checkInsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        seedInitialSanctuaryIfEmpty()
    }

    private fun seedInitialSanctuaryIfEmpty() {
        viewModelScope.launch {
            var existingProfile = repository.getProfileOnce()
            if (existingProfile == null) {
                existingProfile = CompanionProfileEntity(
                    id = 1,
                    avatarName = "Alma",
                    caregiverName = "",
                    trenchContext = "Acompañando en sala de quimio y trámites",
                    onboardingCompleted = false
                )
                repository.saveProfile(existingProfile)
            }
        }
    }

    fun ensureWelcomeGreetingIfEmpty(currentMessages: List<ChatMessageEntity>) {
        if (currentMessages.isNotEmpty()) return
        viewModelScope.launch {
            val profile = repository.getProfileOnce() ?: CompanionProfileEntity()
            val greeting = CompanionPromptEngine.generateInitialGreeting(profile)
            repository.addMessage(
                ChatMessageEntity(
                    sender = "COMPANION",
                    content = greeting,
                    emotionValidated = "Prioridad: Cómo estás tú hoy",
                    microActionPrompt = "Suelta el aire despacio antes de escribir"
                )
            )
        }
    }

    fun selectTab(tab: RefugioTab) {
        _selectedTab.value = tab
    }

    fun setShowAvatarSheet(show: Boolean) {
        _showAvatarSheet.value = show
    }

    fun dismissCrisisBanner() {
        _showCrisisBanner.value = false
    }

    fun triggerCrisisSupportTab() {
        _selectedTab.value = RefugioTab.CRISIS_Y_LIMITES
    }

    fun updateCompanionProfile(
        newAvatarName: String,
        newCaregiverName: String,
        newTrenchContext: String
    ) {
        val cleanAvatar = newAvatarName.trim().ifBlank { "Alma" }
        val cleanCaregiver = newCaregiverName.trim()
        val cleanContext = newTrenchContext.trim().ifBlank { "Acompañando en sala de quimio y trámites" }

        viewModelScope.launch {
            val current = repository.getProfileOnce() ?: CompanionProfileEntity()
            val previousAvatar = current.avatarName
            val updated = current.copy(
                avatarName = cleanAvatar,
                caregiverName = cleanCaregiver,
                trenchContext = cleanContext,
                onboardingCompleted = true,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveProfile(updated)
            _showAvatarSheet.value = false

            // If only the initial greeting exists or avatar name changed, post a warm acknowledgment focused on the caregiver
            val msgs = messagesState.value
            if (msgs.size <= 1) {
                repository.clearConversation()
                repository.addMessage(
                    ChatMessageEntity(
                        sender = "COMPANION",
                        content = CompanionPromptEngine.generateInitialGreeting(updated),
                        emotionValidated = "Prioridad: Tu estado físico y emocional",
                        microActionPrompt = "Descruza los hombros un instante"
                    )
                )
            } else if (previousAvatar != cleanAvatar) {
                val caregiverPrefix = if (cleanCaregiver.isNotBlank()) "$cleanCaregiver, a" else "A"
                repository.addMessage(
                    ChatMessageEntity(
                        sender = "COMPANION",
                        content = "${caregiverPrefix} partir de ahora me quedo a tu lado como $cleanAvatar. No importa el nombre que elijas: este rincón sigue siendo exclusivamente para ti y para lo que tu cuerpo viene aguantando en «$cleanContext». ¿Cómo llevas el cansancio en este minuto?",
                        emotionValidated = "Espacio personalizado con $cleanAvatar",
                        microActionPrompt = "Toma una respiración suave"
                    )
                )
            }
        }
    }

    fun sendCaregiverMessage(rawText: String) {
        val trimmed = rawText.trim()
        if (trimmed.isEmpty() || _isSendingMessage.value) return

        viewModelScope.launch {
            _isSendingMessage.value = true
            val isImmediateCrisis = CompanionPromptEngine.detectCrisis(trimmed)
            if (isImmediateCrisis) {
                _showCrisisBanner.value = true
            }

            val currentHistory = messagesState.value
            repository.addMessage(
                ChatMessageEntity(
                    sender = "CAREGIVER",
                    content = trimmed,
                    isCrisisAlert = isImmediateCrisis
                )
            )

            val profile = repository.getProfileOnce() ?: profileState.value
            val result = CompanionPromptEngine.generateCompanionReply(
                userMessage = trimmed,
                history = currentHistory,
                profile = profile
            )

            if (result.isCrisisDetected) {
                _showCrisisBanner.value = true
            }

            repository.addMessage(
                ChatMessageEntity(
                    sender = "COMPANION",
                    content = result.replyText,
                    emotionValidated = result.emotionValidated,
                    microActionPrompt = result.microActionSuggestion,
                    isCrisisAlert = result.isCrisisDetected
                )
            )
            _isSendingMessage.value = false
        }
    }

    fun sendQuickPromptAndNavigateToChat(promptText: String) {
        _selectedTab.value = RefugioTab.CONTENCION
        sendCaregiverMessage(promptText)
    }

    fun toggleMessageBookmark(message: ChatMessageEntity) {
        viewModelScope.launch {
            repository.toggleBookmark(message.id, message.isBookmarked)
        }
    }

    fun startFreshConversation() {
        viewModelScope.launch {
            repository.clearConversation()
            val profile = repository.getProfileOnce() ?: profileState.value
            repository.addMessage(
                ChatMessageEntity(
                    sender = "COMPANION",
                    content = CompanionPromptEngine.generateInitialGreeting(profile),
                    emotionValidated = "Prioridad: Cómo estás tú hoy",
                    microActionPrompt = "Respira profundo antes de empezar"
                )
            )
        }
    }

    fun openMicroExerciseFromChat(microActionHint: String?) {
        val matched = when {
            microActionHint == null -> CaregiverCatalog.microExercises.first()
            microActionHint.contains("mandíbula", ignoreCase = true) ||
                microActionHint.contains("hombros", ignoreCase = true) ->
                CaregiverCatalog.microExercises[1]
            microActionHint.contains("pecho", ignoreCase = true) ||
                microActionHint.contains("permiso", ignoreCase = true) ->
                CaregiverCatalog.microExercises[2]
            else -> CaregiverCatalog.microExercises[0]
        }
        _activeExercise.value = matched
        _selectedTab.value = RefugioTab.MICRO_PAUSAS
    }

    fun selectMicroExercise(exercise: MicroRegulationExercise) {
        _activeExercise.value = exercise
    }

    fun recordCaregiverCheckIn(
        physicalState: String,
        emotionalState: String,
        trenchEffort: String,
        personalNote: String,
        alsoSendToChat: Boolean
    ) {
        viewModelScope.launch {
            val profile = repository.getProfileOnce() ?: profileState.value
            val avatar = profile.avatarName.ifBlank { "Alma" }
            val caregiverPrefix = if (profile.caregiverName.isNotBlank()) "${profile.caregiverName}, " else ""

            val validationText = "${caregiverPrefix}es completamente comprensible que sientas «${emotionalState.lowercase()}» cuando llevas el cuerpo con «${physicalState.lowercase()}». Sostener «${trenchEffort.lowercase()}» desgasta muchísimo más de lo que cualquiera ve desde afuera. Suelta el aire lento un minuto y afloja los hombros; hoy $avatar reconoce todo lo que estás haciendo."

            val entity = CaregiverCheckInEntity(
                physicalState = physicalState,
                emotionalState = emotionalState,
                trenchEffort = trenchEffort,
                personalNote = personalNote.trim(),
                companionValidation = validationText
            )
            repository.addCheckIn(entity)
            _latestCheckInFeedback.value = entity

            if (alsoSendToChat) {
                val summaryMsg = buildString {
                    append("Hoy mi cuerpo está con: $physicalState. ")
                    append("Lo que siento por dentro es: $emotionalState. ")
                    append("Vengo de sostener: $trenchEffort.")
                    if (personalNote.isNotBlank()) {
                        append(" $personalNote")
                    }
                }
                _selectedTab.value = RefugioTab.CONTENCION
                sendCaregiverMessage(summaryMsg)
            }
        }
    }

    fun deleteCheckIn(id: Long) {
        viewModelScope.launch {
            repository.deleteCheckIn(id)
        }
    }
}
