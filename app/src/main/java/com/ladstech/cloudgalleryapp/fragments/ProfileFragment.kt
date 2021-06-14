package com.ladstech.cloudgalleryapp.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amplifyframework.api.graphql.MutationType
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.Posts
import com.amplifyframework.datastore.generated.model.UserCloudGallery
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.activities.GalleryAppHome
import com.ladstech.cloudgalleryapp.activities.MainActivity
import com.ladstech.cloudgalleryapp.callBacks.MessageEvent
import com.ladstech.cloudgalleryapp.databinding.FragmentProfileBinding
import com.ladstech.cloudgalleryapp.utils.AppConstant
import com.ladstech.cloudgalleryapp.utils.AppConstant.Companion.TAG
import com.ladstech.cloudgalleryapp.utils.Helper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File


class ProfileFragment() : BaseFragment() {

    lateinit var mBinding: FragmentProfileBinding

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
        mBinding = FragmentProfileBinding.inflate(layoutInflater)
        loadingLayout = mBinding.loadingLayout.rlLoading
//        noDataFoundLayout=mBinding.noDataLayout.noDataChild
        firebaseAuth = FirebaseAuth.getInstance()



        userPhoneNumber =
            firebaseAuth.currentUser.phoneNumber.toString().replace("-", "").replace(" ", "")



        Helper.refreshFcmToken(requireContext())
        checkIfUserExists()


        mBinding.btnDone.setOnClickListener {

            if (validateForm()) {
                Helper.hideKeyboard(requireActivity())
                showLoading()
                uploadImage()
            }
        }
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
                            mBinding.circleImageView.setImageURI(fileUri)
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



        return mBinding.root
    }

    private fun uploadImage() {

        if (filePath != null) {
            val myfie = File(filePath.toString())
            if (!myfie.exists()) {
                myfie.mkdir()
            }

            val profileImagePath =
                userPhoneNumber + "/profileImage" + "/" + myfie.name
//        val bucketName = "contactsharing113011-dev."
//        imageS3Url = "https://"+bucketName+"s3.amazonaws.com/"+"public/"+profileImagePath
//        imageS3Url= imageS3Url.replace("+","%2B")
            // Toast.makeText(requireContext(),imageS3Url,Toast.LENGTH_LONG).show()
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

    private fun uploadDataToDataStore() {
        val user = UserCloudGallery.Builder()
            .name(mBinding.textView3.text.toString())
            .phone(userPhoneNumber)
            .deviceToken(deviceToken)
            .createdTime(Helper.getCurrentTimeStamp())
            .isPublic(true)
            .about(mBinding.etAbout.text.toString())
            .image(imagekey)
            .id(userPhoneNumber)
            .build()



        if (isUserFound) {
            printLog("updating previous user")
            val request =
                ModelQuery.get(UserCloudGallery::class.java, userPhoneNumber)
            Amplify.API.query(request, { response ->
                if (response.data != null) {
                    printLog("user found")
                    var previousUser = response.data

                    val updatedUser = previousUser.copyOfBuilder()
                        .about(mBinding.etAbout.text.toString())
                        .name(mBinding.textView3.text.toString())


                    if (imagekey != "null") {
                        updatedUser.image(imagekey)
                    }


                    Amplify.API.mutate(
                        ModelMutation.update(user),
                        {
                            printLog("updated  with id: ${it.data.id.toString()}")
                            if (it.hasErrors()) {
                                ThreadUtils.runOnUiThread() {
                                    Toast.makeText(
                                        requireContext(), "User updation Failed ", Toast
                                            .LENGTH_LONG
                                    ).show()
                                }
                                return@mutate
                            } else {

                                sessionManager.createUserLoginSession(user)
                                Helper.startActivity(
                                    requireActivity(),
                                    Intent(requireContext(), GalleryAppHome::class.java),
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
                            Intent(requireContext(), GalleryAppHome::class.java),
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


//        Amplify.DataStore.clear({
//            Log.i(AppConstant.TAG, "cleared datastore success")
//            Amplify.DataStore.save(user,
//                {
//                    Log.i(AppConstant.TAG, "user saved in aws data store")
//
//                    //     Amplify.DataStore.start({}, {})
//
//                    ThreadUtils.runOnUiThread {
//                        hideLoading()
//                        sessionManager.createUserLoginSession(user)
//                        Helper.startActivity(
//                            requireActivity(),
//                            Intent(requireContext(), MainActivity::class.java),
//                            true
//                        );
//                    }
//
//                },
//                {
//                    Log.e(AppConstant.TAG, "user saved  failed in aws data store", it)
//                    ThreadUtils.runOnUiThread {
//                        hideLoading()
//                    }
//                })
//        }, {
//            Log.i(AppConstant.TAG, "cleared datastore failed")
//
//
//        })


    }

    private fun getFcmToken() {

        if (sessionManager.token != null) {
            printLog("...........fcm token not null ............")
            deviceToken = sessionManager.token
            uploadDataToDataStore()
        } else {
            printLog("fcm token was null ,, refreshing.....")
            Helper.refreshFcmToken(requireContext())
            getFcmToken()
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true
        if (mBinding.etAbout.text.isNullOrEmpty()) {
            mBinding.etAbout.error =
                getString(R.string.error_form_empty_field_of, " About ")
            isValid = false

        }


        if (mBinding.textView3.text.isNullOrEmpty()) {
            mBinding.textView3.error =
                getString(R.string.error_form_empty_field_of, " Name ")
            isValid = false
        }
        return isValid;
    }

    companion object {
        private val ARG_DATA = "position"
        fun newInstance(index: Int): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putInt(ARG_DATA, index)
            fragment.arguments = args
            return fragment
        }
    }

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
                    mBinding.textView3.setText(user.name)
                    mBinding.etAbout.setText(user.about)
                    Glide.with(requireContext()).load(Helper.getImageUrl(user.image))
                    isUserFound = true
                    printLog("user found")
                    sessionManager.createUserLoginSession(user)
                    Helper.startActivity(
                        requireActivity(),
                        Intent(requireContext(), GalleryAppHome::class.java),
                        true
                    );
                    requireActivity().finishAffinity()
                }


            } else {
                printLog("no previous user found")
            }

        }, { error -> printLog("error while finding user $error") })
        hideLoading()

    }

    private fun changeFragment(fragment: Fragment, needToAddBackstack: Boolean) {
        val mFragmentTransaction: FragmentTransaction =
            activity?.supportFragmentManager!!.beginTransaction()
        mFragmentTransaction.replace(R.id.fragmentContainerLogin, fragment)
        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (needToAddBackstack) mFragmentTransaction.addToBackStack(null)
        mFragmentTransaction.commit()
    }











}

