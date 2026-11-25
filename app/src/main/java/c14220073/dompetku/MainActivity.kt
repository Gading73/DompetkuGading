package c14220073.dompetku

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import c14220073.dompetku.databinding.ActivityMainBinding
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefHelper: PrefHelper
    private val rupiahFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefHelper = PrefHelper(this)

        binding.btnMenuKategori.setOnClickListener {
            startActivity(Intent(this, KategoriActivity::class.java))
        }

        binding.btnMenuIncome.setOnClickListener {
            val intent = Intent(this, KelolaTransaksiActivity::class.java)
            intent.putExtra("TIPE", "INCOME")
            startActivity(intent)
        }

        binding.btnMenuExpense.setOnClickListener {
            val intent = Intent(this, KelolaTransaksiActivity::class.java)
            intent.putExtra("TIPE", "EXPENSE")
            startActivity(intent)
        }

        binding.btnMenuTransaksi.setOnClickListener {
            Toast.makeText(
                this,
                "Fitur Daftar Transaksi, Search, Filter & Sorting siap dilanjutkan!",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnMenuLaporan.setOnClickListener {
            Toast.makeText(
                this,
                "Fitur Laporan Bulanan siap dilanjutkan!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        perbaruiRingkasanDashboard()
    }

    private fun perbaruiRingkasanDashboard() {
        val transaksiList = prefHelper.getTransaksiList()

        val totalIncome = transaksiList
            .filter { it.tipe == "INCOME" }
            .sumOf { it.nominal }

        val totalExpense = transaksiList
            .filter { it.tipe == "EXPENSE" }
            .sumOf { it.nominal }

        val saldo = totalIncome - totalExpense

        binding.tvTotalIncome.text = "Rp ${rupiahFormat.format(totalIncome)}"
        binding.tvTotalExpense.text = "Rp ${rupiahFormat.format(totalExpense)}"
        binding.tvTotalSaldo.text = "Rp ${rupiahFormat.format(saldo)}"

        if (saldo < 0) {
            binding.tvTotalSaldo.setTextColor(Color.parseColor("#C62828"))
        } else {
            binding.tvTotalSaldo.setTextColor(Color.parseColor("#0D47A1"))
        }
    }
}