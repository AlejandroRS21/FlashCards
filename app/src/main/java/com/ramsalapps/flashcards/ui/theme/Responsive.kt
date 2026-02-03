package com.ramsalapps.flashcards.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Responsive Design Breakpoints
 * 
 * Define constantes y funciones para manejar responsive design en diferentes tamaños de pantalla.
 * 
 * Breakpoints:
 * - Mobile: < 600dp (teléfonos)
 * - Tablet: 600dp - 840dp (tablets)
 * - Desktop: > 840dp (tablets grandes, desktops)
 */
object ResponsiveConstants {
    // Breakpoints en dp
    val MOBILE_MAX = 600.dp
    val TABLET_MIN = 600.dp
    val TABLET_MAX = 840.dp
    val DESKTOP_MIN = 840.dp
    
    // Columnas por breakpoint
    const val MOBILE_COLUMNS = 1
    const val TABLET_COLUMNS = 2
    const val DESKTOP_COLUMNS = 3
    
    // Padding por breakpoint
    val MOBILE_PADDING = Spacing.lg      // 16dp
    val TABLET_PADDING = Spacing.xl      // 24dp
    val DESKTOP_PADDING = 32.dp          // 32dp
}

/**
 * Enum para representar el tipo de dispositivo actual
 */
enum class DeviceType {
    MOBILE,
    TABLET,
    DESKTOP
}

/**
 * Obtiene el tipo de dispositivo basado en el ancho disponible
 */
fun getDeviceType(screenWidth: Dp): DeviceType {
    return when {
        screenWidth < ResponsiveConstants.MOBILE_MAX -> DeviceType.MOBILE
        screenWidth < ResponsiveConstants.DESKTOP_MIN -> DeviceType.TABLET
        else -> DeviceType.DESKTOP
    }
}

/**
 * Obtiene el número de columnas para grid según el tipo de dispositivo
 */
fun getGridColumns(screenWidth: Dp): Int {
    return when (getDeviceType(screenWidth)) {
        DeviceType.MOBILE -> ResponsiveConstants.MOBILE_COLUMNS
        DeviceType.TABLET -> ResponsiveConstants.TABLET_COLUMNS
        DeviceType.DESKTOP -> ResponsiveConstants.DESKTOP_COLUMNS
    }
}

/**
 * Obtiene el padding según el tipo de dispositivo
 */
fun getResponsivePadding(screenWidth: Dp): Dp {
    return when (getDeviceType(screenWidth)) {
        DeviceType.MOBILE -> ResponsiveConstants.MOBILE_PADDING
        DeviceType.TABLET -> ResponsiveConstants.TABLET_PADDING
        DeviceType.DESKTOP -> ResponsiveConstants.DESKTOP_PADDING
    }
}

/**
 * Composable que proporciona información del ancho actual de la pantalla
 */
@Composable
fun getCurrentScreenWidth(): Dp {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp.dp
}

/**
 * Composable que proporciona el tipo de dispositivo actual
 */
@Composable
fun getCurrentDeviceType(): DeviceType {
    return getDeviceType(getCurrentScreenWidth())
}
