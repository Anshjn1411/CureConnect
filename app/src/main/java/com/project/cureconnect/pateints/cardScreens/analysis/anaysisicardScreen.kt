package com.project.cureconnect.pateints.cardScreens.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.project.cureconnect.pateints.cardScreens.analysis.Sampledataana.analyses
import login.AuthViewModel

// Data class to represent each analysis card
data class AnalysisItem(
    val id: String,
    val icon: ImageVector,
    val title: String,
    val description: String,
    val color: Color,
    val specialty: String,
    val routes : String
)

@Composable
fun AnalysisCard(
    analysis: AnalysisItem,
     navController: NavController,
    Id : String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Icon and Title Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                // Circular Icon Background
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(analysis.color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = analysis.icon,
                        contentDescription = analysis.title,
                        tint = analysis.color,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = analysis.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Description
            Text(
                text = analysis.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Start Analysis Button
            Button(
                onClick = { navController.navigate("ananyisis/${analysis.id}") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = analysis.color.copy(alpha = 0.1f),
                    contentColor = analysis.color
                )
            ) {
                Text(
                    text = "Start Analysis →",
                    color = analysis.color
                )
            }
        }
    }
}

@Composable
fun MedicalAnalysisDashboard(navController: NavController) {
    val viewModel :AuthViewModel = viewModel()
    var id by remember { mutableStateOf("") }
    viewModel.getUserData { user ->
        id = user?.userId.toString()
    }



    // List of analysis items

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            contentPadding = PaddingValues(8.dp)
        ) {
            item {
                ExtraArea(navController)
                Spacer(Modifier.height(20.dp))
            }
            items(analyses) { analysis ->
                AnalysisCard(analysis , navController , id  )
            }
        }
    }


}

// Example of how to use in a Compose Activity
@Preview(showBackground = true)
@Composable
fun MedicalAnalysisDashboardPreview() {
    MaterialTheme {
        MedicalAnalysisDashboard(navController = rememberNavController())
    }
}
object Sampledataana{
    val analyses = listOf(
        AnalysisItem(
            id = "0",
            icon = Icons.Default.Image,
            title = "X-Ray Analysis",
            description = "Advanced image processing for accurate X-ray diagnostics with AI-powered detection",
            color = Color(0xFF2196F3), // Blue
            specialty = "Radiology",
            routes = "xray"
        ),
        AnalysisItem(
            id = "1",

            icon = Icons.Default.Favorite,
            title = "ECG Analysis",
            description = "Real-time electrocardiogram analysis for comprehensive heart monitoring",
            color = Color(0xFF4CAF50), // Green
            specialty = "Cardiology",
            routes = "ecg"
        ),
        AnalysisItem(
            id = "2",
            icon = Icons.Default.Warning,
            title = "PET Analysis",
            description = "Advanced PET scan analysis for early cancer detection and monitoring",
            color = Color(0xFF9C27B0), // Purple
            specialty = "Oncology",
            routes = "pet"
        ),
        AnalysisItem(
            id = "3",
            icon = Icons.Default.Face,
            title = "Alzheimer's Detection",
            description = "Early cognitive decline screening using advanced neurological pattern recognition",
            color = Color(0xFFFF5722), // Orange
            specialty = "Neurology",
            routes = "alzheimer"
        ),
        AnalysisItem(
            id = "4",
            icon = Icons.Default.Healing,
            title = "Skin Disease Analysis",
            description = "AI-powered skin lesion classification and potential disease identification",
            color = Color(0xFFE91E63), // Pink
            specialty = "Dermatology",
            routes = "dermatology"
        ),
        AnalysisItem(
            id = "5",
            icon = Icons.Default.RemoveRedEye,
            title = "Retinopathy Detection",
            description = "Advanced retinal screening for early diabetic eye disease detection",
            color = Color(0xFF009688), // Teal
            specialty = "Ophthalmology",
            routes = "ophthalmology"
        )
    )
}