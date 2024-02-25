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

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.firebaseauthentication.databinding.ActivityMainBinding

/**
 * An activity class with several methods to manage the main activity of Firebase Authentication
 * application.
 */
class MainActivity : AppCompatActivity() {

    /**
     * The entry point of the Firebase Authentication SDK.
     */
    private lateinit var auth: FirebaseAuth

    /**
     * View binding is a feature that allows you to more easily write code that interacts with
     * views. Once view binding is enabled in a module, it generates a binding class for each XML
     * layout file present in that module. An instance of a binding class contains direct references
     * to all views that have an ID in the corresponding layout.
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * Configuration options for `NavigationUI` methods that interact with implementations of the
     * app bar pattern such as `androidx.appcompat.widget.Toolbar`,
     * `com.google.android.material.appbar.CollapsingToolbarLayout`, and
     * `androidx.appcompat.app.ActionBar`.
     */
    private lateinit var appBarConfiguration: AppBarConfiguration

    /**
     * Called when the activity is starting. This is where most initialization should go: calling
     * `setContentView(int)` to inflate the activity's UI, using `findViewById()` to
     * programmatically interact with widgets in the UI, calling
     * `managedQuery(android.net.Uri, String[], String, String[], String)` to retrieve cursors for
     * data being displayed, etc.
     *
     * You can call `finish()` from within this function, in which case `onDestroy()` will be
     * immediately called after `onCreate()` without any of the rest of the activity lifecycle
     * (`onStart()`, `onResume()`, onPause()`, etc) executing.
     *
     * <em>Derived classes must call through to the super class's implementation of this method. If
     * they do not, an exception will be thrown.</em>
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in `onSaveInstanceState()`.
     * <b><i>Note: Otherwise it is null.</i></b>
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        // Sets whether the decor view should fit root-level content views for `WindowInsetsCompat`.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        // Migrate from Kotlin synthetics to Jetpack view binding.
        // https://developer.android.com/topic/libraries/view-binding/migration
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Search the view hierarchy and fragment for the `NavController` and return it to you.
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController

        // Setup the action bar.
        setSupportActionBar(binding.toolbar)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Setup the bottom navigation (portrait) and the navigation rail (landscape).
        binding.bottomNavigation?.setupWithNavController(navController)
        binding.navigationRail?.setupWithNavController(navController)

        // Initialize Firebase Auth.
        auth = FirebaseAuth.getInstance()
    }

    /**
     * Called after `onCreate()` method; or after `onRestart()` method when the activity had been
     * stopped, but is now again being displayed to the user. It will usually be followed by
     * `onResume()`. This is a good place to begin drawing visual elements, running animations, etc.
     *
     * You can call `finish()` from within this function, in which case `onStop()` will be
     * immediately called after `onStart()` without the lifecycle transitions in-between
     * (`onResume()`, `onPause()`, etc) executing.
     *
     * <em>Derived classes must call through to the super class's implementation of this method. If
     * they do not, an exception will be thrown.</em>
     */
    override fun onStart() {
        super.onStart()

        // Redirect the user to the LoginActivity if they are not logged in.
        auth.currentUser ?: startLoginActivity()
    }

    /**
     * This method starts the login activity which allows the user log in or sign up to the Firebase
     * Authentication application.
     *
     * Before accessing the main activity, the user must log in the application through a Firebase
     * Auth backend service. The method starts a new activity using explicit intent and used the
     * method `finish()` to disable back button.
     */
    private fun startLoginActivity() {
        Intent(this, LoginActivity::class.java).apply {
            // An alternative to instead of calling finish() method.
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let(::startActivity)
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You should place your menu
     * items in to menu.  This is only called once, the first time the options menu is displayed.
     * To update the menu every time it is displayed, see `onPrepareOptionsMenu(Menu)`.  The default
     * implementation populates the menu with standard system menu items.  These are placed in the
     * `Menu#CATEGORY_SYSTEM` group so that they will be correctly ordered with application-defined
     * menu items.  Deriving classes should always call through to the base implementation.  You can
     * safely hold on to menu (and any items created from it), making modifications to it as
     * desired, until the next time `onCreateOptionsMenu()` is called.  When you add items to the
     * menu, you can implement the Activity's `onOptionsItemSelected(MenuItem)` method to handle
     * them there.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return `true` for the menu to be displayed; if you return `false` it will
     *      not be shown.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    /**
     * This hook is called whenever an item in your options menu is selected.  The default
     * implementation simply returns `false` to have the normal processing happen (calling the
     * item's `Runnable` or sending a message to its `Handler` as appropriate).  You can use this
     * method for any items for which you would like to do processing without those other
     * facilities.  Derived classes should call through to the base class for it to perform the
     * default menu handling.
     *
     * @param item The menu item that was selected.  This value cannot be `null`.
     *
     * @return Return `false` to allow normal menu processing to proceed, `true` to consume it here.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        // Handle top app bar menu item clicks.
        R.id.action_user_info -> {
            UserInfoDialogFragment().apply {
                isCancelable = false
            }.also { dialogFragment ->
                dialogFragment.show(supportFragmentManager, "UserInfoDialogFragment")
            }
            true
        }
        R.id.action_logout -> {
            auth.signOut()
            startLoginActivity()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    /**
     * This method is called whenever the user chooses to navigate Up within your application's
     * activity hierarchy from the action bar.
     *
     * If a parent was specified in the manifest for this activity or an activity-alias to it,
     * default Up navigation will be handled automatically. See `getSupportParentActivityIntent()`
     * for how to specify the parent. If any activity along the parent chain requires extra `Intent`
     * arguments, the `Activity` subclass should override the method
     * `onPrepareSupportNavigateUpTaskStack(androidx.core.app.TaskStackBuilder)` to supply those
     * arguments.
     *
     * @return `true` if Up navigation completed successfully and this `Activity` was finished,
     *      `false` otherwise.
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment_container_view)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}
