package com.ladstech.cloudgalleryapp.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.widget.Toast
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Posts
import com.github.dhaval2404.imagepicker.ImagePicker
import com.ladstech.cloudgalleryapp.callBacks.MessageEvent
import com.ladstech.cloudgalleryapp.databinding.ActivityAddPostBinding
import com.ladstech.cloudgalleryapp.utils.AppConstant
import com.ladstech.cloudgalleryapp.utils.Helper
import org.greenrobot.eventbus.EventBus
import java.io.File

class AddPostActivity : BaseActivity() {

    private lateinit var mBinding: ActivityAddPostBinding
    private var postType: Boolean? = null
    var file: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        postType = intent.getBooleanExtra(AppConstant.KEY_DATA, true)
        postType?.let {
            mBinding.galleryBtn.setOnClickListener {
                ImagePicker.with(this)
                    .compress(1024)         //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        1080,
                        1080
                    )  //Final image resolution will be less than 1080 x 1080(Optional)
                    .start { resultCode, data ->
                        when (resultCode) {
                            Activity.RESULT_OK -> {
                                //Image Uri will not be null for RESULT_OK
                                val fileUri = data?.data
                                mBinding.pImageIv.setImageURI(fileUri)
                                //You can get File object from intent

                                file = ImagePicker.getFile(data)
                                //You can also get File Path from intent


                            }
                            ImagePicker.RESULT_ERROR -> {
                                Toast.makeText(
                                    this@AddPostActivity,
                                    ImagePicker.getError(data),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {
                                Toast.makeText(
                                    this@AddPostActivity,
                                    "Task Cancelled",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }

            }
            mBinding.pUploadBtn.setOnClickListener {
                if (validateForm()) {
                    uploadData()

                } else {
                    Toast.makeText(
                        this@AddPostActivity,
                        "Enter in all fields or set picture",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        }

    }

    private fun uploadData() {

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
                }
            )


        }

    }

    private fun uploadDataToDataStore(fileKey: String) {
        val post = Posts.Builder()
            .createdTime(Helper.getCurrentTimeStamp())
            .image(fileKey)
            .isPublic(postType)
            .title(mBinding.tvTitle.text.toString())
            .description(mBinding.tvDescription.text.toString())
            .whoPostedUser(sessionManager.user)
            .build()

        Amplify.API.mutate(ModelMutation.create(post), {
            if (it.hasErrors()) {
                ThreadUtils.runOnUiThread() {
                    Toast.makeText(
                        this, "Posting failed ", Toast
                            .LENGTH_LONG
                    ).show()
                }
                return@mutate
            } else {
                printLog("Post created successfully")
                finish()
            }
        }, { printLog("Post created failed") })


//        Amplify.DataStore.save(post,
//            {
//                printLog("new post added....")
//                EventBus.getDefault().post(MessageEvent(AppConstant.POSTCREATED))
//                finish()
//            },
//            { printLog("new post add failed= ${it.cause}...") }
//        )


    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (mBinding.tvTitle.text.isEmpty()) {
            isValid = false
        }
        if (mBinding.tvDescription.text.isEmpty()) {
            isValid = false
        }
        if (file == null) {
            isValid = false
        }

        return isValid
    }
}