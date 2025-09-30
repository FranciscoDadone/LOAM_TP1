package com.loam.trabajopractico1loam.presentation.ui.mapa

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.loam.trabajopractico1loam.R
import com.loam.trabajopractico1loam.models.LugarGuardado
import java.util.Locale

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var edtDireccion: EditText
    private lateinit var btnBuscar: ImageButton
    private lateinit var btnGuardar: Button
    private lateinit var btnBack: ImageButton
    private lateinit var btnMostrarGuardados: Button

    private var ultimaLatLng: LatLng? = null
    private var ultimaDireccion: String? = null
    private var lugaresGuardados = mutableListOf<LugarGuardado>()
    private var marcadoresGuardados = mutableListOf<Marker>()
    private var mostrandoGuardados = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Oculta la Toolbar del Activity (si existe) en el oncreatedview
        try {
            (requireActivity() as? AppCompatActivity)?.supportActionBar?.hide()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        edtDireccion = view.findViewById(R.id.edtDireccion)
        btnBuscar = view.findViewById(R.id.btnBuscarDireccion)
        btnGuardar = view.findViewById(R.id.btnGuardar)
        btnBack = view.findViewById(R.id.btnBack)
        btnMostrarGuardados = view.findViewById(R.id.btnMostrarGuardados)

        // Cuando tocan buscar
        btnBuscar.setOnClickListener {
            val query = edtDireccion.text.toString()
            if (query.isNotEmpty()) {
                buscarLugar(query)
            } else {
                Toast.makeText(requireContext(), getString(R.string.ingrese_direccion), Toast.LENGTH_SHORT).show()
            }
        }

        // Guardar en Firebase cuando aprietan el botón
        btnGuardar.setOnClickListener {
            if (ultimaLatLng != null && ultimaDireccion != null) {
                mostrarDialogoReferencia()
            } else {
                Toast.makeText(requireContext(), getString(R.string.seleccione_ubicacion_primero), Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para mostrar/ocultar puntos guardados en el mapa
        btnMostrarGuardados.setOnClickListener {
            if (mostrandoGuardados) {
                ocultarPuntosGuardados()
            } else {
                mostrarPuntosGuardados()
            }
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        
        // Obtener referencia al fragment del mapa e inicializarlo
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        if (mapFragment == null) {
            // Si el fragment no existe, crearlo programáticamente
            val newMapFragment = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction()
                .replace(R.id.map, newMapFragment)
                .commit()
            newMapFragment.getMapAsync(this)
        } else {
            mapFragment.getMapAsync(this)
        }

        btnBack.setOnClickListener {
//            stopRecording()
            try {
                findNavController().navigateUp()
            } catch (e: Exception) {
                // Si no hay navigation controller, cerrar la actividad
                requireActivity().finish()
            }
        }
    }

    private fun mostrarDialogoReferencia() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialogo_referencia, null)
        builder.setView(dialogView)

        //Para que escriba en la referencia
        val input = dialogView.findViewById<EditText>(R.id.edtReferenciaDialog)
        val btnGuardarDialog = dialogView.findViewById<Button>(R.id.btnGuardarDialog)
        val btnCancelarDialog = dialogView.findViewById<Button>(R.id.btnCancelarDialog)

        val dialog = builder.create()
        dialog.show()

        // Botón Guardar
        btnGuardarDialog.setOnClickListener {
            val referencia = input.text.toString()
            if (referencia.isNotEmpty()) {
                guardarConReferencia(ultimaLatLng!!, ultimaDireccion!!, referencia)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), getString(R.string.ingrese_referencia), Toast.LENGTH_SHORT).show()
            }
        }

        // Botón Cancelar
        btnCancelarDialog.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun guardarConReferencia(latLng: LatLng, direccion: String, referencia: String) {
        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection("lugares")
        Log.d("MAPA", latLng.toString())
        
        val lugar = LugarGuardado(
            id = "", // Firestore generará el ID automáticamente
            lat = latLng.latitude,
            lng = latLng.longitude,
            direccion = direccion,
            referencia = referencia
        )

        collection.add(lugar.toMap())
            .addOnSuccessListener { documentReference ->
                Toast.makeText(requireContext(), getString(R.string.lugar_guardado_exitosamente), Toast.LENGTH_SHORT).show()
                // Actualizar lista si se están mostrando los puntos
                if (mostrandoGuardados) {
                    cargarPuntosGuardados()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), getString(R.string.error_guardar_lugar), Toast.LENGTH_SHORT).show()
                Log.e("MapFragment", "Error al guardar lugar", exception)
            }
    }

    private fun buscarLugar(query: String) {
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_maps_key))
        }

        val placesClient = Places.createClient(requireContext())
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setCountries("AR") // limitar a Argentina
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                if (response.autocompletePredictions.isNotEmpty()) {
                    val prediction = response.autocompletePredictions[0]
                    val placeId = prediction.placeId

                    val placeRequest = FetchPlaceRequest.builder(
                        placeId,
                        listOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)
                    ).build()

                    placesClient.fetchPlace(placeRequest)
                        .addOnSuccessListener { placeResponse ->
                            val place = placeResponse.place
                            val latLng = place.latLng

                            if (latLng != null) {
                                mMap.clear()
                                mMap.addMarker(MarkerOptions().position(latLng).title(place.name))
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

                                // 👇 Actualizar EditText con la dirección encontrada
                                val direccion = place.address ?: place.name
                                edtDireccion.setText(direccion)
                                ultimaDireccion = direccion
                                ultimaLatLng = latLng
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.no_encontrado_lugar), Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun mostrarPuntosGuardados() {
        if (!mostrandoGuardados) {
            cargarPuntosGuardados()
            btnMostrarGuardados.text = getString(R.string.ocultar_guardados)
            mostrandoGuardados = true
        }
    }

    private fun ocultarPuntosGuardados() {
        if (mostrandoGuardados) {
            // Remover todos los marcadores guardados del mapa
            marcadoresGuardados.forEach { it.remove() }
            marcadoresGuardados.clear()
            btnMostrarGuardados.text = getString(R.string.mostrar_en_mapa)
            mostrandoGuardados = false
        }
    }

    private fun cargarPuntosGuardados() {
        val firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection("lugares")

        collection.get()
            .addOnSuccessListener { documents ->
                // Limpiar marcadores existentes
                marcadoresGuardados.forEach { it.remove() }
                marcadoresGuardados.clear()
                lugaresGuardados.clear()

                if (!documents.isEmpty) {
                    for (document in documents) {
                        try {
                            val lat = document.getDouble("lat") ?: 0.0
                            val lng = document.getDouble("lng") ?: 0.0
                            val direccion = document.getString("direccion") ?: ""
                            val referencia = document.getString("referencia") ?: ""
                            val id = document.id

                            val lugar = LugarGuardado(id, lat, lng, direccion, referencia)
                            lugaresGuardados.add(lugar)

                            // Crear marcador en el mapa
                            val latLng = LatLng(lat, lng)
                            val marker = mMap.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(direccion)
                                    .snippet("📝 $referencia")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            )
                            marker?.let { marcadoresGuardados.add(it) }

                        } catch (e: Exception) {
                            // Log del error pero continuar con otros puntos
                            Log.e("MapFragment", "Error al cargar punto: ${e.message}")
                        }
                    }
                    
                    if (lugaresGuardados.isNotEmpty()) {
                        Toast.makeText(requireContext(), getString(R.string.puntos_guardados_cargados, lugaresGuardados.size), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.no_puntos_guardados), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.no_puntos_guardados), Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), getString(R.string.error_cargar_puntos), Toast.LENGTH_SHORT).show()
                Log.e("MapFragment", "Error Firestore: ${exception.message}")
            }
    }

    private fun mostrarDialogoEliminarPunto(marker: Marker) {
        // Encontrar el lugar guardado correspondiente al marcador
        val lugar = lugaresGuardados.find { 
            it.lat == marker.position.latitude && it.lng == marker.position.longitude 
        }

        if (lugar != null) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.eliminar_punto_guardado))
            builder.setMessage("${getString(R.string.confirmar_eliminar_punto)}\n\n📍 ${lugar.direccion}\n📝 ${lugar.referencia}")
            
            builder.setPositiveButton(getString(R.string.eliminar)) { _, _ ->
                eliminarPuntoGuardado(lugar, marker)
            }
            
            builder.setNegativeButton(getString(R.string.cancelar)) { dialog, _ ->
                dialog.dismiss()
            }
            
            builder.show()
        }
    }

    private fun eliminarPuntoGuardado(lugar: LugarGuardado, marker: Marker) {
        val firestore = FirebaseFirestore.getInstance()
        val documentRef = firestore.collection("lugares").document(lugar.id)

        documentRef.delete()
            .addOnSuccessListener {
                // Remover marcador del mapa
                marker.remove()
                marcadoresGuardados.remove(marker)
                lugaresGuardados.remove(lugar)
                
                Toast.makeText(requireContext(), getString(R.string.punto_eliminado_exitosamente), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), getString(R.string.error_eliminar_punto), Toast.LENGTH_SHORT).show()
                Log.e("MapFragment", "Error al eliminar punto", exception)
            }
    }


    //lo hice para cargar el mapa y que se guarde en mMap y pedir los permisos de ubicacion para usarlo
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap //guarda referencia al mapa
        pedirPermisos()

        //para mover el boton de "mi ubicacion"
        val mapView =(childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).view
        mapView?.let {
            val locationButton = (it.parent as View).findViewById<View>("1".toInt())?.parent
                ?.let { parent -> (parent as View).findViewById<View>("2".toInt()) }
            locationButton?.let { btn ->
                val params = btn.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(0, 500, 50, 0)
                btn.layoutParams =params
            }
        }

        //click en el mapa
        mMap.setOnMapClickListener { latLng ->
            mMap.clear()
            // Si estábamos mostrando guardados, volver a mostrarlos
            if (mostrandoGuardados) {
                cargarPuntosGuardados()
            }
            mMap.addMarker(MarkerOptions().position(latLng).title(getString(R.string.punto_elegido)))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

            // Usamos Geocoder para traducir coords -> dirección
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val direcciones = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            if (direcciones != null && direcciones.isNotEmpty()) {
                val direccion = direcciones[0].getAddressLine(0)
                edtDireccion.setText(direccion)
                ultimaDireccion = direccion
            } else {
                ultimaDireccion = "${latLng.latitude}, ${latLng.longitude}"
                edtDireccion.setText(ultimaDireccion)
//                edtDireccion.setText("${latLng.latitude}, ${latLng.longitude}")
            }
            ultimaLatLng = latLng
        }

        // Listener para clicks en marcadores guardados
        mMap.setOnMarkerClickListener { marker ->
            // Verificar si es un marcador guardado
            val marcadorGuardado = marcadoresGuardados.find { it == marker }
            if (marcadorGuardado != null) {
                // Es un punto guardado, mostrar diálogo para eliminar
                mostrarDialogoEliminarPunto(marker)
                true // Consumir el evento
            } else {
                false // No consumir el evento, comportamiento normal
            }
        }
    }

    //si los permisos se dieron, se muestra la ubicacion sino se vuelven a pedir los permisos
    private fun pedirPermisos() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mostrarUbicacion()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }
    }

    private fun mostrarUbicacion() {
        // Verificar explícitamente el permiso antes de usar la ubicación
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        mMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val miPos = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miPos, 16f))
                mMap.addMarker(MarkerOptions().position(miPos).title(getString(R.string.estoy_aqui)))

                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val direcciones = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (direcciones != null && direcciones.isNotEmpty()) {
                    val direccion = direcciones[0].getAddressLine(0)
                    edtDireccion.setText(direccion)
                }
            }
        }
    }

    // Para manejar el permiso
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mostrarUbicacion()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        
        // Limpiar marcadores para evitar memory leaks
        marcadoresGuardados.forEach { it.remove() }
        marcadoresGuardados.clear()
        lugaresGuardados.clear()
        
        // Vuelve a mostrar la ActionBar cuando salís del fragment
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        // Vuelve a mostrar la BottomNavigationView (si existe)
        // val bottomNav = requireActivity().findViewById<View>(R.id.nav_view)
        // bottomNav?.visibility = View.VISIBLE
    }
}