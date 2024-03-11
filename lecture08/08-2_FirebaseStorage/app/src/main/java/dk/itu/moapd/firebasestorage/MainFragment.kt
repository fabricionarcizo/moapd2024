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

import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import dk.itu.moapd.firebasestorage.databinding.FragmentMainBinding

/**
 * A fragment to display the main screen of the app.
 */
class MainFragment : Fragment(), OnItemClickListener {

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

        // Initialize Firebase Auth and connect to the Firebase Realtime Database.
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            val query = Firebase.database(DATABASE_URL).reference
                .child("images")
                .child(userId)
                .orderByChild("createdAt")

            // A class provided by FirebaseUI to make a query in the database to fetch the
            // appropriate data.
            val options = FirebaseRecyclerOptions.Builder<Image>()
                .setQuery(query, Image::class.java)
                .setLifecycleOwner(this)
                .build()

            // Create the custom adapter to bind a list of strings.
            val adapter = CustomAdapter(this@MainFragment, options)

            // Setup the RecyclerView.
            setupRecyclerView(adapter)
        }
    }

    /**
     * Configures the RecyclerView with a custom adapter and sets up its layout manager, item
     * animator, and item decoration. The layout manager is determined to be a GridLayoutManager
     * with a column count based on the current orientation of the device (2 columns in portrait
     * mode and 4 columns in landscape mode). The item animator is explicitly set to null to disable
     * RecyclerView animations for item changes. Additionally, an item decoration is added to
     * provide uniform padding between items in the grid.
     *
     * @param adapter The CustomAdapter instance that will be used to populate items within the
     *      RecyclerView. This adapter is responsible for creating ViewHolder instances and binding
     *      data to them when the RecyclerView requests it.
     */
    private fun setupRecyclerView(adapter: CustomAdapter) {
        binding.recyclerView.apply {

            // Define the recycler view layout manager.
            val padding = 2
            val columns = when (resources.configuration.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> 2
                else -> 4
            }

            // Set the layout manager, item animator, and item decoration.
            layoutManager = GridLayoutManager(requireContext(), columns)
            itemAnimator = null
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.set(padding, padding, padding, padding)
                }
            })

            // Set the adapter.
            this.adapter = adapter
        }
    }

    /**
     * This method will be executed when the user press an item in the `RecyclerView` for a short
     * time.
     *
     * @param image An instance of `Image` class.
     */
    override fun onItemClickListener(image: Image) {
        // Using Gson to convert the `Image` instance to a JSON string.
        val json = Gson().toJson(image)
        val bundle = bundleOf("ARG_IMAGE" to json)

        // Navigate to the `ImageFragment` passing the `Image` instance as an argument.
        requireActivity().findNavController(R.id.fragment_container_view).navigate(
            R.id.action_main_to_image, bundle
        )
    }

    /**
     * This method will be executed when the user press an item in the `RecyclerView` for a long
     * time.
     *
     * @param image An instance of `Image` class.
     * @param position The position of selected view holder in the `RecyclerView`.
     */
    override fun onItemLongClickListener(image: Image, position: Int) {
        binding.recyclerView.adapter?.let { adapter ->
            DeleteDataDialogFragment(image, position, adapter as CustomAdapter).apply {
                isCancelable = false
            }.show(requireActivity().supportFragmentManager, "DeleteDataDialogFragment")
        }
    }

}
