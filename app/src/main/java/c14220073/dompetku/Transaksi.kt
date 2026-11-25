package c14220073.dompetku

data class Transaksi(
    val id: String,
    var tipe: String,       // "INCOME" atau "EXPENSE"
    var nominal: Long,
    var kategori: String,
    var tanggal: String,    // Format: yyyy-MM-dd
    var keterangan: String
)