package com.ladstech.cloudgalleryapp.activities

import android.content.Intent

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils


import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.UserCloudGallery
import com.ladstech.cloudgalleryapp.adapters.AdapterAllUsers
import com.ladstech.cloudgalleryapp.databinding.ActivityMainBinding
import com.ladstech.cloudgalleryapp.models.ModelAdapeterConnectionsCloudUserInfo


class MainActivity : BaseActivity() {
    private var mFragmentManager: FragmentManager? = null
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var rv: RecyclerView
    private lateinit var adapterAllUsers: AdapterAllUsers
    private var dataListAllUsers = ArrayList<ModelAdapeterConnectionsCloudUserInfo>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initRv()
        mBinding.btnTestAct.setOnClickListener {
            val mainIntent = Intent(this, TestActivity::class.java)
            startActivity(mainIntent)
        }
        mBinding.button2.setOnClickListener {
            sessionManager.clearSession()
            printLog("data store clearing.....")
            showLoading()
            Amplify.DataStore.clear({
                hideLoading()
                printLog("data store cleared")
                val mainIntent = Intent(this, SplashActivity::class.java)
                startActivity(mainIntent)
            }, {
                hideLoading()
                printLog("data store cleared failed")

            })
        }

        mBinding.button3.setOnClickListener {


            Amplify.DataStore.query(UserCloudGallery::class.java, Where.id(sessionManager.user.id),
                { matches ->
                    if (matches.hasNext()) {
                        val original = matches.next()
                        val edited = original.copyOfBuilder()
                            .name(mBinding.etName.text.toString())
                            .build()
                        Amplify.DataStore.save(edited,
                            {
                                printLog("updated.....")

                                for (i in 0..dataListAllUsers.size) {
                                    if (dataListAllUsers[i].userCloudGallery.id == original.id) {
                                        dataListAllUsers[i].userCloudGallery = edited
                                        ThreadUtils.runOnUiThread {
                                            adapterAllUsers.notifyDataSetChanged()
                                        }

                                    }
                                }

//                                dataListAllUsers.forEach { userCloudGallery ->
//
//                                    if (userCloudGallery.id == original.id) {
//
//                                        ThreadUtils.runOnUiThread {
//                                            adapterAllUsers.notifyDataSetChanged()
//                                        }
//                                    }
//                                }
                            },
                            { printLog("updtaion failed ${it.cause}") }
                        )
                    }
                },
                { printLog("query faiold.....") }
            )
        }
//        mBinding.button4.setOnClickListener {
//            val postUser = UserCloudGallery.builder()
//                .name(mBinding.etName.text.toString())
//                .phone("0000")
//                .deviceToken("devicetokeennnn")
//                .createdTime("ceratetd TIme")
//                .isPublic(true)
//                .about("abouttt")
//                .image("imageeee")
//                .build()
//
//
//
//            Amplify.DataStore.save(postUser,
//                {
//                    printLog("new user added....")
//                    ThreadUtils.runOnUiThread {
//                        dataListAllUsers.add(postUser)
//                        adapterAllUsers.notifyDataSetChanged()
//                    }
//
//                },
//                { printLog("new user add failed= ${it.cause}...") }
//            )
//        }
//        mBinding.button5.setOnClickListener {
//            com.ladstech.cloudgalleryapp.utils.Helper.startActivity(
//                this@MainActivity,
//                Intent(this@MainActivity, GalleryAppHome::class.java),
//                false
//            )
//        }
    }

    private fun initRv() {
        dataListAllUsers.clear()
        rv = mBinding.rv
        adapterAllUsers = AdapterAllUsers(this, dataListAllUsers)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapterAllUsers
        adapterAllUsers.notifyDataSetChanged()

//        ///reading one time
//        Amplify.DataStore.query(UserCloudGallery::class.java,
//            { users ->
//                while (users.hasNext()) {
//                    val user = users.next()
////                    Log.i(TAG, "User Name: ${user.name}")
//
//
//                    if (user.id != sessionManager.user.id)
//                        dataListAllUsers.add(user)
//
//                    ThreadUtils.runOnUiThread {
//                        adapterAllUsers.notifyDataSetChanged()
//                    }
//
//                }
//            },
//            { Log.e(TAG, "Query failed", it) }
//        )
//        val subscription = Amplify.API.subscribe(
//            ModelSubscription.onCreate(UserCloudGallery::class.java),
//            { Log.i(TAG, "Subscription established") },
//            {
//                Log.i(
//                    TAG,
//                    "Todo create subscription received: ${(it.data as UserCloudGallery).name}"
//                )
////                dataListAllUsers.add(it.data as UserCloudGallery)
////                ThreadUtils.runOnUiThread {
////                    adapterAllUsers.notifyDataSetChanged()
////                }
//
//
//            },
//            { Log.e(TAG, "Subscription failed", it) },
//            { Log.i(TAG, "Subscription completed") }
//        )
//        subscription?.start()


        Amplify.DataStore.observe(UserCloudGallery::class.java,
            {
                Log.d(TAG, "Observation began")
            },
            {
                val user = it.item()


                Log.d(TAG, "${it.type().name} user: $user")
//                dataListAllUsers.add(user)
//                ThreadUtils.runOnUiThread {
//                    adapterAllUsers.notifyDataSetChanged()
//                }

            },
            { Log.d(TAG, "Observation failed", it) },
            { Log.d(TAG, "Observation complete") }
        )


    }


//    private fun changeFragment(fragment: Fragment, needToAddBackstack: Boolean) {
//        val mFragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
//        mFragmentTransaction.replace(R.id.mainActivityFragmentContainer, fragment)
//        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//        if (needToAddBackstack) mFragmentTransaction.addToBackStack(null)
//        mFragmentTransaction.commit()
//    }
}
