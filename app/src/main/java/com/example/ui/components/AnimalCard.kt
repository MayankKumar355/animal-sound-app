package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Animal

@Composable
fun AnimalCard(
    animal: Animal,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onCardClicked: () -> Unit,
    onFavoriteClicked: () -> Unit,
    onInfoClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.93f
            isPlaying -> 1.04f
            else -> 1.0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_bounce"
    )

    // Animated pulse wave for playing state
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_trans")
    val pulseBorder by infiniteTransition.animateFloat(
        initialValue = 2.dp.value,
        targetValue = 6.dp.value,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_border"
    )

    val cardBaseColor = Color(animal.cardColorHex)
    val badgeColor = Color(animal.badgeColorHex)

    val borderStroke = if (isPlaying) {
        BorderStroke(pulseBorder.dp, badgeColor)
    } else {
        BorderStroke(2.dp, cardBaseColor.copy(alpha = 0.8f))
    }

    Surface(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (isPlaying) 12.dp else 4.dp,
                shape = RoundedCornerShape(26.dp)
            )
            .clip(RoundedCornerShape(26.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onCardClicked
            )
            .testTag("animal_card_${animal.id}"),
        shape = RoundedCornerShape(26.dp),
        color = Color.White,
        border = borderStroke
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            cardBaseColor.copy(alpha = 0.35f),
                            Color.White
                        )
                    )
                )
                .padding(14.dp)
        ) {
            // Action buttons row: Info and Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Info button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                        .clickable { onInfoClicked() }
                        .testTag("info_btn_${animal.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Animal fun facts",
                        tint = Color(0xFF546E7A),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Favorite button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                        .clickable { onFavoriteClicked() }
                        .testTag("fav_btn_${animal.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove favorite" else "Add favorite",
                        tint = if (isFavorite) Color(0xFFE91E63) else Color(0xFF90A4AE),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large tactile animal emoji avatar
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(cardBaseColor, cardBaseColor.copy(alpha = 0.45f))
                            )
                        )
                        .shadow(4.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = animal.emoji,
                        fontSize = 52.sp,
                        textAlign = TextAlign.Center
                    )

                    // Audio playing badge
                    if (isPlaying) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(badgeColor),
                            contentAlignment = Alignment.Center
                        ) {
                            EqualizerWave()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Animal Name
                Text(
                    text = animal.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF212121)
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Sound Bubble Pill
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isPlaying) badgeColor else cardBaseColor.copy(alpha = 0.65f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = animal.soundName,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = if (isPlaying) Color.White else Color(0xFF37474F)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun EqualizerWave() {
    val transition = rememberInfiniteTransition(label = "eq")
    val h1 by transition.animateFloat(
        initialValue = 4f, targetValue = 14f,
        animationSpec = infiniteRepeatable(tween(250), RepeatMode.Reverse), label = "h1"
    )
    val h2 by transition.animateFloat(
        initialValue = 14f, targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(300), RepeatMode.Reverse), label = "h2"
    )
    val h3 by transition.animateFloat(
        initialValue = 6f, targetValue = 16f,
        animationSpec = infiniteRepeatable(tween(220), RepeatMode.Reverse), label = "h3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(2.5.dp).height(h1.dp).background(Color.White, RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.width(2.5.dp).height(h2.dp).background(Color.White, RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.width(2.5.dp).height(h3.dp).background(Color.White, RoundedCornerShape(1.dp)))
    }
}
