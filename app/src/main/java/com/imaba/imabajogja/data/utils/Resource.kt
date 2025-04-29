package com.imaba.imabajogja.data.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.itextpdf.io.IOException
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
private const val MAXIMAL_SIZE = 2_000_000 // 🔥 Maksimum 2MB


fun EditText.setTextOrPlaceholder(value: String?, placeholder: String) {
    if (!value.isNullOrEmpty()) {
        this.setText(value)
    } else {
        this.setText("")
        this.hint = placeholder
    }
}

fun compressPdf(file: File): File {
    val maxSize = 2 * 1024 * 1024 // 2MB
    var compressedFile = File(file.parent, "compressed_${file.name}")

    Log.d("CompressPDF", "📝 Ukuran awal: ${file.length()} bytes")

    if (file.length() <= maxSize) {
        return file // Jika sudah sesuai, langsung return
    }

    try {
        // 🔥 Gunakan iText 7 untuk menulis ulang PDF
        val reader = PdfReader(file)
        val writer = PdfWriter(FileOutputStream(compressedFile))
        val pdfDoc = PdfDocument(reader, writer)

        pdfDoc.close() // Selesai menulis ulang PDF

        Log.d("CompressPDF", "✅ Ukuran setelah kompresi: ${compressedFile.length()} bytes")

        if (compressedFile.length() > maxSize) {
            throw IOException("⚠️ File masih terlalu besar setelah kompresi.")
        }

        return compressedFile
    } catch (e: Exception) {
        Log.e("CompressPDF", "❌ Error kompresi: ${e.message}")
        throw IOException("Gagal mengompres PDF: ${e.message}")
    }
}



// 🔥 1. Fungsi untuk mendapatkan URI gambar (Android 10+)
fun getImageUri(context: Context): Uri {
    var uri: Uri? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
        }
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }
    return uri ?: getImageUriForPreQ(context)
}

// 🔥 2. Untuk Android 9 (Pie) ke bawah
private fun getImageUriForPreQ(context: Context): Uri {
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
    if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

// 🔥 3. Buat file gambar sementara
fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(timeStamp, ".jpg", filesDir)
}

// 🔥 4. Convert URI ke File
fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = createCustomTempFile(context)
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
    outputStream.close()
    inputStream.close()
    return myFile
}

fun uriToFilePdf(uri: Uri, context: Context): File {
    val myFile = File(context.cacheDir, "temp_document.pdf")
    val inputStream = context.contentResolver.openInputStream(uri) ?: return myFile
    val outputStream = FileOutputStream(myFile)

    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) {
        outputStream.write(buffer, 0, length)
    }
    outputStream.close()
    inputStream.close()

    return myFile
}



// 🔥 5. Kompresi gambar agar tidak lebih dari 2MB
fun File.reduceFileImage(): File {
    val file = this
    val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE && compressQuality > 0)

    bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

// 🔥 6. Pastikan gambar tidak berputar saat diunggah
fun Bitmap.getRotatedBitmap(file: File): Bitmap? {
    val orientation = ExifInterface(file).getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
        ExifInterface.ORIENTATION_NORMAL -> this
        else -> this
    }
}

// 🔥 7. Fungsi untuk rotasi gambar
fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}

fun showLoading(view: View, state: Boolean) {
    view.visibility = if (state) View.VISIBLE else View.GONE
}


infix fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

@SuppressLint("SimpleDateFormat")
fun formatter(date: String): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val outputFormat = SimpleDateFormat("dd MMMM yyyy")

    return try {
        val dateFormat = inputFormat.parse(date)
        val formattedDate = dateFormat?.let { outputFormat.format(it) }
        formattedDate ?: ""
    } catch (e: ParseException) {
        e.printStackTrace()
        ""
    }
}

//fun showCustomDialog(
//    context: Context,
//    title: String,
//    iconRes: Int,
//    showYesButton: Boolean = true,
//    showNoButton: Boolean = true,
//    showNextButton: Boolean = true,
//    onYesClicked: (() -> Unit)? = null,
//    onNoClicked: (() -> Unit)? = null,
//    onNextClicked: (() -> Unit)? = null
//) {
//    // Inflate layout dengan View Binding
//    val bindingDialog = DialogConfirmBinding.inflate(LayoutInflater.from(context))
//
//    // Inisialisasi AlertDialog
//    val dialog = AlertDialog.Builder(context)
//        .setView(bindingDialog.root)
//        .setCancelable(true)
//        .create()
//
//    // Set data untuk dialog
//    bindingDialog.title.text = title
//    bindingDialog.icon.setImageResource(iconRes)
//
//    // Atur visibilitas tombol
//    bindingDialog.btnYes.visibility = if (showYesButton) View.VISIBLE else View.GONE
//    bindingDialog.btnNo.visibility = if (showNoButton) View.VISIBLE else View.GONE
//    bindingDialog.btnNext.visibility = if (showNextButton) View.VISIBLE else View.GONE
//
//
//    // Tambahkan aksi untuk tombol No
//    bindingDialog.btnNo.setOnClickListener {
//        dialog.dismiss()
//        onNoClicked?.invoke()
//    }
//
//    // Tambahkan aksi untuk tombol Yes
//    bindingDialog.btnYes.setOnClickListener {
//        dialog.dismiss()
//        onYesClicked?.invoke()
//    }
//    bindingDialog.btnNext.setOnClickListener {
//        dialog.dismiss()
//        onNextClicked?.invoke()
//    }
//
//    // Tampilkan dialog
//    dialog.show()


