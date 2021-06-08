package com.ladstech.cloudgalleryapp.activities


import android.os.Bundle
import android.util.Log
import android.view.View
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.StorageAccessLevel
import com.amplifyframework.storage.options.StorageDownloadFileOptions
import com.amplifyframework.storage.options.StorageListOptions
import com.amplifyframework.storage.options.StorageUploadFileOptions

import com.ladstech.cloudgalleryapp.databinding.ActivityTestBinding
import java.io.File

class TestActivity : BaseActivity() {
    private lateinit var mBinding: ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnUploadPrivt.setOnClickListener {
            val storageUploadOption = StorageUploadFileOptions.builder()
                .accessLevel(StorageAccessLevel.PRIVATE)
                .build()

            val exampleFile = File(this.applicationContext.filesDir, "ExampleKey")
            exampleFile.writeText("Example file contents")
            Amplify.Storage.uploadFile("ExampleKey", exampleFile, storageUploadOption,
                {
                    Log.i(TAG, "Successfully uploaded: private File ${it.key}")
                },
                { Log.e(TAG, "Upload failed private file", it) }
            )
        }
        mBinding.btnDownloadPrvtFile.setOnClickListener {
            val file = File("${this.applicationContext.filesDir}/download.txt")
//            val options = StorageListOptions.builder()
//                .accessLevel(StorageAccessLevel.PROTECTED)
//                .targetIdentityId("otherUserID")
//                .build()

            val options = StorageDownloadFileOptions.builder()
                .accessLevel(StorageAccessLevel.PRIVATE)
            Amplify.Storage.downloadFile("ExampleKey", file, options.build(),
                { Log.i("MyAmplifyApp", "Fraction private download completed: ${it.fractionCompleted}") },
                {
                    Log.i("MyAmplifyApp", "Successfully downloaded private file: ${it.file.name}")

                },
                { Log.e("MyAmplifyApp", "Download Failure private file", it) }
            )
        }

        mBinding.btnUploadPublic.setOnClickListener {
//            val storageUploadOption = StorageUploadFileOptions.builder()
//                .accessLevel(StorageAccessLevel.PRIVATE)
//                .build()

            val exampleFile = File(this.applicationContext.filesDir, "ExampleKey")
            exampleFile.writeText("Example file contents")
            Amplify.Storage.uploadFile("ExampleKey", exampleFile,
                {
                    Log.i("MyAmplifyApp", "Successfully uploaded: public File ${it.key}")
                },
                { Log.e("MyAmplifyApp", "Upload failed public", it) }
            )
        }
        mBinding.btnDownloadPublicFile.setOnClickListener {
            val file = File("${this.applicationContext.filesDir}/download.txt")
            val options = StorageDownloadFileOptions.defaultInstance()
            Amplify.Storage.downloadFile("ExampleKey", file, options,
                { Log.i("MyAmplifyApp", "Fraction completed public : ${it.fractionCompleted}") },
                {
                    Log.i("MyAmplifyApp", "Successfully downloaded public file: ${it.file.name}")
                },
                { Log.e("MyAmplifyApp", "Download Failure public", it) }
            )
        }


    }
}