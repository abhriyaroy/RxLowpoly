package com.zebrostudio.rxlowpoly.helpers

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.*

class UriHelper {

  val DOCUMENTS_DIR = "documents"
  val AUTHORITY = "YOUR_AUTHORITY.provider"

  private fun getExtension(uri: String): String {
    val dot = uri.lastIndexOf("")
    return if (dot >= 0) {
      uri.substring(dot)
    } else {
      // No extension.
      ""
    }
  }

  private fun isLocal(url: String): Boolean {
    return !url.startsWith("http://") && !url.startsWith("https://")
  }

  fun isMediaUri(uri: Uri): Boolean {
    return "media".equals(uri.authority!!, ignoreCase = true)
  }

  fun getUri(file: File): Uri {
    return Uri.fromFile(file)
  }

  fun getPathWithoutFilename(file: File): File {
    if (file.isDirectory) {
      return file
    } else {
      val filename = file.name
      val filepath = file.absolutePath
      var pathwithoutname = filepath.substring(0, filepath.length - filename.length)
      if (pathwithoutname.endsWith("/")) {
        pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length - 1)
      }
      return File(pathwithoutname)
    }
  }

  private fun getMimeType(file: File): String {
    val extension = getExtension(file.name)
    return if (extension.isNotEmpty()) {
      MimeTypeMap.getSingleton().getMimeTypeFromExtension(
        extension.substring(1)
      )!!
    } else "application/octet-stream"

  }

  fun getMimeType(context: Context, uri: Uri): String {
    val file = File(getPath(context, uri))
    return getMimeType(file)
  }

  fun getMimeType(context: Context, url: String): String {
    var type = context.contentResolver.getType(Uri.parse(url))
    if (type == null) {
      type = "application/octet-stream"
    }
    return type
  }

  private fun isLocalStorageDocument(uri: Uri): Boolean {
    return AUTHORITY == uri.authority
  }

  private fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
  }

  private fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
  }

  private fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
  }

  private fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.authority
  }

  fun getDataColumn(
    context: Context, uri: Uri?, selection: String?,
    selectionArgs: Array<String>?
  ): String? {
    var cursor: Cursor? = null
    val column = MediaStore.Files.FileColumns.DATA
    val projection = arrayOf(column)

    try {
      cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
      if (cursor != null && cursor.moveToFirst()) {

        val column_index = cursor.getColumnIndexOrThrow(column)
        return cursor.getString(column_index)
      }
    } catch (e: Exception) {

    } finally {
      cursor?.close()
    }
    return null
  }

  fun getPath(context: Context, uri: Uri): String {
    val absolutePath = getLocalPath(context, uri)
    return absolutePath ?: uri.path
  }

  private fun getLocalPath(context: Context, uri: Uri): String? {
    val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    // DocumentProvider
    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
      // LocalStorageProvider
      if (isLocalStorageDocument(uri)) {
        // The path is the id
        return DocumentsContract.getDocumentId(uri)
      } else if (isExternalStorageDocument(uri)) {
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split((":").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val type = split[0]

        if ("primary".equals(type, ignoreCase = true)) {
          return (Environment.getExternalStorageDirectory()).toString() + "/" + split[1]
        } else if ("home".equals(type, ignoreCase = true)) {
          return (Environment.getExternalStorageDirectory()).toString() + "/documents/" + split[1]
        }
      } else if (isDownloadsDocument(uri)) {

        val id = DocumentsContract.getDocumentId(uri)

        if (id.startsWith("raw:")) {
          return id.substring(4)
        }
        val contentUriPrefixesToTry =
          arrayOf("content://downloads/public_downloads", "content://downloads/my_downloads")
        for (contentUriPrefix in contentUriPrefixesToTry) {
          val contentUri =
            ContentUris.withAppendedId(Uri.parse(contentUriPrefix), java.lang.Long.valueOf(id!!))
          try {
            val path = getDataColumn(context, contentUri, null, null)
            if (path != null) {
              return path
            }
          } catch (e: Exception) {
          }
        }

        // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
        val fileName = getFileName(context, uri)
        val cacheDir = getDocumentCacheDir(context)
        val file = generateFileName(fileName, cacheDir)
        var destinationPath: String? = null
        if (file != null) {
          destinationPath = file.absolutePath
          saveFileFromUri(context, uri, destinationPath)
        }

        return destinationPath
      } else if (isMediaDocument(uri)) {
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split((":").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val type = split[0]

        var contentUri: Uri? = null
        if ("image" == type) {
          contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        } else if ("video" == type) {
          contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else if ("audio" == type) {
          contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val selection = "_id=?"
        val selectionArgs = arrayOf(split[1])

        return getDataColumn(context, contentUri, selection, selectionArgs)
      }// MediaProvider
      // DownloadsProvider
      // ExternalStorageProvider
    } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {

      // Return the remote address
      return if (isGooglePhotosUri(uri)) {
        uri.lastPathSegment
      } else getDataColumn(context, uri, null, null)

    } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
      return uri.path
    }
    return null
  }

  fun getFile(context: Context, uri: Uri): File? {
    val path = getPath(context, uri)
    if (isLocal(path)) {
      return File(path)
    }
    return null
  }

  fun getDocumentCacheDir(context: Context): File {
    val dir = File(context.cacheDir, DOCUMENTS_DIR)
    if (!dir.exists()) {
      dir.mkdirs()
    }
    return dir
  }

  fun generateFileName(name: String?, directory: File): File? {
    var name: String? = name ?: return null

    var file = File(directory, name)

    if (file.exists()) {
      var fileName: String? = name
      var extension = ""
      val dotIndex = name!!.lastIndexOf('.')
      if (dotIndex > 0) {
        fileName = name.substring(0, dotIndex)
        extension = name.substring(dotIndex)
      }

      var index = 0

      while (file.exists()) {
        index++
        name = "$fileName($index)$extension"
        file = File(directory, name)
      }
    }

    try {
      if (!file.createNewFile()) {
        return null
      }
    } catch (e: IOException) {
      return null
    }
    return file
  }

  private fun saveFileFromUri(context: Context, uri: Uri, destinationPath: String?) {
    var `is`: InputStream? = null
    var bos: BufferedOutputStream? = null
    try {
      `is` = context.contentResolver.openInputStream(uri)
      bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
      val buf = ByteArray(1024)
      `is`!!.read(buf)
      do {
        bos.write(buf)
      } while (`is`.read(buf) != -1)
    } catch (e: IOException) {
      e.printStackTrace()
    } finally {
      try {
        if (`is` != null) {
          `is`.close()
        }
        if (bos != null) {
          bos.close()
        }
      } catch (e: IOException) {
        e.printStackTrace()
      }

    }
  }

  fun getFileName(context: Context, uri: Uri): String? {
    val mimeType = context.contentResolver.getType(uri)
    var filename: String? = null

    if (mimeType == null) {
      val path = getPath(context, uri)
      if (path == null) {
        filename = getName(uri.path)
      } else {
        val file = File(path)
        filename = file.name
      }
    } else {
      val returnCursor = context.contentResolver.query(uri, null, null, null, null)
      if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        filename = returnCursor.getString(nameIndex)
        returnCursor.close()
      }
    }
    return filename
  }

  fun getName(filename: String?): String? {
    if (filename == null) {
      return null
    }
    val index = filename.lastIndexOf('/')
    return filename.substring(index + 1)
  }
}