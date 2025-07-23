package com.project.cureconnect.presentation.screens.pateints.CardScreen.analysis

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat

import android.graphics.Paint
import android.graphics.Typeface
import com.project.cureconnect.R

import java.util.Date
import java.util.Locale

class ReportGenerator {
    val imageList = mapOf(
        0 to "X-ray",
        1 to "ECG",
        2 to "PET",
        3 to "MRI",
        4 to "Alzheimer's Detection",
        5 to "skin Dieases Analysis",
        6 to "Retinopathy Detection"
    )

    fun generateReport(context: Context, jsonResponseStr: String, userName: String , ID :Int): File {
        try {
            // Try to parse as JSON
            val jsonResponse = jsonResponseStr

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

            // Load Company Icon
            val iconBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.cureconnect_logo)

// Define desired icon width
            val iconWidth = 100f // Adjust size as needed
            val iconHeight = (iconWidth * iconBitmap.height) / iconBitmap.width

// Resize the icon
            val resizedBitmap = Bitmap.createScaledBitmap(
                iconBitmap,
                iconWidth.toInt(),
                iconHeight.toInt(),
                true
            )

            val iconPaint = Paint()

// Set opacity for watermark effect (0-255, where 255 is fully opaque)
            iconPaint.alpha = 80  // Adjust as needed for transparency

// Calculate center position and move it slightly downward
            val iconLeft = (canvas.width - iconWidth) / 2
            val iconTop = (canvas.height - iconHeight) / 2 + 50 // Adjust this value for more downward movement

// Draw the bitmap with transparency
            canvas.drawBitmap(
                resizedBitmap,
                iconLeft,
                iconTop,
                iconPaint
            )



            // Define text styles
            val titlePaint = Paint().apply {
                color = android.graphics.Color.parseColor("#0047AB") // Cobalt blue for title
                textSize = 24f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }
            val boldTextPaint = Paint().apply {
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 14f
                color = Color.BLACK
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
            canvas.drawText("${imageList.get(ID)} ANALYSIS REPORT", pageWidth / 2, margin + 30f, titlePaint)

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
            canvas.drawText("${imageList.get(ID)} ANALYSIS RESULTS", margin + 20f, yPosition, headerPaint)
            yPosition += 30f

            try {
                val predictionLines = breakTextIntoLines(jsonResponse, 70)

                canvas.drawText("Prediction:", margin + 20f, yPosition, subtitlePaint)
                yPosition += 25f

                for (line in predictionLines) {
                    val segments = line.split("**")
                    var xPos = margin + 30f

                    for ((index, segment) in segments.withIndex()) {
                        val paintToUse = if (index % 2 == 1) boldTextPaint else textPaint

                        canvas.drawText(segment, xPos, yPosition, paintToUse)
                        xPos += paintToUse.measureText(segment)
                    }

                    yPosition += 20f

                    // Optional: Start a new line if the line seems like a new topic
                    if (line.trim().startsWith("**")) {
                        yPosition += 10f
                    }

                    // Handle page overflow
                    if (yPosition > pageHeight - margin - 100f) {
                        document.finishPage(page)
                        val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, document.pages.size + 1).create()
                        page = document.startPage(newPageInfo)
                        canvas = page.canvas

                        canvas.drawRect(0f, 0f, pageWidth, pageHeight, backgroundPaint)
                        canvas.drawRect(margin, margin, pageWidth - margin, pageHeight - margin, borderPaint)

                        yPosition = margin + 40f
                    }
                }
            } catch (e: Exception) {
                // If JSON parsing fails, display raw response
                canvas.drawText("Raw Analysis Result:", margin + 20f, yPosition, subtitlePaint)
                yPosition += 25f

                val rawLines = breakTextIntoLines(jsonResponseStr, 70)

                for (line in rawLines) {
                    val segments = line.split("**")
                    var xPos = margin + 30f

                    for ((index, segment) in segments.withIndex()) {
                        val paintToUse = if (index % 2 == 1) boldTextPaint else textPaint

                        canvas.drawText(segment, xPos, yPosition, paintToUse)
                        xPos += paintToUse.measureText(segment)
                    }

                    yPosition += 20f

                    if (line.trim().startsWith("**")) {
                        yPosition += 10f
                    }

                    if (yPosition > pageHeight - margin - 100f) {
                        document.finishPage(page)
                        val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, document.pages.size + 1).create()
                        page = document.startPage(newPageInfo)
                        canvas = page.canvas

                        canvas.drawRect(0f, 0f, pageWidth, pageHeight, backgroundPaint)
                        canvas.drawRect(margin, margin, pageWidth - margin, pageHeight - margin, borderPaint)

                        yPosition = margin + 40f
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
            val outputFile = File(context.filesDir, "${imageList.get(ID)}_Report_${System.currentTimeMillis()}.pdf")
            val outputStream = FileOutputStream(outputFile)
            document.writeTo(outputStream)
            document.close()
            outputStream.close()

            return outputFile

        } catch (e: Exception) {
            throw Exception("Error generating PDF: ${e.message}", e)
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