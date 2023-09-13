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
    val audioPlayerNodeBWithMasterControl = AdvancedAudioPlayerNode()
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
        audioPlayerNodeBWithMasterControl.isLoopingEnabled = true
        audioPlayerNodeB.isLoopingEnabled = true

        audioGraph.addNode(audioPlayerNodeBWithMasterControl)
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

        audioGraph.connect(audioPlayerNodeBWithMasterControl, gainNodeA)
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

        audioPlayerNodeBWithMasterControl.setNodeToSyncWith(audioPlayerNodeB)

        audioEngine.start(audioGraph)
    }
    fun pausePlayback() {
        audioGraph.stop()
        audioPlayerNodeBWithMasterControl.pause()
        audioPlayerNodeB.pause()
    }

    fun startPlayback() {
        if (audioPlayerNodeBWithMasterControl.isMaster) {
            audioPlayerNodeBWithMasterControl.play()
            audioPlayerNodeB.playSynchronized()
        } else {
            audioPlayerNodeBWithMasterControl.playSynchronized()
            audioPlayerNodeB.play()
        }

        audioGraph.start()
    }

    fun loadA(packageResourcePath: String, fileOffset: Int, fileLength: Int) {
        audioPlayerNodeBWithMasterControl.loadFromAssetFile(packageResourcePath, fileOffset, fileLength)
    }

    fun loadB(packageResourcePath: String, fileOffset: Int, fileLength: Int) {
        audioPlayerNodeB.loadFromAssetFile(packageResourcePath, fileOffset, fileLength)
    }

    fun setBeatGridInformationA(originalBPM: Double, firstBeatMs: Double) {
        audioPlayerNodeBWithMasterControl.setBeatGridInformation(originalBPM, firstBeatMs)
    }

    fun setBeatGridInformationB(originalBPM: Double, firstBeatMs: Double) {
        audioPlayerNodeB.setBeatGridInformation(originalBPM, firstBeatMs)
    }

    fun setCrossfader(crossFaderPosition: Float, volumeA: Float, volumeB: Float) {
        gainNodeA.gain = (volumeA * cos(Math.PI / 2 * crossFaderPosition)).toFloat()
        gainNodeB.gain = (volumeB * cos(Math.PI / 2 * (1 - crossFaderPosition))).toFloat()

        audioPlayerNodeBWithMasterControl.isMaster = crossFaderPosition <= 0.5
    }

    fun close() {
        audioEngine.stop()
        audioGraph.close()
        audioPlayerNodeBWithMasterControl.close()
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