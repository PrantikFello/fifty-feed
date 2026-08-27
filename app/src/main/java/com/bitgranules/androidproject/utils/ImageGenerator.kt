package com.bitgranules.androidproject.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.res.ResourcesCompat
import com.bitgranules.androidproject.data.QuoteStruct
import java.io.OutputStream

fun generateFinalImage(
    context: Context, backgroundBitmap: Bitmap, quoteStruct: QuoteStruct, fontResId: Int?
): Bitmap {

    val targetWidth = 1080
    val targetHeight = 1920

    val output = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    // Aspect-ratio preserving background layout crop fill matrix math
    val scaleX = targetWidth.toFloat() / backgroundBitmap.width
    val scaleY = targetHeight.toFloat() / backgroundBitmap.height
    val scale = Math.max(scaleX, scaleY)

    val srcScaledWidth = backgroundBitmap.width * scale
    val srcScaledHeight = backgroundBitmap.height * scale
    val left = (targetWidth - srcScaledWidth) / 2f
    val top = (targetHeight - srcScaledHeight) / 2f

    val bgPaint = Paint().apply { isFilterBitmap = true }
    canvas.save()
    canvas.translate(left, top)
    canvas.scale(scale, scale)
    canvas.drawBitmap(backgroundBitmap, 0f, 0f, bgPaint)
    canvas.restore()

    // Contrast protection scrim veil
    val scrimPaint = Paint().apply {
        color = Color.parseColor("#99000000") // 60% opacity dark overlay for premium high contrast
        style = Paint.Style.FILL
    }
    canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), scrimPaint)

    // Resolve Font Typeface
    val resolvedTypeface = if (fontResId != null) {
        try {
            ResourcesCompat.getFont(context, fontResId) ?: Typeface.create(
                Typeface.SERIF, Typeface.BOLD_ITALIC
            )
        } catch (e: Exception) {
            Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC)
        }
    } else {
        Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC)
    }

    val maxTextWidth = (targetWidth * 0.82f).toInt()
    // Define strict maximum bounding layout space for text (max 45% of total image height)
    val maxAllowedTextHeight = (targetHeight * 0.45f).toInt()

    // Initialize foundational TextPaint engine properties
    val textPaint = TextPaint().apply {
        color = Color.WHITE
        isAntiAlias = true
        typeface = resolvedTypeface
        setShadowLayer(18f, 0f, 6f, Color.parseColor("#B3000000"))
    }

    val quoteTextStr = "\"${quoteStruct.content}\""

    // --- HIGH-FIDELITY DYNAMIC AUTO-SCALE LOOP ---
    // Binary searching optimal scale based directly on font glyph canvas sizing feedback metrics
    var lowFontSize = 30f
    var highFontSize = 140f
    var optimalFontSize = 64f
    val maxIterations = 8 // Hard constraint layout pass gate limit

    for (i in 0 until maxIterations) {
        val testSize = (lowFontSize + highFontSize) / 2f
        textPaint.textSize = testSize

        val testLayout = StaticLayout.Builder.obtain(
            quoteTextStr, 0, quoteTextStr.length, textPaint, maxTextWidth
        ).setAlignment(Layout.Alignment.ALIGN_CENTER).setLineSpacing(0f, 1.25f).build()

        if (testLayout.height <= maxAllowedTextHeight) {
            optimalFontSize = testSize
            lowFontSize = testSize + 1f // Try pushing it larger to hit scale limits
        } else {
            highFontSize = testSize - 1f // Too large, restrict constraints downwards
        }
    }

    // Apply the mathematically discovered optimal resolution text size
    textPaint.textSize = optimalFontSize

    val authorPaint = TextPaint(textPaint).apply {
        // Author signature dynamically tracking proportional text scaling transformations
        textSize = optimalFontSize * 0.65f
        typeface = Typeface.create(resolvedTypeface, Typeface.NORMAL)
        setShadowLayer(12f, 0f, 4f, Color.parseColor("#B3000000"))
    }

    // Final calculations using optimal structural parameters
    val finalQuoteLayout =
        StaticLayout.Builder.obtain(quoteTextStr, 0, quoteTextStr.length, textPaint, maxTextWidth)
            .setAlignment(Layout.Alignment.ALIGN_CENTER).setLineSpacing(0f, 1.25f).build()

    val authorSignatureStr = "— ${quoteStruct.author}"
    val finalAuthorLayout = StaticLayout.Builder.obtain(
        authorSignatureStr, 0, authorSignatureStr.length, authorPaint, maxTextWidth
    ).setAlignment(Layout.Alignment.ALIGN_CENTER).build()

    // Calculate vertical layout distribution metrics
    val totalLayoutBlockHeight = finalQuoteLayout.height + 70 + finalAuthorLayout.height
    var runningVerticalY = (targetHeight - totalLayoutBlockHeight) / 2f
    val layoutLeftPos = (targetWidth - maxTextWidth) / 2f

    // Paint layout vectors to output canvas surface
    canvas.save()
    canvas.translate(layoutLeftPos, runningVerticalY)
    finalQuoteLayout.draw(canvas)
    canvas.restore()

    runningVerticalY += finalQuoteLayout.height + 70

    canvas.save()
    canvas.translate(layoutLeftPos, runningVerticalY)
    finalAuthorLayout.draw(canvas)
    canvas.restore()

    return output
}

fun saveBitmapToGallery(
    context: Context, bitmap: Bitmap, fileName: String
): Uri? {

    val resolver = context.contentResolver
    val buildVersionOld = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    val imageCollection = if (buildVersionOld) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.png")
        put(MediaStore.Images.Media.MIME_TYPE, "images/png")
        if (buildVersionOld) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Quote_Fetcher")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }
    val imageUri = resolver.insert(imageCollection, contentValues) ?: return null
    try {
        val outputStream: OutputStream? = resolver.openOutputStream(imageUri)
        outputStream.use { stream ->
            if (stream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
        }
        if (buildVersionOld) {
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(imageUri, contentValues, null, null)
        }
        return imageUri
    } catch (e: Exception) {
        e.printStackTrace()
        resolver.delete(imageUri, null, null)
        return null
    }


}