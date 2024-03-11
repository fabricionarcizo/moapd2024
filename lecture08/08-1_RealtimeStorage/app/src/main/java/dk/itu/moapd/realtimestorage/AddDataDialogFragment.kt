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
package dk.itu.moapd.realtimestorage

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.realtimestorage.databinding.DialogDummyDataBinding

/**
 * A DialogFragment subclass for adding data to a database. This dialog shows a field to type a
 * `String` value.
 */
class AddDataDialogFragment : DialogFragment() {

    /**
     * View binding is a feature that allows you to more easily write code that interacts with
     * views. Once view binding is enabled in a module, it generates a binding class for each XML
     * layout file present in that module. An instance of a binding class contains direct references
     * to all views that have an ID in the corresponding layout.
     */
    private var _binding: DialogDummyDataBinding? = null

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

        // Initialize Firebase Auth and connect to the Firebase Realtime Database.
        val auth = FirebaseAuth.getInstance()
        val database = Firebase.database(DATABASE_URL).reference

        // Inflate the view using view binding.
        _binding = DialogDummyDataBinding.inflate(layoutInflater)

        // Define a lambda for positive button click handling.
        val onPositiveButtonClick: (DialogInterface, Int) -> Unit = { dialog, _ ->
            val name = binding.editTextName.text.toString()
            if (name.isNotEmpty()) {
                val timestamp = System.currentTimeMillis()
                val dummy = Dummy(name, timestamp, timestamp)

                // If the user is authenticated, create a new unique key for the object in the
                // database.
                auth.currentUser?.let { user ->
                    database.child("dummies")
                        .child(user.uid)
                        .push()
                        .key?.let { uid->

                            // Insert the object in the database.
                            database.child("dummies")
                                .child(user.uid)
                                .child(uid)
                                .setValue(dummy)
                        }
                }
            }
            dialog.dismiss()
        }

        // Create and return a new instance of MaterialAlertDialogBuilder.
        return MaterialAlertDialogBuilder(requireContext()).apply {
            setView(binding.root)
            setTitle(getString(R.string.dialog_add_title))
            setMessage(getString(R.string.dialog_add_message))
            setPositiveButton(getString(R.string.button_add), onPositiveButtonClick)
            setNegativeButton(getString(R.string.button_cancel)) { dialog, _ -> dialog.dismiss() }
        }.create()
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
