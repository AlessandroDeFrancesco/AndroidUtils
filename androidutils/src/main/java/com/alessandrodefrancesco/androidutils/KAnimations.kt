package com.alessandrodefrancesco.androidutils

import android.animation.*
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import androidx.annotation.UiThread
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

/**
 * Fade In the View, at the end f the animation the visibility is GONE
 * @param duration the duration of the animation
 * @param endAnimationCallback called when the animation ends
 * To use in conjunction with [animateFadeOut]
 * */
@UiThread
fun View.animateFadeIn(duration: Long, endAnimationCallback: (() -> Unit)? = null): Animator {
    val showAnimator = ObjectAnimator.ofFloat(this, "alpha", alpha, 1f).setDuration(duration)
    showAnimator.interpolator = LinearInterpolator()
    showAnimator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            visibility = View.GONE
            endAnimationCallback?.invoke()
        }
    })

    showAnimator.start()
    return showAnimator
}

/**
 * Fade Out the View, at the start of the animation the visibility is VISIBLE
 * @param duration the duration of the animation
 * @param endAnimationCallback called when the animation ends
 * To use in conjunction with [animateFadeIn]
 * */
@UiThread
fun View.animateFadeOut(duration: Long, endAnimationCallback: (() -> Unit)? = null): Animator {
    visibility = View.VISIBLE
    val hideAnimator = ObjectAnimator.ofFloat(this, "alpha", alpha, 1f).setDuration(duration)
    hideAnimator.interpolator = LinearInterpolator()
    hideAnimator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            endAnimationCallback?.invoke()
        }
    })

    hideAnimator.start()
    return hideAnimator
}

/**
 * Hide the View with a shrinking animation, at the end f the animation the visibility is GONE
 * @param removeFromParent when true and the animation ends the View will be removed from the parent ViewGroup
 * @param endAnimationCallback called when the animation ends
 * To use in conjunction with [animateAppear]
 * */
@UiThread
fun View.animateDisappear(removeFromParent: Boolean = false, endAnimationCallback: (() -> Unit)? = null): Animator {
    val hideAnimator = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0f).setDuration(350)
    hideAnimator.startDelay = 50
    hideAnimator.interpolator = AnticipateInterpolator(2.0f)

    val thisView = this
    hideAnimator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            visibility = View.GONE
            if (removeFromParent)
                (parent as ViewGroup).removeView(thisView)
            endAnimationCallback?.invoke()
        }
    })

    hideAnimator.start()
    return hideAnimator
}

/**
 * Show the View with an enlarge animation, at the start of the animation the visibility is VISIBLE
 * @param endAnimationCallback called when the animation ends
 * To use in conjunction with [animateDisappear]
 * */
@UiThread
fun View.animateAppear(endAnimationCallback: (() -> Unit)? = null): Animator {
    visibility = View.VISIBLE

    val appearAnimator = ObjectAnimator.ofFloat(this, "scaleX", 0f, 1f).setDuration(350)
    appearAnimator.interpolator = OvershootInterpolator(2.0f)

    appearAnimator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            endAnimationCallback?.invoke()
        }
    })

    appearAnimator.start()
    return appearAnimator
}

/**
 * Hide the View with a shrinking animation, at the end f the animation the visibility is GONE
 * @param duration the duration of the animation
 * @param ascendPixels how many pixels to ascend before disappearing
 * @param endAnimationCallback called when the animation ends
 * To use in conjunction with [animateAppear]
 * */
@UiThread
fun View.animateFlipAscendDisappear(
    duration: Long = 1500,
    ascendPixels: Int = 100,
    endAnimationCallback: (() -> Unit)? = null
) {
    if (duration <= 0)
        return

    val flipDuration = 100L
    val flipAnimator = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0f).setDuration(flipDuration)
    flipAnimator.repeatMode = ObjectAnimator.REVERSE
    flipAnimator.repeatCount = (duration / flipDuration).toInt()
    flipAnimator.interpolator = AccelerateInterpolator()

    val ascendAnimator = ObjectAnimator.ofFloat(this, "translationY", translationY, translationY - ascendPixels)
        .setDuration(duration)
    ascendAnimator.interpolator = AccelerateDecelerateInterpolator()

    val animatorSet = AnimatorSet()
    animatorSet.playTogether(flipAnimator, ascendAnimator)
    animatorSet.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            animateDisappear(endAnimationCallback = endAnimationCallback)
        }
    })
    animatorSet.start()
}

/**
 * Flip the View one time
 * @param duration the duration of the animation
 * @param middleAnimationCallback called when the animation is at the middle
 * @param endAnimationCallback called when the animation ends
 */
@UiThread
fun View.animateFlip(
    duration: Long = 500,
    middleAnimationCallback: (() -> Unit)? = null,
    endAnimationCallback: (() -> Unit)? = null
) {
    val firstHalfAnim = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0f).setDuration(duration / 2)
    val secondHalfAnim = ObjectAnimator.ofFloat(this, "scaleX", 0f, 1f).setDuration(duration / 2)
    firstHalfAnim.interpolator = AnticipateInterpolator()
    secondHalfAnim.interpolator = OvershootInterpolator()

    firstHalfAnim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            middleAnimationCallback?.invoke()
            secondHalfAnim.start()
        }
    })

    secondHalfAnim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            endAnimationCallback?.invoke()
        }
    })

    firstHalfAnim.start()
}

/**
 * Pulse the View indefinitely
 * @param pulseDuration the duration of the pulse animation before repeating
 */
@UiThread
fun View.animateInfinitePulse(
    pulseDuration: Long = 500
): ObjectAnimator {
    val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat("scaleX", 1.1f),
        PropertyValuesHolder.ofFloat("scaleY", 1.1f)
    )
    scaleDown.duration = pulseDuration
    scaleDown.interpolator = FastOutSlowInInterpolator()
    scaleDown.repeatCount = ObjectAnimator.INFINITE
    scaleDown.repeatMode = ObjectAnimator.REVERSE

    scaleDown.start()
    return scaleDown
}

/**
 * Rotate the View on the Y axis
 * @param duration the duration of the animation
 * @param middleAnimationCallback called when the animation is at the middle
 * @param endAnimationCallback called when the animation ends
 * @param rotation the degrees of Y rotation at the end of the animation
 */
@UiThread
fun View.animateRotateOnY(
    duration: Long = 500,
    rotation: Float,
    middleAnimationCallback: (() -> Unit)? = null,
    endAnimationCallback: (() -> Unit)? = null
) {
    val firstHalfAnim = ObjectAnimator.ofFloat(this, "rotationY", rotationY, rotation / 2).setDuration(duration)
    val secondHalfAnim = ObjectAnimator.ofFloat(this, "rotationY", rotation / 2, rotation).setDuration(duration / 2)
    firstHalfAnim.interpolator = AnticipateInterpolator()
    secondHalfAnim.interpolator = OvershootInterpolator()

    firstHalfAnim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            middleAnimationCallback?.invoke()
            secondHalfAnim.start()
        }
    })

    secondHalfAnim.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            endAnimationCallback?.invoke()
        }
    })

    firstHalfAnim.start()
}

/**
 * Compress and expand the View on the Y axis
 * @param duration the duration of the animation
 * @param middleAnimationCallback called when the animation is at the middle
 * @param endAnimationCallback called when the animation ends
 */
@UiThread
fun View.animateCompressAndExpand(
    duration: Long = 1000,
    middleAnimationCallback: (() -> Unit)? = null,
    endAnimationCallback: (() -> Unit)? = null
) {
    val firstHalfAnim1 = ObjectAnimator.ofFloat(this, "scaleX", 1f, 1.3f).setDuration(duration / 2)
    firstHalfAnim1.interpolator = AnticipateInterpolator(4f)
    val firstHalfAnim2 = ObjectAnimator.ofFloat(this, "scaleY", 1f, 0.3f).setDuration(duration / 2)
    firstHalfAnim2.interpolator = AnticipateInterpolator(4f)

    val secondHalfAnim1 = ObjectAnimator.ofFloat(this, "scaleX", 1.3f, 1f).setDuration(duration / 2)
    secondHalfAnim1.interpolator = OvershootInterpolator()
    val secondHalfAnim2 = ObjectAnimator.ofFloat(this, "scaleY", 0.3f, 1f).setDuration(duration / 2)
    secondHalfAnim2.interpolator = OvershootInterpolator()

    val firstHalfAnimSet = AnimatorSet()
    firstHalfAnimSet.playTogether(firstHalfAnim1, firstHalfAnim2)

    val secondHalfAnimSet = AnimatorSet()
    secondHalfAnimSet.playTogether(secondHalfAnim1, secondHalfAnim2)

    firstHalfAnimSet.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            middleAnimationCallback?.invoke()
            secondHalfAnimSet.start()
        }
    })

    secondHalfAnimSet.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            endAnimationCallback?.invoke()
        }
    })

    firstHalfAnimSet.start()
}

/**
 * Animate the View with a bounce
 * @param duration the duration of the animation
 * @param endAnimationCallback called when the animation ends
 */
@UiThread
fun View.animateBounce(
    duration: Long = 1000,
    endAnimationCallback: (() -> Unit)? = null
) {
    val anim1 = ObjectAnimator.ofFloat(this, "scaleX", 1.5f, 1.0f).setDuration(duration / 2)
    anim1.interpolator = AnticipateInterpolator()
    val anim2 = ObjectAnimator.ofFloat(this, "scaleY", 1.5f, 1.0f).setDuration(duration / 2)
    anim2.interpolator = AnticipateInterpolator()

    val animSet = AnimatorSet()
    animSet.playTogether(anim1, anim2)

    animSet.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            endAnimationCallback?.invoke()
        }
    })
    animSet.start()
}

/**
 * Move the View with an animation
 * @param x the x position at the end of the animation
 * @param y the y position at the end of the animation
 * @param speed the speed at which the animation should be made (higher values means more speed)
 * @param endAnimationCallback called when the animation ends
 */
@UiThread
fun View.animateMoveToPosition(
    x: Float,
    y: Float,
    speed: Float = 1f,
    endAnimationCallback: (() -> Unit)? = null
) {
    val duration: Long = (distance(x, y, this.x, this.y) / speed).toLong()
    val anim1 = ObjectAnimator.ofFloat(this, "x", this.x, x).setDuration(duration)
    anim1.interpolator = AccelerateDecelerateInterpolator()
    val anim2 = ObjectAnimator.ofFloat(this, "y", this.y, y).setDuration(duration)
    anim2.interpolator = AccelerateDecelerateInterpolator()

    val animSet = AnimatorSet()
    animSet.playTogether(anim1, anim2)

    animSet.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            endAnimationCallback?.invoke()
        }
    })
    animSet.start()
}

