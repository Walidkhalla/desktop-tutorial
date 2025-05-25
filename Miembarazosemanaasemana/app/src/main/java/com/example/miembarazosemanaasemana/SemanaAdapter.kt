package com.example.miembarazosemanaasemana.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.miembarazosemanaasemana.R
import com.example.miembarazosemanaasemana.modelo.SemanaInfo

class SemanaAdapter(private val listaSemanas: List<SemanaInfo>) :
    RecyclerView.Adapter<SemanaAdapter.SemanaViewHolder>() {

    inner class SemanaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgFruta: ImageView = itemView.findViewById(R.id.imgFruta)
        val tvSemana: TextView = itemView.findViewById(R.id.tvSemana)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SemanaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_semana, parent, false)
        return SemanaViewHolder(view)
    }

    override fun onBindViewHolder(holder: SemanaViewHolder, position: Int) {
        val semana = listaSemanas[position]
        holder.tvSemana.text = "Semana ${semana.semana}"
        holder.tvDescripcion.text = semana.descripcion
        holder.imgFruta.setImageResource(semana.imagenResId)
    }

    override fun getItemCount(): Int = listaSemanas.size
}
