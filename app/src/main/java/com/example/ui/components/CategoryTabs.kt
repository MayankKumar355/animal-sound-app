package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AnimalCategory

@Composable
fun CategoryTabs(
    selectedCategory: AnimalCategory,
    onCategorySelected: (AnimalCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimalCategory.values().forEach { category ->
            val isSelected = selectedCategory == category
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "category_scale"
            )

            val backgroundBrush = if (isSelected) {
                when (category) {
                    AnimalCategory.ALL -> Brush.horizontalGradient(listOf(Color(0xFFFF7043), Color(0xFFFFB300)))
                    AnimalCategory.WILD -> Brush.horizontalGradient(listOf(Color(0xFFFB8C00), Color(0xFFFFA726)))
                    AnimalCategory.DOMESTIC -> Brush.horizontalGradient(listOf(Color(0xFFE91E63), Color(0xFFFF4081)))
                    AnimalCategory.BIRDS -> Brush.horizontalGradient(listOf(Color(0xFF0288D1), Color(0xFF26C6DA)))
                }
            } else {
                Brush.horizontalGradient(listOf(Color.White, Color(0xFFF7F7F7)))
            }

            val textColor = if (isSelected) Color.White else Color(0xFF424242)

            Surface(
                modifier = Modifier
                    .scale(scale)
                    .clip(RoundedCornerShape(24.dp))
                    .shadow(
                        elevation = if (isSelected) 6.dp else 2.dp,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onCategorySelected(category) }
                    .testTag("category_tab_${category.name.lowercase()}"),
                shape = RoundedCornerShape(24.dp),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .background(backgroundBrush)
                        .height(48.dp)
                        .padding(horizontal = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category.emoji,
                            fontSize = 20.sp
                        )
                        Text(
                            text = category.displayName,
                            color = textColor,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
