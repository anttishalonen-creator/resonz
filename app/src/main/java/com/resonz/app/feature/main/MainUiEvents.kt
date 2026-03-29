package com.resonz.app.feature.main

sealed interface MainUiEvents { data class ShowMessage(val text: String) : MainUiEvents }
