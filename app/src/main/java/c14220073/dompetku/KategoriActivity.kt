package c14220073.dompetku

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import c14220073.dompetku.databinding.ActivityKategoriBinding
import java.util.UUID

class KategoriActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKategoriBinding
    private lateinit var prefHelper: PrefHelper
    private lateinit var adapter: KategoriAdapter
    private var kategoriList = ArrayList<Kategori>()
    private var editingKategori: Kategori? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKategoriBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefHelper = PrefHelper(this)
        kategoriList = prefHelper.getKategoriList()

        setupRecyclerView()

        binding.btnSimpanKategori.setOnClickListener {
            simpanKategori()
        }

        binding.btnBatalEdit.setOnClickListener {
            resetForm()
        }
    }

    private fun setupRecyclerView() {
        adapter = KategoriAdapter(
            listKategori = kategoriList,
            onEdit = { kategori -> mulaiEdit(kategori) },
            onDelete = { kategori -> konfirmasiHapus(kategori) }
        )
        binding.rvKategori.layoutManager = LinearLayoutManager(this)
        binding.rvKategori.adapter = adapter
    }

    private fun simpanKategori() {
        val nama = binding.etNamaKategori.text.toString().trim()
        if (nama.isEmpty()) {
            Toast.makeText(this, "Nama kategori wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        val tipe = if (binding.rbIncome.isChecked) "INCOME" else "EXPENSE"

        if (editingKategori == null) {
            // Mode Tambah Baru
            val baru = Kategori(
                id = UUID.randomUUID().toString(),
                nama = nama,
                tipe = tipe
            )
            kategoriList.add(baru)
            Toast.makeText(this, "Kategori ditambahkan", Toast.LENGTH_SHORT).show()
        } else {
            // Mode Ubah Data
            editingKategori?.nama = nama
            editingKategori?.tipe = tipe
            Toast.makeText(this, "Kategori diperbarui", Toast.LENGTH_SHORT).show()
        }

        prefHelper.saveKategoriList(kategoriList)
        adapter.notifyDataSetChanged()
        resetForm()
    }

    private fun mulaiEdit(kategori: Kategori) {
        editingKategori = kategori
        binding.etNamaKategori.setText(kategori.nama)
        if (kategori.tipe == "INCOME") {
            binding.rbIncome.isChecked = true
        } else {
            binding.rbExpense.isChecked = true
        }
        binding.btnSimpanKategori.text = "Update Kategori"
        binding.btnBatalEdit.visibility = View.VISIBLE
    }

    private fun konfirmasiHapus(kategori: Kategori) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Kategori")
            .setMessage("Yakin ingin menghapus kategori '${kategori.nama}'?")
            .setPositiveButton("Hapus") { _, _ ->
                kategoriList.remove(kategori)
                prefHelper.saveKategoriList(kategoriList)
                adapter.notifyDataSetChanged()
                if (editingKategori?.id == kategori.id) {
                    resetForm()
                }
                Toast.makeText(this, "Kategori dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun resetForm() {
        editingKategori = null
        binding.etNamaKategori.text.clear()
        binding.rbIncome.isChecked = true
        binding.btnSimpanKategori.text = "Tambah Kategori"
        binding.btnBatalEdit.visibility = View.GONE
    }
}