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
    val playerNodeWithMasterControl = AdvancedAudioPlayerNode()
    val audioPlayerNodeRight = AdvancedAudioPlayerNode()
    val gainNodeLeft = GainNode()
    val gainNodeRight = GainNode()
    val compressorNodeLeft = CompressorNode()
    val compressorNodeRight = CompressorNode()
    val flangerNodeLeft = FlangerNode()
    val flangerNodeRight = FlangerNode()
    val reverbNodeLeft = ReverbNode()
    val reverbNodeRight = ReverbNode()
    val filterNodeLeft = FilterNode()
    val filterNodeRight = FilterNode()
    val mixerNode = MixerNode()
    val audioEngine = AudioEngine(context)

    init {
        playerNodeWithMasterControl.isLoopingEnabled = true
        audioPlayerNodeRight.isLoopingEnabled = true

        audioGraph.addNode(playerNodeWithMasterControl)
        audioGraph.addNode(audioPlayerNodeRight)
        audioGraph.addNode(mixerNode)
        audioGraph.addNode(gainNodeLeft)
        audioGraph.addNode(gainNodeRight)

        audioGraph.addNode(compressorNodeLeft)
        audioGraph.addNode(compressorNodeRight)
        audioGraph.addNode(flangerNodeLeft)
        audioGraph.addNode(flangerNodeRight)
        audioGraph.addNode(reverbNodeLeft)
        audioGraph.addNode(reverbNodeRight)
        audioGraph.addNode(filterNodeLeft)
        audioGraph.addNode(filterNodeRight)

        audioGraph.connect(playerNodeWithMasterControl, gainNodeLeft)
        audioGraph.connect(gainNodeLeft, compressorNodeLeft)
        audioGraph.connect(compressorNodeLeft, flangerNodeLeft)
        audioGraph.connect(flangerNodeLeft, reverbNodeLeft)
        audioGraph.connect(reverbNodeLeft, filterNodeLeft)
        audioGraph.connect(filterNodeLeft, mixerNode)

        audioGraph.connect(audioPlayerNodeRight, gainNodeRight)
        audioGraph.connect(gainNodeRight, compressorNodeRight)
        audioGraph.connect(compressorNodeRight, flangerNodeRight)
        audioGraph.connect(flangerNodeRight, reverbNodeRight)
        audioGraph.connect(reverbNodeRight, filterNodeRight)
        audioGraph.connect(filterNodeRight, mixerNode)

        audioGraph.connect(mixerNode, audioGraph.outputNode)

        playerNodeWithMasterControl.setNodeToSyncWith(audioPlayerNodeRight)

        audioEngine.start(audioGraph)
    }
    fun pausePlayback() {
        audioGraph.stop()
        playerNodeWithMasterControl.pause()
        audioPlayerNodeRight.pause()
    }

    fun startPlayback() {
        if (playerNodeWithMasterControl.isMaster) {
            playerNodeWithMasterControl.play()
            audioPlayerNodeRight.playSynchronized()
        } else {
            playerNodeWithMasterControl.playSynchronized()
            audioPlayerNodeRight.play()
        }

        audioGraph.start()
    }

    fun loadLeft(packageResourcePath: String, fileOffset: Int, fileLength: Int) {
        playerNodeWithMasterControl.loadFromAssetFile(packageResourcePath, fileOffset, fileLength)
    }

    fun loadRight(packageResourcePath: String, fileOffset: Int, fileLength: Int) {
        audioPlayerNodeRight.loadFromAssetFile(packageResourcePath, fileOffset, fileLength)
    }

    fun setBeatGridInformationA(originalBPM: Double, firstBeatMs: Double) {
        playerNodeWithMasterControl.setBeatGridInformation(originalBPM, firstBeatMs)
    }

    fun setBeatGridInformationB(originalBPM: Double, firstBeatMs: Double) {
        audioPlayerNodeRight.setBeatGridInformation(originalBPM, firstBeatMs)
    }

    fun setCrossfader(crossFaderPosition: Float, volumeA: Float, volumeB: Float) {
        gainNodeLeft.gain = (volumeA * cos(Math.PI / 2 * crossFaderPosition)).toFloat()
        gainNodeRight.gain = (volumeB * cos(Math.PI / 2 * (1 - crossFaderPosition))).toFloat()

        playerNodeWithMasterControl.isMaster = crossFaderPosition <= 0.5
    }

    fun close() {
        audioEngine.stop()
        audioGraph.close()
        playerNodeWithMasterControl.close()
        audioPlayerNodeRight.close()
        gainNodeLeft.close()
        gainNodeRight.close()
        compressorNodeLeft.close()
        compressorNodeRight.close()
        flangerNodeLeft.close()
        flangerNodeRight.close()
        reverbNodeLeft.close()
        reverbNodeRight.close()
        filterNodeLeft.close()
        filterNodeRight.close()
        mixerNode.close()
        audioEngine.close()
    }
}