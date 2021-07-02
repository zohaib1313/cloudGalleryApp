package com.ladstech.cloudgalleryapp.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.UserCloudGallery
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.ladstech.cloudgalleryapp.R

import com.ladstech.cloudgalleryapp.activities.HomeActivity
import com.ladstech.cloudgalleryapp.callBacks.MessageEvent
import com.ladstech.cloudgalleryapp.databinding.FragmentMyAccountBinding
import com.ladstech.cloudgalleryapp.databinding.FragmentProfileFragmentBinding
import com.ladstech.cloudgalleryapp.databinding.FragmentWelcomBinding
import com.ladstech.cloudgalleryapp.utils.Helper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.lang.Exception

class ProfileFragmentF : BaseFragment() {

    lateinit var mBinding: FragmentProfileFragmentBinding
    lateinit var firebaseAuth: FirebaseAuth
    var filePath: String? = null
    var imagekey: String? = "null"
    var deviceToken: String? = null
    var isUserFound = false
    lateinit var userPhoneNumber: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentProfileFragmentBinding.inflate(layoutInflater)
        loadingLayout=mBinding.loadingLayout.rlLoading
        mBinding.ivBack.setOnClickListener {
            ///event bus
        }
        loadingLayout = mBinding.loadingLayout.rlLoading
        noDataFoundLayout = mBinding.noDataLayout.noDataChild
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null)
            userPhoneNumber =
                firebaseAuth.currentUser.phoneNumber.toString().replace("-", "").replace(" ", "")


        mBinding.imageView.setOnClickListener {
            ImagePicker.with(requireActivity())
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
                                requireContext(),
                                ImagePicker.getError(data),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
        }
        Helper.refreshFcmToken(requireContext())


        // Inflate the layout for this fragment
        return mBinding.root
    }


    //!....................................!!!!!!!!!!!!!.........................................!!!!!!


    //!....................................!!!!!!!!!!!!!.........................................!!!!!!


    //!....................................!!!!!!!!!!!!!.........................................!!!!!!

    private fun uploadDataToDataStore() {
        val user = UserCloudGallery.Builder()
            .name(mBinding.editTextTextPersonName.text.toString())
            .phone(userPhoneNumber)
            .deviceToken(deviceToken)
            .createdTime(Helper.getCurrentTimeStamp())
            .isPublic(!(mBinding.switch1.isChecked))
            .about("About")
            .image(imagekey)
            .id(userPhoneNumber)
            .build()



        if (isUserFound) {
            printLog("updating previous user")
            val request =
                ModelQuery.get(UserCloudGallery::class.java, userPhoneNumber)
            Amplify.API.query(request, { response ->
                if (response.data != null) {
                    printLog("user found ${response.data}")
                    var previousUser = response.data

                    val updatedUser = previousUser.copyOfBuilder()
                        .about("My Status")
                        .name(mBinding.editTextTextPersonName.text.toString())
                        .isPublic(!mBinding.switch1.isChecked)

                    if (imagekey != "null") {
                        updatedUser.image(imagekey)
                    }

                    Amplify.API.mutate(
                        ModelMutation.update(updatedUser.build()),
                        {
                            printLog("updated  with : ${it.data.toString()}")
                            if (it.hasErrors()) {
                                ThreadUtils.runOnUiThread() {
                                    Toast.makeText(
                                        requireContext(), "User updation Failed ", Toast
                                            .LENGTH_LONG
                                    ).show()
                                }
                                return@mutate
                            } else {

                                sessionManager.createUserLoginSession(updatedUser.build())
                                Helper.startActivity(
                                    requireActivity(),
                                    Intent(requireContext(), HomeActivity::class.java),
                                    true
                                )
                                requireActivity().finishAffinity()
                            }

                            ThreadUtils.runOnUiThread() {
                                hideLoading()
                            }

                        },
                        {
                            printLog("User Updation Failed DataStore: ${it.cause}")
                            ThreadUtils.runOnUiThread() {
                                hideLoading()
                                Toast.makeText(
                                    requireContext(), "User Updation Failed ${it.cause}", Toast
                                        .LENGTH_LONG
                                ).show()
                            }
                        }
                    )

                }

            }, { printLog("error finding user") })
        } else {

            Amplify.API.mutate(
                ModelMutation.create(user),
                {
                    printLog("Added  with id: ${it.data.toString()}")
                    if (it.hasErrors()) {
                        ThreadUtils.runOnUiThread() {
                            Toast.makeText(
                                requireContext(), "User Creation Failed ", Toast
                                    .LENGTH_LONG
                            ).show()
                        }
                        return@mutate
                    } else {

                        sessionManager.createUserLoginSession(user)
                        Helper.startActivity(
                            requireActivity(),
                            Intent(requireContext(), HomeActivity::class.java),
                            true
                        )
                        requireActivity().finishAffinity()
                    }
                },
                {
                    printLog("User Add Failed DataStore: ${it.cause}")
                    ThreadUtils.runOnUiThread() {
                        Toast.makeText(
                            requireContext(), "User Creation Failed ${it.cause}", Toast
                                .LENGTH_LONG
                        ).show()
                    }
                }
            )
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
            Helper.refreshFcmToken(requireContext())
            Toast.makeText(requireContext(), "Try again", Toast.LENGTH_SHORT)
                .show()
        }
    }

    //!....................................!!!!!!!!!!!!!.........................................!!!!!!

    private fun uploadImage() {
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

    private fun validateForm(): Boolean {
        var isValid = true
        if (mBinding.editTextTextPersonName.text.isNullOrEmpty()) {
            mBinding.editTextTextPersonName.error =
                getString(R.string.error_form_empty_field_of, " Name ")
            isValid = false
        }
        return isValid
    }
    //!....................................!!!!!!!!!!!!!.........................................!!!!!!

    private fun checkIfUserExists() {
        showLoading()
        val request =
            ModelQuery.get(UserCloudGallery::class.java, userPhoneNumber)

        Amplify.API.query(request, { response ->

            if (response.data != null) {
                printLog("user found in query first")
                isUserFound = true
                val user = response.data
                ThreadUtils.runOnUiThread {
                    mBinding.editTextTextPersonName.setText(user.name)
                    //  mBinding.etAbout.setText(user.about)

                    if (user.image != "null")
                        Glide.with(requireContext()).load(Helper.getImageUrl(user.image))
                            .into(mBinding.imageView4)
                    mBinding.switch1.isChecked = !user.isPublic

                    isUserFound = true
                    printLog("user found")
//                    sessionManager.createUserLoginSession(user)
//                    Helper.startActivity(
//                        requireActivity(),
//                        Intent(requireContext(), HomeActivity::class.java),
//                        true
//                    );
//                    requireActivity().finishAffinity()
                }


            } else {
                printLog("no previous user found")
            }

        }, { error -> printLog("error while finding user $error") })
        hideLoading()

    }
    //!....................................!!!!!!!!!!!!!.........................................!!!!!!

    override fun onResume() {
        super.onResume()
        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.app_color_blue)
        if (firebaseAuth.currentUser != null) {
            userPhoneNumber =
                firebaseAuth.currentUser.phoneNumber.toString().replace("-", "").replace(" ", "")
            checkIfUserExists()
        }
    }
    //!....................................!!!!!!!!!!!!!.........................................!!!!!!

    companion object {
        private val ARG_DATA = "position"
        fun newInstance(index: Int): ProfileFragmentF {
            val fragment = ProfileFragmentF()
            val args = Bundle()
            args.putInt(ARG_DATA, index)
            fragment.arguments = args
            return fragment
        }
    }

    //!....................................!!!!!!!!!!!!!.........................................!!!!!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        printLog("profile fragment......")
    }

    //!....................................!!!!!!!!!!!!!.........................................!!!!!!

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)

    }

    //!....................................!!!!!!!!!!!!!.........................................!!!!!!
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) {
        if (event != null) {
            Log.d("onEventt", event.event)
            when (event.event) {
                getString(R.string.login) -> {
                    if (validateForm()) {
                        Helper.hideKeyboard(requireActivity())
                        showLoading()
                        uploadImage()
                    }
                }
            }
        }
    }


}