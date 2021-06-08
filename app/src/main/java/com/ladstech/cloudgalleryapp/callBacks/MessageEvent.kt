package com.ladstech.cloudgalleryapp.callBacks

import android.os.Bundle
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MessageEvent(var event: String) {


   public var bundle: Bundle? = null

}