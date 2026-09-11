package com.example.audio

import com.example.model.SoundWaveProfile
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/**
 * Procedural Audio Synthesizer generating rich, 16-bit PCM mono waveforms
 * at 44.1kHz for animal vocalizations with harmonic formants and expressive envelopes.
 */
object AnimalSoundSynthesizer {
    const val SAMPLE_RATE = 44100

    fun generatePcm(profile: SoundWaveProfile): ShortArray {
        return when (profile) {
            SoundWaveProfile.LION_ROAR -> synthesizeLionRoar()
            SoundWaveProfile.ELEPHANT_TRUMPET -> synthesizeElephantTrumpet()
            SoundWaveProfile.TIGER_GROWL -> synthesizeTigerGrowl()
            SoundWaveProfile.BEAR_GROWL -> synthesizeBearGrowl()
            SoundWaveProfile.WOLF_HOWL -> synthesizeWolfHowl()
            SoundWaveProfile.MONKEY_CHATTER -> synthesizeMonkeyChatter()
            SoundWaveProfile.SNAKE_HISS -> synthesizeSnakeHiss()
            SoundWaveProfile.FROG_CROAK -> synthesizeFrogCroak()
            SoundWaveProfile.DOG_BARK -> synthesizeDogBark()
            SoundWaveProfile.CAT_MEOW -> synthesizeCatMeow()
            SoundWaveProfile.COW_MOO -> synthesizeCowMoo()
            SoundWaveProfile.SHEEP_BAA -> synthesizeSheepBaa()
            SoundWaveProfile.HORSE_NEIGH -> synthesizeHorseNeigh()
            SoundWaveProfile.PIG_OINK -> synthesizePigOink()
            SoundWaveProfile.GOAT_MAA -> synthesizeGoatMaa()
            SoundWaveProfile.DONKEY_HEEHAW -> synthesizeDonkeyHeehaw()
            SoundWaveProfile.ROOSTER_CROW -> synthesizeRoosterCrow()
            SoundWaveProfile.DUCK_QUACK -> synthesizeDuckQuack()
            SoundWaveProfile.OWL_HOOT -> synthesizeOwlHoot()
            SoundWaveProfile.PARROT_SQUAWK -> synthesizeParrotSquawk()
            SoundWaveProfile.EAGLE_SCREECH -> synthesizeEagleScreech()
            SoundWaveProfile.SPARROW_CHIRP -> synthesizeSparrowChirp()
            SoundWaveProfile.PIGEON_COO -> synthesizePigeonCoo()
            SoundWaveProfile.PENGUIN_HONK -> synthesizePenguinHonk()
        }
    }

    private fun synthesizeDogBark(): ShortArray {
        // Double bark: "Woof! ... Woof!"
        val duration = 1.2
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        fun renderBark(startSec: Double, lengthSec: Double) {
            val startIdx = (startSec * SAMPLE_RATE).toInt()
            val count = (lengthSec * SAMPLE_RATE).toInt()
            for (i in 0 until count) {
                val t = i.toDouble() / SAMPLE_RATE
                val normT = t / lengthSec
                // Fast explosive attack, sharp decay
                val env = (1.0 - normT) * (1.0 - exp(-normT * 40.0))
                // Pitch falls from 420Hz down to 140Hz
                val freq = 420.0 - (normT * 280.0)
                val fundamental = sin(2.0 * PI * freq * t)
                val overtone = 0.5 * sin(4.0 * PI * freq * t)
                val sub = 0.3 * sin(PI * freq * t)
                val noise = (Random.nextDouble() * 2.0 - 1.0) * 0.25 * (1.0 - normT)

                val sample = (fundamental + overtone + sub + noise) * env * 0.85
                val idx = startIdx + i
                if (idx < totalSamples) {
                    buffer[idx] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
                }
            }
        }

        renderBark(0.05, 0.38)
        renderBark(0.55, 0.42)
        return buffer
    }

    private fun synthesizeCatMeow(): ShortArray {
        // Expressive feline meow: starts ~420Hz, rises to ~760Hz, eases down to ~480Hz
        val duration = 1.4
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val normT = t / duration

            // Envelope: gentle swell and soft fade
            val env = sin(normT * PI) * (if (normT < 0.2) normT / 0.2 else 1.0)

            // Pitch contour
            val freq = when {
                normT < 0.35 -> 420.0 + (normT / 0.35) * 340.0
                normT < 0.7 -> 760.0 - ((normT - 0.35) / 0.35) * 180.0
                else -> 580.0 - ((normT - 0.7) / 0.3) * 160.0
            }

            val tremolo = 1.0 + 0.08 * sin(2.0 * PI * 6.5 * t)
            val wave = sin(2.0 * PI * freq * t) +
                    0.45 * sin(4.0 * PI * freq * t) +
                    0.25 * sin(6.0 * PI * freq * t) +
                    0.12 * sin(8.0 * PI * freq * t)

            val sample = wave * env * tremolo * 0.65
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
        }
        return buffer
    }

    private fun synthesizeLionRoar(): ShortArray {
        // Deep resonant roar with low subharmonics and heavy guttural amplitude modulation
        val duration = 2.0
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val normT = t / duration
            val env = when {
                normT < 0.15 -> normT / 0.15
                normT < 0.75 -> 1.0
                else -> 1.0 - (normT - 0.75) / 0.25
            }

            val growlMod = 0.5 + 0.5 * sin(2.0 * PI * 32.0 * t)
            val pitch = 85.0 + 25.0 * sin(normT * PI)
            val sub = sin(2.0 * PI * (pitch * 0.5) * t)
            val base = sin(2.0 * PI * pitch * t)
            val overtone1 = 0.6 * sin(4.0 * PI * pitch * t)
            val overtone2 = 0.4 * sin(6.0 * PI * pitch * t)
            val turbulence = (Random.nextDouble() * 2.0 - 1.0) * 0.35

            val sample = (base + overtone1 + overtone2 + sub + turbulence) * env * growlMod * 0.75
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
        }
        return buffer
    }

    private fun synthesizeCowMoo(): ShortArray {
        // Warm low-frequency fundamental ~115Hz with smooth formants and gentle vibrato
        val duration = 1.8
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val normT = t / duration
            val env = sin(normT * PI) * (if (normT < 0.1) normT / 0.1 else 1.0)
            val pitch = 115.0 + 12.0 * sin(2.0 * PI * 4.0 * t) - (normT * 15.0)

            val f0 = sin(2.0 * PI * pitch * t)
            val f1 = 0.7 * sin(4.0 * PI * pitch * t)
            val f2 = 0.5 * sin(6.0 * PI * pitch * t)
            val f3 = 0.3 * sin(8.0 * PI * pitch * t)

            val sample = (f0 + f1 + f2 + f3) * env * 0.65
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
        }
        return buffer
    }

    private fun synthesizeRoosterCrow(): ShortArray {
        // "Cock - a - doodle - doooo!"
        val duration = 1.9
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        data class Note(val startSec: Double, val durSec: Double, val freq: Double)
        val notes = listOf(
            Note(0.05, 0.22, 392.0), // Cock
            Note(0.32, 0.18, 440.0), // a
            Note(0.54, 0.28, 523.0), // doodle
            Note(0.86, 0.95, 659.0)  // dooooo!
        )

        for (note in notes) {
            val startIdx = (note.startSec * SAMPLE_RATE).toInt()
            val noteSamples = (note.durSec * SAMPLE_RATE).toInt()
            for (i in 0 until noteSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val normT = t / note.durSec
                val env = sin(normT * PI).coerceAtLeast(0.0)
                val vibrato = if (note.durSec > 0.5) 1.0 + 0.03 * sin(2.0 * PI * 6.0 * t) else 1.0
                val freq = note.freq * vibrato

                // Rich brassy harmonics
                val wave = sin(2.0 * PI * freq * t) +
                        0.6 * sin(4.0 * PI * freq * t) +
                        0.4 * sin(6.0 * PI * freq * t) +
                        0.25 * sin(8.0 * PI * freq * t)

                val idx = startIdx + i
                if (idx < totalSamples) {
                    val sample = wave * env * 0.7
                    buffer[idx] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
                }
            }
        }
        return buffer
    }

    private fun synthesizeDuckQuack(): ShortArray {
        // Double quack: nasal resonant bursts ~440Hz
        val duration = 1.1
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        fun renderQuack(startSec: Double, durSec: Double) {
            val startIdx = (startSec * SAMPLE_RATE).toInt()
            val count = (durSec * SAMPLE_RATE).toInt()
            for (i in 0 until count) {
                val t = i.toDouble() / SAMPLE_RATE
                val normT = t / durSec
                val env = (1.0 - normT) * sin(normT * PI)
                val freq = 440.0 - (normT * 120.0)

                // Sawtooth-like nasal harmonic stack
                var wave = 0.0
                for (h in 1..7) {
                    wave += (1.0 / h) * sin(2.0 * PI * freq * h * t)
                }

                val idx = startIdx + i
                if (idx < totalSamples) {
                    val sample = wave * env * 0.65
                    buffer[idx] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
                }
            }
        }

        renderQuack(0.05, 0.35)
        renderQuack(0.48, 0.40)
        return buffer
    }

    private fun synthesizeSheepBaa(): ShortArray {
        // Warm ~260Hz with fast ~8Hz amplitude vibrato
        val duration = 1.4
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val normT = t / duration
            val env = sin(normT * PI)
            val tremolo = 0.5 + 0.5 * sin(2.0 * PI * 8.5 * t)
            val pitch = 260.0 - (normT * 30.0)

            val wave = sin(2.0 * PI * pitch * t) +
                    0.5 * sin(4.0 * PI * pitch * t) +
                    0.3 * sin(6.0 * PI * pitch * t)

            val sample = wave * env * tremolo * 0.7
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
        }
        return buffer
    }

    private fun synthesizeHorseNeigh(): ShortArray {
        // High whinny descending with rapid pitch flutter
        val duration = 1.6
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val normT = t / duration
            val env = sin(normT * PI)
            val flutter = 1.0 + 0.08 * sin(2.0 * PI * 14.0 * t)
            val baseFreq = 780.0 - (normT * 340.0)
            val freq = baseFreq * flutter

            val wave = sin(2.0 * PI * freq * t) + 0.4 * sin(4.0 * PI * freq * t)
            val breath = (Random.nextDouble() * 2.0 - 1.0) * 0.15 * normT

            val sample = (wave + breath) * env * 0.68
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
        }
        return buffer
    }

    private fun synthesizeElephantTrumpet(): ShortArray {
        // Brassy sweep upward from 320Hz to 620Hz with brassy FM modulation
        val duration = 1.5
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val normT = t / duration
            val env = sin(normT * PI)
            val pitch = 320.0 + (normT * 300.0)
            // FM modulation adds brassy rasp
            val mod = 0.5 * sin(2.0 * PI * 220.0 * t)
            val carrier = sin(2.0 * PI * (pitch + mod * 60.0) * t)
            val overtone = 0.5 * sin(4.0 * PI * pitch * t)

            val sample = (carrier + overtone) * env * 0.75
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
        }
        return buffer
    }

    private fun synthesizeOwlHoot(): ShortArray {
        // Soft double hoot: "Hoo ... Hoooo!" pure mellow tones ~320Hz
        val duration = 1.6
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        fun renderHoot(startSec: Double, durSec: Double) {
            val startIdx = (startSec * SAMPLE_RATE).toInt()
            val count = (durSec * SAMPLE_RATE).toInt()
            for (i in 0 until count) {
                val t = i.toDouble() / SAMPLE_RATE
                val normT = t / durSec
                val env = sin(normT * PI) * sin(normT * PI)
                val freq = 320.0 + 15.0 * sin(normT * PI)
                val sample = sin(2.0 * PI * freq * t) * env * 0.8
                val idx = startIdx + i
                if (idx < totalSamples) {
                    buffer[idx] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
                }
            }
        }

        renderHoot(0.1, 0.4)
        renderHoot(0.65, 0.75)
        return buffer
    }

    private fun synthesizeSparrowChirp(): ShortArray {
        // Fast cheerful melodic chirps (3200Hz - 4800Hz)
        val duration = 1.3
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        fun renderChirp(startSec: Double, durSec: Double, startF: Double, endF: Double) {
            val startIdx = (startSec * SAMPLE_RATE).toInt()
            val count = (durSec * SAMPLE_RATE).toInt()
            for (i in 0 until count) {
                val t = i.toDouble() / SAMPLE_RATE
                val normT = t / durSec
                val env = sin(normT * PI)
                val freq = startF + (endF - startF) * normT
                val sample = sin(2.0 * PI * freq * t) * env * 0.7
                val idx = startIdx + i
                if (idx < totalSamples) {
                    buffer[idx] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
                }
            }
        }

        renderChirp(0.05, 0.18, 3000.0, 4600.0)
        renderChirp(0.28, 0.16, 4200.0, 3200.0)
        renderChirp(0.50, 0.22, 3400.0, 4800.0)
        renderChirp(0.78, 0.25, 4600.0, 3100.0)
        return buffer
    }

    private fun synthesizeWolfHowl(): ShortArray {
        // Evocative rising and slowly tapering howl: 280Hz -> 540Hz -> 360Hz
        val duration = 2.2
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val normT = t / duration
            val env = sin(normT * PI) * (if (normT < 0.15) normT / 0.15 else 1.0)
            val freq = when {
                normT < 0.4 -> 280.0 + (normT / 0.4) * 260.0
                normT < 0.7 -> 540.0 + 8.0 * sin(2.0 * PI * 4.5 * t)
                else -> 540.0 - ((normT - 0.7) / 0.3) * 200.0
            }

            val wave = sin(2.0 * PI * freq * t) + 0.35 * sin(4.0 * PI * freq * t)
            val sample = wave * env * 0.75
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
        }
        return buffer
    }

    private fun synthesizeTigerGrowl(): ShortArray {
        val duration = 1.8
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val normT = t / duration
            val env = sin(normT * PI)
            val mod = 0.5 + 0.5 * sin(2.0 * PI * 38.0 * t)
            val pitch = 110.0 + 20.0 * sin(normT * PI)
            val wave = sin(2.0 * PI * pitch * t) + 0.6 * sin(4.0 * PI * pitch * t)
            val noise = (Random.nextDouble() * 2.0 - 1.0) * 0.35
            val sample = (wave + noise) * env * mod * 0.75
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
        }
        return buffer
    }

    private fun synthesizeBearGrowl(): ShortArray {
        val duration = 1.7
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val normT = t / duration
            val env = sin(normT * PI)
            val mod = 0.5 + 0.5 * sin(2.0 * PI * 24.0 * t)
            val pitch = 75.0 + 10.0 * sin(normT * PI)
            val wave = sin(2.0 * PI * pitch * t) + 0.5 * sin(4.0 * PI * pitch * t)
            val noise = (Random.nextDouble() * 2.0 - 1.0) * 0.4
            val sample = (wave + noise) * env * mod * 0.8
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
        }
        return buffer
    }

    private fun synthesizeMonkeyChatter(): ShortArray {
        // Fast rhythmic bursts: "Ooh-ooh aah-aah!"
        val duration = 1.4
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        fun renderBurst(startSec: Double, durSec: Double, freq: Double) {
            val startIdx = (startSec * SAMPLE_RATE).toInt()
            val count = (durSec * SAMPLE_RATE).toInt()
            for (i in 0 until count) {
                val t = i.toDouble() / SAMPLE_RATE
                val normT = t / durSec
                val env = sin(normT * PI)
                val sample = (sin(2.0 * PI * freq * t) + 0.4 * sin(4.0 * PI * freq * t)) * env * 0.7
                val idx = startIdx + i
                if (idx < totalSamples) {
                    buffer[idx] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
                }
            }
        }

        renderBurst(0.05, 0.22, 340.0)
        renderBurst(0.32, 0.22, 340.0)
        renderBurst(0.60, 0.28, 520.0)
        renderBurst(0.92, 0.32, 480.0)
        return buffer
    }

    private fun synthesizeSnakeHiss(): ShortArray {
        // Sibilant white noise burst ~5000Hz
        val duration = 1.5
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val normT = t / duration
            val env = sin(normT * PI)
            val noise = Random.nextDouble() * 2.0 - 1.0
            val sibilant = sin(2.0 * PI * 5500.0 * t) * noise
            val sample = sibilant * env * 0.8
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
        }
        return buffer
    }

    private fun synthesizeFrogCroak(): ShortArray {
        // Pulsed clicking low rattle ~170Hz with fast amplitude chop
        val duration = 1.2
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        fun renderCroak(startSec: Double, durSec: Double) {
            val startIdx = (startSec * SAMPLE_RATE).toInt()
            val count = (durSec * SAMPLE_RATE).toInt()
            for (i in 0 until count) {
                val t = i.toDouble() / SAMPLE_RATE
                val normT = t / durSec
                val env = sin(normT * PI)
                val chop = if (sin(2.0 * PI * 35.0 * t) > 0) 1.0 else 0.0
                val freq = 175.0
                val wave = sin(2.0 * PI * freq * t) + 0.5 * sin(4.0 * PI * freq * t)
                val sample = wave * env * chop * 0.75
                val idx = startIdx + i
                if (idx < totalSamples) {
                    buffer[idx] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
                }
            }
        }

        renderCroak(0.08, 0.42)
        renderCroak(0.58, 0.46)
        return buffer
    }

    private fun synthesizePigOink(): ShortArray {
        val duration = 1.1
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        fun renderGrunt(startSec: Double, durSec: Double) {
            val startIdx = (startSec * SAMPLE_RATE).toInt()
            val count = (durSec * SAMPLE_RATE).toInt()
            for (i in 0 until count) {
                val t = i.toDouble() / SAMPLE_RATE
                val normT = t / durSec
                val env = (1.0 - normT) * sin(normT * PI)
                val pitch = 150.0 - (normT * 40.0)
                val wave = sin(2.0 * PI * pitch * t) + 0.6 * sin(4.0 * PI * pitch * t)
                val noise = (Random.nextDouble() * 2.0 - 1.0) * 0.25
                val sample = (wave + noise) * env * 0.75
                val idx = startIdx + i
                if (idx < totalSamples) {
                    buffer[idx] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
                }
            }
        }

        renderGrunt(0.08, 0.35)
        renderGrunt(0.52, 0.38)
        return buffer
    }

    private fun synthesizeGoatMaa(): ShortArray {
        // High pitched bleat with rapid vibrato ~330Hz
        val duration = 1.3
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val normT = t / duration
            val env = sin(normT * PI)
            val vibrato = 1.0 + 0.12 * sin(2.0 * PI * 11.0 * t)
            val freq = 330.0 * vibrato

            val wave = sin(2.0 * PI * freq * t) + 0.45 * sin(4.0 * PI * freq * t)
            val sample = wave * env * 0.7
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
        }
        return buffer
    }

    private fun synthesizeDonkeyHeehaw(): ShortArray {
        // "Hee - Haw!" high whistle then low bray
        val duration = 1.7
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        fun renderTone(startSec: Double, durSec: Double, freq: Double, isLow: Boolean) {
            val startIdx = (startSec * SAMPLE_RATE).toInt()
            val count = (durSec * SAMPLE_RATE).toInt()
            for (i in 0 until count) {
                val t = i.toDouble() / SAMPLE_RATE
                val normT = t / durSec
                val env = sin(normT * PI)
                val flutter = 1.0 + (if (isLow) 0.1 else 0.04) * sin(2.0 * PI * 8.0 * t)
                val f = freq * flutter
                val wave = sin(2.0 * PI * f * t) + 0.4 * sin(4.0 * PI * f * t)
                val noise = if (isLow) (Random.nextDouble() * 2.0 - 1.0) * 0.25 else 0.0
                val sample = (wave + noise) * env * 0.75
                val idx = startIdx + i
                if (idx < totalSamples) {
                    buffer[idx] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
                }
            }
        }

        renderTone(0.05, 0.45, 680.0, false) // Hee
        renderTone(0.55, 0.85, 190.0, true)  // Hawww!
        return buffer
    }

    private fun synthesizeParrotSquawk(): ShortArray {
        // Bright metallic squawk
        val duration = 1.0
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val normT = t / duration
            val env = sin(normT * PI)
            val pitch = 1400.0 - (normT * 400.0)
            val mod = 0.5 * sin(2.0 * PI * 180.0 * t)
            val sample = sin(2.0 * PI * (pitch + mod * 120.0) * t) * env * 0.7
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
        }
        return buffer
    }

    private fun synthesizeEagleScreech(): ShortArray {
        // High piercing screech sweeping 2200Hz to 1100Hz
        val duration = 1.4
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val normT = t / duration
            val env = sin(normT * PI)
            val freq = 2200.0 - (normT * 1100.0)
            val flutter = 1.0 + 0.05 * sin(2.0 * PI * 12.0 * t)
            val sample = sin(2.0 * PI * freq * flutter * t) * env * 0.7
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
        }
        return buffer
    }

    private fun synthesizePigeonCoo(): ShortArray {
        // Soft mellow bubbling coo ~240Hz
        val duration = 1.3
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val normT = t / duration
            val env = sin(normT * PI)
            val tremolo = 0.6 + 0.4 * sin(2.0 * PI * 9.0 * t)
            val freq = 240.0 + 20.0 * sin(normT * PI)
            val sample = sin(2.0 * PI * freq * t) * env * tremolo * 0.75
            buffer[i] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
        }
        return buffer
    }

    private fun synthesizePenguinHonk(): ShortArray {
        // Playful double squawk honk
        val duration = 1.2
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        fun renderHonk(startSec: Double, durSec: Double) {
            val startIdx = (startSec * SAMPLE_RATE).toInt()
            val count = (durSec * SAMPLE_RATE).toInt()
            for (i in 0 until count) {
                val t = i.toDouble() / SAMPLE_RATE
                val normT = t / durSec
                val env = sin(normT * PI)
                val freq = 360.0 + (normT * 80.0)
                val wave = sin(2.0 * PI * freq * t) + 0.5 * sin(4.0 * PI * freq * t)
                val sample = wave * env * 0.75
                val idx = startIdx + i
                if (idx < totalSamples) {
                    buffer[idx] = (sample.coerceIn(-1.0, 1.0) * 32000).toInt().toShort()
                }
            }
        }

        renderHonk(0.08, 0.42)
        renderHonk(0.58, 0.45)
        return buffer
    }

    /**
     * Synthesize a cheerful celebration chime for quiz success
     */
    fun synthesizeCelebrationChime(): ShortArray {
        val duration = 1.0
        val totalSamples = (SAMPLE_RATE * duration).toInt()
        val buffer = ShortArray(totalSamples)

        val notes = listOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
        val noteDur = 0.22

        for ((idx, freq) in notes.withIndex()) {
            val startIdx = (idx * 0.18 * SAMPLE_RATE).toInt()
            val count = (noteDur * SAMPLE_RATE).toInt()
            for (i in 0 until count) {
                val t = i.toDouble() / SAMPLE_RATE
                val normT = t / noteDur
                val env = exp(-normT * 4.0)
                val sample = sin(2.0 * PI * freq * t) * env * 0.5
                val outIdx = startIdx + i
                if (outIdx < totalSamples) {
                    buffer[outIdx] = (buffer[outIdx] + (sample * 30000).toInt()).coerceIn(-32768, 32767).toShort()
                }
            }
        }
        return buffer
    }
}
