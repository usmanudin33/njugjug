@file:Suppress("DEPRECATION", "NAME_SHADOWING")

package njugjug.project.njugjug

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST



class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var latitudeTextView: TextView
    private lateinit var longitudeTextView: TextView
    private var handler: Handler? = null
    private var isSendingLocation = false
    private lateinit var mapView: MapView
    data class LocationData(val latitude: Double, val longitude: Double)

    interface ApiService {
        @POST("index.php")
        fun sendLocation(@Body location: LocationData): Call<Void>
    }

    private val apiService: ApiService = Retrofit.Builder()
        .baseUrl("http://192.168.77.73:8081/") // Ganti URL sesuai dengan endpoint server
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi konfigurasi OSMDroid
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))

        // Inisialisasi MapView
        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.controller.setZoom(16.0)




        // Inisialisasi elemen UI
        bottomNavigationView = findViewById(R.id.bottomNavView)
        latitudeTextView = findViewById(R.id.latitudeTextView)
        longitudeTextView = findViewById(R.id.longitudeTextView)

        val startButton = findViewById<Button>(R.id.buttonstartgps)
        val stopButton = findViewById<Button>(R.id.buttonstopgps)

        // Mengatur tindakan saat tombol "Start" ditekan
        startButton.setOnClickListener {
            startSendingLocation()
        }

        // Mengatur tindakan saat tombol "Stop" ditekan
        stopButton.setOnClickListener {
            stopSendingLocation()
        }

        // Inisialisasi layanan lokasi
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        if (checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val startPoint = GeoPoint(location.latitude, location.longitude)
                mapView.controller.setCenter(startPoint)
            }
        }

// Inisialisasi overlayItems dan itemizedOverlay di luar pemanggilan onLocationResult
        val overlayItems = ArrayList<OverlayItem>()
        val itemizedOverlay = ItemizedIconOverlay(overlayItems, null, mapView.context)
        mapView.overlayManager.add(itemizedOverlay)
        mapView.invalidate()

        locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(10000) // 10 seconds
            .setFastestInterval(5000) // 5 seconds

        locationCallback = object : LocationCallback() {
            @SuppressLint("SetTextI18n")
            override fun onLocationResult(locationResult: LocationResult) {
                val locations = locationResult.locations
                if (locations.isNotEmpty()) {
                    val lastLocation = locations[locations.size - 1]
                    val latitude = lastLocation.latitude
                    val longitude = lastLocation.longitude

                    // Mengatur teks TextView dengan nilai latitude dan longitude
                    latitudeTextView.text = "Latitude: $latitude"
                    longitudeTextView.text = "Longitude: $longitude"
                    Log.d("Location", "Latitude: $latitude, Longitude: $longitude")

                    val marker = OverlayItem("Marker Title", "Marker Description", GeoPoint(latitude, longitude))
                    marker.setMarker(resources.getDrawable(R.drawable.ic_marker))
                    val overlayItems = ArrayList<OverlayItem>()
                    overlayItems.add(marker)

                    val startPoint = GeoPoint(latitude, longitude) // Koordinat awal
                    mapView.controller.setCenter(startPoint)
                    // Buat overlay yang akan menampilkan marker
                    val itemizedOverlay = ItemizedIconOverlay(overlayItems, null, mapView.context)
                    mapView.overlayManager.add(itemizedOverlay)
                    mapView.invalidate()
                }
            }
        }
        requestLocationPermission()
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle permission request if not granted.
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun requestLocationPermission() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val requestCode = 123 // Nomor kode permintaan yang bisa Anda tentukan

        if (checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            // Izin belum diberikan, maka lakukan permintaan izin
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            // Izin telah diberikan
            // Mulai mendapatkan lokasi
            startLocationUpdates()
        }

    }

    private fun startSendingLocation() {
        if (!isSendingLocation) {
            isSendingLocation = true
            handler = Handler()
            handler?.postDelayed(sendLocationTask, 60000) // Kirim koordinat setiap 1 menit
        }
    }

    private val sendLocationTask = object : Runnable {
        override fun run() {
            // Kirim data lokasi ke server di sini
            sendLocationToServer()
            if (isSendingLocation) {
                handler?.postDelayed(this, 60000) // Jadwalkan tugas kirim lagi setelah 1 menit
            }
        }
    }

    private fun sendLocationToServer() {
        if (checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val locationData = LocationData(location.latitude, location.longitude)
                    val call = apiService.sendLocation(locationData)

                    call.enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Log.d("Send Location", "Location data sent successfully")
                            } else {
                                Log.e("Send Location", "Failed to send location data")
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e("Send Location", "Failed to send location data", t)
                        }
                    })
                } else {
                    Log.e("Send Location", "Location is null")
                }
            }
        } else {
            // Handle permission request if not granted.
            requestLocationPermission()
        }
    }

    private fun stopSendingLocation() {
        isSendingLocation = false
        handler?.removeCallbacks(sendLocationTask)
    }
}