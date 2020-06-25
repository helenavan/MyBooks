package com.helenacorp.android.mybibliotheque.utils

import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

/**
 * Created by helena on 21/03/2018.
 */
class TouchEffect : OnTouchListener {

    /* (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val d = v.background
            d.mutate()
            d.alpha = 150
            v.setBackgroundDrawable(d)
        } else if (event.action == MotionEvent.ACTION_UP
                || event.action == MotionEvent.ACTION_CANCEL) {
            val d = v.background
            d.alpha = 255
            v.setBackgroundDrawable(d)
        }
        return false
    }


}