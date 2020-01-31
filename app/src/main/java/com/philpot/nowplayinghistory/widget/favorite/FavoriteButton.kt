package com.philpot.nowplayinghistory.widget.favorite

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import com.philpot.nowplayinghistory.R
import com.philpot.nowplayinghistory.util.Utils

/**
 * Created by MattPhilpot on 11/30/2017.
 */
class FavoriteButton : ImageView {

    private var mFavorite: Boolean = false
    var mAnimateFavorite: Boolean = true
    private var mAnimateUnfavorite: Boolean = false
    private var mFavoriteResource: Int = FAVORITE_HEART_BLACK
    private var mNotFavoriteResource: Int = FAVORITE_HEART_BORDER_BLACK
    //var mRotationDuration: Int = 0
    //var mRotationAngle: Int = 0
    var mBounceDuration: Int = 0
    //var mColor: Int = 0
    //private var mType: Int = 0

    var mOnFavoriteChangeListener: OnFavoriteChangeListener? = null
    private var mOnFavoriteAnimationEndListener: OnFavoriteAnimationEndListener? = null
    private var mBroadcasting: Boolean = false
    
    /**
     * Returns favorite state.
     *
     * @return true if button is in favorite state, false if not
     */
    /**
     * Changes the favorite state of this button.
     *
     * @param favorite true to favorite the button, false to uncheck it
     */
    // Avoid infinite recursions if setChecked() is called from a listener
    var isFavorite: Boolean
        get() = mFavorite
        set(favorite) {
            if (mFavorite != favorite) {
                mFavorite = favorite
                if (mBroadcasting) {
                    return
                }

                mBroadcasting = true
                mOnFavoriteChangeListener?.onFavoriteChanged(this, mFavorite)
                updateFavoriteButton(favorite)
                mBroadcasting = false
            }
        }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    /**
     * Interface definition for a callback to be invoked when the favorite state is changed.
     */
    interface OnFavoriteChangeListener {
        /**
         * Called when the favorite state is changed.
         *
         * @param buttonView the button view whose state has changed
         * @param favorite the favorite state
         */
        fun onFavoriteChanged(buttonView: FavoriteButton, favorite: Boolean)
    }

    /**
     * Interface definition for a callback to be invoked when the favorite animation ends.
     */
    interface OnFavoriteAnimationEndListener {
        /**
         * Called when the favorite animation ended.
         *
         * @param buttonView the button view whose animation ended
         * @param favorite the favorite state
         */
        fun onAnimationEnd(buttonView: FavoriteButton, favorite: Boolean)
    }

    /**
     * Set a listener will be called when the favorite state is changed.
     *
     * @param listener the [FavoriteButton.OnFavoriteAnimationEndListener] will be
     * called
     */
    fun setOnFavoriteAnimationEndListener(listener: OnFavoriteAnimationEndListener) {
        mOnFavoriteAnimationEndListener = listener
    }

    /**
     * Initialize the default values
     *
     *  * size = 48 dp
     *  * padding = 12 dp
     *  * is mFavorite = false
     *  * animated = true
     *  * default drawables - stars
     *  * rotation duration = 300 ms
     *  * rotation angle = 360 degrees
     *  * bounce duration = 300 ms
     *  * color of default icon = black
     *  * type of default icon = star
     *
     */
    private fun init(context: Context, attrs: AttributeSet?) {
        mFavorite = DEFAULT_FAVORITE
        mAnimateFavorite = DEFAULT_ANIMATE_FAVORITE
        mAnimateUnfavorite = DEFAULT_ANIMATE_UNFAVORITE
        mFavoriteResource = FAVORITE_HEART_BLACK
        mNotFavoriteResource = FAVORITE_HEART_BORDER_BLACK
        //mRotationDuration = DEFAULT_ROTATION_DURATION
        //mRotationAngle = DEFAULT_ROTATION_ANGLE
        mBounceDuration = DEFAULT_BOUNCE_DURATION
        //mColor = STYLE_BLACK
        //mType = STYLE_HEART
        if (!isInEditMode) {
            if (attrs != null) {
                initAttributes(context, attrs)
            }
            setOnClickListener { toggleFavorite() }
        }
        if (mFavorite) {
            setImageResource(mFavoriteResource)
        } else {
            setImageResource(mNotFavoriteResource)
        }
    }

    private fun initAttributes(context: Context, attributeSet: AttributeSet) {
        getTypedArray(context, attributeSet, R.styleable.FavoriteButton)?.let { attr ->
            try {
                //mButtonSize = Utils.dpToPx(attr.getInt(R.styleable.FavoriteButton_favbutton_size, DEFAULT_BUTTON_SIZE).toFloat(), resources)
                mAnimateFavorite = attr.getBoolean(R.styleable.FavoriteButton_favbutton_animate_favorite, mAnimateFavorite)
                mAnimateUnfavorite = attr.getBoolean(R.styleable.FavoriteButton_favbutton_animate_unfavorite, mAnimateUnfavorite)
                //mPadding = Utils.dpToPx(attr.getInt(R.styleable.FavoriteButton_favbutton_padding, DEFAULT_PADDING).toFloat(), resources)

                mFavoriteResource = attr.getResourceId(R.styleable.FavoriteButton_favbutton_favorite_image, FAVORITE_HEART_BLACK)
                mNotFavoriteResource = attr.getResourceId(R.styleable.FavoriteButton_favbutton_not_favorite_image, FAVORITE_HEART_BORDER_BLACK)

                //mRotationDuration = attr.getInt(R.styleable.FavoriteButton_favbutton_rotation_duration, mRotationDuration)
                //mRotationAngle = attr.getInt(R.styleable.FavoriteButton_favbutton_rotation_angle, mRotationAngle)
                mBounceDuration = attr.getInt(R.styleable.FavoriteButton_favbutton_bounce_duration, mBounceDuration)
            } finally {
                attr.recycle()
            }
        }
    }

    private fun getTypedArray(context: Context, attributeSet: AttributeSet, attr: IntArray): TypedArray? {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0)
    }

    /**
     * Toggle the favorite state of this button.
     */
    fun toggleFavorite() {
        isFavorite = !mFavorite
    }

    /**
     * Toggle the favorite state of this button.
     *
     * @param animated true to force animated toggle, false to force not animated one
     */
    fun toggleFavorite(animated: Boolean) {
        if (!mFavorite) {
            val orig = mAnimateFavorite
            mAnimateFavorite = animated
            isFavorite = !mFavorite
            mAnimateFavorite = orig
        } else {
            val orig = mAnimateUnfavorite
            mAnimateUnfavorite = animated
            isFavorite = !mFavorite
            mAnimateUnfavorite = orig
        }
    }

    private fun updateFavoriteButton(favorite: Boolean) {
        if (favorite) {
            if (mAnimateFavorite) {
                animateButton(favorite)
            } else {
                super.setImageResource(mFavoriteResource)
                mOnFavoriteAnimationEndListener?.onAnimationEnd(this, mFavorite)
            }
        } else {
            if (mAnimateUnfavorite) {
                animateButton(favorite)
            } else {
                super.setImageResource(mNotFavoriteResource)
                mOnFavoriteAnimationEndListener?.onAnimationEnd(this, mFavorite)
            }
        }
    }

    private fun animateButton(toFavorite: Boolean) {
        //val startAngle = 0
        //val endAngle: Int
        val startBounce: Float
        val endBounce: Float
        if (toFavorite) {
            //endAngle = mRotationAngle
            startBounce = 0.2f
            endBounce = 1.0f
        } else {
            //endAngle = -mRotationAngle
            startBounce = 1.3f
            endBounce = 1.0f
        }
        val animatorSet = AnimatorSet()
        /*
        val rotationAnim = ObjectAnimator.ofFloat(this, "rotation", startAngle.toFloat(), endAngle.toFloat())
        rotationAnim.duration = mRotationDuration.toLong()
        rotationAnim.interpolator = ACCELERATE_INTERPOLATOR
        */

        val bounceAnimX = ObjectAnimator.ofFloat(this, "scaleX", startBounce, endBounce)
        bounceAnimX.duration = mBounceDuration.toLong()
        bounceAnimX.interpolator = OVERSHOOT_INTERPOLATOR

        val bounceAnimY = ObjectAnimator.ofFloat(this, "scaleY", startBounce, endBounce)
        bounceAnimY.duration = mBounceDuration.toLong()
        bounceAnimY.interpolator = OVERSHOOT_INTERPOLATOR
        bounceAnimY.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                if (mFavorite) {
                    setImageResource(mFavoriteResource)
                } else {
                    setImageResource(mNotFavoriteResource)
                }
            }
        })

        animatorSet.play(bounceAnimX).with(bounceAnimY)

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mOnFavoriteAnimationEndListener?.onAnimationEnd(this@FavoriteButton, mFavorite)
            }
        })

        animatorSet.start()
    }

    companion object {
        private val DEFAULT_FAVORITE = false
        private val DEFAULT_ANIMATE_FAVORITE = true
        private val DEFAULT_ANIMATE_UNFAVORITE = false
        private val DEFAULT_ROTATION_DURATION = 400
        private val DEFAULT_ROTATION_ANGLE = 360
        private val DEFAULT_BOUNCE_DURATION = 300
        private val FAVORITE_HEART_BLACK = R.drawable.ic_favorite_black
        private val FAVORITE_HEART_BORDER_BLACK = R.drawable.ic_non_favorite_black
        private val ACCELERATE_INTERPOLATOR = AccelerateInterpolator()
        private val OVERSHOOT_INTERPOLATOR = OvershootInterpolator(4f)
    }
}