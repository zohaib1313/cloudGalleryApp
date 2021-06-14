package com.ladstech.cloudgalleryapp.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.UserStateDetails
import com.amplifyframework.core.Amplify
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.callBacks.MessageEvent
import com.ladstech.cloudgalleryapp.databinding.FragmentLoginBinding
import com.ladstech.cloudgalleryapp.utils.AppConstant
import com.ladstech.cloudgalleryapp.utils.Helper
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit


class LoginFragment : BaseFragment() {

    lateinit var mBinding: FragmentLoginBinding

    //if code sending failed
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private var vCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerificationId: String? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var phoneNumberWithCountryCode: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentLoginBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        printLog("login fragment")
        firebaseAuth = FirebaseAuth.getInstance()
        loadingLayout = mBinding.loadingLayout.rlLoading
        noDataFoundLayout = mBinding.noDataLayout.noDataChild
        //...........................................................................................!!
        mBinding.btnSend.setOnClickListener {
            val countryCode = mBinding.etCountryCode.selectedCountryCodeWithPlus.toString().trim()
            val phoneNumber = mBinding.etPhoneNumber.text.toString().trim()

            if (phoneNumber.isEmpty()) {
                Toast.makeText(requireContext(), "Please input phone number ", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            phoneNumberWithCountryCode = countryCode + phoneNumber
            startPhoneNumberVerification(phoneNumberWithCountryCode)
        }


        //...........................................................................................!!
        vCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                printLog("on verification complete")

                //    signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                hideLoading()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                showLoading()
                Log.d(AppConstant.TAG, "onCodeSent: ")
                mVerificationId = verificationId
                forceResendingToken = token
                mVerificationId?.let {
                    showCustomOtpVerifyAlert(it, forceResendingToken!!)
                }


                Toast.makeText(requireContext(), "Verification Code Sent", Toast.LENGTH_SHORT)
                    .show()

            }

        }




        return mBinding.root
    }

    //...........................................................................................!!
    private fun showCustomOtpVerifyAlert(
        mVerificationId: String,
        forceResendingToken: PhoneAuthProvider.ForceResendingToken
    ) {

        val layout = requireActivity().layoutInflater.inflate(
            R.layout.verify_otp_layout,
            requireActivity().findViewById(R.id.containerOtp)
        )
        val dialog = AlertDialog.Builder(requireContext())
            .setView(layout)
            .setCancelable(false)
            .create()

        val et1 = layout.findViewById<EditText>(R.id.et1)
        val et2 = layout.findViewById<EditText>(R.id.et2)
        val et3 = layout.findViewById<EditText>(R.id.et3)
        val et4 = layout.findViewById<EditText>(R.id.et4)
        val et5 = layout.findViewById<EditText>(R.id.et5)
        val et6 = layout.findViewById<EditText>(R.id.et6)

        layout.findViewById<TextView>(R.id.btnVerifyOtpDlg).setOnClickListener {
            showLoading()
            printLog("verifying code...")
            var validation = true
            when {
                et1.text.isNullOrEmpty() -> {
                    validation = false
                }
                et2.text.isNullOrEmpty() -> {
                    validation = false
                }
                et3.text.isNullOrEmpty() -> {
                    validation = false
                }
                et4.text.isNullOrEmpty() -> {
                    validation = false
                }
                et5.text.isNullOrEmpty() -> {
                    validation = false
                }
                et6.text.isNullOrEmpty() -> {
                    validation = false
                }
            }
            if (validation) {
                verifyPhoneNumberThroughCode(
                    verificationId = mVerificationId,
                    et1.text.toString()
                            + et2.text.toString()
                            + et3.text.toString()
                            + et4.text.toString()
                            + et5.text.toString()
                            + et6.text.toString()
                )
                dialog.dismiss()
            } else {
                printLog("code not inserted complete")
                hideLoading()
                Toast.makeText(requireContext(), "Enter code", Toast.LENGTH_SHORT).show()
            }
        }

        layout.findViewById<TextView>(R.id.btnResendCode).setOnClickListener {
            resendVerificationCode(phoneNumberWithCountryCode, forceResendingToken)
            dialog.dismiss()

        }

        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                layout.findViewById<TextView>(R.id.countDown).text =
                    "00:" + millisUntilFinished / 1000
            }

            override fun onFinish() {
                layout.findViewById<TextView>(R.id.btnResendCode).visibility = View.VISIBLE
            }
        }.start()


        et1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    et2.requestFocus()
                }
            }
        })
        et2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    et3.requestFocus()
                } else {
                    et1.requestFocus()
                }
            }
        })
        et3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    et4.requestFocus()
                } else {
                    et2.requestFocus()
                }
            }
        })
        et4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    et5.requestFocus()
                } else {
                    et3.requestFocus()
                }
            }
        })
        et5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 1) {
                    et6.requestFocus()
                } else {
                    et4.requestFocus()
                }
            }
        })
        et6.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    et5.requestFocus()
                } else
                    Helper.hideSoftKeybord(requireContext(), requireView())

            }
        })

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()


    }

    //...........................................................................................!!
    private fun startPhoneNumberVerification(phoneNumber: String) {
        ThreadUtils.runOnUiThread {
            showLoading()
        }
        printLog("starting phone number otp verification")
        val phoneOptions = vCallBacks?.let {
            PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(it)
                .build()
        }
        PhoneAuthProvider.verifyPhoneNumber(phoneOptions!!)
    }

    //...........................................................................................!!
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {

        ThreadUtils.runOnUiThread {
            showLoading()
        }
        val phoneOptions = vCallBacks?.let {
            token?.let { it1 ->
                PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(50L, TimeUnit.SECONDS)
                    .setActivity(requireActivity())
                    .setCallbacks(it)
                    .setForceResendingToken(it1)
                    .build()
            }
        }

        PhoneAuthProvider.verifyPhoneNumber(phoneOptions!!)
    }

    //...........................................................................................!!
    private fun verifyPhoneNumberThroughCode(verificationId: String, code: String) {
        printLog("verifying phone with code $code")
        ThreadUtils.runOnUiThread {
            showLoading()
        }
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)

    }

    //...........................................................................................!!
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        printLog("signing in with credentials")

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                //login success
                printLog("signing in firebase success")
                val phoneNumber = firebaseAuth.currentUser?.phoneNumber
                Toast.makeText(requireContext(), "Logged In as " + phoneNumber, Toast.LENGTH_SHORT)
                    .show()
                val mUser = FirebaseAuth.getInstance().currentUser
                mUser!!.getIdToken(true)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val idToken = task.result?.token
                            Log.d(AppConstant.TAG, "firebase token = ${idToken.toString()}")
                            sendOpenIDToAWS(idToken)
                        }
                    }
            }.addOnFailureListener { e ->
                printLog("signing in firebase failed= ${e.cause}")
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                ThreadUtils.runOnUiThread {
                    hideLoading()
                }
            }
    }

    //...........................................................................................!!
    private fun sendOpenIDToAWS(idToken: String?) {
        printLog("sending token to aws...")
        val mobileClient =
            Amplify.Auth.getPlugin("awsCognitoAuthPlugin").escapeHatch as AWSMobileClient?
        mobileClient?.federatedSignIn(
            // aws provider URL
            AppConstant.IAMPROVIDER,
            idToken,
            object : com.amazonaws.mobile.client.Callback<UserStateDetails?> {
                override fun onResult(userStateDetails: UserStateDetails?) {
                    // changeFragment(ProfileFragment.newInstance(0), true)
                    //goto next page
                    printLog("signing in aws success")
                    ThreadUtils.runOnUiThread {
                        hideLoading()
                        //.........goto next page.......\\


                        EventBus.getDefault().post(MessageEvent(getString(R.string.gotonextpage)))
                    }
                }

                override fun onError(e: Exception) {
                    printLog("signing in aws failed =${e.cause}")
                    ThreadUtils.runOnUiThread {
                        hideLoading()
                    }
                }
            }
        )
    }


    ////////////////////// ui functions////////////////
    override fun onResume() {
        super.onResume()
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
    }

    //...........................................................................................!!
    companion object {
        private val ARG_DATA = "position"
        fun newInstance(index: Int): LoginFragment {
            val fragment = LoginFragment()
            val args = Bundle()
            args.putInt(ARG_DATA, index)
            fragment.arguments = args
            return fragment
        }
    }
}