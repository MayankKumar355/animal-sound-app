package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VoiceOverOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlaybackState
import com.example.model.PitchMode

@Composable
fun BottomPlaybackBar(
    playbackState: PlaybackState,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onToggleLoop: () -> Unit,
    onToggleAnnounce: () -> Unit,
    onToggleAutoplay: () -> Unit,
    onPitchModeChanged: (PitchMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val animal = playbackState.currentAnimal

    AnimatedVisibility(
        visible = animal != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        if (animal == null) return@AnimatedVisibility

        val accentColor = Color(animal.badgeColorHex)
        val cardColor = Color(animal.cardColorHex)

        val animatedProgress by animateFloatAsState(
            targetValue = playbackState.progress,
            label = "playback_progress"
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Progress Bar
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = accentColor,
                    trackColor = cardColor.copy(alpha = 0.4f),
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Animal Info & Main Transport Controls Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Avatar & Titles
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(cardColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = animal.emoji, fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = animal.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                maxLines = 1
                            )
                            Text(
                                text = animal.soundName,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = accentColor
                                )
                            )
                        }
                    }

                    // Transport Buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = onSkipPrevious,
                            modifier = Modifier.size(40.dp).testTag("btn_skip_prev")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous Animal",
                                tint = Color(0xFF455A64)
                            )
                        }

                        // Big Play / Pause Button
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                                .clickable { onPlayPause() }
                                .testTag("btn_play_pause"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        IconButton(
                            onClick = onSkipNext,
                            modifier = Modifier.size(40.dp).testTag("btn_skip_next")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next Animal",
                                tint = Color(0xFF455A64)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom feature toggles: Pitch modifier, Loop, Voiced Announcer, Autoplay
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Silly Pitch Switcher
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PitchMode.values().forEach { mode ->
                            val isSelected = playbackState.pitchMode == mode
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) accentColor else Color(0xFFF0F0F0),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onPitchModeChanged(mode) }
                                    .testTag("pitch_mode_${mode.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(text = mode.emoji, fontSize = 14.sp)
                                    Text(
                                        text = mode.displayName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else Color(0xFF555555)
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Toggles (Loop, Announce, Auto)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Loop toggle
                        IconButton(
                            onClick = onToggleLoop,
                            modifier = Modifier.size(36.dp).testTag("btn_toggle_loop")
                        ) {
                            Icon(
                                imageVector = if (playbackState.isLooping) Icons.Default.RepeatOne else Icons.Default.Repeat,
                                contentDescription = "Toggle Loop",
                                tint = if (playbackState.isLooping) accentColor else Color(0xFF9E9E9E),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Voice Announce Toggle
                        IconButton(
                            onClick = onToggleAnnounce,
                            modifier = Modifier.size(36.dp).testTag("btn_toggle_announce")
                        ) {
                            Icon(
                                imageVector = if (playbackState.isAnnounceEnabled) Icons.Default.RecordVoiceOver else Icons.Default.VoiceOverOff,
                                contentDescription = "Toggle Voice Name Announcement",
                                tint = if (playbackState.isAnnounceEnabled) accentColor else Color(0xFF9E9E9E),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Autoplay Continuous Toggle
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (playbackState.isAutoplayEnabled) Color(0xFF4CAF50) else Color(0xFFECEFF1),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onToggleAutoplay() }
                                .testTag("btn_toggle_autoplay")
                        ) {
                            Text(
                                text = if (playbackState.isAutoplayEnabled) "Auto ON" else "Auto OFF",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (playbackState.isAutoplayEnabled) Color.White else Color(0xFF607D8B)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
