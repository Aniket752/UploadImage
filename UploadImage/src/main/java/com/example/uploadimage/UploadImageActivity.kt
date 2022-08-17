package com.example.uploadimage

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.loader.content.CursorLoader
import com.example.uploadimage.databinding.ActivityUploadImageBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream


/**
 *
 *  Created By Aniket on 12/08/22.
 *
 */
class UploadImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadImageBinding
    var intent1 = Intent()
    lateinit var finalFile : File
    private val result: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult>() {
                if (it.resultCode == RESULT_OK) {
                    if (it.data!!.data == null) {
                        val photo = it.data?.extras?.get("data") as Bitmap
                        binding.preview.setImageBitmap(photo)
                        binding.preview.visibility = View.VISIBLE
                        showImage(photo)
                    } else {
                        val uri = it.data?.data as Uri
                        binding.preview.setImageURI(uri)
                        binding.preview.visibility = View.VISIBLE
//                        val path = Environment.getExternalStorageDirectory().toString()
                        val path = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                        try{
                        val file = File(path, "/Image")
                        file.mkdirs()
                        val image = File(file.absolutePath, "image1.jpeg")
                            intent1.data = image.absolutePath.toUri()
                        val source = FileInputStream(getRealPathFromURI(uri)?.let { it1 ->
                            File(
                                it1
                            )
                        }).channel
                        val destination = FileOutputStream(image).channel
                        if (destination != null && source != null) {
                            destination.transferFrom(source, 0, 2000000);
                        }
                        source?.close()
                        destination?.close()
                        }catch (e : Exception){
                            println(e.message)
                        }

                    }

                } else
                    Toast.makeText(this, "No", Toast.LENGTH_LONG).show()
            })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityUploadImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.select.setOnClickListener {
            uploadImage()
        }

        binding.done.setOnClickListener {

            val bundle = Bundle().also {
                it.putString("path","hat")
            }
            intent1.putExtra("path",bundle)
            setResult(RESULT_OK,intent1)
            finish()

        }
    }

    private fun showImage(bitmap: Bitmap) {
        val path = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(path, "/Image")
        file.mkdirs()
        val image = File(file.absolutePath, "image1.jpeg")
        val fileOutputStream = FileOutputStream(image)
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream)
            intent1.data = image.absolutePath.toUri()
                fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace();
        } catch (e: Exception) {
            e.printStackTrace();
        }

    }

    private fun uploadImage() {

        if (binding.camera.isChecked) {
            result.launch(Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE_SECURE))
        } else if (binding.gallery.isChecked) {
            result.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        } else if (binding.file.isChecked) {
            result.launch(
                Intent(Intent.ACTION_GET_CONTENT).setType("image/jpg")
                    .putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            )
        } else {
            result.launch(Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE_SECURE))
        }

    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(this, contentUri, proj, null, null, null)
        val cursor = loader.loadInBackground()
        val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val result = column_index?.let { cursor.getString(it) }
        cursor?.close()
        return result
    }
}