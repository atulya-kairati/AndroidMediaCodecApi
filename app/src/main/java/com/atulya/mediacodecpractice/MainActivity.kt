package com.atulya.mediacodecpractice

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.atulya.mediacodecpractice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val width = 1280
    private val height = 720
    private val mime = "video/avc"

    private val encoder = MediaCodec.createEncoderByType(mime)


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val encoder = MediaCodec.createEncoderByType(mime)

        printSupportedWH()
        checkSupportedResolution(width, height, mime)

        val format = createMediaFormat(
            mime,
            height,
            width,
            bitrate = 2_000_000,
            fps = 30,
            iFrameInterval = 15
        )

        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
    }

    private fun checkSupportedResolution(width: Int, height: Int, mime: String) {

        /**
         * Acc to docs 144p, 480p & 720p are sure to be supported
         * https://developer.android.com/guide/topics/media/media-formats#video-encoding
         */


        val isSupported = encoder.codecInfo.getCapabilitiesForType(mime)
            .videoCapabilities
            .isSizeSupported(width, height)

        Log.d("#> ${this::class.simpleName}", "is ($width*$height) supported: $isSupported")
    }

    private fun printSupportedWH() {
        /**
         * Get supported width and height
         */
        val heightRange = encoder.codecInfo.getCapabilitiesForType(mime)
                .videoCapabilities.supportedHeights
        val widthRange = encoder.codecInfo.getCapabilitiesForType(mime)
                .videoCapabilities.supportedWidths

        Log.d("#> ${this::class.simpleName}", "height range: $heightRange")
        Log.d("#> ${this::class.simpleName}", "width range: $widthRange")
    }

    private fun createMediaFormat(
        mime: String,
        height: Int,
        width: Int,
        bitrate: Int,
        fps: Int,
        iFrameInterval: Int,
    ): MediaFormat {
        val format = MediaFormat.createVideoFormat(mime, width, height)


        /**
         * [MediaFormat.KEY_COLOR_FORMAT] is set as
         * [MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface]
         * because we will provide input via surface
         */
        format.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitrate)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, fps)

        // Distance between 2 keyframes in seconds
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iFrameInterval)

        return format
    }
}