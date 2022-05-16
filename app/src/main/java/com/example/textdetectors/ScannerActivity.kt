package com.example.textdetectors

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.lang.Exception
import java.lang.StringBuilder


class ScannerActivity : AppCompatActivity() {

    private lateinit var btnSnap : Button
    private lateinit var btnDetect : Button
    private lateinit var txtDetect : TextView
    private lateinit var imgDetectImage : ImageView

    private val requestCodeCamera = 2
    private var cameraPermissionCode = 1
    private lateinit var imgBitmap : Bitmap
    private var lastValue : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        initView()

        btnDetect.setOnClickListener {
            detectText()
        }

        btnSnap.setOnClickListener {

            checkPermissionCamera()
        }
    }

    // detected Text Logic
    private fun detectText() {
        try {

            val image : InputImage = InputImage.fromBitmap(imgBitmap,0)
            val recognizer : TextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

//            val result : Task<Text> = recognizer.process(image).addOnSuccessListener(object : OnSuccessListener<Text>{
            recognizer.process(image).addOnSuccessListener(object : OnSuccessListener<Text>{
                override fun onSuccess(p0: Text?) {

                    val result = StringBuilder()
                    for (block: Text.TextBlock in p0!!.textBlocks){
//                        val blockText = block.text
                        val blockCornerPoint : Array<out Point>? = block.cornerPoints
                        val blockFrame : Rect = block.boundingBox!!
                        for (line : Text.Line in block.lines){
                            val lineTExt = line.text
                            val lineCornerPoint : Array<out Point> = line.cornerPoints!!
                            val linRect = line.boundingBox
                            for (element : Text.Element in line.elements){
                                val elementText = element.text
                                val space = " "
                                result.append(elementText)
                                result.append(space)
                                lastValue = result.toString()
//                                txtDetect.text = blockText
                            }
//                            txtDetect.text = blockText
                        }

                    }

                    if(result.isEmpty()){
                        Toast.makeText(this@ScannerActivity,"It is Empty",Toast.LENGTH_SHORT).show()
                    }else{
                        txtDetect.text = lastValue
//                        txtDetect.isSelected
                    }

                }
            }).addOnFailureListener {
                Toast.makeText(
                    this@ScannerActivity,
                    "Fail to detect text from image..",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(this,"Please Click the Photo!!!",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestCodeCamera && resultCode == RESULT_OK){
            imgBitmap = data!!.extras!!["data"] as Bitmap
            imgDetectImage.setImageBitmap(imgBitmap)
        }

    }


    // initialization of View
    private fun initView(){
        btnSnap = findViewById(R.id.btnSnap)
        btnDetect = findViewById(R.id.btnDetect)

        txtDetect = findViewById(R.id.txtDetect)
        imgDetectImage = findViewById(R.id.imgDetectImage)
    }

    private fun captureWithCamara(){
        val cameraPicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraPicture, requestCodeCamera)
    }

    // App Permission Setting
    private fun appPermissionSetting(){
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${this.packageName}")).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    }

    private fun checkPermissionCamera() {

//        val cameraPermission = ContextCompat.checkSelfPermission(applicationContext,CAMERA)
//        return cameraPermission == PackageManager.PERMISSION_GRANTED


        val permissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            captureWithCamara()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // Camara Permission Denied by user
                val cameraPermissionAlertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
                cameraPermissionAlertDialog.setTitle(getString(R.string.permission))
                cameraPermissionAlertDialog.setMessage(getString(R.string.must_allow_camera))
                cameraPermissionAlertDialog.setPositiveButton(getString(R.string.open_setting)) { _, _ -> appPermissionSetting() }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                cameraPermissionAlertDialog.show()
            } else {
                // User allow Camera permission
                cameraPermissionCode = 0
//                resumeFilePermissionCode = 1
//                galleryPermissionCode = 1
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraPermissionCode)
            }
        }

    }

    private fun showCamaraError() {
        Toast.makeText(this, "Please Allow Camera Reading", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Camara Permission
        if (requestCode == cameraPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User Allow Camera Permission
                captureWithCamara()
            } else {
                showCamaraError()
            }
        }
    }
//    private fun requestPermission(){
//
////        val permissionCode = 200
//        int permissionCode = 200;
//        ActivityCompat.requestPermissions(this,new String[]{CAMERA},permissionCode);
//    }

}