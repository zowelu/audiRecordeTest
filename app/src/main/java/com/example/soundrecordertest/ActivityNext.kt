package com.example.soundrecordertest

import android.app.Activity
import android.os.Bundle
import com.example.soundrecordertest.databinding.ActivityMainBinding
import com.example.soundrecordertest.databinding.ActivityNextBinding
import java.io.File

class ActivityNext: Activity() {

    private lateinit var binding: ActivityNextBinding
    private var audioFilePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        audioFilePath = intent.extras?.getString("audioFilePath")

        binding.checkAvaibiliotyBtn.setOnClickListener {
            checkIfIsFileAvailable()
        }

    }

    private fun checkIfIsFileAvailable(){
        val fileDictionary = File(audioFilePath)
        if(fileDictionary.exists()) {
            binding.checkUriTV.text = "ano"
        } else {
            binding.checkUriTV.text = "ne"
        }
    }

}