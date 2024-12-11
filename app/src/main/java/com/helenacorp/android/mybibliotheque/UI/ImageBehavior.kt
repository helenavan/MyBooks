package com.helenacorp.android.mybibliotheque.UI

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.helenacorp.android.mybibliotheque.R

/**
 * Created by helena on 19/01/2018.
 */
class ImageBehavior(private val mContext: Context, attrs: AttributeSet?) : CoordinatorLayout.Behavior<ImageView>() {
    private var mAvatarMaxSize = 0f
    private val mFinalLeftAvatarPadding: Float
    private val mStartPosition = 0f
    private var mStartXPosition = 0
    private var mStartToolbarPosition = 0f
    private val mChangeBehaviorPoint = 0f
    private fun init() {
        bindDimensions()
    }

    private fun bindDimensions() {
        mAvatarMaxSize = mContext.resources.getDimension(R.dimen.image_width)
    }

    private var mStartYPosition = 0
    private var mFinalYPosition = 0
    private var finalHeight = 0
    private var mStartHeight = 0
    private var mFinalXPosition = 0

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun layoutDependsOn(parent: CoordinatorLayout, child: ImageView, dependency: View): Boolean {
        return dependency is Toolbar
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: ImageView, dependency: View): Boolean {
        maybeInitProperties(child, dependency)
        val maxScrollDistance = mStartToolbarPosition.toInt()
        val expandedPercentageFactor = dependency.y / maxScrollDistance
        if (expandedPercentageFactor < mChangeBehaviorPoint) {
            val distanceYToSubtract = ((mStartYPosition - mFinalYPosition)
                    * (1f - expandedPercentageFactor)) + child.height / 2
            val distanceXToSubtract = ((mStartXPosition - mFinalXPosition)
                    * (1f - expandedPercentageFactor)) + child.width / 2
            val heightToSubtract = (mStartHeight - finalHeight) * (1f - expandedPercentageFactor)
            child.y = mStartYPosition - distanceYToSubtract
            child.x = mStartXPosition - distanceXToSubtract

            // int proportionalAvatarSize = (int) (mAvatarMaxSize * (expandedPercentageFactor));
            val lp = child.layoutParams as CoordinatorLayout.LayoutParams
            lp.width = (mStartHeight - heightToSubtract).toInt()
            lp.height = (mStartHeight - heightToSubtract).toInt()
            child.layoutParams = lp
        } else {
            val distanceYToSubtract = ((mStartYPosition - mFinalYPosition)
                    * (1f - expandedPercentageFactor)) + mStartHeight / 2
            child.x = mStartXPosition - child.width / 2.toFloat()
            child.y = mStartYPosition - distanceYToSubtract
            val lp = child.layoutParams as CoordinatorLayout.LayoutParams
            lp.width = mStartHeight
            lp.height = mStartHeight
            child.layoutParams = lp
        }
        return true
    }

    @SuppressLint("PrivateResource")
    private fun maybeInitProperties(child: ImageView, dependency: View) {
        if (mStartYPosition == 0) mStartYPosition = dependency.y.toInt()
        if (mFinalYPosition == 0) mFinalYPosition = dependency.height / 2
        if (mStartHeight == 0) mStartHeight = child.height
        if (finalHeight == 0) finalHeight = mContext.resources.getDimensionPixelOffset(R.dimen.image_small_width)
        if (mStartXPosition == 0) mStartXPosition = (child.x + child.width / 2).toInt()
        if (mFinalXPosition == 0) mFinalXPosition = mContext.resources.getDimensionPixelOffset(R.dimen.fab_margin) + finalHeight / 2
        if (mStartToolbarPosition == 0f) mStartToolbarPosition = dependency.y + dependency.height / 2
    }

    val statusBarHeight: Int
        get() {
            var result = 0
            val resourceId = mContext.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = mContext.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

    companion object {
        private const val MIN_AVATAR_PERCENTAGE_SIZE = 0.3f
        private const val EXTRA_FINAL_AVATAR_PADDING = 80
        private const val TAG = "behavior"
    }

    init {
        init()
        mFinalLeftAvatarPadding = mContext.resources.getDimension(R.dimen.activity_horizontal_margin)
    }
}