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
package dk.itu.moapd.geolocation

import android.Manifest
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dk.itu.moapd.geolocation.SharedPreferenceUtil.toSimpleDateFormat
import dk.itu.moapd.geolocation.databinding.FragmentMainBinding

/**
 * A fragment to display the main screen of the app.
 */
class MainFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Receiver for location broadcasts from [LocationService].
     */
    private inner class LocationBroadcastReceiver : BroadcastReceiver() {

        /**
         * This method is called when the BroadcastReceiver is receiving an Intent broadcast. During
         * this time you can use the other methods on BroadcastReceiver to view/modify the current
         * result values. This method is always called within the main thread of its process, unless
         * you explicitly asked for it to be scheduled on a different thread using
         * `registerReceiver(BroadcastReceiver, IntentFilter, String, android.os.Handler)`. When it
         * runs on the main thread you should never perform long-running operations in it (there is
         * a timeout of 10 seconds that the system allows before considering the receiver to be
         * blocked and a candidate to be killed). You cannot launch a popup dialog in your
         * implementation of onReceive().
         *
         * If this BroadcastReceiver was launched through a `receiver` tag, then the object is no
         * longer alive after returning from this function. This means you should not perform any
         * operations that return a result to you asynchronously. If you need to perform any follow
         * up background work, schedule a `android.app.job.JobService` with
         * `android.app.job.JobScheduler`.
         *
         * If you wish to interact with a service that is already running and previously bound using
         * `bindService(Intent, ServiceConnection, int)`, you can use `peekService()`.
         *
         * The Intent filters used in `registerReceiver()` and in application manifests are not
         * guaranteed to be exclusive. They are hints to the operating system about how to find
         * suitable recipients. It is possible for senders to force delivery to specific recipients,
         * bypassing filter resolution.  For this reason, `onReceive(Context, Intent)`
         * implementations should respond only to known actions, ignoring any unexpected Intents
         * that they may receive.
         *
         * @param context The Context in which the receiver is running.
         * @param intent The Intent being received.
         */
        override fun onReceive(context: Context, intent: Intent) {
            val location = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getParcelableExtra(LocationService.EXTRA_LOCATION, Location::class.java)
            else
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(LocationService.EXTRA_LOCATION)
            location?.let {
                updateLocationDetails(it)
            }
        }

    }

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
     * A set of private constants used in this class.
     */
    companion object {
        private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
    }

    /**
     * The SharedPreferences instance that can be used to save and retrieve data.
     */
    private lateinit var sharedPreferences: SharedPreferences

    /**
     * Receiver for location broadcasts from [LocationService].
     */
    private lateinit var locationBroadcastReceiver: LocationBroadcastReceiver

    /**
     * Provides location updates for while-in-use feature.
     */
    private var locationService: LocationService? = null

    /**
     * A flag to indicate whether a bound to the service.
     */
    private var locationServiceBound = false

    /**
     * Defines callbacks for service binding, passed to `bindService()`.
     */
    private val serviceConnection = object : ServiceConnection {

        /**
         * Called when a connection to the Service has been established, with the
         * `android.os.IBinder` of the communication channel to the Service.
         *
         * If the system has started to bind your client app to a service, it's possible that your
         * app will never receive this callback. Your app won't receive a callback if there's an
         * issue with the service, such as the service crashing while being created.
         *
         * @param name The concrete component name of the service that has been connected.
         * @param service The IBinder of the Service's communication channel, which you can now make
         *      calls on.
         */
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.service
            locationServiceBound = true
        }

        /**
         * Called when a connection to the Service has been lost. This typically happens when the
         * process hosting the service has crashed or been killed. This does not remove the
         * ServiceConnection itself -- this binding to the service will remain active, and you will
         * receive a call to `onServiceConnected()` when the Service is next running.
         *
         * @param name The concrete component name of the service whose connection has been lost.
         */
        override fun onServiceDisconnected(name: ComponentName) {
            locationService = null
            locationServiceBound = false
        }
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

        // Get the SharedPreferences instance.
        sharedPreferences = requireActivity()
            .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        // Initialize the broadcast receiver.
        locationBroadcastReceiver = LocationBroadcastReceiver()

        // Define the UI behavior using lambda expressions.
        binding.buttonState.setOnClickListener {
            sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)
                .let { enabled ->
                    if (enabled) {
                        resetLocationDetails()
                        locationService?.unsubscribeToLocationUpdates()
                    } else {
                        if (checkPermission()) {
                            locationService?.subscribeToLocationUpdates()
                        } else {
                            requestUserPermissions()
                        }
                    }
                }
        }
    }

    /**
     * Called when the Fragment is visible to the user. This is generally tied to
     * `Activity.onStart()` of the containing Activity's lifecycle.
     */
    override fun onStart() {
        super.onStart()

        // Update the UI to reflect the state of the service.
        updateButtonState(
            sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)
        )

        // Register the shared preference change listener.
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        // Bind to the service.
        Intent(requireContext(), LocationService::class.java).let { serviceIntent ->
            requireActivity().bindService(
                serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    /**
     * Called when the fragment is visible to the user and actively running. This is generally tied
     * to `Activity.onResume()` of the containing Activity's lifecycle.
     */
    override fun onResume() {
        super.onResume()

        // Register the broadcast receiver.
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            locationBroadcastReceiver,
            IntentFilter(LocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
        )
    }

    /**
     * Called when the Fragment is no longer resumed. This is generally tied to `Activity.onPause()`
     * of the containing Activity's lifecycle.
     */
    override fun onPause() {
        // Unregister the broadcast receiver.
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(
            locationBroadcastReceiver
        )
        super.onPause()
    }

    /**
     * Called when the Fragment is no longer started. This is generally tied to `Activity.onStop()`
     * of the containing Activity's lifecycle.
     */
    override fun onStop() {
        // Unbind from the service.
        if (locationServiceBound) {
            requireActivity().unbindService(serviceConnection)
            locationServiceBound = false
        }

        // Unregister the shared preference change listener.
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
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
     * This method checks if the user allows the application uses all location-aware resources to
     * monitor the user's location.
     *
     * @return A boolean value with the user permission agreement.
     */
    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    /**
     * Create a set of dialogs to show to the users and ask them for permissions to get the device's
     * resources.
     */
    private fun requestUserPermissions() {
        if (!checkPermission())
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
    }

    /**
     * Called when a shared preference is changed, added, or removed. This may be called even if a
     * preference is set to its existing value. This callback will be run on your main thread.
     *
     * @param sharedPreferences The `SharedPreferences` that received the change.
     * @param key The key of the preference that was changed, added, or removed. Apps targeting
     *      android.os.Build.VERSION_CODES#R on devices running OS versions
     *      android.os.Build.VERSION_CODES#R Android R} or later, will receive a `null` value when
     *      preferences are cleared.
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if (key == SharedPreferenceUtil.KEY_FOREGROUND_ENABLED)
            sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)
                .let(::updateButtonState)
    }

    /**
     * Updates the text of the button based on the `trackingLocation` state.
     *
     * @param trackingLocation Boolean indicating whether the location tracking is active or not.
     */
    private fun updateButtonState(trackingLocation: Boolean) {
        val buttonText = if (trackingLocation) R.string.button_stop else R.string.button_start
        binding.buttonState.text = getString(buttonText)
    }

    /**
     * Updates the location details into the UI components. Sets the latitude, longitude, altitude,
     * and speed in the respective EditTexts.
     *
     * @param location The location to be updated in the UI components.
     */
    private fun updateLocationDetails(location: Location) {
        with(binding) {
            // Fill the event details into the UI components.
            editTextLatitude.setText(location.latitude.toString())
            editTextLongitude.setText(location.longitude.toString())
            editTextAltitude.setText(location.altitude.toString())
            editTextSpeed.setText(
                getString(R.string.text_speed_km, location.speed.toInt())
            )
            editTextTime.setText(location.time.toSimpleDateFormat())
        }
    }

    /**
     * Resets the location details into the UI components. Sets the latitude, longitude, altitude,
     * and speed in the respective EditTexts.
     */
    private fun resetLocationDetails() {
        with(binding) {
            // Fill the event details into the UI components.
            editTextLatitude.setText(getString(R.string.text_not_available))
            editTextLongitude.setText(getString(R.string.text_not_available))
            editTextAltitude.setText(getString(R.string.text_not_available))
            editTextSpeed.setText(getString(R.string.text_not_available))
            editTextTime.setText(getString(R.string.text_not_available))
        }
    }

}
