package com.ladstech.cloudgalleryapp.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.UserStateDetails
import com.amplifyframework.core.Amplify
import com.goodiebag.pinview.Pinview
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.hbb20.CountryCodePicker
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.utils.AppConstant
import com.ladstech.cloudgalleryapp.utils.AppConstant.Companion.TAG
import kotlinx.android.synthetic.main.fragment_login_hammad.*

import java.util.concurrent.TimeUnit


class FragmentLoginRegisterHammad : Fragment() {

    //if code sending failed
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private var vCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerificationId: String? = null
    private lateinit var firebaseAuth: FirebaseAuth

    //Progress Dialog
    private lateinit var progressDialog: ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login_hammad, container, false)

        val phoneLinearLayout = view.findViewById<LinearLayout>(R.id.phoneLl)
        val otpLinearLayout = view.findViewById<LinearLayout>(R.id.codeLl)
        otpLinearLayout.visibility = View.GONE
        val bottomAanim =
            AnimationUtils.loadAnimation(requireContext(), R.anim.bottom_animation)
        phoneLinearLayout.animation = bottomAanim

        val phoneContinueButton = view.findViewById<Button>(R.id.phoneContinueButton)
        val codeSubmitButton = view.findViewById<Button>(R.id.codeSubmitButton)
        val resendCodeTv = view.findViewById<TextView>(R.id.resendCodeTv)
        val codeEt = view.findViewById<TextView>(R.id.codeEt)
        val pinView = view.findViewById<Pinview>(R.id.pinview)
        val etPhoneNumber = view.findViewById<EditText>(R.id.etPhoneNumber)
        val etCountryCode = view.findViewById<CountryCodePicker>(R.id.etCountryCode)

        val codeSentDescTv = view.findViewById<TextView>(R.id.codeSentDescTv)

        phoneContinueButton.setOnClickListener {
            val phoneNumber =
                etCountryCode.selectedCountryCodeWithPlus + etPhoneNumber.text.toString()
                    .trim()
            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(requireContext(), "Please enter phone number", Toast.LENGTH_SHORT)
                    .show()
            } else {
                startPhoneNumberVerification(phoneNumber)
            }
        }

        codeSubmitButton.setOnClickListener {
            val verificationCode = pinView.value
            if (TextUtils.isEmpty(verificationCode)) {
                Toast.makeText(
                    requireContext(),
                    "Please enter verification Code",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                verifyPhoneNumberThroughCode(mVerificationId, verificationCode)
            }
        }

        resendCodeTv.setOnClickListener {
            val phoneNumber =
                etCountryCode.selectedCountryCodeWithPlus + etPhoneNumber.text.toString()
                    .trim()
            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(requireContext(), "Please enter phone number", Toast.LENGTH_SHORT)
                    .show()
            } else {
                resendVerificationCode(phoneNumber, forceResendingToken)
            }
        }

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)
        vCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

                Log.d(TAG, "onVerificationCompleted: ")
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {

                Log.d(TAG, "onVerificationFailed: ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent: ")
                mVerificationId = verificationId
                forceResendingToken = token
                progressDialog.dismiss()
                phoneLinearLayout.visibility = View.GONE
                otpLinearLayout.visibility = View.VISIBLE

                Toast.makeText(requireContext(), "Verification Code Sent", Toast.LENGTH_SHORT)
                    .show()
                val phoneNumber =
                    etCountryCode.selectedCountryCodeWithPlus + etPhoneNumber.text.toString()
                        .trim()
                codeSentDescTv.text =
                    "Please type the verification code we sent to ${phoneNumber.toString().trim()}"
            }

        }
        return view
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {

        Log.d(TAG, "startPhoneNumberVerification: $phoneNumber")
        progressDialog.setMessage("Verifying Phone Number...")
        progressDialog.show()

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

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {

        progressDialog.setMessage("Resending Code...")
        progressDialog.show()

        Log.d(TAG, "resendVerificationCode: $phoneNumber")

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

    private fun verifyPhoneNumberThroughCode(verificationId: String?, code: String) {
        Log.d(TAG, "verifyPhoneNumberThroughCode: $verificationId $code")
        progressDialog.setMessage("Verifying Code...")
        progressDialog.show()

        val credential = PhoneAuthProvider.getCredential(verificationId.toString(), code)
        signInWithPhoneAuthCredential(credential)

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d(TAG, "signInWithPhoneAuthCredential: ")

        progressDialog.setMessage("Logging In")
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                //login success
                progressDialog.dismiss()
                val phoneNumber = firebaseAuth.currentUser?.phoneNumber
                Toast.makeText(requireContext(), "Logged In as " + phoneNumber, Toast.LENGTH_SHORT)
                    .show()

                val mUser = FirebaseAuth.getInstance().currentUser
                mUser!!.getIdToken(true)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val idToken = task.result?.token
                            Log.d(TAG, "aws token = ${idToken.toString()}")
                            sendOpenIDToAWS(idToken)
                        }
                    }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendOpenIDToAWS(idToken: String?) {

        val mobileClient =
            Amplify.Auth.getPlugin("awsCognitoAuthPlugin").escapeHatch as AWSMobileClient?
        mobileClient?.federatedSignIn(
            // aws provider URL
            AppConstant.IAMPROVIDER,
            idToken,
            object : com.amazonaws.mobile.client.Callback<UserStateDetails?> {
                override fun onResult(userStateDetails: UserStateDetails?) {
                    Log.d(TAG, "sign in success")
//                    startActivity(Intent(requireContext(), TestActivity::class.java))
//                    requireActivity().finishAffinity()


//                    changeFragment(ProfileFragment.newInstance(0), true)

                }

                override fun onError(e: Exception?) {

                    Log.d(TAG, "sign-in error", e)
                }
            }
        )
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