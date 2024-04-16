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
package dk.itu.moapd.bluetoothconnection

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dk.itu.moapd.bluetoothconnection.databinding.FragmentMainBinding

/**
 * A fragment to display the main screen of the app.
 */
class MainFragment : Fragment() {

    /**
     * Create a BroadcastReceiver for ACTION_FOUND.
     */
    private val broadcastReceiver = object : BroadcastReceiver() {

        /**
         * This method is called when the `BroadcastReceiver` is receiving an Intent broadcast.
         * During this time you can use the other methods on `BroadcastReceiver` to view/modify the
         * current result values. This method is always called within the main thread of its
         * process, unless you explicitly asked for it to be scheduled on a different thread using
         * `registerReceiver(BroadcastReceiver, IntentFilter, String, Handler)`. When it runs on the
         * main thread you should never perform long-running operations in it (there is a timeout of
         * 10 seconds that the system allows before considering the receiver to be blocked and a
         * candidate to be killed). You cannot launch a popup dialog in your implementation of
         * `onReceive()`.
         *
         * @param context The Context in which the receiver is running.
         * @param intent The Intent being received.
         */
        override fun onReceive(context: Context, intent: Intent) {
            // Check user's permission.
            if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) return

            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice object and its info
                    // from the Intent.
                    val device = when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                            intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                        else ->
                            @Suppress("DEPRECATION")
                            intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE) as? BluetoothDevice
                    }

                    // Device parameters.
                    device?.let {
                        val deviceName = it.name
                        val deviceHardwareAddress = it.address
                        adapter.addItem(DeviceModel(deviceName, deviceHardwareAddress, true))
                    }
                }
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
     * The adapter for the recycler view.
     */
    private lateinit var adapter: CustomAdapter

    /**
     * A set of static attributes used in this activity class.
     */
    companion object {

        private const val REQUEST_CODE_PERMISSIONS = 10

        @RequiresApi(Build.VERSION_CODES.S)
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
    }

    /**
     * This object launches a new permission dialog and receives back the user's permission.
     */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        permissionsResult(result)
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
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Request camera permissions.
        if (!allPermissionsGranted())
            requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)

        // Setup the Bluetooth, recycler view, and broadcast receiver.
        setupBluetooth()
        setupRecyclerView()
        registerBluetoothReceiver()
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
     * A method to show a dialog to the users as ask permission to access their Android mobile
     * device resources.
     *
     * @return `PackageManager#PERMISSION_GRANTED` if the given pid/uid is allowed that permission,
     *      or `PackageManager#PERMISSION_DENIED` if it is not.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Handles the result of permissions request. If any permission is denied, it finishes the
     * current activity.
     *
     * @param results A map containing the results of permissions request, where keys are permission
     *      names and values are boolean indicating whether the permission is granted or not.
     */
    private fun permissionsResult(results: Map<String, Boolean>) {
        if (results.values.contains(false)) {
            requireActivity().finish()
        }
    }

    /**
     * Sets up Bluetooth functionality including enabling/disabling Bluetooth and making the device
     * discoverable.
     */
    private fun setupBluetooth() {
        val bluetoothManager = requireContext().getSystemService(
            Context.BLUETOOTH_SERVICE
        ) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        setupBluetoothButton(bluetoothAdapter)
        setupVisibleButton()
    }

    /**
     * Sets up the Bluetooth button's functionality including enabling/disabling Bluetooth.
     *
     * @param bluetoothAdapter The Bluetooth adapter instance to be used for toggling Bluetooth.
     */
    private fun setupBluetoothButton(bluetoothAdapter: BluetoothAdapter) {
        binding.bluetoothButton.apply {
            isEnabled = bluetoothAdapter.isEnabled
            text = if (isEnabled) getString(R.string.turn_off) else getString(R.string.turn_on)
            setOnClickListener {
                toggleBluetooth(bluetoothAdapter)
            }
        }
    }

    /**
     * Toggles the Bluetooth state.
     *
     * If Bluetooth is currently disabled, this method prompts the user to enable it. If Bluetooth
     * is currently enabled, this method disables it.
     *
     * @param bluetoothAdapter The Bluetooth adapter instance to be toggled.
     */
    private fun toggleBluetooth(bluetoothAdapter: BluetoothAdapter) {
        if (!bluetoothAdapter.isEnabled) {
            startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            binding.bluetoothButton.text = getString(R.string.turn_off)
            binding.visibleButton.isEnabled = true
        } else {
            bluetoothAdapter.disable()
            binding.bluetoothButton.text = getString(R.string.turn_on)
            binding.visibleButton.isEnabled = false
        }
    }

    /**
     * Sets up the behavior of the "Visible" button. When clicked, it prompts the user to make the
     * device discoverable via Bluetooth.
     */
    private fun setupVisibleButton() {
        binding.visibleButton.setOnClickListener {
            startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
        }
    }

    /**
     * Sets up the RecyclerView to display the list of devices. Initializes an empty list of devices
     * and an adapter, configures the RecyclerView with the adapter and layout manager, and adds
     * paired devices to the RecyclerView.
     */
    private fun setupRecyclerView() {
        // Create an empty list of devices and an adapter.
        val data = ArrayList<DeviceModel>()
        val adapter = CustomAdapter(data)

        // Set the adapter and layout manager for the recycler view.
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            )
            this.adapter = adapter
        }

        // Add paired devices to the recycler view.
        showPairedDevices(adapter)
    }

    /**
     * Retrieves paired Bluetooth devices and adds them to the provided adapter.
     *
     * @param adapter The adapter used to display the list of paired devices.
     */
    private fun showPairedDevices(adapter: CustomAdapter) {
        // Get the paired devices.
        val bluetoothManager = requireContext().getSystemService(
            Context.BLUETOOTH_SERVICE
        ) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        // Add paired devices to the adapter.
        val pairedDevices = bluetoothAdapter.bondedDevices
        pairedDevices.forEach { device ->
            adapter.addItem(DeviceModel(device.name, device.address))
        }
    }

    /**
     * Registers a broadcast receiver to listen for Bluetooth device discovery events and starts
     * Bluetooth discovery.
     */
    private fun registerBluetoothReceiver() {
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothDevice.ACTION_NAME_CHANGED)
        }
        requireActivity().registerReceiver(broadcastReceiver, filter)

        val bluetoothManager = requireContext().getSystemService(
            Context.BLUETOOTH_SERVICE
        ) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        bluetoothAdapter.startDiscovery()
    }

}
