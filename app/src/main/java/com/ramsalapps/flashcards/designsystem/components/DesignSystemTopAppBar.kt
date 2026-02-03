package com.ramsalapps.flashcards.designsystem.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ramsalapps.flashcards.ui.theme.AccentBlue

/**
 * Design System TopAppBar Component
 * 
 * Wrapper alrededor de Material3 TopAppBar con altura estándar (56dp).
 * Mantiene consistencia en la barra de navegación superior.
 * 
 * @param title Composable para el título
 * @param modifier Modifier adicional
 * @param navigationIcon Icono de navegación (izquierda)
 * @param actions Acciones (derecha)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignSystemTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit) = {},
    actions: @Composable (RowScope.() -> Unit) = {}
) {
    TopAppBar(
        title = title,
        modifier = modifier.height(56.dp),
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = AccentBlue
        )
    )
}
