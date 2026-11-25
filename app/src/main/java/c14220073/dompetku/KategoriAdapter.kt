package c14220073.dompetku

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import c14220073.dompetku.databinding.ItemKategoriBinding

class KategoriAdapter(
    private val listKategori: List<Kategori>,
    private val onEdit: (Kategori) -> Unit,
    private val onDelete: (Kategori) -> Unit
) : RecyclerView.Adapter<KategoriAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemKategoriBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemKategoriBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listKategori.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listKategori[position]
        holder.binding.tvNamaKategori.text = item.nama
        holder.binding.tvTipeKategori.text = item.tipe

        if (item.tipe == "INCOME") {
            holder.binding.tvTipeKategori.setTextColor(Color.parseColor("#2E7D32"))
        } else {
            holder.binding.tvTipeKategori.setTextColor(Color.parseColor("#C62828"))
        }

        holder.binding.btnEditKategori.setOnClickListener { onEdit(item) }
        holder.binding.btnHapusKategori.setOnClickListener { onDelete(item) }
    }
}