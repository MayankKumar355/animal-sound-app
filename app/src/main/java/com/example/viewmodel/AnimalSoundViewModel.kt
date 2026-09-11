package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioEngine
import com.example.audio.PlaybackState
import com.example.model.Animal
import com.example.model.AnimalCatalog
import com.example.model.AnimalCategory
import com.example.model.PitchMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class QuizState(
    val isActive: Boolean = false,
    val targetAnimal: Animal? = null,
    val options: List<Animal> = emptyList(),
    val selectedAnimal: Animal? = null,
    val isAnswered: Boolean = false,
    val isCorrect: Boolean = false,
    val score: Int = 0,
    val streak: Int = 0,
    val totalAnswered: Int = 0
)

class AnimalSoundViewModel(application: Application) : AndroidViewModel(application) {

    val audioEngine = AudioEngine(application)

    val playbackState: StateFlow<PlaybackState> = audioEngine.playbackState

    private val _selectedCategory = MutableStateFlow(AnimalCategory.ALL)
    val selectedCategory: StateFlow<AnimalCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _showOnlyFavorites = MutableStateFlow(false)
    val showOnlyFavorites: StateFlow<Boolean> = _showOnlyFavorites.asStateFlow()

    private val _factAnimal = MutableStateFlow<Animal?>(null)
    val factAnimal: StateFlow<Animal?> = _factAnimal.asStateFlow()

    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    val filteredAnimals: StateFlow<List<Animal>> = combine(
        _selectedCategory,
        _searchQuery,
        _favoriteIds,
        _showOnlyFavorites
    ) { category, query, favorites, favOnly ->
        AnimalCatalog.animals.filter { animal ->
            val matchesCategory = (category == AnimalCategory.ALL || animal.category == category)
            val matchesQuery = query.isBlank() ||
                    animal.name.contains(query, ignoreCase = true) ||
                    animal.soundName.contains(query, ignoreCase = true) ||
                    animal.habitat.contains(query, ignoreCase = true)
            val matchesFav = !favOnly || favorites.contains(animal.id)
            matchesCategory && matchesQuery && matchesFav
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnimalCatalog.animals)

    init {
        // Setup automatic progression if autoplay is turned on
        audioEngine.onSongFinished = {
            if (audioEngine.playbackState.value.isAutoplayEnabled) {
                playNext()
            }
        }
    }

    fun selectCategory(category: AnimalCategory) {
        _selectedCategory.value = category
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavoriteOnly() {
        _showOnlyFavorites.value = !_showOnlyFavorites.value
    }

    fun toggleFavorite(animalId: String) {
        val current = _favoriteIds.value.toMutableSet()
        if (current.contains(animalId)) {
            current.remove(animalId)
        } else {
            current.add(animalId)
        }
        _favoriteIds.value = current
    }

    fun playAnimal(animal: Animal) {
        audioEngine.playAnimal(animal)
    }

    fun stopPlayback() {
        audioEngine.stop()
    }

    fun togglePlayPauseCurrent() {
        val current = playbackState.value.currentAnimal
        if (playbackState.value.isPlaying) {
            audioEngine.stop()
        } else if (current != null) {
            audioEngine.playAnimal(current)
        } else {
            val list = filteredAnimals.value
            if (list.isNotEmpty()) {
                audioEngine.playAnimal(list.first())
            }
        }
    }

    fun playNext() {
        val list = filteredAnimals.value
        if (list.isEmpty()) return
        val current = playbackState.value.currentAnimal
        val currentIndex = list.indexOfFirst { it.id == current?.id }
        val nextIndex = if (currentIndex == -1 || currentIndex >= list.size - 1) 0 else currentIndex + 1
        audioEngine.playAnimal(list[nextIndex])
    }

    fun playPrevious() {
        val list = filteredAnimals.value
        if (list.isEmpty()) return
        val current = playbackState.value.currentAnimal
        val currentIndex = list.indexOfFirst { it.id == current?.id }
        val prevIndex = if (currentIndex <= 0) list.size - 1 else currentIndex - 1
        audioEngine.playAnimal(list[prevIndex])
    }

    fun setPitchMode(mode: PitchMode) {
        audioEngine.setPitchMode(mode)
    }

    fun toggleLoop() {
        audioEngine.toggleLoop()
    }

    fun toggleAnnounce() {
        audioEngine.toggleAnnounce()
    }

    fun toggleAutoplay() {
        audioEngine.toggleAutoplay()
    }

    fun setVolume(vol: Float) {
        audioEngine.setVolume(vol)
    }

    fun openFactDialog(animal: Animal) {
        _factAnimal.value = animal
    }

    fun closeFactDialog() {
        _factAnimal.value = null
    }

    // ----------------- Quiz Mini-Game -----------------

    fun startQuiz() {
        audioEngine.stop()
        generateNewQuizQuestion(score = 0, streak = 0, totalAnswered = 0)
    }

    private fun generateNewQuizQuestion(score: Int, streak: Int, totalAnswered: Int) {
        val all = AnimalCatalog.animals.shuffled()
        val target = all.first()
        val otherOptions = all.filter { it.id != target.id }.take(3)
        val options = (otherOptions + target).shuffled()

        _quizState.value = QuizState(
            isActive = true,
            targetAnimal = target,
            options = options,
            selectedAnimal = null,
            isAnswered = false,
            isCorrect = false,
            score = score,
            streak = streak,
            totalAnswered = totalAnswered
        )

        // Automatically play the target sound
        audioEngine.playAnimal(target)
    }

    fun replayQuizSound() {
        _quizState.value.targetAnimal?.let {
            audioEngine.playAnimal(it)
        }
    }

    fun submitQuizAnswer(guessed: Animal) {
        if (_quizState.value.isAnswered) return
        val target = _quizState.value.targetAnimal ?: return
        val isCorrect = guessed.id == target.id

        val newScore = if (isCorrect) _quizState.value.score + 10 + (_quizState.value.streak * 2) else _quizState.value.score
        val newStreak = if (isCorrect) _quizState.value.streak + 1 else 0
        val newTotal = _quizState.value.totalAnswered + 1

        _quizState.value = _quizState.value.copy(
            selectedAnimal = guessed,
            isAnswered = true,
            isCorrect = isCorrect,
            score = newScore,
            streak = newStreak,
            totalAnswered = newTotal
        )

        if (isCorrect) {
            audioEngine.playCelebrationChime()
        }
    }

    fun nextQuizQuestion() {
        generateNewQuizQuestion(
            score = _quizState.value.score,
            streak = _quizState.value.streak,
            totalAnswered = _quizState.value.totalAnswered
        )
    }

    fun exitQuiz() {
        _quizState.value = QuizState(isActive = false)
        audioEngine.stop()
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
