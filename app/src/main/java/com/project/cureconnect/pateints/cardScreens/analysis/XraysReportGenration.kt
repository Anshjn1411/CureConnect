package com.project.cureconnect.pateints.cardScreens.analysis


import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import login.AuthViewModel
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface

import java.util.Date
import java.util.Locale

class XRayReportGenerator {

    fun generateXRayReport(context: Context, jsonResponseStr: String, userName: String): File {
        try {
            // Try to parse as JSON
            val jsonResponse = try {
                JSONObject(jsonResponseStr)
            } catch (e: Exception) {
                // If parsing fails, try to clean up the string
                val cleanedJson = cleanupJsonString(jsonResponseStr)
                JSONObject(cleanedJson)
            }

            // Create PDF document
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            var page = document.startPage(pageInfo)
            var canvas = page.canvas

            // Get page dimensions
            val pageWidth = pageInfo.pageWidth.toFloat()
            val pageHeight = pageInfo.pageHeight.toFloat()
            val margin = 40f

            // Fill background with light blue color
            val backgroundPaint = Paint().apply {
                color = android.graphics.Color.parseColor("#E6F3FF") // Light blue color
                style = Paint.Style.FILL
            }
            canvas.drawRect(0f, 0f, pageWidth, pageHeight, backgroundPaint)

            // Draw border
            val borderPaint = Paint().apply {
                color = android.graphics.Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = 2f
            }
            canvas.drawRect(margin, margin, pageWidth - margin, pageHeight - margin, borderPaint)

            // Define text styles
            val titlePaint = Paint().apply {
                color = android.graphics.Color.parseColor("#0047AB") // Cobalt blue for title
                textSize = 24f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }

            val headerPaint = Paint().apply {
                color = android.graphics.Color.parseColor("#2C3E50") // Dark blue for headers
                textSize = 18f
                typeface = Typeface.DEFAULT_BOLD
            }

            val textPaint = Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 14f
            }

            val subtitlePaint = Paint().apply {
                color = android.graphics.Color.parseColor("#34495E") // Slate for subtitles
                textSize = 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            }

            // Add Report Title - centered
            canvas.drawText("X-Ray ANALYSIS REPORT", pageWidth / 2, margin + 30f, titlePaint)

            // Add decorative line under title
            val linePaint = Paint().apply {
                color = android.graphics.Color.parseColor("#0047AB")
                strokeWidth = 2f
            }
            canvas.drawLine(margin + 50f, margin + 45f, pageWidth - margin - 50f, margin + 45f, linePaint)

            // Starting position for content
            var yPosition = margin + 80f

            // Add Date and Time
            val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
            val currentDate = dateFormat.format(Date())
            canvas.drawText("Date: $currentDate", margin + 20f, yPosition, textPaint)
            yPosition += 25f

            // Add Patient Information
            canvas.drawText("Patient: $userName", margin + 20f, yPosition, textPaint)
            yPosition += 40f

            // Add ECG Analysis Results header
            canvas.drawText("X-Ray ANALYSIS RESULTS", margin + 20f, yPosition, headerPaint)
            yPosition += 30f

            // Extract prediction from the JSON
            try {
                // Try different possible JSON structures
                if (jsonResponse.has("prediction")) {
                    val prediction = jsonResponse.getString("prediction")
                    val predictionLines = breakTextIntoLines(prediction, 70) // Ensure text doesn't go off page

                    canvas.drawText("Prediction:", margin + 20f, yPosition, subtitlePaint)
                    yPosition += 25f

                    for (line in predictionLines) {
                        canvas.drawText(line, margin + 30f, yPosition, textPaint)
                        yPosition += 20f
                    }
                }
                // Option 2: Nested in a 'data' or 'result' object
                else if (jsonResponse.has("data") && jsonResponse.getJSONObject("data").has("prediction")) {
                    val prediction = jsonResponse.getJSONObject("data").getString("prediction")
                    val predictionLines = breakTextIntoLines(prediction, 70)

                    canvas.drawText("Prediction:", margin + 20f, yPosition, subtitlePaint)
                    yPosition += 25f

                    for (line in predictionLines) {
                        canvas.drawText(line, margin + 30f, yPosition, textPaint)
                        yPosition += 20f
                    }
                }
                // Option 3: If there are multiple fields
                else {
                    // Iterate through all keys and print them
                    val keys = jsonResponse.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val value = jsonResponse.get(key).toString()

                        // Draw the key as a subtitle
                        canvas.drawText("$key:", margin + 20f, yPosition, subtitlePaint)
                        yPosition += 25f

                        // Break the value into multiple lines
                        val valueLines = breakTextIntoLines(value, 70)
                        for (line in valueLines) {
                            canvas.drawText(line, margin + 30f, yPosition, textPaint)
                            yPosition += 20f

                            // Check if we're reaching the bottom of the page
                            if (yPosition > pageHeight - margin - 100f) {
                                // Finish current page and start a new one
                                document.finishPage(page)

                                // Create new page
                                val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, document.pages.size + 1).create()
                                val newPage = document.startPage(newPageInfo)
                                val newCanvas = newPage.canvas

                                // Fill background with light blue color for the new page
                                newCanvas.drawRect(0f, 0f, pageWidth, pageHeight, backgroundPaint)

                                // Draw border for the new page
                                newCanvas.drawRect(margin, margin, pageWidth - margin, pageHeight - margin, borderPaint)

                                // Reset y position for new page
                                yPosition = margin + 40f

                                // Update canvas reference
                                canvas = newCanvas
                                page = newPage
                            }
                        }

                        yPosition += 10f // Add space between different key-value pairs
                    }
                }
            } catch (e: Exception) {
                // If JSON parsing fails, just display the raw response
                canvas.drawText("Raw Analysis Result:", margin + 20f, yPosition, subtitlePaint)
                yPosition += 25f

                // Break the raw response into multiple lines to fit in the PDF
                val rawLines = breakTextIntoLines(jsonResponseStr, 70)
                for (line in rawLines) {
                    canvas.drawText(line, margin + 30f, yPosition, textPaint)
                    yPosition += 20f

                    // Check if we need a new page
                    if (yPosition > pageHeight - margin - 100f) {
                        document.finishPage(page)
                        val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, document.pages.size + 1).create()
                        val newPage = document.startPage(newPageInfo)
                        val newCanvas = newPage.canvas

                        // Fill background with light blue color for the new page
                        newCanvas.drawRect(0f, 0f, pageWidth, pageHeight, backgroundPaint)

                        // Draw border for the new page
                        newCanvas.drawRect(margin, margin, pageWidth - margin, pageHeight - margin, borderPaint)

                        yPosition = margin + 40f
                        canvas = newCanvas
                        page = newPage
                    }
                }
            }

            // Check if we have space for disclaimer
            if (yPosition > pageHeight - margin - 70f) {
                document.finishPage(page)
                val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, document.pages.size + 1).create()
                val newPage = document.startPage(newPageInfo)
                val newCanvas = newPage.canvas

                // Fill background with light blue color for the new page
                newCanvas.drawRect(0f, 0f, pageWidth, pageHeight, backgroundPaint)

                // Draw border for the new page
                newCanvas.drawRect(margin, margin, pageWidth - margin, pageHeight - margin, borderPaint)

                yPosition = margin + 40f
                canvas = newCanvas
                page = newPage
            }

            // Add disclaimer at the bottom of the last page
            yPosition = pageHeight - margin - 60f
            canvas.drawText("DISCLAIMER:", margin + 20f, yPosition, headerPaint)
            yPosition += 25f

            val disclaimer = "This analysis is automated and should be reviewed by a healthcare professional."
            val disclaimerLines = breakTextIntoLines(disclaimer, 70)
            for (line in disclaimerLines) {
                canvas.drawText(line, margin + 20f, yPosition, textPaint)
                yPosition += 20f
            }

            // Add footer with page numbers
            val pageCount = document.pages.size
            val footerPaint = Paint().apply {
                color = android.graphics.Color.parseColor("#555555")
                textSize = 12f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("Page ${pageCount} of ${pageCount}", pageWidth / 2, pageHeight - margin / 2, footerPaint)

            // Finish the page and document
            document.finishPage(page)

            // Create output file
            val outputFile = File(context.filesDir, "X-Ray_Report_${System.currentTimeMillis()}.pdf")
            val outputStream = FileOutputStream(outputFile)
            document.writeTo(outputStream)
            document.close()
            outputStream.close()

            return outputFile

        } catch (e: Exception) {
            throw Exception("Error generating PDF: ${e.message}", e)
        }
    }

    private fun cleanupJsonString(input: String): String {
        // Same as your existing method
        return if (input.startsWith("jsonModel(") && input.endsWith(")")) {
            val content = input.substring(input.indexOf('(') + 1, input.lastIndexOf(')'))
            if (content.contains("=")) {
                val parts = content.split("=")
                if (parts.size >= 2) {
                    val key = parts[0].trim()
                    val value = parts[1].trim()
                    """{"$key":"$value"}"""
                } else {
                    """{"data":"$content"}"""
                }
            } else {
                """{"data":"$content"}"""
            }
        } else {
            if (!input.trim().startsWith("{")) {
                """{"data":"$input"}"""
            } else {
                input
            }
        }
    }

    private fun breakTextIntoLines(text: String, maxCharsPerLine: Int): List<String> {
        val lines = mutableListOf<String>()
        var remainingText = text

        while (remainingText.length > maxCharsPerLine) {
            // Find a good breaking point (space) before the max line length
            var breakPoint = remainingText.lastIndexOf(' ', maxCharsPerLine)
            if (breakPoint == -1) breakPoint = maxCharsPerLine

            lines.add(remainingText.substring(0, breakPoint))
            remainingText = remainingText.substring(breakPoint).trim()
        }

        if (remainingText.isNotEmpty()) {
            lines.add(remainingText)
        }

        return lines
    }
}