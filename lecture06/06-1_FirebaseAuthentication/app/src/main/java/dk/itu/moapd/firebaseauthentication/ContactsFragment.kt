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

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.javafaker.Faker
import dk.itu.moapd.firebaseauthentication.databinding.FragmentContactsBinding
import java.util.Random

/**
 * A fragment to display a list of contacts with name and phone number. In practice, we are not
 * going to design the UI components in this way. We are going to use ListView, ViewHolder, and
 * RecyclerView to display the lists dynamically.
 */
class ContactsFragment : Fragment() {

    /**
     * View binding is a feature that allows you to more easily write code that interacts with
     * views. Once view binding is enabled in a module, it generates a binding class for each XML
     * layout file present in that module. An instance of a binding class contains direct references
     * to all views that have an ID in the corresponding layout.
     */
    private var _binding: FragmentContactsBinding? = null

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
    ): View = FragmentContactsBinding.inflate(inflater, container, false).also {
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

        // Create a new instance of the Faker class to generate fake data.
        val faker = Faker(Random(42))

        // IMPORTANT: This is a terrible way to display a list of contacts. DON'T DO THIS! In the
        //            future, we are going to use ListView, ViewHolder, and RecyclerView to display
        //            lists dynamically.
        val inflater = LayoutInflater.from(requireContext())

        (1..50).forEach { _ ->
            val contactView = createContactView(inflater, faker)
            binding.linearLayoutContacts.addView(contactView)

            val dividerView = createDividerView()
            binding.linearLayoutContacts.addView(dividerView)
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
     * Creates a view representing a contact item.
     *
     * @param inflater The layout inflater to inflate the view.
     * @param faker The faker instance to generate fake contact data.
     *
     * @return The created contact view.
     */
    private fun createContactView(inflater: LayoutInflater, faker: Faker): View {
        // Create a ViewGroup to act as the contact item.
        val contactView = inflater.inflate(
            R.layout.contact_row_item, binding.linearLayoutContacts, false) as ViewGroup

        // Set the contact data view.
        val textViewLetter = contactView.findViewById<TextView>(R.id.text_view_letter)
        val textViewName = contactView.findViewById<TextView>(R.id.text_view_name)
        val textViewPhone = contactView.findViewById<TextView>(R.id.text_view_phone)

        // Generate fake contact data based on The Lord of the Rings' characters.
        faker.lordOfTheRings().character().let { name ->
            textViewLetter.text = name.first().uppercase()
            textViewName.text = name
        }
        textViewPhone.text = faker.phoneNumber().phoneNumber()

        return contactView
    }

    /**
     * Creates a horizontal line view to be used as a divider. The height of the line is set to 2dp.
     *
     * @return The created line view.
     */
    private fun createDividerView(): View = View(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            2 // Height of the line (2dp in this example)
        )
        setBackgroundColor(
            ContextCompat.getColor(context, androidx.appcompat.R.color.primary_dark_material_dark))
    }

}
