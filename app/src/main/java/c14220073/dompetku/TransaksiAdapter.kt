package c14220073.dompetku

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import c14220073.dompetku.databinding.ItemTransaksiBinding
import java.text.NumberFormat
import java.util.Locale

class TransaksiAdapter(
    private val listTransaksi: List<Transaksi>,
    private val onEdit: (Transaksi) -> Unit,
    private val onDelete: (Transaksi) -> Unit
) : RecyclerView.Adapter<TransaksiAdapter.ViewHolder>() {

    private val rupiahFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))

    inner class ViewHolder(val binding: ItemTransaksiBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransaksiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listTransaksi.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listTransaksi[position]

        holder.binding.tvKategoriItem.text = item.kategori
        holder.binding.tvNominalItem.text = "Rp ${rupiahFormat.format(item.nominal)}"
        holder.binding.tvTanggalItem.text = "Tanggal: ${item.tanggal}"
        holder.binding.tvKeteranganItem.text =
            if (item.keterangan.isNotEmpty()) item.keterangan else "-"

        if (item.tipe == "INCOME") {
            holder.binding.tvNominalItem.setTextColor(Color.parseColor("#2E7D32"))
        } else {
            holder.binding.tvNominalItem.setTextColor(Color.parseColor("#C62828"))
        }

        holder.binding.btnEditTransaksi.setOnClickListener { onEdit(item) }
        holder.binding.btnHapusTransaksi.setOnClickListener { onDelete(item) }
    }
}