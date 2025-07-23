package com.project.cureconnect.presentation.screens.AuthScreen


sealed interface LoginUiState {
    data object Inti : LoginUiState
    data class Error(val mess: String): LoginUiState
    data object success : LoginUiState
}