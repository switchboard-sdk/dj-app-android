package com.synervoz.djapp

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.synervoz.djapp.databinding.ActivityMainBinding
import com.synervoz.switchboard.sdk.SwitchboardSDK
import com.synervoz.switchboard.sdk.logger.Logger
import com.synervoz.switchboardsuperpowered.SuperpoweredExtension
import java.io.IOException
import kotlin.math.cos

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var audioEngine: MainAudioEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setFullScreen()
        setContentView(binding.root)

        SwitchboardSDK.initialize("Your client ID", "Your client secret")
        SuperpoweredExtension.initialize("ExampleLicenseKey-WillExpire-OnNextUpdate")

        audioEngine = MainAudioEngine()

        loadAudioFiles()
        setupButtons()
        setupVolumeSliders()
        setupEffects()
        setupCrossfader()
    }

    private fun setupCrossfader() {
        audioEngine.gainNodeLeft.gain = (binding.volumeLeft.value * cos(Math.PI / 2 * binding.crossfader.value)).toFloat()
        audioEngine.gainNodeRight.gain = (binding.volumeRight.value * cos(Math.PI / 2 * (1.0f - binding.crossfader.value))).toFloat()

        binding.crossfader.addOnChangeListener { slider, value, fromUser ->
            audioEngine.gainNodeLeft.gain = (binding.volumeLeft.value * cos(Math.PI / 2 * value)).toFloat()
            audioEngine.gainNodeRight.gain = (binding.volumeRight.value * cos(Math.PI / 2 * (1.0f - value))).toFloat()
        }
    }

    private fun setupEffects() {

        binding.playbackRateLeft.value = audioEngine.audioPlayerNodeLeft.playbackRate.toFloat()
        binding.playbackRateLeft.addOnChangeListener { slider, value, fromUser ->
            audioEngine.audioPlayerNodeLeft.playbackRate = value.toDouble()
        }

        binding.playbackRateRight.value = audioEngine.audioPlayerNodeRight.playbackRate.toFloat()
        binding.playbackRateRight.addOnChangeListener { slider, value, fromUser ->
            audioEngine.audioPlayerNodeRight.playbackRate = value.toDouble()
        }

        binding.compressorLeft.isChecked = audioEngine.compressorNodeLeft.isEnabled
        binding.compressorLeft.setOnCheckedChangeListener { compoundButton, isChecked ->
            audioEngine.compressorNodeLeft.isEnabled = isChecked
        }

        binding.flangerLeft.isChecked = audioEngine.flangerNodeLeft.isEnabled
        binding.flangerLeft.setOnCheckedChangeListener { compoundButton, isChecked ->
            audioEngine.flangerNodeLeft.isEnabled = isChecked
        }

        binding.reverbLeft.isChecked = audioEngine.reverbNodeLeft.isEnabled
        binding.reverbLeft.setOnCheckedChangeListener { compoundButton, isChecked ->
            audioEngine.reverbNodeLeft.isEnabled = isChecked
        }

        binding.filterLeft.isChecked = audioEngine.filterNodeLeft.isEnabled
        binding.filterLeft.setOnCheckedChangeListener { compoundButton, isChecked ->
            audioEngine.filterNodeLeft.isEnabled = isChecked
        }

        binding.compressorRight.isChecked = audioEngine.compressorNodeRight.isEnabled
        binding.compressorRight.setOnCheckedChangeListener { compoundButton, isChecked ->
            audioEngine.compressorNodeRight.isEnabled = isChecked
        }

        binding.flangerRight.isChecked = audioEngine.flangerNodeRight.isEnabled
        binding.flangerRight.setOnCheckedChangeListener { compoundButton, isChecked ->
            audioEngine.flangerNodeRight.isEnabled = isChecked
        }

        binding.reverbRight.isChecked = audioEngine.reverbNodeRight.isEnabled
        binding.reverbRight.setOnCheckedChangeListener { compoundButton, isChecked ->
            audioEngine.reverbNodeRight.isEnabled = isChecked
        }

        binding.filterRight.isChecked = audioEngine.filterNodeRight.isEnabled
        binding.filterRight.setOnCheckedChangeListener { compoundButton, isChecked ->
            audioEngine.filterNodeRight.isEnabled = isChecked

        }
    }

    fun loadAudioFiles() {
        // Files under res/raw are not zipped, just copied into the APK.
        // Get the offset and length to know where our file is located.
        val assetFileDescriptorLeft = assets.openFd("lycka.mp3")
        try {
            assetFileDescriptorLeft.parcelFileDescriptor.close()
        } catch (e: IOException) {
            Logger.debug("Could not close asset file!")
        }

        audioEngine.loadLeft(
            packageResourcePath,
            assetFileDescriptorLeft.startOffset.toInt(),
            assetFileDescriptorLeft.length.toInt()
        )

        val assetFileDescriptorRight = assets.openFd("nuyorica.m4a")
        try {
            assetFileDescriptorRight.parcelFileDescriptor.close()
        } catch (e: IOException) {
            Logger.debug("Could not close asset file!")
        }

        audioEngine.loadRight(
            packageResourcePath,
            assetFileDescriptorRight.startOffset.toInt(),
            assetFileDescriptorRight.length.toInt()
        )
    }

    private fun setupVolumeSliders() {
        binding.volumeLeft.value = audioEngine.gainNodeLeft.gain
        binding.volumeLeft.addOnChangeListener { slider, value, fromUser ->
            audioEngine.gainNodeLeft.gain = value
        }

        binding.volumeRight.value = audioEngine.gainNodeRight.gain
        binding.volumeRight.addOnChangeListener { slider, value, fromUser ->
            audioEngine.gainNodeRight.gain = value
        }
    }

    private fun setupButtons() {
        binding.playPauseButton.setOnClickListener {
            if (audioEngine.audioPlayerNodeLeft.isPlaying) {
                audioEngine.pausePlayback()
                binding.playPauseButton.text = "Play"
            } else {
                binding.playPauseButton.text = "Pause"
                audioEngine.startPlayback()
            }
        }
    }

    private fun setFullScreen() {
        // Hide the status bar
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Hide the navigation bar
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = uiOptions
    }


}