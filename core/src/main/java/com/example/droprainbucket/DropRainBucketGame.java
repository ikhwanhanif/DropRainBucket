package com.example.droprainbucket; // Menentukan package untuk aplikasi ini

import com.badlogic.gdx.ApplicationAdapter; // Import kelas dasar untuk aplikasi libGDX
import com.badlogic.gdx.Gdx; // Import kelas untuk interaksi dengan sistem
import com.badlogic.gdx.audio.Music; // Import untuk musik
import com.badlogic.gdx.audio.Sound; // Import untuk suara efek
import com.badlogic.gdx.graphics.Texture; // Import untuk mengelola gambar
import com.badlogic.gdx.graphics.g2d.BitmapFont; // Import untuk font
import com.badlogic.gdx.graphics.g2d.GlyphLayout; // Untuk menghitung lebar teks
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Untuk menggambar gambar dan teks
import com.badlogic.gdx.math.Rectangle; // Menggunakan bentuk persegi panjang (rect) untuk objek
import com.badlogic.gdx.utils.Array; // Import untuk array dinamis
import com.badlogic.gdx.utils.TimeUtils; // Import untuk pengelolaan waktu

import java.util.Iterator; // Import untuk pengelolaan iterator

public class DropRainBucketGame extends ApplicationAdapter { // Kelas utama yang mewarisi ApplicationAdapter
    private SpriteBatch batch; // Objek untuk menggambar gambar dan teks
    private Texture background; // Gambar latar belakang
    private Texture bucketTexture; // Gambar ember
    private Texture dropletTexture; // Gambar tetesan hujan
    private Sound dropSound; // Suara tetesan hujan
    private Music backgroundMusic; // Musik latar belakang

    private Rectangle bucket; // Representasi ember dengan bentuk persegi panjang
    private Array<Rectangle> raindrops; // Array yang menyimpan tetesan hujan
    private long lastDropTime; // Waktu terakhir tetesan hujan jatuh
    private int score; // Skor permainan
    private int missedDrops; // Jumlah tetesan hujan yang terlewat

    private BitmapFont font; // Font untuk menampilkan teks
    private boolean isGameOver; // Menandakan apakah permainan selesai
    private boolean isSuccess; // Menandakan apakah permainan berhasil
    private float dropSpeed = 200; // Kecepatan tetesan hujan

    private long gameOverTime; // Waktu saat permainan selesai
    private final long gameOverDelay = 2000; // Waktu tunggu sebelum permainan dimulai lagi
    private boolean isWaiting; // Menandakan apakah permainan sedang menunggu untuk diulang

    private GlyphLayout glyphLayout; // Digunakan untuk mengatur layout teks

    // Variabel untuk interval waktu antar tetesan
    private float dropInterval = 2.0f; // Interval pertama untuk jatuhnya tetesan hujan (dalam detik)
    private float timeSinceLastDrop = 0; // Waktu yang telah berlalu sejak tetesan terakhir

    @Override
    public void create() { // Metode yang dipanggil saat game dimulai
        batch = new SpriteBatch(); // Membuat objek SpriteBatch untuk menggambar
        background = new Texture("backgroundhujan.jpg"); // Memuat gambar latar belakang
        bucketTexture = new Texture("bucket.png"); // Memuat gambar ember
        dropletTexture = new Texture("drop.png"); // Memuat gambar tetesan hujan

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3")); // Memuat suara tetesan hujan
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3")); // Memuat musik latar belakang

        backgroundMusic.setLooping(true); // Memutar musik secara terus-menerus
        backgroundMusic.setVolume(0.5f); // Mengatur volume musik
        backgroundMusic.play(); // Memulai pemutaran musik

        font = new BitmapFont(); // Membuat font untuk teks
        font.getData().setScale(4.0f); // Mengatur skala font
        glyphLayout = new GlyphLayout(); // Membuat objek GlyphLayout untuk menghitung teks

        initializeGame(); // Memulai permainan
    }

    private void initializeGame() { // Metode untuk menginisialisasi status awal game
        bucket = new Rectangle(); // Membuat objek persegi panjang untuk ember
        bucket.width = 64; // Mengatur lebar ember
        bucket.height = 64; // Mengatur tinggi ember
        bucket.x = Gdx.graphics.getWidth() / 2 - bucket.width / 2; // Mengatur posisi ember di tengah layar
        bucket.y = 20; // Posisi ember di bagian bawah layar

        raindrops = new Array<>(); // Membuat array untuk tetesan hujan
        spawnRaindrop(); // Memulai tetesan hujan pertama

        score = 0; // Skor awal permainan
        missedDrops = 0; // Jumlah tetesan yang terlewat
        dropSpeed = 200; // Kecepatan awal tetesan hujan

        isGameOver = false; // Permainan belum selesai
        isSuccess = false; // Permainan belum berhasil
        isWaiting = false; // Tidak menunggu untuk memulai ulang permainan
    }

    private void spawnRaindrop() { // Metode untuk menambahkan tetesan hujan baru
        Rectangle raindrop = new Rectangle(); // Membuat objek tetesan hujan
        raindrop.width = 30; // Lebar tetesan
        raindrop.height = 32; // Tinggi tetesan
        raindrop.x = (float) Math.random() * (Gdx.graphics.getWidth() - raindrop.width); // Posisi acak di layar
//        raindrop.x = Math.max(0, Math.min(Gdx.graphics.getWidth() - raindrop.width, (float) Math.random() * (Gdx.graphics.getWidth() - raindrop.width)));

        raindrop.y = Gdx.graphics.getHeight(); // Posisi awal di atas layar
        raindrops.add(raindrop); // Menambahkan tetesan ke array
        lastDropTime = TimeUtils.nanoTime(); // Menyimpan waktu saat tetesan dibuat
    }

    @Override
    public void render() { // Metode untuk menggambar objek dan memperbarui status permainan

        if (isGameOver || isSuccess) { // Jika permainan selesai atau berhasil
            if (isWaiting) { // Jika permainan menunggu
                batch.begin(); // Memulai menggambar

                String message = isGameOver ? "GAME OVER! Tap anywhere to restart" : "SUCCESS! Tap anywhere to restart"; // Menampilkan pesan game over atau sukses
                glyphLayout.setText(font, message); // Mengatur teks
                float messageWidth = glyphLayout.width; // Lebar pesan
                float messageX = (Gdx.graphics.getWidth() - messageWidth) / 2; // Mengatur posisi pesan di tengah layar
                font.draw(batch, message, messageX, Gdx.graphics.getHeight() / 2f); // Menampilkan pesan

                String scoreText = "Score: " + score; // Menampilkan skor
                glyphLayout.setText(font, scoreText); // Mengatur teks skor
                float scoreWidth = glyphLayout.width; // Lebar teks skor
                float scoreX = (Gdx.graphics.getWidth() - scoreWidth) / 2; // Posisi teks skor di tengah
                font.draw(batch, scoreText, scoreX, Gdx.graphics.getHeight() / 2f - 70); // Menampilkan teks skor

                batch.end(); // Selesai menggambar

                if (Gdx.input.isTouched()) { // Jika layar disentuh
                    initializeGame(); // Mulai ulang permainan
                }
            } else { // Jika menunggu waktu delay sebelum memulai ulang
                if (TimeUtils.nanoTime() - gameOverTime > gameOverDelay * 1_000_000) { // Mengecek apakah waktu delay sudah habis
                    isWaiting = true; // Ubah status ke menunggu
                }
            }
            return; // Menghentikan render
        }

        if (score % 10 == 0 && score > 0) { // Jika skor kelipatan 10
            dropSpeed += 2; // Tambahkan kecepatan tetesan
        }

        if (Gdx.input.isTouched()) { // Jika layar disentuh
            bucket.x = Gdx.input.getX() - bucket.width / 2; // Ubah posisi ember mengikuti sentuhan
        }

        if (bucket.x < 0) bucket.x = 0; // Ember tidak boleh keluar dari layar kiri
        if (bucket.x > Gdx.graphics.getWidth() - bucket.width) bucket.x = Gdx.graphics.getWidth() - bucket.width; // Ember tidak boleh keluar dari layar kanan

        timeSinceLastDrop += Gdx.graphics.getDeltaTime(); // Menambah waktu sejak tetesan terakhir

        if (timeSinceLastDrop >= dropInterval) { // Jika waktu antara tetesan cukup
            spawnRaindrop(); // Tambahkan tetesan hujan
            timeSinceLastDrop = 0; // Reset waktu

            dropInterval = 0.7f; // Atur interval jatuhnya tetesan berikutnya
        }

        for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) { // Untuk setiap tetesan hujan
            Rectangle raindrop = iter.next(); // Ambil tetesan hujan berikutnya
            raindrop.y -= dropSpeed * Gdx.graphics.getDeltaTime(); // Gerakkan tetesan turun

            if (raindrop.y + raindrop.height < 0) { // Jika tetesan keluar dari layar
                missedDrops++; // Tambahkan jumlah tetesan yang terlewat
                iter.remove(); // Hapus tetesan tersebut
                if (missedDrops >= 10) { // Jika sudah lebih dari 10 tetesan terlewat
                    isGameOver = true; // Set game over
                    gameOverTime = TimeUtils.nanoTime(); // Catat waktu game over
                }
            }

            if (raindrop.overlaps(bucket)) { // Jika tetesan mengenai ember
                dropSound.play(); // Mainkan suara tetesan
                score++; // Tambahkan skor
                iter.remove(); // Hapus tetesan yang mengenai ember
                if (score >= 100) { // Jika skor sudah mencapai 100
                    isSuccess = true; // Set permainan berhasil
                    gameOverTime = TimeUtils.nanoTime(); // Catat waktu sukses
                }
            }
        }

        batch.begin(); // Memulai menggambar
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Gambar latar belakang
        batch.draw(bucketTexture, bucket.x, bucket.y); // Gambar ember
        for (Rectangle raindrop : raindrops) { // Gambar setiap tetesan hujan
            batch.draw(dropletTexture, raindrop.x, raindrop.y);
        }

        String scoreText = "Score: " + score; // Tampilkan skor
        glyphLayout.setText(font, scoreText); // Mengatur teks skor
        float scoreWidth = glyphLayout.width; // Lebar teks skor
        float scoreX = (Gdx.graphics.getWidth() - scoreWidth) / 2; // Posisi skor di tengah
        font.draw(batch, scoreText, scoreX, Gdx.graphics.getHeight() - 20); // Gambar teks skor

        String missedText = "Missed: " + missedDrops; // Tampilkan jumlah tetesan terlewat
        glyphLayout.setText(font, missedText); // Mengatur teks terlewat
        float missedWidth = glyphLayout.width; // Lebar teks terlewat
        float missedX = (Gdx.graphics.getWidth() - missedWidth) / 2; // Posisi teks terlewat di tengah
        font.draw(batch, missedText, missedX, Gdx.graphics.getHeight() - 80); // Gambar teks terlewat

        batch.end(); // Selesai menggambar
    }

    @Override
    public void dispose() { // Metode untuk membebaskan sumber daya
        batch.dispose(); // Bebaskan SpriteBatch
        background.dispose(); // Bebaskan gambar latar belakang
        bucketTexture.dispose(); // Bebaskan gambar ember
        dropletTexture.dispose(); // Bebaskan gambar tetesan hujan
        dropSound.dispose(); // Bebaskan suara tetesan
        backgroundMusic.dispose(); // Bebaskan musik latar
        font.dispose(); // Bebaskan font
    }
}
