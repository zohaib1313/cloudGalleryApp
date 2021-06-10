package com.ladstech.cloudgalleryapp.fragments

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*

import androidx.appcompat.app.AlertDialog
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.databinding.FragmentLoginBinding


class LoginFragment : BaseFragment() {

    lateinit var mBinding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentLoginBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        mBinding.textView.setOnClickListener {
            showCustomAppAlert(requireActivity())
        }
        return mBinding.root
    }


    fun showCustomAppAlert(activity: Activity) {


        val layout = activity.layoutInflater.inflate(
            R.layout.send_request_layout,
            activity.findViewById(R.id.container)
        )
        val dialog = AlertDialog.Builder(activity)
            .setView(layout)
            .create()
      //  val layoutParams = dialog.window!!.attributes
      //  layoutParams.y = 160 // top margin
        //dialog.window!!.attributes = layoutParams
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
       // dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
       // dialog.window!!.setGravity(Gravity.TOP)
        dialog.show()
//        val timer2 = Timer()
//        timer2.schedule(object : TimerTask() {
//            override fun run() {
//                dialog.dismiss()
//                timer2.cancel() //this will cancel the timer of the system
//            }
//        }, 2000)
    }


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