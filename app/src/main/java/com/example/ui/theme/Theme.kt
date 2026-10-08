package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val SanctuaryDarkColorScheme = darkColorScheme(
    primary = NightSagePrimary,
    onPrimary = NightOnPrimary,
    primaryContainer = NightSageContainer,
    onPrimaryContainer = NightCreamText,
    secondary = NightTerracottaSecondary,
    onSecondary = Color(0xFF2B1109),
    secondaryContainer = NightTerracottaContainer,
    onSecondaryContainer = Color(0xFFFCE4DC),
    tertiary = SoftEucalyptus,
    onTertiary = NightOnPrimary,
    tertiaryContainer = Color(0xFF2D3F39),
    onTertiaryContainer = NightCreamText,
    background = NightSanctuaryBg,
    onBackground = NightCreamText,
    surface = NightSanctuarySurface,
    onSurface = NightCreamText,
    surfaceVariant = NightCardSurface,
    onSurfaceVariant = NightMutedText,
    outline = Color(0xFF3E4F49),
    error = Color(0xFFF28B82),
    errorContainer = Color(0xFF5C1F1F),
    onErrorContainer = Color(0xFFFCE8E8)
)

private val SanctuaryLightColorScheme = lightColorScheme(
    primary = ForestSagePrimary,
    onPrimary = Color.White,
    primaryContainer = MintMistContainer,
    onPrimaryContainer = OnMintMistContainer,
    secondary = WarmTerracottaSecondary,
    onSecondary = Color.White,
    secondaryContainer = TerracottaContainer,
    onSecondaryContainer = OnTerracottaContainer,
    tertiary = WarmAmberTertiary,
    onTertiary = Color.White,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = OnAmberContainer,
    background = LinenBackground,
    onBackground = DeepInkText,
    surface = ParchmentSurface,
    onSurface = DeepInkText,
    surfaceVariant = WarmCardSurface,
    onSurfaceVariant = MutedSlateText,
    outline = SubtleSandOutline,
    error = CrisisCoralRed,
    errorContainer = CrisisSoftBackground,
    onErrorContainer = OnCrisisSoftBackground
)

val SanctuaryShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SanctuaryDarkColorScheme else SanctuaryLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = SanctuaryShapes,
        content = content
    )
}
