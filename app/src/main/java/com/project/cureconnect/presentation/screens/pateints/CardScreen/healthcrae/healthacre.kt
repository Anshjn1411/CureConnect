package com.project.cureconnect.presentation.screens.pateints.CardScreen.healthcrae

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

@Composable
fun healthcre(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var filteredTips by remember { mutableStateOf(sampleHealthTips) }
    var isLoading by remember { mutableStateOf(false) }

    val categories = listOf("All", "Daily Habits", "Nutrition", "Fitness", "Rest", "Mental Health")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Health Tips",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Text(
                text = "For Your Wellbeing",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Discover simple yet effective tips to improve your health and wellness daily.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                isLoading = true
                filteredTips = filterTips(sampleHealthTips, it, selectedCategory)
                isLoading = false
            },
            placeholder = {
                Text(
                    "Search health tips...",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Category Chips
        LazyRow(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryChip(
                    category = category,
                    isSelected = selectedCategory == category,
                    onClick = {
                        selectedCategory = category
                        filteredTips = filterTips(sampleHealthTips, searchQuery, category)
                    }
                )
            }
        }

        // Tips count
        Text(
            text = "Showing 1-${filteredTips.size} of ${sampleHealthTips.size} health tips",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Health Tips Grid
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredTips) { tip ->
                    HealthTipCard(tip)
                }
            }
        }
    }
}

@Composable
fun CategoryChip(category: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = when {
        isSelected -> when (category) {
            "All" -> MaterialTheme.colorScheme.primary
            "Daily Habits" -> MaterialTheme.colorScheme.primary
            "Nutrition" -> MaterialTheme.colorScheme.secondary
            "Fitness" -> MaterialTheme.colorScheme.tertiary
            "Rest" -> MaterialTheme.colorScheme.primaryContainer
            "Mental Health" -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.primary
        }
        else -> MaterialTheme.colorScheme.surface
    }

    val textColor = if (isSelected) {
        when (category) {
            "All", "Daily Habits" -> MaterialTheme.colorScheme.onPrimary
            "Nutrition" -> MaterialTheme.colorScheme.onSecondary
            "Fitness" -> MaterialTheme.colorScheme.onTertiary
            "Rest" -> MaterialTheme.colorScheme.onPrimaryContainer
            "Mental Health" -> MaterialTheme.colorScheme.onError
            else -> MaterialTheme.colorScheme.onPrimary
        }
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = backgroundColor,
        modifier = Modifier
            .clickable { onClick() }
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.extraLarge
            )
    ) {
        Text(
            text = category,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun HealthTipCard(tip: HealthTip) {
    val categoryColor = when (tip.category) {
        "Daily Habits" -> MaterialTheme.colorScheme.primary
        "Nutrition" -> MaterialTheme.colorScheme.secondary
        "Fitness" -> MaterialTheme.colorScheme.tertiary
        "Rest" -> MaterialTheme.colorScheme.primaryContainer
        "Mental Health" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    val onCategoryColor = when (tip.category) {
        "Daily Habits" -> MaterialTheme.colorScheme.onPrimary
        "Nutrition" -> MaterialTheme.colorScheme.onSecondary
        "Fitness" -> MaterialTheme.colorScheme.onTertiary
        "Rest" -> MaterialTheme.colorScheme.onPrimaryContainer
        "Mental Health" -> MaterialTheme.colorScheme.onError
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Category header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(categoryColor)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Category icon placeholder
                    when (tip.category) {
                        "Daily Habits" -> Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = categoryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        "Nutrition" -> Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = categoryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        "Fitness" -> Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = categoryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        "Rest" -> Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = categoryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        "Mental Health" -> Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = categoryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        else -> Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = categoryColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = tip.category,
                    color = onCategoryColor,
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = onCategoryColor.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = tip.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Learn more",
                        style = MaterialTheme.typography.labelMedium,
                        color = categoryColor
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Learn more",
                        tint = categoryColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// Sample health tips with expanded content
val sampleHealthTips = listOf(
    HealthTip(
        title = "Stay Hydrated",
        description = "Drink at least 8 glasses of water daily to maintain proper hydration and support bodily functions.",
        category = "Daily Habits"
    ),
    HealthTip(
        title = "Mindful Eating",
        description = "Pay attention to what and when you eat. Avoid distractions like TV during meals to prevent overeating.",
        category = "Nutrition"
    ),
    HealthTip(
        title = "Regular Exercise",
        description = "Aim for at least 30 minutes of moderate physical activity most days of the week for heart health.",
        category = "Fitness"
    ),
    HealthTip(
        title = "Quality Sleep",
        description = "Adults should aim for 7-9 hours of uninterrupted sleep per night to support physical and mental health.",
        category = "Rest"
    ),
    HealthTip(
        title = "Stress Management",
        description = "Practice relaxation techniques like deep breathing, meditation, or yoga to reduce stress and anxiety.",
        category = "Mental Health"
    ),
    HealthTip(
        title = "Morning Sunlight",
        description = "Get 10-30 minutes of morning sunlight to regulate your circadian rhythm and boost vitamin D production.",
        category = "Daily Habits"
    ),
    HealthTip(
        title = "Balanced Diet",
        description = "Include a variety of fruits, vegetables, whole grains, lean proteins, and healthy fats in your daily meals.",
        category = "Nutrition"
    ),
    HealthTip(
        title = "Strength Training",
        description = "Include resistance exercises 2-3 times per week to build muscle mass and strengthen bones.",
        category = "Fitness"
    ),
    HealthTip(
        title = "Digital Detox",
        description = "Take regular breaks from screens and practice a digital detox before bedtime to improve sleep quality.",
        category = "Rest"
    )
)

fun filterTips(tips: List<HealthTip>, query: String, category: String): List<HealthTip> {
    return tips.filter {
        (category == "All" || it.category == category) &&
                (query.isEmpty() || it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true))
    }
}