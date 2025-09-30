package com.loam.trabajopractico1loam.presentation.ui.precios

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.loam.trabajopractico1loam.R
import com.loam.trabajopractico1loam.models.PrecioReferencia

class PreciosAdapter(private var precios: List<PrecioReferencia>) : 
    RecyclerView.Adapter<PreciosAdapter.PrecioViewHolder>() {

    class PrecioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrecioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_precio, parent, false)
        return PrecioViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrecioViewHolder, position: Int) {
        val precio = precios[position]
        holder.tvProductName.text = precio.tipo.displayName
        holder.tvPrice.text = "${precio.moneda} ${String.format("%.2f", precio.valor)} / ${precio.unidad}"
        holder.tvDescription.text = precio.descripcion
    }

    override fun getItemCount(): Int = precios.size
    
    fun updatePrecios(nuevosPrecios: List<PrecioReferencia>) {
        precios = nuevosPrecios
        notifyDataSetChanged()
    }
}