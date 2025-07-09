-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Waktu pembuatan: 06 Jul 2025 pada 14.56
-- Versi server: 10.11.13-MariaDB-cll-lve
-- Versi PHP: 8.3.22

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dtip1888_cupangsarep`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `tbl_order`
--

CREATE TABLE `tbl_order` (
  `id_order` int(11) NOT NULL,
  `id_pelanggan` int(11) NOT NULL,
  `tgl_order` date DEFAULT NULL,
  `subtotal` double DEFAULT NULL,
  `ongkir` double DEFAULT NULL,
  `total_bayar` double DEFAULT NULL,
  `kurir` varchar(50) DEFAULT NULL,
  `service` varchar(50) DEFAULT NULL,
  `asal` varchar(50) DEFAULT NULL,
  `tujuan` varchar(50) DEFAULT NULL,
  `alamat_kirim` varchar(100) DEFAULT NULL,
  `telp_kirim` varchar(15) DEFAULT NULL,
  `kota` varchar(30) DEFAULT NULL,
  `provinsi` varchar(30) DEFAULT NULL,
  `lama_kirim` varchar(20) DEFAULT NULL,
  `kodepos` varchar(20) DEFAULT NULL,
  `metodebayar` int(11) DEFAULT NULL,
  `bukti_bayar` varchar(100) DEFAULT NULL,
  `status` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tbl_order`
--

INSERT INTO `tbl_order` (`id_order`, `id_pelanggan`, `tgl_order`, `subtotal`, `ongkir`, `total_bayar`, `kurir`, `service`, `asal`, `tujuan`, `alamat_kirim`, `telp_kirim`, `kota`, `provinsi`, `lama_kirim`, `kodepos`, `metodebayar`, `bukti_bayar`, `status`) VALUES
(44, 39, '2025-06-29', 100000, 40000, 140000, 'jne', 'REG', '78', '155', 'jalan walisongo semarang', '089657', NULL, NULL, NULL, '3425', 0, NULL, 2),
(45, 39, '2025-06-29', 175000, 380000, 555000, 'jne', 'REG', '78', '77', 'zz', '088', NULL, NULL, NULL, '88', 0, NULL, 2),
(46, 39, '2025-06-29', 175000, 380000, 555000, 'jne', 'REG', '78', '77', 'f', '9', NULL, NULL, NULL, '6', 1, '6860f56b46352_Screenshot_20250615_212705_Clash Royale.jpg', 1),
(47, 39, '2025-06-29', 175000, 30000, 205000, 'tiki', 'REG', '78', '151', 'cvcg', '0895', NULL, NULL, NULL, '885', 1, NULL, 0),
(48, 39, '2025-06-29', 275000, 100000, 375000, 'jne', 'REG', '78', '471', 'Jalan walisongo 9 tambak aji ngaliyan', '089657324', NULL, NULL, NULL, '8965', 1, NULL, 0),
(49, 29, '2025-06-30', 100000, 55000, 155000, 'jne', 'REG', '78', '501', 'cdyfdty', '08965', NULL, NULL, NULL, '08965', 1, NULL, 0),
(50, 29, '2025-07-02', 175000, 0, 175000, 'tiki', 'REG', '78', '17', 'tedgg', '855', NULL, NULL, NULL, '55', 0, NULL, 2),
(51, 39, '2025-07-03', 175000, 65000, 240000, 'jne', 'REG', '78', '210', 'f', '88', NULL, NULL, NULL, '8', 1, '6865709f943bd_Screenshot_20250524_081917_TikTok.jpg', 1),
(52, 39, '2025-07-05', 510000, 38000, 548000, 'jne', 'REG', '78', '62', 'f', '0', NULL, NULL, NULL, '5', 3, NULL, 0),
(53, 39, '2025-07-06', 175000, 40000, 215000, 'jne', 'REG', '78', '151', 'muhammdjajbs', '089654', NULL, NULL, NULL, '0895', 2, '6869e048297cc_Screenshot_20250705_175813_Clash Royale.jpg', 2),
(54, 39, '2025-07-06', 115000, 90000, 205000, 'jne', 'REG', '78', '49', 'batang Selatan ', '0896', NULL, NULL, NULL, '8566', 1, '686a2433a0161_Screenshot_20250705_175813_Clash Royale.jpg', 5),
(55, 39, '2025-07-06', 35000, 24000, 59000, 'tiki', 'REG', '78', '419', 'ccg', '0895', NULL, NULL, NULL, '055', 0, NULL, 2);

-- --------------------------------------------------------

--
-- Struktur dari tabel `tbl_order_detail`
--

CREATE TABLE `tbl_order_detail` (
  `id_order` int(11) NOT NULL,
  `kode_product` char(10) NOT NULL,
  `harga_satuan` double DEFAULT NULL,
  `qty` int(11) DEFAULT NULL,
  `bayar` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tbl_order_detail`
--

INSERT INTO `tbl_order_detail` (`id_order`, `kode_product`, `harga_satuan`, `qty`, `bayar`) VALUES
(44, 'CI002', 100000, 1, 100000),
(45, 'CI003', 175000, 1, 175000),
(46, 'CI003', 175000, 1, 175000),
(47, 'CI003', 175000, 1, 175000),
(48, 'CI002', 100000, 1, 100000),
(48, 'CI003', 175000, 1, 175000),
(49, 'CI002', 100000, 1, 100000),
(50, 'CI003', 175000, 1, 175000),
(51, 'CI003', 175000, 1, 175000),
(52, 'CI002', 100000, 3, 300000),
(52, 'CI005', 90000, 1, 90000),
(52, 'CI006', 120000, 1, 120000),
(53, 'CI003', 175000, 1, 175000),
(54, 'CL009', 45000, 1, 45000),
(54, 'CL011', 70000, 1, 70000),
(55, 'CL010', 35000, 1, 35000);

-- --------------------------------------------------------

--
-- Struktur dari tabel `tbl_pelanggan`
--

CREATE TABLE `tbl_pelanggan` (
  `id` int(11) UNSIGNED NOT NULL,
  `nama` varchar(200) DEFAULT NULL,
  `alamat` varchar(200) DEFAULT NULL,
  `kota` char(100) DEFAULT NULL,
  `provinsi` char(100) DEFAULT NULL,
  `kodepos` char(20) DEFAULT NULL,
  `telp` char(20) DEFAULT NULL,
  `email` char(100) DEFAULT NULL,
  `status` int(11) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `foto` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tbl_pelanggan`
--

INSERT INTO `tbl_pelanggan` (`id`, `nama`, `alamat`, `kota`, `provinsi`, `kodepos`, `telp`, `email`, `status`, `password`, `foto`) VALUES
(1, 'admin', 'admin', 'admin', 'admin', '0000', '0000', 'admin@gmail.com', 1, '$2y$10$Ibt/aZEJmPssMzcBJ8nmyeJJEw9tk6daOSGEy/NFTS1IRxuCArVdC', ''),
(27, 'sarep', 'zzz', 'ffg', 'gcuuc', '869', '6580', 'sarep@gmail.com', 1, '$2y$10$iZmny6BSQyATVc6U0Izo3Ok2MOteQWGvK3h3uLMd.H7bhrNMiLNWC', ''),
(29, 'eko123', 'Gajah raya', 'Semarang', 'jawa tengah', '97634', '089675431245', 'eko@gmail.com', 1, '$2y$10$cavrcawPe.pb5fpn1bjNUeY2bTM687H5rWhjOHYd8vxykMtZMcDSm', 'uploadimage/profile_eko_gmail_com_1751327677.jpg'),
(30, 'samsul', 'jsjjakak', 'njsjskll', 'nullkzkkzkakak', '44646', '676997', 'samsul@gmail.com', 1, '$2y$10$Mezkagv8A.NlrDTB1so4TeNvMRl6sNXcTeZWemu8A/oHOmRNJ82A.', ''),
(33, 'andi', 'jalan pemuda', '', '', '', '', 'andi@gmail.com', 1, '$2y$10$Sbw5qv8YivpcYGDkA3XmeuHjEzq7thdjiVI36UXN5aIyyab6VNriW', ''),
(39, 'yanto kopyok', 'JL Plewan 3 ', 'Semarang', 'Jawa tengah', '0093335', '0897254163', 'y@gmail.com', 1, '$2y$12$QQ4DEOlCaXLomtWrKO58F.Uza9n9E0vd.B/3fB4MDDcV5.mQuaWZu', 'uploadimage/profile_y_gmail_com_1751786655.jpg'),
(41, 'feb', '', 'smg', 'jtg', '0896', '08964575', 'feb@gmail.com', 1, '$2y$12$nEiZaXsJv7wcgBllWB8Ab.P.B6khjWnHQe2ZvV7ZZvNoTwZdbBb4K', 'uploadimage/profile_feb_gmail_com_1751787408.jpeg');

-- --------------------------------------------------------

--
-- Struktur dari tabel `tbl_product`
--

CREATE TABLE `tbl_product` (
  `kode` char(10) NOT NULL,
  `merk` varchar(200) DEFAULT NULL,
  `kategori` char(30) DEFAULT NULL,
  `satuan` char(20) DEFAULT NULL,
  `hargabeli` double DEFAULT NULL,
  `diskonbeli` double DEFAULT NULL,
  `hargapokok` double DEFAULT NULL,
  `hargajual` double DEFAULT NULL,
  `diskonjual` double DEFAULT NULL,
  `stok` int(11) DEFAULT NULL,
  `foto` varchar(255) DEFAULT NULL,
  `deskripsi` longtext DEFAULT NULL,
  `view` int(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tbl_product`
--

INSERT INTO `tbl_product` (`kode`, `merk`, `kategori`, `satuan`, `hargabeli`, `diskonbeli`, `hargapokok`, `hargajual`, `diskonjual`, `stok`, `foto`, `deskripsi`, `view`) VALUES
('CI002', 'Rosetail Red', 'Impor', 'Ekor', 100000, 0, 80000, 100000, 0, 12, 'Rosetail.jpg', 'Varian Halfmoon yang langka dan menawan, Rosetail Red memiliki ciri khas ujung ekor yang bertumpuk-tumpuk menyerupai kelopak mawar yang sedang mekar, memberikan kesan lembut dan anggun pada gerakan renangnya.', 9),
('CI003', 'Alien Betta Blue', 'Impor', 'Ekor', 175000, 0, 175000, 175000, 0, 7, 'alien.jpg', 'Ikan cupang hasil persilangan genetik antara wild betta dan domestik ini menghadirkan kombinasi warna dan bentuk yang eksotis. Alien Betta Blue didominasi warna biru yang intens dengan pola unik yang liar dan memikat.', 8),
('CI004', 'Giant Betta', 'Cupang Impor', 'Ekor', 75000, 8, 75000, 75000, 0, 7, 'GiantBetta.jpg', 'Sesuai namanya, Giant Betta memiliki ukuran tubuh yang jauh lebih besar dari cupang pada umumnya. Ikan ini dapat tumbuh hingga 10 cm, menampilkan kegagahan dan kekuatan yang mengesankan di dalam akuarium.', 3),
('CI005', 'Dragon Red', 'Cupang Impor', 'Ekor', 90000, 12, 90000, 90000, 0, 10, 'DragonRed.jpg', 'Cupang Dragon Red mempesona dengan sisiknya yang unik, menyerupai sisik naga yang kokoh. Warna merah metaliknya yang menyala menciptakan tampilan yang mistis dan gagah, seolah berasal dari dunia fantasi.', 3),
('CI006', 'Koi Galaxy', 'Cupang Impor', 'Ekor', 120000, 18, 120000, 120000, 0, 13, 'Koi.jpg', 'Inspirasi dari keindahan ikan Koi tercermin jelas pada cupang Koi Galaxy. Coraknya yang unik menyerupai pola warna Koi, dipadukan dengan efek Galaxy yang berkilauan seperti taburan bintang, menghasilkan karya seni hidup yang mempesona.', 1),
('CI007', 'Dumbo Ear Blue', 'Cupang Impor', 'Ekor', 125000, 5, 125000, 125000, 0, 4, 'DumboBlue.jpg', 'Dumbo Ear Blue memiliki daya tarik tersendiri pada sirip dadanya yang besar dan lebar, menyerupai telinga gajah yang menggemaskan. Warna biru yang menenangkan menambah kesan damai pada ikan cupang ini.', 0),
('CI008', 'Halfmoon Plakat Thailand', 'Impor', 'Ekor', 120000, 0, 120000, 150000, 0, 12, 'CI008.jpg', 'Halfmoon Plakat mempunyai ciri yang eksotis, dan memikat hati para penggemar ikan cupang sejati , dengan konfigurasi warna yang cantik dan memanjakan mata', 0),
('CL001', 'Crown Tail Merah', 'Cupang Lokal', 'Ekor', 55000, 7, 55000, 55000, 0, 7, 'FancyBetta.jpg', 'Cupang Crown Tail Merah menampilkan keindahan ekornya yang bergerigi, menyerupai mahkota yang megah. Warna merahnya yang berani dan menyala menambah kesan mewah dan elegan.', 0),
('CL002', 'Hellboy Betta', 'Cupang Lokal', 'Ekor', 45000, 9, 45000, 45000, 0, 9, 'HellboyBetta.jpg', 'Seperti namanya, Hellboy Betta memancarkan aura garang dengan warna merah pekat yang intens dan karakter yang cenderung agresif. Ikan ini cocok untuk Anda yang menyukai tantangan dan keindahan yang kuat.', 0),
('CL003', 'Black Samural', 'Cupang Lokal', 'Ekor', 150000, 4, 150000, 150000, 0, 3, 'samurai.jpg', 'Elegan dan misterius, Black Samural memikat dengan warna hitamnya yang pekat dan sisik metalik yang berkilauan. Penampilannya yang gagah mengingatkan pada seorang samurai yang siap bertarung.', 0),
('CL004', 'Plakat Lokal', 'Cupang Lokal', 'Ekor', 50000, 6, 50000, 50000, 0, 5, 'PlakatLokal.jpg', 'Cupang Plakat Lokal dikenal dengan ekornya yang pendek dan kuat, menjadikannya lincah dan gesit. Jenis ini sangat cocok untuk aduan karena daya tahannya dan gerakan cepatnya.', 0),
('CL005', 'Halfmoon Lokal', 'Cupang Lokal', 'Ekor', 35000, 11, 35000, 35000, 0, 11, 'HalfmoonLokal.jpg', 'Halfmoon Lokal menampilkan keindahan ekor berbentuk setengah lingkaran yang simetris. Warna solidnya yang menarik perhatian menjadikannya pilihan populer bagi pecinta cupang.', 0),
('CL006', 'Nemo Fancy', 'Cupang Lokal', 'Ekor', 40000, 13, 40000, 40000, 0, 13, 'Nemo.jpg', 'Terinspirasi dari ikan Nemo yang ceria, cupang Nemo Fancy memiliki warna campuran oranye, biru, dan hitam yang cerah dan kontras. Penampilannya yang unik dan menggemaskan pasti akan mencuri perhatian.', 0),
('CL007', 'Avatar Betta', 'Cupang Lokal', 'Ekor', 80000, 3, 80000, 80000, 0, 8, 'avatar.jpg', 'Cupang Avatar Betta menawarkan kombinasi warna yang futuristik. Warna dasar hitamnya yang gelap berpadu dengan corak biru neon yang mencolok, menciptakan tampilan yang unik dan modern.', 0),
('CL008', 'Blue Rim', 'Cupang Lokal', 'Ekor', 55000, 14, 55000, 55000, 0, 14, 'bluerim.jpg', 'Kelembutan warna putih mutiara berpadu dengan elegan dengan pinggiran biru yang membingkai siripnya. Blue Rim memberikan kesan tenang dan anggun, cocok untuk pecinta keindahan yang kalem.', 0),
('CL009', 'Fancy Betta', 'Cupang Lokal', 'Ekor', 45000, 2, 45000, 45000, 0, 21, 'FancyBetta.jpg', 'Setiap cupang Fancy Betta adalah karya seni yang unik. Warna campur cerah dan beragam menciptakan kombinasi yang tak terduga dan mempesona, menjadikannya koleksi yang menarik.', 0),
('CL010', 'Multicolor Betta', 'Cupang Lokal', 'Ekor', 35000, 16, 35000, 35000, 0, 15, 'Multicolor.jpg', 'Cupang Multicolor Betta menawarkan kebebasan dalam ekspresi warna. Variasi warna yang bebas dan acak menciptakan tampilan yang penuh kejutan dan dinamis.', 0),
('CL011', 'Double Tail', 'Cupang Lokal', 'Ekor', 70000, 17, 70000, 70000, 0, 15, 'DoubleTail.jpg', 'Keunikan cupang Double Tail terletak pada ekornya yang bercabang dua secara simetris. Bentuk ekor yang istimewa ini menambah daya tarik dan keanggunan pada penampilannya.', 0),
('CL012', 'Candy Betta', 'Cupang Lokal', 'Ekor', 45000, 19, 45000, 45000, 0, 18, 'CandyBetta.jpg', 'Cupang Candy Betta menghadirkan keceriaan dengan kombinasi warna manis seperti permen. Warna-warnanya yang cerah dan menggugah selera akan membuat akuarium Anda semakin hidup.', 0),
('CL013', 'Marble Betta', 'Cupang Lokal', 'Ekor', 15000, 21, 15000, 15000, 0, 20, 'Marble.jpg', 'Seperti lukisan abstrak, cupang Marble Betta menampilkan warna acak seperti bercak tinta di atas kanvas. Tampilan artistiknya yang unik menjadikannya pilihan yang menarik bagi pecinta seni.', 0);

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `tbl_order`
--
ALTER TABLE `tbl_order`
  ADD PRIMARY KEY (`id_order`);

--
-- Indeks untuk tabel `tbl_order_detail`
--
ALTER TABLE `tbl_order_detail`
  ADD PRIMARY KEY (`id_order`,`kode_product`);

--
-- Indeks untuk tabel `tbl_pelanggan`
--
ALTER TABLE `tbl_pelanggan`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `tbl_product`
--
ALTER TABLE `tbl_product`
  ADD PRIMARY KEY (`kode`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `tbl_order`
--
ALTER TABLE `tbl_order`
  MODIFY `id_order` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=56;

--
-- AUTO_INCREMENT untuk tabel `tbl_pelanggan`
--
ALTER TABLE `tbl_pelanggan`
  MODIFY `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=42;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
