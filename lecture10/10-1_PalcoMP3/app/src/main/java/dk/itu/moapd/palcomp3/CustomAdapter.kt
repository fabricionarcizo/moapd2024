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
package dk.itu.moapd.palcomp3

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dk.itu.moapd.palcomp3.databinding.RowArtistItemBinding
import dk.itu.moapd.palcomp3.databinding.RowSongItemBinding

/**
 * A class to customize an adapter with a `ViewHolder` to populate a music dataset into a
 * `RecyclerView`.
 */
class CustomAdapter(
    private val itemClickListener: ItemClickListener,
    private val data: ArrayList<ExpandableModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * A set of private constants used in this class.
     */
    companion object {
        private val TAG = CustomAdapter::class.qualifiedName
    }

    /**
     * An internal view holder class used to represent the layout that shows a single `ArtistModel`
     * instance in the `RecyclerView`.
     */
    inner class ParentViewHolder(private val binding: RowArtistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * This method binds the `ViewHolder` instance and more cleanly separate concerns between
         * the view holder and the adapter.
         *
         * @param row A `row` instance in the `RecycleView`.
         * @param position The position of the item within the adapter's data set.
         */
        fun bind(row: ExpandableModel, position: Int) {
            with(binding) {
                // Set the artist data.
                textViewArtistName.text = row.artistParent.name
                textViewArtistStyle.text = row.artistParent.style

                // Download the artist photo.
                Picasso.get().load(row.artistParent.photo).into(imageViewArtist)

                // Toggle arrow visibility based on expansion state.
                imageViewOpenArrow.visibility = if (row.isExpanded) View.VISIBLE else View.GONE
                imageViewCloseArrow.visibility = if (row.isExpanded) View.GONE else View.VISIBLE

                // Set click listeners to toggle expansion.
                imageViewOpenArrow.setOnClickListener {
                    row.isExpanded = !row.isExpanded
                    toggleExpansionViews(binding)
                    expandRow(position)
                }

                imageViewCloseArrow.setOnClickListener {
                    row.isExpanded = !row.isExpanded
                    toggleExpansionViews(binding)
                    collapseRow(position)
                }
            }
        }

        /**
         * Toggles the visibility of expansion arrows in the artist row item.
         *
         * @param binding The binding object containing the views of the artist row item.
         */
        private fun toggleExpansionViews(binding: RowArtistItemBinding) {
            with(binding) {
                imageViewOpenArrow.visibility =
                    if (imageViewOpenArrow.visibility == View.VISIBLE) View.VISIBLE else View.GONE
                imageViewCloseArrow.visibility =
                    if (imageViewCloseArrow.visibility == View.VISIBLE) View.VISIBLE else View.GONE
            }
        }

    }

    /**
     * An internal view holder class used to represent the layout that shows a single `SongModel`
     * instance in the `RecyclerView`.
     */
    inner class ChildViewHolder(
        private val binding: RowSongItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * This method binds the `ViewHolder` instance and more cleanly separate concerns between
         * the view holder and the adapter.
         *
         * @param row A `row` instance in the `RecycleView`.
         */
        fun bind(row: ExpandableModel) {
            with(binding) {
                // Set the song data
                textViewSongName.text = row.songChild.name
                textViewAlbumName.text = row.songChild.album

                // Download the album photo
                Picasso.get().load(row.songChild.photo).into(imageViewAlbum)

                // Play control button
                val song = row.songChild
                binding.imageViewPlayer.setOnClickListener { togglePlayback(song) }
                updateButtonIcon(binding.imageViewPlayer, song)
            }
        }

        /**
         * Toggles the playback state of a song and updates the UI accordingly.
         *
         * @param song The song model representing the song whose playback state is to be toggled.
         */
        private fun togglePlayback(song: SongModel) {
            song.isPlaying = !song.isPlaying
            itemClickListener.onItemClickListener(song, binding.imageViewPlayer)
            updateButtonIcon(binding.imageViewPlayer, song)
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Create a new view, which defines the UI of the list item
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            ExpandableModel.CHILD ->
                ChildViewHolder(RowSongItemBinding.inflate(inflater, parent, false))
            else ->
                ParentViewHolder(RowArtistItemBinding.inflate(inflater, parent, false))
        }
    }

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
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        // Get item from the RecyclerView.
        val row = data[position]
        Log.d(TAG, "Populate an item at position: $position")

        when (row.type) {
            // Bind the view holder with the selected `Artist` data.
            ExpandableModel.PARENT -> (holder as ParentViewHolder).bind(row, position)

            // Bind the view holder with the selected `Song` data.
            ExpandableModel.CHILD -> (holder as ChildViewHolder).bind(row)
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = data.size

    /**
     * Return the view type of the item at `position` for the purposes of view recycling.
     *
     * The default implementation of this method returns 0, making the assumption of a single view
     * type for the adapter. Unlike ListView adapters, types need not be contiguous. Consider using
     * id resources to uniquely identify item view types.
     *
     * @param position Position to query.
     *
     * @return An integer value identifying the type of the view needed to represent the item at
     *      `position`. Type codes need not be contiguous.
     */
    override fun getItemViewType(position: Int): Int = data[position].type

    /**
     * This method updates the play control button.
     *
     * @param button The play control button.
     * @param song The selected song.
     */
    private fun updateButtonIcon(button: ImageView, song: SongModel) {
        button.setImageResource(
            if (song.isPlaying)
                R.drawable.baseline_stop_circle_64
            else
                R.drawable.baseline_play_circle_outline_64
        )
    }

    /**
     * This method expand a set of rows to show the songs of a specific artist.
     *
     * @param position Position to query.
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun expandRow(position: Int) {
        data[position].let { row ->
            val nextPosition = position + 1
            when (row.type) {
                ExpandableModel.PARENT -> {
                    outerloop@ while (true) {
                        if (nextPosition == data.size ||
                            data[nextPosition].type == ExpandableModel.PARENT
                        )
                            break@outerloop
                        data.removeAt(nextPosition)
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

    /**
     * This method collapses a set of rows to hide the songs of a specific artist.
     *
     * @param position Position to query.
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun collapseRow(position: Int) {
        data[position].let { row ->
            when (row.type) {
                ExpandableModel.PARENT -> {
                    val childSongs = row.artistParent.songs.map {
                        ExpandableModel(ExpandableModel.CHILD, it)
                    }
                    data.addAll(position + 1, childSongs)
                    notifyDataSetChanged()
                }

                ExpandableModel.CHILD -> notifyDataSetChanged()
            }
        }
    }

}
