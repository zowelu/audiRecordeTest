package com.example.soundrecordertest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.soundrecordertest.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding

    private val recorder = AudioRecorder(this)
    private val player = AudioPlayer(this)

    private var audioFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * Kontrola povolení k zápisu a ke čtení na zařízení. Asi bude stačit jen mikrofon.
         * */
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, 0)
        }

        binding.buttonStarRec.setOnClickListener {

            /** Vytvoření a pojmenování nového souboru*/
            val audioFile = File(
                cacheDir,
                "audio_record_" + SimpleDateFormat("yy-MM-dd-HH-mm-ss", Locale.getDefault())
                    .format(System.currentTimeMillis()) + ".mp3",
            )

            recorder.start(audioFile)
            this.audioFile = audioFile

            binding.currentAudioPathTV.text = audioFile.path
        }

        binding.buttonStopRec.setOnClickListener {
            recorder.stop()
        }

        binding.buttonStartPlay.setOnClickListener {
            player.playFile(audioFile ?: return@setOnClickListener)
        }

        binding.buttonStopPlay.setOnClickListener {
            player.stop()
        }

        binding.buttonNextScreen.setOnClickListener {
            goToNextScreen()
        }

    }

    private fun goToNextScreen(){
        val intent = Intent(applicationContext, ActivityNext::class.java)
        intent.putExtra("audioFilePath", audioFile?.toURI()?.path)
        startActivity(intent)
        finish()
    }
}


class AudioRecorder(private val context: Context) {

    private var recorder: MediaRecorder? = null

    private fun createRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return MediaRecorder(context)
        } else {
           return MediaRecorder()
        }
    }

    /**
     * Spuští nahrávání
     * */
    fun start(outputFile: File) {
        val recorder = createRecorder()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        recorder.setOutputFile(FileOutputStream(outputFile).fd)

        recorder.prepare()
        recorder.start()

        this.recorder = recorder
    }

    /**
     * Zastavuje nahrávání
     * */
    fun stop() {
        recorder?.stop()
        recorder?.reset()
        recorder = null
    }

}

class AudioPlayer(private val context: Context) {
    private var player: MediaPlayer? = null

    /**
     * Spuští přehrávání
     * */
    fun playFile(file: File) {
        val mediaPlayer = MediaPlayer.create(context, Uri.fromFile(file))

        mediaPlayer.start()
        player = mediaPlayer
    }

    /**
     * Zastavuje přehrávání
     * */
    fun stop(){
        player?.stop()
        player?.reset()
        player = null
    }
}