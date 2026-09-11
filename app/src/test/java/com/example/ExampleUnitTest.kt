package com.example

import com.example.audio.AnimalSoundSynthesizer
import com.example.model.AnimalCatalog
import com.example.model.AnimalCategory
import com.example.model.SoundWaveProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun verifyCatalogCategories() {
        val animals = AnimalCatalog.animals
        assertTrue("Catalog should contain animals", animals.isNotEmpty())

        val wild = animals.filter { it.category == AnimalCategory.WILD }
        val domestic = animals.filter { it.category == AnimalCategory.DOMESTIC }
        val birds = animals.filter { it.category == AnimalCategory.BIRDS }

        assertTrue("Wild category should have animals", wild.isNotEmpty())
        assertTrue("Domestic category should have animals", domestic.isNotEmpty())
        assertTrue("Birds category should have animals", birds.isNotEmpty())

        assertEquals(animals.size, wild.size + domestic.size + birds.size)
    }

    @Test
    fun verifySoundSynthesizerGeneratesValidPcm() {
        val lionPcm = AnimalSoundSynthesizer.generatePcm(SoundWaveProfile.LION_ROAR)
        assertTrue("Lion roar buffer should not be empty", lionPcm.isNotEmpty())

        val dogPcm = AnimalSoundSynthesizer.generatePcm(SoundWaveProfile.DOG_BARK)
        assertTrue("Dog bark buffer should not be empty", dogPcm.isNotEmpty())

        val birdPcm = AnimalSoundSynthesizer.generatePcm(SoundWaveProfile.SPARROW_CHIRP)
        assertTrue("Sparrow chirp buffer should not be empty", birdPcm.isNotEmpty())

        val chimePcm = AnimalSoundSynthesizer.synthesizeCelebrationChime()
        assertTrue("Chime buffer should not be empty", chimePcm.isNotEmpty())
    }
}
