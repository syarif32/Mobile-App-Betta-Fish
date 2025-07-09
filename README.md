Aplikasi e-commerce ini dirancang untuk memudahkan jual beli ikan cupang secara online, menyediakan pengalaman belanja yang intuitif bagi pengguna serta sistem manajemen pesanan yang efisien. Proyek ini terdiri dari aplikasi Android native dan API backend berbasis PHP.

Fitur Utama
Pencarian & Penjelajahan Produk: Pengguna dapat dengan mudah mencari dan menelusuri berbagai jenis ikan cupang, baik impor maupun lokal, beserta detail deskripsi dan harga.

Keranjang Belanja: Fungsionalitas keranjang belanja memungkinkan pengguna menambahkan beberapa produk, mengubah kuantitas, dan melihat ringkasan pesanan sebelum checkout.

Proses Checkout Lanjut:

Integrasi API RajaOngkir untuk perhitungan ongkos kirim real-time berdasarkan lokasi pengiriman (provinsi dan kota tujuan).

Pilihan metode pembayaran:

COD (Cash On Delivery): Pesanan langsung berstatus "Pembayaran Diverifikasi" tanpa perlu unggah bukti pembayaran.

Transfer Bank (BNI, BCA, BRI): Pengguna dapat mengunggah bukti pembayaran, dan status pesanan akan diperbarui setelah dikonfirmasi.

Detail pengiriman lengkap termasuk alamat, nomor telepon, dan kode pos.

Riwayat Pesanan (History): Pengguna dapat melihat riwayat semua pesanan mereka, termasuk status pesanan, detail produk yang dibeli, informasi pengiriman, dan riwayat bukti pembayaran.

Manajemen Bukti Pembayaran: Untuk metode transfer bank, pengguna dapat mengunggah bukti pembayaran langsung dari aplikasi, yang kemudian akan dikirim ke backend untuk verifikasi.

Teknologi yang Digunakan
Aplikasi Android (Frontend)
Bahasa Pemrograman: Java

UI/UX: XML (Layouts), Material Design Components

Networking: Retrofit2 (untuk komunikasi dengan API RESTful)

Image Loading: Glide (untuk memuat dan menampilkan gambar produk dan bukti pembayaran)

JSON Parsing: Gson (untuk serialisasi dan deserialisasi objek JSON)

Data Persistence: SharedPreferences (untuk menyimpan data sesi pengguna dan keranjang belanja sementara)

RecyclerView: Untuk menampilkan daftar produk dan riwayat pesanan yang efisien.

API Backend (PHP & Database)
Bahasa Pemrograman: PHP

Database: MySQL (dengan struktur tabel tbl_order, tbl_order_detail, tbl_product, tbl_pelanggan)

API Pihak Ketiga: RajaOngkir (untuk perhitungan ongkos kirim)

Manajemen Database: PDO (PHP Data Objects) untuk interaksi database yang aman dan efisien.

File Upload: Penanganan upload gambar untuk bukti pembayaran.

Struktur Proyek (Ringkasan)
activity_checkout.xml: Layout untuk halaman checkout.

activity_history_detail.xml: Layout untuk detail riwayat pesanan.

Checkout.java: Logika Android untuk proses checkout, integrasi RajaOngkir, dan pengiriman pesanan.

HistoryDetail.java: Logika Android untuk menampilkan detail pesanan, mengelola status, dan fungsionalitas unggah bukti pembayaran.

ProductInOrderAdapter.java: Adapter untuk menampilkan daftar produk di dalam detail pesanan.

OrderDetailResponse.java: Model data untuk respons detail pesanan dari API.

APIService.java: Interface Retrofit untuk definisi endpoint API.

post_order.php: API endpoint untuk memproses pesanan baru, menyimpan ke database, dan menyesuaikan stok serta status berdasarkan metode pembayaran.

get_order_detail.php: API endpoint untuk mengambil detail lengkap suatu pesanan berdasarkan ID.

get_order.php: API endpoint untuk mengambil daftar riwayat pesanan pengguna.

Cara Menjalankan Proyek
Siapkan Lingkungan Backend:

Pastikan Anda memiliki server web (Apache/Nginx) dan PHP terinstal.

Import skema database dari android (6).sql ke MySQL Anda.

Tempatkan file-file PHP (post_order.php, get_order_detail.php, get_order.php, koneksi.php, dll.) di direktori root server web Anda (atau sub-direktori yang sesuai).

Pastikan path koneksi.php sudah benar di semua file PHP yang memerlukannya.

Konfigurasi database di koneksi.php sesuai dengan kredensial database Anda.

Pastikan folder produk/ dan bukti_bayar/ ada di server web Anda dan memiliki izin tulis yang benar untuk menyimpan gambar.

Pastikan API RajaOngkir Anda dikonfigurasi dengan benar di backend PHP.

Konfigurasi Aplikasi Android:

Buka proyek di Android Studio.

Perbarui BASE_URL di RajaOngkirClient.java (atau di mana pun Anda mendefinisikannya, seperti di Checkout.java) ke URL dasar server PHP Anda (contoh: https://allend.site/API_Product/).

Bangun dan jalankan aplikasi di emulator atau perangkat fisik.
