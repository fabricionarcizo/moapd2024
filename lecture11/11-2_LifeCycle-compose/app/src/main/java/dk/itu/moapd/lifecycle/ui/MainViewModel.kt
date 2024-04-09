/*
 * MIT License
 *
 * Copyright (c) 2024 Fabricio Batista Narcizo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package dk.itu.moapd.lifecycle.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import dk.itu.moapd.lifecycle.R

/**
 * A view model sensitive to changes in the `MainActivity` UI components.
 */
class MainViewModel : ViewModel() {

    /**
     * The current state of all UI components shown in the main activity.
     */
    private var _uiState = MutableStateFlow(MainUiState())

    /**
     * A `StateFlow` which publicly exposes any update in the UI components.
     */
    val uiState: StateFlow<MainUiState> = _uiState

    /**
     * This method will be executed when the user interacts with any UI component and it is
     * necessary to update the text in the UI TextView. It sets the text into the `StateFlow`
     * instance.
     *
     * @param id The ID of a string resource available in this project.
     */
    fun onTextChanged(id: Int) {
        updateUiState { currentState ->
            currentState.copy(textId = id)
        }
    }

    /**
     * This method changes the boolean status of the UI checkbox.
     */
    fun toggleChecked() {
        updateUiState { currentState ->
            val checked = !currentState.checked
            val textId = if (checked) R.string.checked_text else R.string.unchecked_text
            currentState.copy(textId = textId, checked = checked)
        }
    }

    /**
     * Private function to update the UI state by applying a transformation function to the current
     * state.
     *
     * @param updateFunction The function used to update the current UI state.
     */
    private fun updateUiState(updateFunction: (MainUiState) -> MainUiState) {
        _uiState.update { currentState ->
            updateFunction(currentState)
        }
    }

}
