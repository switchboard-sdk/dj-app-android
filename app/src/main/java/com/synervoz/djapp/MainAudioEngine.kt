package com.synervoz.djapp

import android.content.Context
import com.synervoz.switchboard.sdk.audioengine.AudioEngine
import com.synervoz.switchboard.sdk.audiograph.AudioGraph
import com.synervoz.switchboard.sdk.audiographnodes.GainNode
import com.synervoz.switchboard.sdk.audiographnodes.MixerNode
import com.synervoz.switchboardsuperpowered.audiographnodes.AdvancedAudioPlayerNode
import com.synervoz.switchboardsuperpowered.audiographnodes.CompressorNode
import com.synervoz.switchboardsuperpowered.audiographnodes.FilterNode
import com.synervoz.switchboardsuperpowered.audiographnodes.FlangerNode
import com.synervoz.switchboardsuperpowered.audiographnodes.ReverbNode
import kotlin.math.cos

class MainAudioEngine(context: Context) {
    val audioGraph = AudioGraph()
    val audioPlayerNodeAWithMasterControl = AdvancedAudioPlayerNode()
    val audioPlayerNodeB = AdvancedAudioPlayerNode()
    val gainNodeA = GainNode()
    val gainNodeB = GainNode()
    val compressorNodeA = CompressorNode()
    val compressorNodeB = CompressorNode()
    val flangerNodeA = FlangerNode()
    val flangerNodeB = FlangerNode()
    val reverbNodeA = ReverbNode()
    val reverbNodeB = ReverbNode()
    val filterNodeA = FilterNode()
    val filterNodeB = FilterNode()
    val mixerNode = MixerNode()
    val audioEngine = AudioEngine(context)

    init {
        audioPlayerNodeAWithMasterControl.isLoopingEnabled = true
        audioPlayerNodeB.isLoopingEnabled = true

        audioGraph.addNode(audioPlayerNodeAWithMasterControl)
        audioGraph.addNode(audioPlayerNodeB)
        audioGraph.addNode(mixerNode)
        audioGraph.addNode(gainNodeA)
        audioGraph.addNode(gainNodeB)

        audioGraph.addNode(compressorNodeA)
        audioGraph.addNode(compressorNodeB)
        audioGraph.addNode(flangerNodeA)
        audioGraph.addNode(flangerNodeB)
        audioGraph.addNode(reverbNodeA)
        audioGraph.addNode(reverbNodeB)
        audioGraph.addNode(filterNodeA)
        audioGraph.addNode(filterNodeB)

        audioGraph.connect(audioPlayerNodeAWithMasterControl, gainNodeA)
        audioGraph.connect(gainNodeA, compressorNodeA)
        audioGraph.connect(compressorNodeA, flangerNodeA)
        audioGraph.connect(flangerNodeA, reverbNodeA)
        audioGraph.connect(reverbNodeA, filterNodeA)
        audioGraph.connect(filterNodeA, mixerNode)

        audioGraph.connect(audioPlayerNodeB, gainNodeB)
        audioGraph.connect(gainNodeB, compressorNodeB)
        audioGraph.connect(compressorNodeB, flangerNodeB)
        audioGraph.connect(flangerNodeB, reverbNodeB)
        audioGraph.connect(reverbNodeB, filterNodeB)
        audioGraph.connect(filterNodeB, mixerNode)

        audioGraph.connect(mixerNode, audioGraph.outputNode)

        audioPlayerNodeAWithMasterControl.setNodeToSyncWith(audioPlayerNodeB)

        audioEngine.start(audioGraph)
    }

    val isPlaying: Boolean
        get() {
            return audioPlayerNodeAWithMasterControl.isPlaying || audioPlayerNodeB.isPlaying
        }

    fun pausePlayback() {
        audioGraph.stop()
        audioPlayerNodeAWithMasterControl.pause()
        audioPlayerNodeB.pause()
    }

    fun startPlayback() {
        if (audioPlayerNodeAWithMasterControl.isMaster) {
            audioPlayerNodeAWithMasterControl.play()
            audioPlayerNodeB.playSynchronized()
        } else {
            audioPlayerNodeAWithMasterControl.playSynchronized()
            audioPlayerNodeB.play()
        }

        audioGraph.start()
    }

    fun loadA(packageResourcePath: String, fileOffset: Int, fileLength: Int) {
        audioPlayerNodeAWithMasterControl.loadFromAssetFile(packageResourcePath, fileOffset, fileLength)
    }

    fun loadB(packageResourcePath: String, fileOffset: Int, fileLength: Int) {
        audioPlayerNodeB.loadFromAssetFile(packageResourcePath, fileOffset, fileLength)
    }

    fun setBeatGridInformationA(originalBPM: Double, firstBeatMs: Double) {
        audioPlayerNodeAWithMasterControl.setBeatGridInformation(originalBPM, firstBeatMs)
    }

    fun setBeatGridInformationB(originalBPM: Double, firstBeatMs: Double) {
        audioPlayerNodeB.setBeatGridInformation(originalBPM, firstBeatMs)
    }

    fun setCrossfader(crossFaderPosition: Float, volumeA: Float, volumeB: Float) {
        gainNodeA.gain = (volumeA * cos(Math.PI / 2 * crossFaderPosition)).toFloat()
        gainNodeB.gain = (volumeB * cos(Math.PI / 2 * (1 - crossFaderPosition))).toFloat()

        audioPlayerNodeAWithMasterControl.isMaster = crossFaderPosition <= 0.5
    }

    fun getPlaybackRateA() = audioPlayerNodeAWithMasterControl.playbackRate

    fun getPlaybackRateB() = audioPlayerNodeB.playbackRate

    fun setPlaybackRateA(rate: Float) {
        audioPlayerNodeAWithMasterControl.playbackRate = rate.toDouble()
    }

    fun setPlaybackRateB(rate: Float) {
        audioPlayerNodeB.playbackRate = rate.toDouble()
    }

    fun setVolumeA(volume: Float) {
        gainNodeA.gain = volume
    }

    fun setVolumeB(volume: Float) {
        gainNodeB.gain = volume
    }

    fun enableFilterA(enable: Boolean) {
        filterNodeA.isEnabled = enable
    }

    fun enableFlangerA(enable: Boolean) {
        flangerNodeA.isEnabled = enable
    }

    fun enableCompressorA(enable: Boolean) {
        compressorNodeA.isEnabled = enable
    }

    fun enableReverbA(enable: Boolean) {
        reverbNodeA.isEnabled = enable
    }

    fun enableFilterB(enable: Boolean) {
        filterNodeB.isEnabled = enable
    }

    fun enableFlangerB(enable: Boolean) {
        flangerNodeB.isEnabled = enable
    }

    fun enableCompressorB(enable: Boolean) {
        compressorNodeB.isEnabled = enable
    }

    fun enableReverbB(enable: Boolean) {
        reverbNodeB.isEnabled = enable
    }

    fun close() {
        audioEngine.stop()
        audioGraph.close()
        audioPlayerNodeAWithMasterControl.close()
        audioPlayerNodeB.close()
        gainNodeA.close()
        gainNodeB.close()
        compressorNodeA.close()
        compressorNodeB.close()
        flangerNodeA.close()
        flangerNodeB.close()
        reverbNodeA.close()
        reverbNodeB.close()
        filterNodeA.close()
        filterNodeB.close()
        mixerNode.close()
        audioEngine.close()
    }
}