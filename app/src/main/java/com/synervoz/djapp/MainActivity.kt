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

        audioEngine = MainAudioEngine(this)

        setupButtons()
        setupVolumeSliders()
        setupEffects()
        setupCrossfader()
        loadAudioFiles()
    }

    private fun setupCrossfader() {
        binding.crossfader.addOnChangeListener { slider, value, fromUser ->
            audioEngine.setCrossfader(value, binding.volumeLeft.value, binding.volumeRight.value)
        }
    }

    private fun setupEffects() {

        binding.playbackRateLeft.value = audioEngine.playerNodeWithMasterControl.playbackRate.toFloat()
        binding.playbackRateLeft.addOnChangeListener { slider, value, fromUser ->
            audioEngine.playerNodeWithMasterControl.playbackRate = value.toDouble()
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

        audioEngine.setBeatGridInformationA(126.0, 353.0)
        audioEngine.setBeatGridInformationB(123.0, 40.0)
        audioEngine.setCrossfader(0.0f, binding.volumeLeft.value, binding.volumeRight.value)
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
            if (audioEngine.playerNodeWithMasterControl.isPlaying) {
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

    override fun onDestroy() {
        audioEngine.close()
        super.onDestroy()
    }
}