package br.com.paulosalvatore.ocean_a8_07_11_18

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), LocationListener {
	companion object {
		const val TAG_MAPA = "GOOGLE_MAPS"
		const val PERMISSAO_LOCALIZACAO = 1
	}

	private lateinit var mapa: GoogleMap
	private lateinit var ultimaLocalizacao: Location

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		inicializarLocalizacao()
	}

	private fun inicializarLocalizacao() {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(
					this,
					arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
							Manifest.permission.ACCESS_COARSE_LOCATION),
					PERMISSAO_LOCALIZACAO
			)

			return
		}

		val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

		val locationProvider = LocationManager.NETWORK_PROVIDER
		// LocationManager.GPS_PROVIDER

		var bestLocation: Location? = locationManager.getLastKnownLocation(locationProvider)

		locationManager.allProviders.forEach { provider ->
			val checkLocation: Location? = locationManager.getLastKnownLocation(provider)

			checkLocation?.let {
				if (bestLocation == null || bestLocation!!.accuracy < it.accuracy) {
					bestLocation = it
				}
			}
		}

		bestLocation?.let {
			ultimaLocalizacao = it
		}

		inicializarMapa()
	}

	private fun inicializarMapa() {
		val mapFragment = supportFragmentManager.findFragmentById(R.id.fragMapa) as SupportMapFragment
		mapFragment.getMapAsync {
			Log.d(TAG_MAPA, "$it")

			mapa = it

			if (::ultimaLocalizacao.isInitialized) {
				val latLng = LatLng(ultimaLocalizacao.latitude, ultimaLocalizacao.longitude)

				mapa.addMarker(MarkerOptions().position(latLng).title("Minha posição"))

				mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14F))
			}
		}
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		when (requestCode) {
			PERMISSAO_LOCALIZACAO -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				inicializarLocalizacao()
			}
		}
	}

	private fun atualizarPosicao() {
		val latLng = LatLng(ultimaLocalizacao.latitude, ultimaLocalizacao.longitude)

		mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14F))
	}

	override fun onLocationChanged(location: Location) {
		ultimaLocalizacao = location

		atualizarPosicao()
	}

	override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
	}

	override fun onProviderEnabled(provider: String?) {
	}

	override fun onProviderDisabled(provider: String?) {
	}
}
