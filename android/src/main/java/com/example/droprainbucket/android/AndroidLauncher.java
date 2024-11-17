package com.example.droprainbucket.android; // Mendeklarasikan package tempat kelas ini berada, yaitu package android dari proyek DropRainBucket

import android.os.Bundle; // Mengimpor kelas Bundle dari Android, yang digunakan untuk menyimpan status aktivitas

import com.badlogic.gdx.backends.android.AndroidApplication; // Mengimpor kelas AndroidApplication dari libGDX, yang memungkinkan aplikasi berjalan di platform Android
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration; // Mengimpor kelas konfigurasi untuk mengatur pengaturan awal aplikasi Android menggunakan libGDX
import com.example.droprainbucket.DropRainBucketGame; // Mengimpor kelas utama DropRainBucketGame dari proyek ini, yang berisi logika permainan

public class AndroidLauncher extends AndroidApplication { // Mendeklarasikan kelas AndroidLauncher yang mewarisi AndroidApplication
    @Override
    protected void onCreate(Bundle savedInstanceState) { // Method onCreate untuk menginisialisasi aplikasi Android saat pertama kali dijalankan
        super.onCreate(savedInstanceState); // Memanggil metode onCreate milik kelas induk (AndroidApplication) untuk melakukan inisialisasi dasar
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration(); // Membuat objek konfigurasi untuk aplikasi Android
        initialize(new DropRainBucketGame(), config); // Menginisialisasi game DropRainBucket dengan konfigurasi yang sudah disiapkan
    }
}
