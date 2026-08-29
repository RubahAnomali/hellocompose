data class Mahasiswa(val nim: String, val nama: String, val ipk: Double)

sealed class ResultStatus {
    data class Success(val data: List<Mahasiswa>) : ResultStatus()
    data class Error(val message: String) : ResultStatus()
}

fun main() {
    val listMahasiswa = listOf(
        Mahasiswa("22001", "Farrell Fyelo", 3.8),
        Mahasiswa("22002", "Andi", 3.4),
        Mahasiswa("22003", "Budi", 3.9),
        Mahasiswa("22004", "Caca", 3.6),
        Mahasiswa("22005", "Deni", 3.2)
    )

    val result = try {
        val filteredAndSorted = listMahasiswa
            .filter { it.ipk >= 3.5 }
            .sortedBy { it.nama }

        if (filteredAndSorted.isNotEmpty()) {
            ResultStatus.Success(filteredAndSorted)
        } else {
            ResultStatus.Error("Tidak ada mahasiswa dengan IPK >= 3.5")
        }
    } catch (e: Exception) {
        ResultStatus.Error("Terjadi kesalahan sistem")
    }

    when (result) {
        is ResultStatus.Success -> {
            println("Data Mahasiswa Berprestasi:")
            result.data.forEach { println("- ${it.nama} (IPK: ${it.ipk})") }
        }
        is ResultStatus.Error -> {
            println("Error: ${result.message}")
        }
    }
}