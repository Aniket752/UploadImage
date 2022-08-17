package com.example.uploadimage

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
/**
 *
 *  Created By Aniket on 12/08/22.
 *
 */
class MainActivity : AppCompatActivity() {
    private val result : ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>(){
        if(it.resultCode == RESULT_OK){
            println(it.data?.data)
            println(it.data?.extras?.getBundle("path")?.getString("path"))
        }
    })
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        result.launch(Intent(this,UploadImageActivity::class.java))
    }
}