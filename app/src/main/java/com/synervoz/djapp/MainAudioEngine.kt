package com.synervoz.djapp

import com.synervoz.switchboard.sdk.audioengine.AudioEngine
import com.synervoz.switchboard.sdk.audiograph.AudioGraph
import com.synervoz.switchboard.sdk.audiographnodes.GainNode
import com.synervoz.switchboard.sdk.audiographnodes.MixerNode
import com.synervoz.switchboardsuperpowered.audiographnodes.AdvancedAudioPlayerNode
import com.synervoz.switchboardsuperpowered.audiographnodes.CompressorNode
import com.synervoz.switchboardsuperpowered.audiographnodes.FilterNode
import com.synervoz.switchboardsuperpowered.audiographnodes.FlangerNode
import com.synervoz.switchboardsuperpowered.audiographnodes.ReverbNode

class MainAudioEngine {
    val audioGraph = AudioGraph()
    val audioPlayerNodeLeft = AdvancedAudioPlayerNode()
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
    val audioEngine = AudioEngine()

    init {
        audioPlayerNodeLeft.isLoopingEnabled = true
        audioPlayerNodeRight.isLoopingEnabled = true

        audioGraph.addNode(audioPlayerNodeLeft)
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

        audioGraph.connect(audioPlayerNodeLeft, gainNodeLeft)
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
        audioEngine.start(audioGraph)
    }
    fun pausePlayback() {
        audioGraph.stop()
        audioPlayerNodeLeft.pause()
        audioPlayerNodeRight.pause()
    }

    fun startPlayback() {
        audioPlayerNodeLeft.play()
        audioPlayerNodeRight.play()
        audioGraph.start()
    }

    fun loadLeft(packageResourcePath: String, fileOffset: Int, fileLength: Int) {
        audioPlayerNodeLeft.loadFromAssetFile(packageResourcePath, fileOffset, fileLength)
    }

    fun loadRight(packageResourcePath: String, fileOffset: Int, fileLength: Int) {
        audioPlayerNodeRight.loadFromAssetFile(packageResourcePath, fileOffset, fileLength)
    }

    fun close() {
        audioEngine.stop()
        audioGraph.close()
        audioPlayerNodeLeft.close()
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