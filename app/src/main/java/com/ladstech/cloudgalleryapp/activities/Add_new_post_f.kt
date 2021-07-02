package com.ladstech.cloudgalleryapp.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.net.toFile
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Posts
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.databinding.ActivityAddNewPostFBinding
import com.ladstech.cloudgalleryapp.utils.AppConstant

import com.ladstech.cloudgalleryapp.utils.Helper
import com.vanniktech.emoji.EmojiPopup
import java.io.File

class Add_new_post_f : BaseActivity() {
    private lateinit var mBinding: ActivityAddNewPostFBinding
    var file: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddNewPostFBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        loadingLayout = mBinding.loadingLayout.rlLoading

        val imageAddress = intent.getStringExtra(AppConstant.KEY_DATA)

        imageAddress?.let {

            val imageUri = Uri.parse(it)
            mBinding.ivPost.setImageURI(imageUri)
            file = imageUri.toFile()
        }


        val emojiPopup =
            EmojiPopup.Builder.fromRootView(mBinding.root).build(mBinding.etDescription)

        mBinding.btUpload.setOnClickListener {

            if (validateForm()) {
                uploadData()
            }

        }
        mBinding.ivEmojis.setOnClickListener {
            emojiPopup.toggle()
        }

    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (mBinding.etDescription.text.isEmpty()) {
            isValid = false
        }
        if (file == null) {
            isValid = false
        }

        return isValid
    }

    private fun uploadData() {
        showLoading()
        file?.let { filee ->

            val profileImagePath =
                sessionManager.user.phone + "/postsImages" + "/" + Helper.getCurrentTimeStamp() + filee.name
            Amplify.Storage.uploadFile(profileImagePath, filee,
                {
                    printLog("post image uploaded key= ${it.key}")
                    uploadDataToDataStore(it.key)
                },
                {
                    printLog("Upload failed=  ${it.cause}")
                    ThreadUtils.runOnUiThread {
                        hideLoading()
                        Toast.makeText(
                            this, "Posting failed ", Toast
                                .LENGTH_LONG
                        ).show()
                    }
                }
            )


        }

    }

    private fun uploadDataToDataStore(fileKey: String) {
        val post = Posts.Builder()
            .createdTime(Helper.getCurrentTimeStamp())
            .image(fileKey)
            .description(mBinding.etDescription.text.toString())
            .whoPostedUser(sessionManager.user)
            .build()

        Amplify.API.mutate(ModelMutation.create(post), {
            if (it.hasErrors()) {
                ThreadUtils.runOnUiThread() {
                    hideLoading()
                    Toast.makeText(
                        this, "Posting failed ", Toast
                            .LENGTH_LONG
                    ).show()
                }
                return@mutate
            } else {
                printLog("Post created successfully")
                finish()
                ThreadUtils.runOnUiThread() {
                    hideLoading()
                    Toast.makeText(
                        this.applicationContext, "Post created ", Toast
                            .LENGTH_LONG
                    ).show()
                }
            }
        }, {
            printLog("Post created failed")
            ThreadUtils.runOnUiThread() {
                hideLoading()
                Toast.makeText(
                    this, "Posting failed ", Toast
                        .LENGTH_LONG
                ).show()
            }
        }
        )


    }
}