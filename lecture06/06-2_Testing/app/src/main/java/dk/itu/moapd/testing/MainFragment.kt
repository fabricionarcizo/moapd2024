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
package dk.itu.moapd.testing

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.testing.databinding.FragmentMainBinding

/**
 * A fragment to display the main screen of the app.
 */
class MainFragment : Fragment() {

    /**
     * View binding is a feature that allows you to more easily write code that interacts with
     * views. Once view binding is enabled in a module, it generates a binding class for each XML
     * layout file present in that module. An instance of a binding class contains direct references
     * to all views that have an ID in the corresponding layout.
     */
    private var _binding: FragmentMainBinding? = null

    /**
     * This property is only valid between `onCreateView()` and `onDestroyView()` methods.
     */
    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    /**
     * A component to validate the form data.
     */
    private lateinit var nameValidator: TextValidator

    /**
     * A component to validate the user's email.
     */
    private lateinit var emailValidator: TextValidator

    /**
     * A component to validate the user's password.
     */
    private lateinit var passwordValidator: TextValidator

    /**
     * Called to have the fragment instantiate its user interface view. This is optional, and
     * non-graphical fragments can return null. This will be called between `onCreate(Bundle)` and
     * `onViewCreated(View, Bundle)`. A default `View` can be returned by calling `Fragment(int)` in
     * your constructor. Otherwise, this method returns null.
     *
     * It is recommended to <strong>only</strong> inflate the layout in this method and move logic
     * that operates on the returned View to `onViewCreated(View, Bundle)`.
     *
     * If you return a `View` from here, you will later be called in `onDestroyView()` when the view
     * is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the
     *      fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be
     *      attached to. The fragment should not add the view itself, but this can be used to
     *      generate the `LayoutParams` of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *      saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMainBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    /**
     * Called immediately after `onCreateView(LayoutInflater, ViewGroup, Bundle)` has returned, but
     * before any saved state has been restored in to the view. This gives subclasses a chance to
     * initialize themselves once they know their view hierarchy has been completely created. The
     * fragment's view hierarchy is not however attached to its parent at this point.
     *
     * @param view The View returned by `onCreateView(LayoutInflater, ViewGroup, Bundle)`.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *      saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Define the UI components behavior.
        binding.apply {

            // Define the UI behavior using lambda expressions.
            buttonRevert.setOnClickListener { onRevertClick(it) }
            buttonSend.setOnClickListener { onSendClick(it) }

            // Setup field validators.
            nameValidator = TextValidator(editTextName)
            editTextName.addTextChangedListener(nameValidator)

            emailValidator = TextValidator(editTextEmail)
            editTextEmail.addTextChangedListener(emailValidator)

            passwordValidator = TextValidator(editTextPassword)
            editTextPassword.addTextChangedListener(passwordValidator)
        }
    }

    /**
     * Called when the view previously created by `onCreateView()` has been detached from the
     * fragment. The next time the fragment needs to be displayed, a new view will be created. This
     * is called after `onStop()` and before `onDestroy()`. It is called <em>regardless</em> of
     * whether `onCreateView()` returned a non-null view. Internally it is called after the view's
     * state has been saved but before it has been removed from its parent.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * This method is called when the `Send` button is clicked.
     *
     * @param view The pressed view UI component.
     */
    private fun onSendClick(view: View) {
        val validationErrors = mutableListOf<Pair<EditText, String>>()

        binding.apply {
            // Check the edit text validations.
            if (!nameValidator.isValidName) {
                validationErrors.add(editTextName to getString(R.string.error))
            }

            if (!emailValidator.isValidEmail) {
                validationErrors.add(editTextEmail to getString(R.string.error))
            }

            if (!passwordValidator.isValidPassword) {
                validationErrors.add(editTextPassword to getString(R.string.error))
            }
        }

        // Hide the virtual keyboard.
        hideKeyboard(view)

        // If there are validation errors, show error messages and return.
        if (validationErrors.isNotEmpty()) {
            validationErrors.forEach { (view, errorMessage) ->
                view.error = errorMessage
            }
            return
        }

        // If validation passes, send the user data.
        showSnackBar("Data was successfully sent")
        onRevertClick(view)
    }

    /**
     * This method is called when the `Revert` button is clicked.
     *
     * @param view The pressed view UI component.
     */
    private fun onRevertClick(view: View) {
        // Clean the UI components data.
        binding.apply {
            editTextName.text?.clear()
            editTextEmail.text?.clear()
            editTextPassword.text?.clear()
        }

        // Hide the virtual keyboard.
        hideKeyboard(view)
    }

    /**
     * Hides the software keyboard associated with the specified view.
     *
     * @param view The view whose keyboard should be hidden.
     */
    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(
            Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Displays a SnackBar to show a brief message about the clicked button.
     *
     * The SnackBar is created using the clicked button's information and is shown at the bottom of
     * the screen.
     *
     * @param message The message to be displayed in the SnackBar.
     */
    private fun showSnackBar(message: String) {
        Snackbar.make(
            binding.root, message, Snackbar.LENGTH_SHORT
        ).show()
    }

}
