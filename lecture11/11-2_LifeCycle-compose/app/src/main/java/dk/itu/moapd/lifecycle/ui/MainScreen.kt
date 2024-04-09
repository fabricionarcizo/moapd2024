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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dk.itu.moapd.lifecycle.R

/**
 * Composable function for displaying the main screen of the application.
 *
 * @param modifier Optional modifier for configuring the layout and behavior of the main screen.
 * @param mainViewModel The view model containing the state and logic for the main screen.
 *
 * @see MainViewModel
 * @see ButtonsRow
 * @see CheckBoxWithText
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel()
) {

    /**
     * Get a `observer` to monitor the states of all UI components in the application.
     */
    val mainUiState by mainViewModel.uiState.collectAsState()

    // A column that spans the entire screen.
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
    ) {
        // This compose shows the latest UI component used by the user.
        Text(
            text = stringResource(mainUiState.textId),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        )

        // A row with two buttons.
        ButtonsRow(
            onTrueClick = { mainViewModel.onTextChanged(R.string.true_text) },
            onFalseClick = { mainViewModel.onTextChanged(R.string.false_text) }
        )

        // A checkbox with a text.
        CheckBoxWithText(
            checked = mainUiState.checked,
            onCheckedChange = { mainViewModel.toggleChecked() }
        )
    }
}

/**
 * Composable function for displaying a row of two buttons.
 *
 * @param onTrueClick Callback function invoked when the "True" button is clicked.
 * @param onFalseClick Callback function invoked when the "False" button is clicked.
 *
 * @see Button
 */
@Composable
fun ButtonsRow(
    onTrueClick: () -> Unit,
    onFalseClick: () -> Unit
) {
    // Adding two buttons in the screen.
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {

        // True button.
        Button(
            onClick = onTrueClick,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text(stringResource(R.string.true_button), textAlign = TextAlign.Center)
        }

        // False button.
        Button(
            onClick = onFalseClick,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(stringResource(R.string.false_button), textAlign = TextAlign.Center)
        }
    }
}

/**
 * Composable function for displaying a checkbox with accompanying text.
 *
 * @param checked The current state of the checkbox (true if checked, false otherwise).
 * @param onCheckedChange Callback function invoked when the checkbox state changes.
 *
 * @see Checkbox
 */
@Composable
fun CheckBoxWithText(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    // Adding a checkbox with a text in the screen.
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .height(56.dp)
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Checkbox
            )
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null
        )
        Text(
            text = stringResource(R.string.checkbox_text),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
