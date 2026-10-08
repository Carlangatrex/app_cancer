package com.example

import com.example.data.local.CompanionProfileEntity
import com.example.data.remote.CompanionPromptEngine
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun systemInstruction_injectsCustomAvatarNameAndRules() {
        val profile = CompanionProfileEntity(
            avatarName = "Noah",
            caregiverName = "Lucía",
            trenchContext = "Esperando en sala de quimio"
        )
        val instruction = CompanionPromptEngine.buildSystemInstruction(profile)
        assertTrue(instruction.contains("Eres Noah"))
        assertTrue(instruction.contains("Lucía"))
        assertTrue(instruction.contains("PROHIBIDO usar frases hechas"))
    }

    @Test
    fun crisisDetection_identifiesSevereCollapseOrSelfHarm() {
        assertTrue(CompanionPromptEngine.detectCrisis("Ya no quiero vivir más, quiero quitarme la vida"))
        assertFalse(CompanionPromptEngine.detectCrisis("Hoy estoy muy cansado después del hospital"))
    }

    @Test
    fun sanitizeCliches_removesBannedPhrases() {
        val sanitized = CompanionPromptEngine.sanitizeCliches("Sé fuerte, todo va a salir bien.")
        assertFalse(sanitized.lowercase().contains("sé fuerte"))
        assertFalse(sanitized.lowercase().contains("todo va a salir bien"))
    }
}
