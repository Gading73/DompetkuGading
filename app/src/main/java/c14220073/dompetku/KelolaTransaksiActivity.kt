package c14220073.dompetku

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import c14220073.dompetku.databinding.ActivityKelolaTransaksiBinding
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class KelolaTransaksiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKelolaTransaksiBinding
    private lateinit var prefHelper: PrefHelper
    private lateinit var adapter: TransaksiAdapter

    private var tipeTransaksi: String = "INCOME"
    private var allTransaksiList = ArrayList<Transaksi>()
    private var filteredList = ArrayList<Transaksi>()
    private var namaKategoriList = ArrayList<String>()
    private var editingTransaksi: Transaksi? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKelolaTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tipeTransaksi = intent.getStringExtra("TIPE") ?: "INCOME"
        prefHelper = PrefHelper(this)

        setupTampilanAwal()
        setupSpinnerKategori()
        setupDatePicker()
        setupRecyclerView()
        muatDataTransaksi()

        binding.btnSimpanTransaksi.setOnClickListener {
            simpanTransaksi()
        }

        binding.btnBatalTransaksi.setOnClickListener {
            resetForm()
        }
    }

    private fun setupTampilanAwal() {
        if (tipeTransaksi == "INCOME") {
            binding.tvJudulHalaman.text = "Kelola Pemasukan (Income)"
            binding.btnSimpanTransaksi.text = "Tambah Pemasukan"
        } else {
            binding.tvJudulHalaman.text = "Kelola Pengeluaran (Expense)"
            binding.btnSimpanTransaksi.text = "Tambah Pengeluaran"
        }
        setTanggalHariIni()
    }

    private fun setTanggalHariIni() {
        val cal = Calendar.getInstance()
        val hariIni = String.format(
            Locale.US,
            "%04d-%02d-%02d",
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
        binding.etTanggal.setText(hariIni)
    }

    private fun setupSpinnerKategori() {
        val semuaKategori = prefHelper.getKategoriList()
        namaKategoriList.clear()
        semuaKategori.filter { it.tipe == tipeTransaksi }.forEach {
            namaKategoriList.add(it.nama)
        }

        if (namaKategoriList.isEmpty()) {
            namaKategoriList.add("Umum")
        }

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            namaKategoriList
        )
        binding.spKategori.adapter = spinnerAdapter
    }

    private fun setupDatePicker() {
        binding.etTanggal.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val terpilih = String.format(
                        Locale.US,
                        "%04d-%02d-%02d",
                        year,
                        month + 1,
                        dayOfMonth
                    )
                    binding.etTanggal.setText(terpilih)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = TransaksiAdapter(
            listTransaksi = filteredList,
            onEdit = { transaksi -> mulaiEdit(transaksi) },
            onDelete = { transaksi -> konfirmasiHapus(transaksi) }
        )
        binding.rvTransaksi.layoutManager = LinearLayoutManager(this)
        binding.rvTransaksi.adapter = adapter
    }

    private fun muatDataTransaksi() {
        allTransaksiList = prefHelper.getTransaksiList()
        filteredList.clear()
        filteredList.addAll(allTransaksiList.filter { it.tipe == tipeTransaksi }.reversed())
        adapter.notifyDataSetChanged()
    }

    private fun simpanTransaksi() {
        val nominalStr = binding.etNominal.text.toString().trim()
        val tanggal = binding.etTanggal.text.toString().trim()
        val keterangan = binding.etKeterangan.text.toString().trim()
        val kategoriTerpilih = binding.spKategori.selectedItem?.toString() ?: "Umum"

        if (nominalStr.isEmpty()) {
            Toast.makeText(this, "Nominal wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        val nominal = nominalStr.toLongOrNull()
        if (nominal == null || nominal <= 0) {
            Toast.makeText(this, "Masukkan nominal yang valid!", Toast.LENGTH_SHORT).show()
            return
        }

        if (editingTransaksi == null) {
            val baru = Transaksi(
                id = UUID.randomUUID().toString(),
                tipe = tipeTransaksi,
                nominal = nominal,
                kategori = kategoriTerpilih,
                tanggal = tanggal,
                keterangan = keterangan
            )
            allTransaksiList.add(baru)
            Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
        } else {
            val target = allTransaksiList.find { it.id == editingTransaksi?.id }
            target?.apply {
                this.kategori = kategoriTerpilih
                this.nominal = nominal
                this.tanggal = tanggal
                this.keterangan = keterangan
            }
            Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
        }

        prefHelper.saveTransaksiList(allTransaksiList)
        muatDataTransaksi()
        resetForm()
    }

    private fun mulaiEdit(transaksi: Transaksi) {
        editingTransaksi = transaksi
        binding.etNominal.setText(transaksi.nominal.toString())
        binding.etTanggal.setText(transaksi.tanggal)
        binding.etKeterangan.setText(transaksi.keterangan)

        val idxKategori = namaKategoriList.indexOf(transaksi.kategori)
        if (idxKategori >= 0) {
            binding.spKategori.setSelection(idxKategori)
        }

        binding.btnSimpanTransaksi.text = "Update Data"
        binding.btnBatalTransaksi.visibility = View.VISIBLE
    }

    private fun konfirmasiHapus(transaksi: Transaksi) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Transaksi")
            .setMessage("Yakin ingin menghapus transaksi '${transaksi.kategori}' sebesar Rp ${transaksi.nominal}?")
            .setPositiveButton("Hapus") { _, _ ->
                allTransaksiList.removeAll { it.id == transaksi.id }
                prefHelper.saveTransaksiList(allTransaksiList)
                muatDataTransaksi()
                if (editingTransaksi?.id == transaksi.id) {
                    resetForm()
                }
                Toast.makeText(this, "Transaksi dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun resetForm() {
        editingTransaksi = null
        binding.etNominal.text.clear()
        binding.etKeterangan.text.clear()
        setTanggalHariIni()
        if (namaKategoriList.isNotEmpty()) {
            binding.spKategori.setSelection(0)
        }
        binding.btnSimpanTransaksi.text =
            if (tipeTransaksi == "INCOME") "Tambah Pemasukan" else "Tambah Pengeluaran"
        binding.btnBatalTransaksi.visibility = View.GONE
    }
}