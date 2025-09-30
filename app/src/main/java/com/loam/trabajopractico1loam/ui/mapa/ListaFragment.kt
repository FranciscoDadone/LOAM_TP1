package com.loam.trabajopractico1loam.ui.mapa

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.loam.trabajopractico1loam.R
import androidx.navigation.fragment.findNavController
import android.widget.Button

class ListaFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val listaDatos = mutableListOf<String>()
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lista, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.listViewLugares)
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listaDatos)
        listView.adapter = adapter

        // Inicializar referencia a Firestore
        firestore = FirebaseFirestore.getInstance()

        // Cargar lugares desde Firestore
        firestore.collection("lugares")
            .get()
            .addOnSuccessListener { documents ->
                listaDatos.clear()
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val direccion = document.getString("direccion") ?: "Sin dirección"
                        val referencia = document.getString("referencia") ?: "Sin referencia"
                        listaDatos.add("📍 $direccion\n📝 $referencia")
                    }
                } else {
                    listaDatos.add("No hay lugares guardados")
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("ListaFragment", "Error al cargar lugares: ${exception.message}")
                Toast.makeText(requireContext(), "Error al cargar lugares", Toast.LENGTH_SHORT).show()
                listaDatos.clear()
                listaDatos.add("Error al cargar datos")
                adapter.notifyDataSetChanged()
            }

        // Agregar funcionalidad al botón "Volver al Mapa"
        val btnVolverMapa = view.findViewById<Button>(R.id.btnVolverMapa)
        btnVolverMapa.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}
