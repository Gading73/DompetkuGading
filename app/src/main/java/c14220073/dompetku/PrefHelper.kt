package c14220073.dompetku

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

class PrefHelper(context: Context) {

    private val prefs = context.getSharedPreferences("dompetku_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_KATEGORI = "key_kategori"
        private const val KEY_TRANSAKSI = "key_transaksi"
    }

    // --- MANAJEMEN KATEGORI ---
    fun getKategoriList(): ArrayList<Kategori> {
        val json = prefs.getString(KEY_KATEGORI, null)
        if (json == null) {
            // Inisialisasi kategori bawaan saat aplikasi pertama kali dijalankan
            val defaultList = arrayListOf(
                Kategori(UUID.randomUUID().toString(), "Gaji", "INCOME"),
                Kategori(UUID.randomUUID().toString(), "Bonus", "INCOME"),
                Kategori(UUID.randomUUID().toString(), "Makan & Minum", "EXPENSE"),
                Kategori(UUID.randomUUID().toString(), "Transportasi", "EXPENSE"),
                Kategori(UUID.randomUUID().toString(), "Belanja", "EXPENSE")
            )
            saveKategoriList(defaultList)
            return defaultList
        }
        val type = object : TypeToken<ArrayList<Kategori>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveKategoriList(list: List<Kategori>) {
        val json = gson.toJson(list)
        prefs.edit().putString(KEY_KATEGORI, json).apply()
    }

    // --- MANAJEMEN TRANSAKSI (INCOME & EXPENSE) ---
    fun getTransaksiList(): ArrayList<Transaksi> {
        val json = prefs.getString(KEY_TRANSAKSI, null) ?: return arrayListOf()
        val type = object : TypeToken<ArrayList<Transaksi>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveTransaksiList(list: List<Transaksi>) {
        val json = gson.toJson(list)
        prefs.edit().putString(KEY_TRANSAKSI, json).apply()
    }
}