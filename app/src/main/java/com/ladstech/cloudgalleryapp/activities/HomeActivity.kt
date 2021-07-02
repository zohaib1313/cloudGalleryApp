package com.ladstech.cloudgalleryapp.activities

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ShareCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.api.graphql.model.ModelSubscription
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Posts
import com.amplifyframework.datastore.generated.model.UserCloudGallery
import com.appseen.contacts.sharing.app.callBacks.OnItemClickListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.adapters.AdapterHomePosts
import com.ladstech.cloudgalleryapp.databinding.ActivityHomeBinding

import com.ladstech.cloudgalleryapp.room.database.AppDatabase
import com.ladstech.cloudgalleryapp.room.entities.PostsTable
import com.ladstech.cloudgalleryapp.room.repositories.PostsRepository
import com.ladstech.cloudgalleryapp.room.viewModels.PostsViewModel
import com.ladstech.cloudgalleryapp.utils.AppConstant
import com.ladstech.cloudgalleryapp.utils.Helper
import ir.shahabazimi.instagrampicker.InstagramPicker

import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.row_posts.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception


class HomeActivity : BaseActivity() {

    private lateinit var mBinding: ActivityHomeBinding
    private lateinit var adapterHomePosts: AdapterHomePosts
    private var dataListPosts = ArrayList<Posts>()
    var popupWindow: PopupWindow? = null
    private lateinit var viewModelPosts: PostsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
//        loadingLayout = mBinding.loadin
//         gLayout.rlLoading
//        noDataFoundLayout = mBinding.noDataLayout.noDataChild

        mBinding.ivNav.setOnClickListener { openLeftMenu() }
        mBinding.ivBlockedUsers.setOnClickListener {
            Helper.startActivity(
                this@HomeActivity,

                Intent(this@HomeActivity, BlockedUserActivity::class.java),
                false
            )
        }
        mBinding.ivHomeProfile.setOnClickListener {
            Helper.startActivity(
                this@HomeActivity,
                Intent(this@HomeActivity, ProfileDetailsWatchActivity::class.java).apply {
                    putExtra(
                        AppConstant.KEY_DATA,
                        Gson().toJson(sessionManager.user)
                    )
                },
                false
            )
        }
        mBinding.navContent.btnAcceptRequst.setOnClickListener {
            openLeftMenu()
            showCustomAppAlert(this, R.layout.accept_request_layout)
        }
        mBinding.navContent.btnVerifyOtp.setOnClickListener {
            openLeftMenu()
            showCustomAppAlert(this, R.layout.verify_otp_layout)
        }
        mBinding.navContent.btnCloseApp.setOnClickListener {
            openLeftMenu()
            showCustomAppAlert(this, R.layout.close_layout)
        }
        mBinding.navContent.btnSendRequest.setOnClickListener {
            openLeftMenu()
            showCustomAppAlert(this, R.layout.send_request_layout)
        }

        mBinding.navContent.btnLogout.setOnClickListener {
            sessionManager.clearSession()
            sessionManager.isLoggedIn = false
            Helper.startActivity(
                this@HomeActivity,
                Intent(this@HomeActivity, SplashActivity::class.java),
                true
            )

        }

        mBinding.fabAddPost.setOnClickListener {
            val `in` = InstagramPicker(this@HomeActivity)
            `in`.show(0, 1) { address: String? ->

//                val uri = Uri.parse(address)
                com.ladstech.cloudgalleryapp.utils.Helper.startActivity(
                    this,
                    Intent(this, Add_new_post_f::class.java).apply {
                        putExtra(AppConstant.KEY_DATA, address.toString())
                    },
                    false
                )
            }


//            val dialog = android.app.AlertDialog.Builder(this)
//                .setTitle("Chose Post type")
//                .setCancelable(true)
//                .setPositiveButton("Public Post", DialogInterface.OnClickListener { dialog, which ->
//
//                    com.ladstech.cloudgalleryapp.utils.Helper.startActivity(
//                        this,
//                        Intent(this, AddPostActivity::class.java).apply {
//                            putExtra(AppConstant.KEY_DATA, true)
//                        },
//                        false
//                    )
//                    dialog.dismiss()
//                })
//                .setNegativeButton(
//                    "Private Post",
//                    DialogInterface.OnClickListener { dialog, which ->
//                        com.ladstech.cloudgalleryapp.utils.Helper.startActivity(
//                            this,
//                            Intent(this, AddPostActivity::class.java).apply {
//                                putExtra(AppConstant.KEY_DATA, false)
//                            },
//                            false
//                        )
//                        dialog.dismiss()
//                    })
//                .create()
//
//            dialog.show()
        }


        initRv()


    }

    private fun initPostsObserver() {
        viewModelPosts = ViewModelProviders.of(this).get(PostsViewModel::class.java)
        val observer = Observer<List<PostsTable>>() {
            dataListPosts.clear()
            it.forEach { posts ->
                val post = Posts.Builder()
                    .createdTime(posts.createdTime)
                    .image(posts.postImage)

                    .description(posts.description)
                    .whoPostedUser(
                        Gson().fromJson(
                            posts.whoPostedUser,
                            UserCloudGallery::class.java
                        )
                    )
                    .id(posts.postId)
                    .build()
                dataListPosts.add(post)


            }
            adapterHomePosts.notifyDataSetChanged()
        }
        viewModelPosts.liveDataList.observeForever(observer)
    }

    fun PopupWindow.dimBehind() {
        val container = contentView.rootView
        val context = contentView.context
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.3f
        wm.updateViewLayout(container, p)
    }

    fun showCustomAppAlert(activity: Activity, view: Int) {

        val layout = this.layoutInflater.inflate(
            view,
            this.findViewById(R.id.container)
        )
        val dialog = AlertDialog.Builder(activity)
            .setView(layout)
            .create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

    }

    private fun setUpPopWindow(post: Posts, view: View) {
        val layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = layoutInflater.inflate(R.layout.popup_filter_layout, null)
        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow?.let { popupWindow ->
            popupWindow.elevation = 8.0f

            popupWindow.setBackgroundDrawable(ColorDrawable())
            popupWindow.isOutsideTouchable = true
            popupWindow.setOnDismissListener(PopupWindow.OnDismissListener {
            })
            popupWindow.contentView.findViewById<ImageView>(R.id.ivLike).setOnClickListener {
                printLog("like")
                popupWindow.dismiss()
            }
            popupView.findViewById<ImageView>(R.id.ivComment).setOnClickListener {
                printLog("comments")
                popupWindow.dismiss()
            }
            popupView.findViewById<ImageView>(R.id.ivShare).setOnClickListener {
                printLog("share")
                ShareCompat.IntentBuilder.from(this@HomeActivity)
                    .setType("text/plain")
                    .setChooserTitle("Share URL")
                    .setText(
                        sessionManager.user.name +
                                " shared a post with you , click on the link to download/view post" +
                                "http://www.url.com"
                    )
                    .startChooser();
                popupWindow.dismiss()

            }
            popupView.findViewById<ImageView>(R.id.ivDelete).setOnClickListener {
                printLog("deleting post")
                popupWindow.dismiss()

                if(post.whoPostedUser.id==sessionManager.user.id){
                    Amplify.API.mutate(ModelMutation.delete(post), { response ->
                        if (!response.hasErrors()) {
                            printLog("Post deleted")
                        }

                    }, {})

                    Amplify.Storage.remove(post.image,
                        { printLog("Post image deleted") },
                        { printLog("Post deleted failed") })
                }else{
                    Toast.makeText(this@HomeActivity, "You can only delete post created by you", Toast.LENGTH_SHORT).show()
                }




            }

            popupWindow?.let { popupWindow ->
                if (popupWindow.isShowing) {
                    popupWindow.dismiss()
                }
                popupWindow.showAsDropDown(view, -view.width * 2, -3 * view.height)
                popupWindow.dimBehind()
            }
        }
    }

    private fun initRv() {
        adapterHomePosts = AdapterHomePosts(this@HomeActivity, dataListPosts)
        mBinding.rvPost.layoutManager = LinearLayoutManager(this)
        mBinding.rvPost.setHasFixedSize(true)
        mBinding.rvPost.adapter = adapterHomePosts
        adapterHomePosts.notifyDataSetChanged()
        mBinding.rvPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                popupWindow?.let { if (it.isShowing) it.dismiss() }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

            }
        })
        adapterHomePosts.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int, character: String) {

                when (view.id) {
                    R.id.btnDownloadPost -> {

                        //download
                        downloadImage(Helper.getImageUrl(dataListPosts[position].image))
                    }
                    R.id.ivUser -> {
                        ///see user profile


                        Helper.startActivity(
                            this@HomeActivity,
                            Intent(
                                this@HomeActivity,
                                ProfileDetailsWatchActivity::class.java
                            ).apply {
                                putExtra(
                                    AppConstant.KEY_DATA,
                                    Gson().toJson(dataListPosts[position].whoPostedUser)
                                )
                            },
                            false
                        )
                    }
                    R.id.btnMorePost -> {
                        //see options
                        try {
                            setUpPopWindow(dataListPosts[position], view)
                        } catch (e: Exception) {
                        }

                    }

                    R.id.imageView5 -> {
                        //see post details

                        Helper.startActivity(
                            this@HomeActivity,
                            Intent(this@HomeActivity, PostDetailActivity::class.java).apply {
                                putExtra(
                                    AppConstant.KEY_DATA,
                                    Gson().toJson(dataListPosts[position])
                                )
                            },
                            false
                        )
                    }
                }

            }
        })

        initPostsObserver()


        // loadPosts()
    }


    private fun openLeftMenu() {
        Helper.hideKeyboard(this)
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            mBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }


    fun downloadImage(imageURL: String) {
        if (!verifyPermissions()) {
            return
        }
        val fileName = imageURL.substring(imageURL.lastIndexOf('/') + 1)
        Glide.with(this)
            .load(imageURL)
            .into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    @NonNull resource: Drawable,
                    @Nullable transition: Transition<in Drawable?>?
                ) {
                    val bitmap = (resource as BitmapDrawable).bitmap
                    Toast.makeText(this@HomeActivity, "Saving Image...", Toast.LENGTH_SHORT).show()
                    saveMediaToStorage(bitmap)
                }

                override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
                override fun onLoadFailed(@Nullable errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    Toast.makeText(
                        this@HomeActivity,
                        "Failed to Download Image! Please try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun saveMediaToStorage(bitmap: Bitmap) {
        //Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        //Output streamfde
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            this@HomeActivity?.contentResolver?.also { resolver ->

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this@HomeActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
        }
    }


    fun verifyPermissions(): Boolean {

        // This will return the current Status
        val permissionExternalMemory =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {
            val STORAGE_PERMISSIONS = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            // If permission not granted then ask for permission real time.
            ActivityCompat.requestPermissions(this, STORAGE_PERMISSIONS, 1)
            return false
        }
        return true
    }
}