package com.hiskytechs.muhallinewuserapp.Ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.databinding.ActivityMapBinding
import java.util.Locale

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding
    private var map: GoogleMap? = null
    private var mode: String = MODE_VIEW
    private var initialCity: String = ""
    private var titleLabel: String = ""
    private var selectedCity: String = ""
    private var selectedLatLng: LatLng? = null
    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (granted) {
                moveToCurrentLocation()
            } else {
                Toast.makeText(this, R.string.location_permission_required, Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        runCatching { MapsInitializer.initialize(applicationContext) }

        mode = intent.getStringExtra(EXTRA_MODE) ?: MODE_VIEW
        initialCity = intent.getStringExtra(EXTRA_CITY).orEmpty()
        titleLabel = intent.getStringExtra(EXTRA_TITLE).orEmpty()

        binding.toolbar.title = if (mode == MODE_PICK) {
            getString(R.string.select_city)
        } else {
            titleLabel.ifBlank { getString(R.string.map) }
        }
        binding.tvMapSubtitle.text = if (mode == MODE_PICK) {
            getString(R.string.map_select_hint)
        } else {
            getString(R.string.map_view_hint, initialCity.ifBlank { getString(R.string.map) })
        }
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnConfirmLocation.setOnClickListener {
            val latLng = selectedLatLng
            setResult(
                Activity.RESULT_OK,
                intent.apply {
                    putExtra(EXTRA_SELECTED_CITY, selectedCity.ifBlank { initialCity })
                    putExtra(EXTRA_SELECTED_ADDRESS, selectedCity.ifBlank { initialCity })
                    if (latLng != null) {
                        putExtra(EXTRA_SELECTED_LATITUDE, latLng.latitude)
                        putExtra(EXTRA_SELECTED_LONGITUDE, latLng.longitude)
                    }
                }
            )
            finish()
        }
        binding.btnConfirmLocation.isEnabled = mode == MODE_VIEW
        binding.btnConfirmLocation.text = if (mode == MODE_PICK) {
            getString(R.string.use_selected_city)
        } else {
            getString(R.string.done)
        }
        binding.layoutMapSearch.visibility = if (mode == MODE_PICK) View.VISIBLE else View.GONE
        LocationSupport.bindSuggestions(this, binding.etMapSearch)
        binding.etMapSearch.setText(initialCity)
        binding.etMapSearch.setOnItemClickListener { _, _, _, _ ->
            searchAndMove(binding.etMapSearch.text?.toString().orEmpty())
        }
        binding.btnSearchLocation.setOnClickListener {
            searchAndMove(binding.etMapSearch.text?.toString().orEmpty())
        }
        binding.btnUseCurrentLocation.setOnClickListener {
            requestCurrentLocation()
        }

        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.mapContainer, mapFragment)
            .commitNow()
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true
        if (hasLocationPermission()) {
            runCatching { googleMap.isMyLocationEnabled = true }
        }
        val fallbackCity = initialCity.ifBlank { getString(R.string.default_city_name) }
        val targetLatLng = geocodeCity(fallbackCity) ?: LatLng(24.8607, 67.0011)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLatLng, 11f))
        updateSelectedLocation(targetLatLng, fallbackCity)

        if (mode == MODE_PICK) {
            binding.btnConfirmLocation.isEnabled = true
            googleMap.setOnMapLongClickListener { latLng ->
                updateSelectedLocation(latLng)
            }
            googleMap.setOnMapClickListener { latLng ->
                updateSelectedLocation(latLng)
            }
        }
    }

    private fun requestCurrentLocation() {
        if (hasLocationPermission()) {
            moveToCurrentLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun moveToCurrentLocation() {
        val location = lastKnownLocation()
        if (location == null) {
            Toast.makeText(this, R.string.location_not_found, Toast.LENGTH_SHORT).show()
            return
        }
        updateSelectedLocation(LatLng(location.latitude, location.longitude))
    }

    private fun searchAndMove(query: String) {
        val latLng = geocodeCity(query)
        if (latLng == null) {
            Toast.makeText(this, R.string.location_not_found, Toast.LENGTH_SHORT).show()
            return
        }
        updateSelectedLocation(latLng, query)
    }

    private fun updateSelectedLocation(latLng: LatLng, label: String? = null) {
        val resolvedLabel = label?.takeIf { it.isNotBlank() }
            ?: reverseGeocode(latLng)
            ?: getString(R.string.map_coordinate_fallback, latLng.latitude, latLng.longitude)

        selectedLatLng = latLng
        selectedCity = resolvedLabel
        map?.clear()
        map?.addMarker(MarkerOptions().position(latLng).title(resolvedLabel))
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
        binding.etMapSearch.setText(resolvedLabel, false)
        binding.tvMapSubtitle.text = getString(R.string.selected_city_label, resolvedLabel)
        binding.btnConfirmLocation.isEnabled = true
    }

    private fun geocodeCity(city: String): LatLng? {
        return runCatching {
            val geocoder = Geocoder(this, Locale.getDefault())
            val results = geocoder.getFromLocationName(city, 1)
            val address = results?.firstOrNull() ?: return null
            LatLng(address.latitude, address.longitude)
        }.getOrNull()
    }

    private fun reverseGeocode(latLng: LatLng): String? {
        return runCatching {
            val geocoder = Geocoder(this, Locale.getDefault())
            val results = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            val address = results?.firstOrNull() ?: return null
            listOfNotNull(
                address.featureName,
                address.subLocality,
                address.locality,
                address.subAdminArea
            ).distinct().joinToString(", ").ifBlank {
                address.locality
                ?: address.subAdminArea
                ?: address.adminArea
                ?: address.countryName
            }
        }.getOrNull()
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
    }

    private fun lastKnownLocation(): Location? {
        if (!hasLocationPermission()) return null
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
            .mapNotNull { provider ->
                runCatching {
                    if (manager.isProviderEnabled(provider)) {
                        manager.getLastKnownLocation(provider)
                    } else {
                        null
                    }
                }.getOrNull()
            }
            .maxByOrNull { it.time }
    }

    companion object {
        const val EXTRA_MODE = "mode"
        const val EXTRA_CITY = "city"
        const val EXTRA_TITLE = "title"
        const val EXTRA_SELECTED_CITY = "selected_city"
        const val EXTRA_SELECTED_ADDRESS = "selected_address"
        const val EXTRA_SELECTED_LATITUDE = "selected_latitude"
        const val EXTRA_SELECTED_LONGITUDE = "selected_longitude"
        const val MODE_VIEW = "view"
        const val MODE_PICK = "pick"
    }
}
