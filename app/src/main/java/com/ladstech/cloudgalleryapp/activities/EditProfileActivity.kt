package com.ladstech.cloudgalleryapp.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.UserCloudGallery
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.databinding.ActivityEditProfileBinding
import com.ladstech.cloudgalleryapp.utils.Helper
import java.io.File

class EditProfileActivity : BaseActivity() {
    private lateinit var mBinding: ActivityEditProfileBinding
    var filePath: String? = null
    lateinit var userPhoneNumber: String
    var imagekey: String? = "null"
    var deviceToken: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityEditProfileBinding.inflate(layoutInflater)
        loadingLayout = mBinding.loadingLayout.rlLoading
        window.statusBarColor =
            ContextCompat.getColor(this@EditProfileActivity, R.color.app_color_blue)
        setContentView(mBinding.root)

        mBinding.ivBack.setOnClickListener { onBackPressed() }
        sessionManager.user?.let { user ->
            if (user.image != "null")
                Glide.with(this@EditProfileActivity).load(Helper.getImageUrl(user.image))
                    .into(mBinding.imageView4)
            mBinding.switch1.isChecked = !user.isPublic
            mBinding.editTextTextPersonName.setText(user.name)
            userPhoneNumber = user.phone
            imagekey = user.image
            deviceToken = user.deviceToken

            printLog("device token = $deviceToken")


            mBinding.btnUpdate.setOnClickListener {
                if (validateForm()) {

                    uploadImage()

                }
            }


            mBinding.imageView.setOnClickListener {
                ImagePicker.with(this@EditProfileActivity)
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
                                mBinding.imageView4.setImageURI(fileUri)
                                //You can get File object from intent
                                val file: File? = ImagePicker.getFile(data)
                                //You can also get File Path from intent

                                ImagePicker.getFilePath(data)?.let {
                                    filePath = it
                                }

                            }
                            ImagePicker.RESULT_ERROR -> {
                                Toast.makeText(
                                    this@EditProfileActivity,
                                    ImagePicker.getError(data),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {
                                Toast.makeText(
                                    this@EditProfileActivity,
                                    "Task Cancelled",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }
            }
        }


    }

    private fun uploadImage() {
        showLoading()
        if (filePath != null) {
            val myfie = File(filePath.toString())
            if (!myfie.exists()) {
                myfie.mkdir()
            }
            val profileImagePath =
                userPhoneNumber + "/profileImage" + "/" + myfie.name
            Amplify.Storage.uploadFile(profileImagePath, myfie,
                {
                    printLog("image uploaded key= ${it.key}")
                    imagekey = it.key
                    getFcmToken()
                },
                {
                    printLog("Upload failed=  ${it.cause}")
                }
            )
        } else {
            getFcmToken()
        }
    }

    //!....................................!!!!!!!!!!!!!.........................................!!!!!!
    private fun getFcmToken() {

        if (sessionManager.token != "null") {
            printLog("...........fcm token not null ............")
            deviceToken = sessionManager.token
            uploadDataToDataStore()
        } else {
            printLog("fcm token was null ,, refreshing.....")
            Helper.refreshFcmToken(this@EditProfileActivity)
            Toast.makeText(this@EditProfileActivity, "Try again", Toast.LENGTH_SHORT)
                .show()
        }
    }

    //!....................................!!!!!!!!!!!!!.........................................!!!!!!

    private fun uploadDataToDataStore() {
        val user = sessionManager.user.copyOfBuilder()
            .name(mBinding.editTextTextPersonName.text.toString())
            .phone(userPhoneNumber)
            .deviceToken(deviceToken)
            .isPublic(!(mBinding.switch1.isChecked))
            .image(imagekey)
            .id(userPhoneNumber)
            .build()

        printLog("updating previous user")
        Amplify.API.mutate(
            ModelMutation.update(user),
            {
                printLog("updated  with : ${it.data.toString()}")
                if (it.hasErrors()) {
                    ThreadUtils.runOnUiThread() {
                        hideLoading()
                        Toast.makeText(
                            this@EditProfileActivity, "User updation Failed ", Toast
                                .LENGTH_LONG
                        ).show()
                    }
                    return@mutate
                } else {

                    sessionManager.updateUserSession(user)

                    ThreadUtils.runOnUiThread() {

                        Toast.makeText(
                            this@EditProfileActivity, "User updated successfully ", Toast
                                .LENGTH_LONG
                        ).show()

                        hideLoading()
                    }


                }


            },
            {
                printLog("User Updation Failed DataStore: ${it.cause}")
                ThreadUtils.runOnUiThread() {
                    hideLoading()
                    Toast.makeText(
                        this@EditProfileActivity, "User Updation Failed ${it.cause}", Toast
                            .LENGTH_LONG
                    ).show()
                }
            }
        )

    }

    //!....................................!!!!!!!!!!!!!.........................................!!!!!!
    private fun validateForm(): Boolean {
        var isValid = true
        if (mBinding.editTextTextPersonName.text.isNullOrEmpty()) {
            mBinding.editTextTextPersonName.error =
                getString(R.string.error_form_empty_field_of, " Name ")
            isValid = false
        }
        return isValid
    }
}