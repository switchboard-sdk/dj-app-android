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
            audioEngine.setCrossfader(value, binding.volumeA.value, binding.volumeB.value)
        }
    }

    private fun setupEffects() {

        binding.playbackRateA.value = audioEngine.audioPlayerNodeBWithMasterControl.playbackRate.toFloat()
        binding.playbackRateA.addOnChangeListener { slider, value, fromUser ->
            audioEngine.audioPlayerNodeBWithMasterControl.playbackRate = value.toDouble()
        }

        binding.playbackRateB.value = audioEngine.audioPlayerNodeB.playbackRate.toFloat()
        binding.playbackRateB.addOnChangeListener { slider, value, fromUser ->
            audioEngine.audioPlayerNodeB.playbackRate = value.toDouble()
        }

        binding.compressorA.isChecked = audioEngine.compressorNodeA.isEnabled
        binding.compressorA.setOnCheckedChangeListener { compoundButton, isChecked ->
            audioEngine.compressorNodeA.isEnabled = isChecked
        }

        binding.flangerA.isChecked = audioEngine.flangerNodeA.isEnabled
        binding.flangerA.setOnCheckedChangeListener { compoundButton, isChecked ->
            audioEngine.flangerNodeA.isEnabled = isChecked
        }

        binding.reverbA.isChecked = audioEngine.reverbNodeA.isEnabled
        binding.reverbA.setOnCheckedChangeListener { compoundButton, isChecked ->
            audioEngine.reverbNodeA.isEnabled = isChecked
        }

        binding.filterA.isChecked = audioEngine.filterNodeA.isEnabled
        binding.filterA.setOnCheckedChangeListener { compoundButton, isChecked ->
            audioEngine.filterNodeA.isEnabled = isChecked
        }

        binding.compressorB.isChecked = audioEngine.compressorNodeB.isEnabled
        binding.compressorB.setOnCheckedChangeListener { compoundButton, isChecked ->
            audioEngine.compressorNodeB.isEnabled = isChecked
        }

        binding.flangerB.isChecked = audioEngine.flangerNodeB.isEnabled
        binding.flangerB.setOnCheckedChangeListener { compoundButton, isChecked ->
            audioEngine.flangerNodeB.isEnabled = isChecked
        }

        binding.reverbB.isChecked = audioEngine.reverbNodeB.isEnabled
        binding.reverbB.setOnCheckedChangeListener { compoundButton, isChecked ->
            audioEngine.reverbNodeB.isEnabled = isChecked
        }

        binding.filterB.isChecked = audioEngine.filterNodeB.isEnabled
        binding.filterB.setOnCheckedChangeListener { compoundButton, isChecked ->
            audioEngine.filterNodeB.isEnabled = isChecked

        }
    }

    fun loadAudioFiles() {
        // Files under res/raw are not zipped, just copied into the APK.
        // Get the offset and length to know where our file is located.
        val assetFileDescriptorA = assets.openFd("lycka.mp3")
        try {
            assetFileDescriptorA.parcelFileDescriptor.close()
        } catch (e: IOException) {
            Logger.debug("Could not close asset file!")
        }

        audioEngine.loadA(
            packageResourcePath,
            assetFileDescriptorA.startOffset.toInt(),
            assetFileDescriptorA.length.toInt()
        )

        val assetFileDescriptorB = assets.openFd("nuyorica.m4a")
        try {
            assetFileDescriptorB.parcelFileDescriptor.close()
        } catch (e: IOException) {
            Logger.debug("Could not close asset file!")
        }

        audioEngine.loadB(
            packageResourcePath,
            assetFileDescriptorB.startOffset.toInt(),
            assetFileDescriptorB.length.toInt()
        )

        audioEngine.setBeatGridInformationA(126.0, 353.0)
        audioEngine.setBeatGridInformationB(123.0, 40.0)
        audioEngine.setCrossfader(0.0f, binding.volumeA.value, binding.volumeB.value)
    }

    private fun setupVolumeSliders() {
        binding.volumeA.value = audioEngine.gainNodeA.gain
        binding.volumeA.addOnChangeListener { slider, value, fromUser ->
            audioEngine.gainNodeA.gain = value
        }

        binding.volumeB.value = audioEngine.gainNodeB.gain
        binding.volumeB.addOnChangeListener { slider, value, fromUser ->
            audioEngine.gainNodeB.gain = value
        }
    }

    private fun setupButtons() {
        binding.playPauseButton.setOnClickListener {
            if (audioEngine.audioPlayerNodeBWithMasterControl.isPlaying) {
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