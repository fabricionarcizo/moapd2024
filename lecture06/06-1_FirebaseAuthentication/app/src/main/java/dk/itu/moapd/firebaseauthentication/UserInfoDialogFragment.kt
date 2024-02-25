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
package dk.itu.moapd.firebaseauthentication

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import dk.itu.moapd.firebaseauthentication.databinding.DialogUserInfoBinding

/**
 * A DialogFragment subclass for displaying user information. This dialog shows the name, email or
 * phone number, and photo of the currently logged-in user.
 */
class UserInfoDialogFragment : DialogFragment() {

    /**
     * View binding is a feature that allows you to more easily write code that interacts with
     * views. Once view binding is enabled in a module, it generates a binding class for each XML
     * layout file present in that module. An instance of a binding class contains direct references
     * to all views that have an ID in the corresponding layout.
     */
    private var _binding: DialogUserInfoBinding? = null

    /**
     * This property is only valid between `onCreateView()` and `onDestroyView()` methods.
     */
    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    /**
     * Override to build your own custom Dialog container. This is typically used to show an
     * AlertDialog instead of a generic Dialog; when doing so, `onCreateView()` does not need to be
     * implemented since the AlertDialog takes care of its own content.
     *
     * This method will be called after `onCreate(Bundle)` and immediately before `onCreateView()`.
     * The default implementation simply instantiates and returns a `Dialog` class.
     *
     * @param savedInstanceState The last saved instance state of the `Fragment`, or null if this
     *      is a freshly created `Fragment`.
     *
     * @return Return a new `Dialog` instance to be displayed by the `Fragment`.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        // Inflate the view using view binding.
        _binding = DialogUserInfoBinding.inflate(layoutInflater)

        // Get the current user from Firebase Authentication.
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Populate the dialog view with user information.
        currentUser?.let { user ->
            with(binding) {
                textViewName.text = user.displayName ?: getString(R.string.unknown_user)
                textViewEmail.text = user.email ?: user.phoneNumber
                user.photoUrl?.let { url ->
                    imageViewPhoto?.let {
                        it.imageTintMode = null
                        Picasso.get().load(url).into(it)
                    }
                }
            }
        }

        // Create and return a new instance of MaterialAlertDialogBuilder.
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.user_info_title)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
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

}
