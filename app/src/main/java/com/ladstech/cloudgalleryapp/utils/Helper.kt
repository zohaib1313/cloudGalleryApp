package com.ladstech.cloudgalleryapp.utils


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.text.format.DateFormat
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.Amplify.Auth
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.UserCloudGallery

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.ladstech.cloudgalleryapp.R

import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

import java.math.RoundingMode
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


object Helper {

    fun startActivity(mActivity: Activity, mIntent: Intent, isFinish: Boolean) {
        mActivity.startActivity(mIntent)
        runAnimation(mActivity)
        if (isFinish) {
            mActivity.finish()
        }
    }

    fun startActivityReverse(mActivity: Activity, mIntent: Intent, isFinish: Boolean) {
        mActivity.startActivity(mIntent)
        runReverseAnimation(mActivity)
        if (isFinish) {
            mActivity.finish()
        }
    }


    private fun runAnimation(mActivity: Activity) {
        mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    fun runReverseAnimation(mActivity: Activity) {
        mActivity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        //        if (LocaleHelper.isEnglish(mActivity)) {
        //            mActivity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        //        } else {
        //            mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        //        }
    }


    fun setAnimation(view: View) {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator() //add this
        fadeIn.duration = 1000
        val animation = AnimationSet(false) //change to false
        animation.addAnimation(fadeIn)
        view.animation = animation
    }

    fun getFormattedDate(
        strDate: String,
        sourceFormat: String,
        destinyFormat: String,
        locale: Locale
    ): String {
        var df = SimpleDateFormat(sourceFormat, locale)
        var date: Date? = null
        try {
            date = df.parse(strDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        df = SimpleDateFormat(destinyFormat, locale)
        return df.format(date)
    }


    fun getStringToDate(strDate: String, sourceFormate: String): Date? {
        var df = SimpleDateFormat(sourceFormate)
        var date: Date? = null
        try {
            date = df.parse(strDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date

    }

    fun getCurrentMYSQLDateTime(): String? {
        return SimpleDateFormat(AppConstant.MYSQL_DATETIME_FORMAT).format(Calendar.getInstance().time)
    }


    fun isValidEmail(target: CharSequence?): Boolean {
        return if (target == null) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    fun hideKeyboard(activity: Activity) {
        // Check if no view has focus:
        val view = activity.currentFocus
        if (view != null) {
            val inputManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun hideSoftKeybord(mContext: Context, v: View) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }


    fun isValidPassword(password: String): Boolean {
        return Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$")
            .matcher(password).matches()
    }


    fun validateCivilId(civilId: String): Boolean {
//        return (civilId.isNotEmpty() && civilId.length == 12 && civilId.startsWith("25"))
        return (civilId.isNotEmpty() && civilId.length == 12)
    }

    fun generateBitmapDescriptorFromRes(context: Context, resId: Int): BitmapDescriptor {
        val drawable = ContextCompat.getDrawable(context, resId)
        drawable!!.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    fun generateSSHKey(context: Context) {
        try {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                Log.i("AppLog", "key:$hashKey=")
            }
        } catch (e: Exception) {
            Log.e("AppLog", "error:", e)
        }

    }


    @JvmStatic
    @Suppress("DEPRECATION")
    fun isNetworkAvailable(context: Context): Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {

                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                        else -> false
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = true
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = true
                    }
                }
            }
        }
        return result
    }


    fun isDigitOnly(text: CharSequence): Boolean {
        return text.matches("-?\\d+(\\.\\d+)?".toRegex())
    }

    @JvmStatic
    fun roundDecimal(discRs: Double): String {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(discRs)
    }


    fun isPermissionGranted(context: Context, permission: String) =
        ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED

    fun isLocationEnabled(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun getImageUrl(path: String): String {

        return "https://cloudgalleryandrbucket232712-dev.s3.amazonaws.com/public/${
            path.replace(
                "+",
                "%2B"
            )
        }"
    }

    fun toCSV(array: Array<String>): String? {
        var result = ""
        val sb = StringBuilder()
        if (array.size > 0) {

            for (s in array) {
                sb.append(s.trim { it <= ' ' })
            }
            result = sb.toString()

            // Toast.makeText(requireContext(),result,Toast.LENGTH_LONG).show()
        }
        return result
    }


    fun getAwsDate(fileTime: Long): String? {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = fileTime
        return DateFormat.format("MMMM dd ,yyyy", calendar).toString()
    }

    fun getFileDirectory(filePath: String): String {
        val fileName: String? = filePath.substringAfterLast("/")
        return "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/${fileName}"
    }

    fun getCurrentTimeStamp(): String {
        return Date().time.toString()
    }

    fun refreshFcmToken(context: Context) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d(
                    AppConstant.TAG,
                    "Fetching FCM registration token failed",
                    task.exception
                )
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            SessionManager.getInstance(context)?.let {
                it.updateToken(token.toString())

                if (it.user != null) {
                    updateUserInAwsForFcmToken(token.toString(), context)
                }
            }
            Log.d(AppConstant.TAG, "fcm token refreshed= ${token.toString()}")
        })
    }

    private fun updateUserInAwsForFcmToken(token: String, context: Context) {

        val sessionUser = SessionManager.getInstance(context.applicationContext).user
        sessionUser?.let { it ->

            val request = ModelQuery.get(UserCloudGallery::class.java, it.id)
            Amplify.API.query(request, { response ->
                if (response.data != null) {
                    val user = response.data.copyOfBuilder()
                        .deviceToken(token)
                        .build()

                    Amplify.API.mutate(ModelMutation.update(user), {
                        if (it.hasErrors()) {
                            Log.i(AppConstant.TAG, "updation in fcm token failed cloud")
                        } else {
                            Log.i(AppConstant.TAG, "Updated a fcm token in cloud")
                        }
                    }, {
                    })
                }

            }, {})


        }
    }

    fun sessionRefresh() {

        val mUser = FirebaseAuth.getInstance().currentUser
        mUser!!.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val idToken = task.result?.token
                    Log.d(AppConstant.TAG, "aws token==  ${idToken.toString()}")
                    // Send token to your backend via HTTPS
                    var mobileClient =
                        Auth.getPlugin("awsCognitoAuthPlugin").escapeHatch as AWSMobileClient?
                    mobileClient?.federatedSignIn(
                        AppConstant.IAMPROVIDER,
                        idToken, object : Callback<UserStateDetails?> {
                            override fun onResult(userStateDetails: UserStateDetails?) {
                                Log.d(AppConstant.TAG, "Aws Token refreshed...")

                            }

                            override fun onError(e: Exception?) {
                                Log.d(AppConstant.TAG, "AWS Token refreshed failed...")
                            }
                        })
                    // ...
                } else {
                    // Handle error -> task.getException();
                    Log.d(AppConstant.TAG, "AWS Token refreshed failed...")
                }
            }
    }

    fun getPhoneNumberWithCountryCode(number: String, phoneUtil: PhoneNumberUtil): String {
        var result = ""

        try {
            val swissNumberProto = phoneUtil.parse(number, null)
            result = phoneUtil.format(swissNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164)
        } catch (e: NumberParseException) {
            System.err.println("NumberParseException was thrown: $e")
        }
        return result
    }

}
