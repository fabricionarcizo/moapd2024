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
package dk.itu.moapd.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import dk.itu.moapd.recyclerview.databinding.RowItemBinding

/**
 * A class to customize an adapter with a `ViewHolder` to populate a dummy dataset into a
 * `RecyclerView`.
 */
class CustomAdapter(private val data: ArrayList<DummyModel>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    /**
     * A set of private constants used in this class.
     */
    companion object {
        private val TAG = CustomAdapter::class.qualifiedName
    }

    /**
     * An internal view holder class used to represent the layout that shows a single `DummyModel`
     * instance in the `RecyclerView`.
     */
    class ViewHolder(private val binding: RowItemBinding) : RecyclerView.ViewHolder(binding.root) {

        /**
         * This method binds the `ViewHolder` instance and more cleanly separate concerns between
         * the view holder and the adapter.
         *
         * @param dummy The current `Dummy` object.
         */
        fun bind(dummy: DummyModel) {
            with(binding) {
                // Fill out the Material Design card.
                Picasso.get().load(dummy.url).into(imageViewPhoto)
                textViewTitle.text = dummy.cityName
                textViewSubtitle.text = binding.root.context.getString(
                    R.string.secondary_text, dummy.country, dummy.zipCode
                )
                textViewDescription.text = dummy.description

                // Set the button click listeners using method references.
                buttonThumbUp.setOnClickListener {
                    showSnackBar("Thumb up: ${dummy.cityName}", it)
                }
                buttonFavorite.setOnClickListener {
                    showSnackBar("Favorite: ${dummy.cityName}", it)
                }
                buttonShare.setOnClickListener {
                    showSnackBar("Share: ${dummy.cityName}", it)
                }
            }
        }


        companion object {
            /**
             * Displays a SnackBar to show a brief message about the clicked button.
             *
             * The SnackBar is created using the clicked button and is shown at the bottom of the screen.
             *
             * @param message The message to be displayed in the SnackBar.
             * @param view The view to find a parent from.
             */
            private fun showSnackBar(message: String, view: View) {
                Snackbar.make(
                    view, message, Snackbar.LENGTH_SHORT
                ).show()
            }
        }

    }

    /**
     * Called when the `RecyclerView` needs a new `ViewHolder` of the given type to represent an
     * item.
     *
     * This new `ViewHolder` should be constructed with a new `View` that can represent the items of
     * the given type. You can either create a new `View` manually or inflate it from an XML layout
     * file.
     *
     * The new `ViewHolder` will be used to display items of the adapter using
     * `onBindViewHolder(ViewHolder, int, List)`. Since it will be re-used to display different
     * items in the data set, it is a good idea to cache references to sub views of the `View` to
     * avoid unnecessary `findViewById(int)` calls.
     *
     * @param parent The `ViewGroup` into which the new `View` will be added after it is bound to an
     *      adapter position.
     * @param viewType The view type of the new `View`.
     *
     * @return A new `ViewHolder` that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = RowItemBinding
        .inflate(LayoutInflater.from(parent.context), parent, false)
        .let(::ViewHolder)

    /**
     * Called by the `RecyclerView` to display the data at the specified position. This method
     * should update the contents of the `itemView()` to reflect the item at the given position.
     *
     * Note that unlike `ListView`, `RecyclerView` will not call this method again if the position
     * of the item changes in the data set unless the item itself is invalidated or the new position
     * cannot be determined. For this reason, you should only use the `position` parameter while
     * acquiring the related data item inside this method and should not keep a copy of it. If you
     * need the position of an item later on (e.g., in a click listener), use
     * `getBindingAdapterPosition()` which will have the updated adapter position.
     *
     * Override `onBindViewHolder(ViewHolder, int, List)` instead if Adapter can handle efficient
     * partial bind.
     *
     * @param holder The `ViewHolder` which should be updated to represent the contents of the item
     *      at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "Populate an item at position: $position")

        // Bind the view holder with the selected `DummyModel` data.
        data[position].let(holder::bind)
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = data.size

}
