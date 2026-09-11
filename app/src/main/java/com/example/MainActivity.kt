package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AnimalCard
import com.example.ui.components.AnimalFactDialog
import com.example.ui.components.BottomPlaybackBar
import com.example.ui.components.CategoryTabs
import com.example.ui.components.SoundQuizDialog
import com.example.ui.theme.KidBackground
import com.example.ui.theme.KidPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AnimalSoundViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AnimalSoundApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalSoundApp(viewModel: AnimalSoundViewModel = viewModel()) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val showOnlyFavorites by viewModel.showOnlyFavorites.collectAsState()
    val animals by viewModel.filteredAnimals.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()
    val factAnimal by viewModel.factAnimal.collectAsState()
    val quizState by viewModel.quizState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = KidBackground,
        bottomBar = {
            BottomPlaybackBar(
                playbackState = playbackState,
                onPlayPause = { viewModel.togglePlayPauseCurrent() },
                onSkipNext = { viewModel.playNext() },
                onSkipPrevious = { viewModel.playPrevious() },
                onToggleLoop = { viewModel.toggleLoop() },
                onToggleAnnounce = { viewModel.toggleAnnounce() },
                onToggleAutoplay = { viewModel.toggleAutoplay() },
                onPitchModeChanged = { viewModel.setPitchMode(it) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .statusBarsPadding()
        ) {
            // App Header Bar
            HeaderBar(
                onOpenQuiz = { viewModel.startQuiz() },
                isFavFilterActive = showOnlyFavorites,
                onToggleFavFilter = { viewModel.toggleFavoriteOnly() }
            )

            // Kid Safari Banner Art
            HeroBanner(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))

            // Search Box
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Category Tabs (All, Wild, Domestic, Birds)
            CategoryTabs(
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.selectCategory(it) }
            )

            // Animals Count & Quick Hint
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showOnlyFavorites) "❤️ Favorites (${animals.size})" else "${selectedCategory.emoji} ${selectedCategory.displayName} (${animals.size})",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF546E7A)
                    )
                )

                Text(
                    text = "Tap to hear sound! 🔊",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = KidPrimary
                    )
                )
            }

            // Grid of Animals
            if (animals.isEmpty()) {
                EmptyStateView(
                    isFavoriteFilter = showOnlyFavorites,
                    searchQuery = searchQuery,
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 155.dp),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 20.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("animal_grid")
                ) {
                    items(animals, key = { it.id }) { animal ->
                        val isPlaying = playbackState.isPlaying && playbackState.currentAnimal?.id == animal.id
                        val isFav = favoriteIds.contains(animal.id)

                        AnimalCard(
                            animal = animal,
                            isPlaying = isPlaying,
                            isFavorite = isFav,
                            onCardClicked = { viewModel.playAnimal(animal) },
                            onFavoriteClicked = { viewModel.toggleFavorite(animal.id) },
                            onInfoClicked = { viewModel.openFactDialog(animal) }
                        )
                    }
                }
            }
        }
    }

    // Educational Animal Fun Fact Modal
    factAnimal?.let { animal ->
        AnimalFactDialog(
            animal = animal,
            onDismiss = { viewModel.closeFactDialog() },
            onPlaySound = { viewModel.playAnimal(animal) }
        )
    }

    // Playful Sound Guessing Quiz Game
    if (quizState.isActive) {
        SoundQuizDialog(
            quizState = quizState,
            onSelectAnimal = { viewModel.submitQuizAnswer(it) },
            onReplaySound = { viewModel.replayQuizSound() },
            onNextQuestion = { viewModel.nextQuizQuestion() },
            onExitQuiz = { viewModel.exitQuiz() }
        )
    }
}

@Composable
fun HeaderBar(
    onOpenQuiz: () -> Unit,
    isFavFilterActive: Boolean,
    onToggleFavFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Title & Logo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFF9800), Color(0xFFFF5722))
                        )
                    )
                    .shadow(4.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = "Animal Sounds",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = Color(0xFF263238)
                    )
                )
                Text(
                    text = "Listen, Learn & Play! 🎵",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF78909C),
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        // Action Buttons: Quiz Game & Favorite Filter
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Quiz Mini-Game Pill Button
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF43A047),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOpenQuiz() }
                    .testTag("btn_open_quiz")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "🎯", fontSize = 16.sp)
                    Text(
                        text = "Quiz",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Favorite Filter Toggle Button
            Surface(
                shape = CircleShape,
                color = if (isFavFilterActive) Color(0xFFFFEBEE) else Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onToggleFavFilter() }
                    .testTag("btn_fav_filter")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isFavFilterActive) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorites Filter",
                        tint = if (isFavFilterActive) Color(0xFFE91E63) else Color(0xFF78909C),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HeroBanner(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .shadow(6.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp)),
        color = Color(0xFFFFF3E0)
    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.img_animal_banner),
                contentDescription = "Animals Safari Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Soft overlay gradient with cheerful caption
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.55f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Text(
                        text = "Explore Animal Sounds 🦁",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Touch any friend to hear their voice!",
                        color = Color(0xFFFFF9C4),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .testTag("search_bar"),
        placeholder = {
            Text(
                text = "Search lion, cat, chirp, wild...",
                color = Color(0xFF90A4AE),
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFF78909C)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = Color(0xFF78909C)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(22.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = KidPrimary,
            unfocusedBorderColor = Color(0xFFE0E0E0)
        )
    )
}

@Composable
fun EmptyStateView(
    isFavoriteFilter: Boolean,
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isFavoriteFilter) "💛" else "🔍",
                fontSize = 54.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (isFavoriteFilter) "No Favorites Yet!" else "No Animals Found",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF37474F)
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isFavoriteFilter) {
                    "Tap the heart ❤️ on any animal card to save your favorites here!"
                } else {
                    "Try searching for another animal or clear the search query."
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF78909C),
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}
