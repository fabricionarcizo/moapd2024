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
package dk.itu.moapd.firebasestorage

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

/**
 * A DialogFragment subclass for deleting data from a database.
 */
class DeleteDataDialogFragment(
    private val image: Image,
    private val position: Int,
    private val adapter: CustomAdapter
) : DialogFragment() {

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

        // Create a lambda for positive button click handling.
        val onPositiveButtonClick: (DialogInterface, Int) -> Unit = { dialog, _ ->
            // Remove an item from the Firebase Realtime database.
            adapter.getRef(position)
                .removeValue()
                .addOnSuccessListener {
                    // Remove the original image.
                    Firebase.storage(BUCKET_URL).reference
                        .child("${image.path}")
                        .delete()
            }
            dialog.dismiss()
        }

        // Create and return a new instance of MaterialAlertDialogBuilder.
        return MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(getString(R.string.dialog_delete_title))
            setMessage(getString(R.string.dialog_delete_message))
            setPositiveButton(getString(R.string.button_delete), onPositiveButtonClick)
            setNegativeButton(getString(R.string.button_cancel)) { dialog, _ -> dialog.dismiss() }
        }.create()
    }

}
