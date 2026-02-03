package com.ramsalapps.flashcards.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Design System Spacing
 * 
 * Define constantes de espaciado para mantener consistencia en toda la aplicación.
 * Sigue la escala: xs (4dp) → xxl (32dp)
 * 
 * Uso:
 *   Modifier.padding(Spacing.lg)  // 16dp
 *   Modifier.padding(horizontal = Spacing.xl)  // 24dp
 */
object DesignSystemPatterns {
    object Spacing {
        // Extra Small - 4dp
        val xs = 4.dp
        
        // Small - 8dp
        val sm = 8.dp
        
        // Medium - 12dp
        val md = 12.dp
        
        // Large - 16dp
        val lg = 16.dp
        
        // Extra Large - 24dp
        val xl = 24.dp
        
        // 2X Large - 32dp
        val xxl = 32.dp
    }
    
    object BorderRadius {
        // None - 0dp
        val none = 0.dp
        
        // Extra Small - 4dp
        val xs = 4.dp
        
        // Small - 8dp
        val sm = 8.dp
        
        // Medium - 12dp
        val md = 12.dp
        
        // Large - 16dp
        val lg = 16.dp
        
        // Full - 999dp (circle)
        val full = 999.dp
    }
}

// Aliases para acceso directo (más corto)
val Spacing = DesignSystemPatterns.Spacing
val BorderRadius = DesignSystemPatterns.BorderRadius
