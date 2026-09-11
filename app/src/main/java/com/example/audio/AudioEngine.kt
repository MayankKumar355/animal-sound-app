package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.model.Animal
import com.example.model.PitchMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt

data class PlaybackState(
    val currentAnimal: Animal? = null,
    val isPlaying: Boolean = false,
    val progress: Float = 0.0f,
    val pitchMode: PitchMode = PitchMode.NORMAL,
    val isLooping: Boolean = false,
    val isAnnounceEnabled: Boolean = true,
    val isAutoplayEnabled: Boolean = false,
    val volume: Float = 0.85f
)

/**
 * High-fidelity Kid-friendly Audio Engine designed specifically for
 * smooth playback transitions, pitch shifting, crossfades, and voiced announcements.
 */
class AudioEngine(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private var playbackJob: Job? = null
    private var faderJob: Job? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val bufferCache = ConcurrentHashMap<String, ShortArray>()

    private var activeTrack: AudioTrack? = null
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    var onSongFinished: (() -> Unit)? = null

    init {
        initTts()
    }

    private fun initTts() {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setSpeechRate(0.92f)
                tts?.setPitch(1.15f) // Slightly higher friendly kid tone
                ttsReady = true
            }
        }
    }

    fun playAnimal(
        animal: Animal,
        pitchMode: PitchMode = _playbackState.value.pitchMode,
        onComplete: (() -> Unit)? = null
    ) {
        playbackJob?.cancel()
        faderJob?.cancel()

        playbackJob = scope.launch {
            // Smoothly transition from any previous audio by fading out old track
            smoothStopActiveTrack(fadeMillis = 90)

            _playbackState.value = _playbackState.value.copy(
                currentAnimal = animal,
                isPlaying = true,
                progress = 0.0f,
                pitchMode = pitchMode
            )

            // Voiced announcement if enabled
            if (_playbackState.value.isAnnounceEnabled && ttsReady) {
                val phrase = "${animal.name}! ${animal.soundName}"
                tts?.speak(phrase, TextToSpeech.QUEUE_FLUSH, null, "animal_tts_${animal.id}")
            }

            // Synthesize or retrieve PCM buffer
            val rawPcm = bufferCache.getOrPut(animal.id) {
                AnimalSoundSynthesizer.generatePcm(animal.soundProfile)
            }

            // Apply pitch scaling
            val pcm = if (pitchMode.pitchFactor != 1.0f) {
                resampleBuffer(rawPcm, pitchMode.pitchFactor)
            } else {
                rawPcm
            }

            val sampleRate = AnimalSoundSynthesizer.SAMPLE_RATE
            val bufferSizeBytes = pcm.size * 2

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            val track = AudioTrack(
                audioAttributes,
                audioFormat,
                bufferSizeBytes,
                AudioTrack.MODE_STATIC,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            )

            // Write PCM and apply smooth fade-in to eliminate clicks
            val smoothBuffer = applySmoothTransitions(pcm, fadeInSamples = 300, fadeOutSamples = 500)
            track.write(smoothBuffer, 0, smoothBuffer.size)

            val initialVol = _playbackState.value.volume
            track.setVolume(initialVol)
            track.play()
            activeTrack = track

            val totalDurationMs = ((pcm.size.toDouble() / sampleRate) * 1000.0).toLong()
            val startTime = System.currentTimeMillis()

            // Update progress smoothly during playback
            while (isActive && track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                val elapsed = System.currentTimeMillis() - startTime
                val progress = (elapsed.toFloat() / totalDurationMs.coerceAtLeast(1L)).coerceIn(0.0f, 1.0f)
                _playbackState.value = _playbackState.value.copy(progress = progress)

                if (elapsed >= totalDurationMs) {
                    break
                }
                delay(30)
            }

            if (isActive) {
                if (_playbackState.value.isLooping) {
                    // Replay seamlessly
                    playAnimal(animal, pitchMode, onComplete)
                } else {
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        progress = 1.0f
                    )
                    onComplete?.invoke()
                    onSongFinished?.invoke()
                }
            }
        }
    }

    fun playCelebrationChime() {
        scope.launch {
            val pcm = AnimalSoundSynthesizer.synthesizeCelebrationChime()
            val sampleRate = AnimalSoundSynthesizer.SAMPLE_RATE

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            val track = AudioTrack(
                audioAttributes,
                audioFormat,
                pcm.size * 2,
                AudioTrack.MODE_STATIC,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            )
            track.write(pcm, 0, pcm.size)
            track.setVolume(0.9f)
            track.play()
            delay(1200)
            track.stop()
            track.release()
        }
    }

    /**
     * Smoothly stops current playing track by fading gain to zero over fadeMillis,
     * ensuring zero audio crackling / pops.
     */
    private suspend fun smoothStopActiveTrack(fadeMillis: Long = 80) {
        val track = activeTrack ?: return
        try {
            if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                val steps = 8
                val stepDelay = (fadeMillis / steps).coerceAtLeast(5)
                val currentVol = _playbackState.value.volume
                for (s in steps downTo 0) {
                    val vol = (currentVol * (s.toFloat() / steps)).coerceAtLeast(0f)
                    track.setVolume(vol)
                    delay(stepDelay)
                }
                track.pause()
                track.stop()
            }
            track.release()
        } catch (e: Exception) {
            Log.e("AudioEngine", "Error stopping track smoothly", e)
        } finally {
            activeTrack = null
        }
    }

    fun stop() {
        playbackJob?.cancel()
        tts?.stop()
        scope.launch {
            smoothStopActiveTrack(fadeMillis = 60)
            _playbackState.value = _playbackState.value.copy(
                isPlaying = false,
                progress = 0.0f
            )
        }
    }

    fun setPitchMode(mode: PitchMode) {
        _playbackState.value = _playbackState.value.copy(pitchMode = mode)
        // If playing, restart with new pitch smoothly
        val animal = _playbackState.value.currentAnimal
        if (_playbackState.value.isPlaying && animal != null) {
            playAnimal(animal, mode)
        }
    }

    fun toggleLoop() {
        _playbackState.value = _playbackState.value.copy(isLooping = !_playbackState.value.isLooping)
    }

    fun toggleAnnounce() {
        val next = !_playbackState.value.isAnnounceEnabled
        _playbackState.value = _playbackState.value.copy(isAnnounceEnabled = next)
        if (!next) {
            tts?.stop()
        }
    }

    fun toggleAutoplay() {
        _playbackState.value = _playbackState.value.copy(isAutoplayEnabled = !_playbackState.value.isAutoplayEnabled)
    }

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0.0f, 1.0f)
        _playbackState.value = _playbackState.value.copy(volume = clamped)
        activeTrack?.setVolume(clamped)
    }

    /**
     * Resample buffer for pitch modifications (linear interpolation)
     */
    private fun resampleBuffer(input: ShortArray, factor: Float): ShortArray {
        if (factor <= 0f || factor == 1.0f) return input
        val newLength = (input.size / factor).toInt()
        val output = ShortArray(newLength)

        for (i in 0 until newLength) {
            val srcPos = i * factor
            val srcIndex = srcPos.toInt()
            val frac = srcPos - srcIndex

            val s1 = input.getOrElse(srcIndex) { 0 }
            val s2 = input.getOrElse(srcIndex + 1) { s1 }
            val interpolated = s1 + (frac * (s2 - s1)).roundToInt()
            output[i] = interpolated.coerceIn(-32768, 32767).toShort()
        }
        return output
    }

    /**
     * Applies smooth micro-fades at buffer boundaries to prevent click artifacts
     */
    private fun applySmoothTransitions(input: ShortArray, fadeInSamples: Int, fadeOutSamples: Int): ShortArray {
        val result = input.copyOf()
        val n = result.size

        // Fade in
        val actualIn = fadeInSamples.coerceAtMost(n / 2)
        for (i in 0 until actualIn) {
            val factor = i.toFloat() / actualIn
            result[i] = (result[i] * factor).toInt().toShort()
        }

        // Fade out
        val actualOut = fadeOutSamples.coerceAtMost(n / 2)
        for (i in 0 until actualOut) {
            val factor = i.toFloat() / actualOut
            val idx = n - 1 - i
            result[idx] = (result[idx] * factor).toInt().toShort()
        }
        return result
    }

    fun release() {
        playbackJob?.cancel()
        tts?.stop()
        tts?.shutdown()
        activeTrack?.release()
        activeTrack = null
    }
}
