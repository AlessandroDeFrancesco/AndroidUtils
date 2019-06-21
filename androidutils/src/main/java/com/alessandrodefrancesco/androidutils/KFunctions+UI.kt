package com.alessandrodefrancesco.androidutils

import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

/**
 * Run the [action] on the UI thread after [delay]
 * @param delay in milliseconds
 * @param action the action to perform
 */
fun runOnUiThread(delay: Long? = null, action: () -> Unit) {
    val mHandler = Handler(Looper.getMainLooper())
    if (delay != null)
        mHandler.postDelayed(action, delay)
    else
        mHandler.post(action)
}

/** Retrieve the position of a [MotionEvent] (for example a touch event), relative to a view */
fun View.fromTouchPointToViewPosition(event: MotionEvent): Point {
    val rect = Rect()
    this.getGlobalVisibleRect(rect)
    val screenX = event.rawX
    val screenY = event.rawY
    val viewX = screenX - rect.left
    val viewY = screenY - rect.top
    return Point(viewX.toInt(), viewY.toInt())
}

/**
 * Find the view in the [list] that has been touched
 * @param list the list of views to check
 * @param touchedPoint the point in screen coordinate that has been touched
 * @param touchRadius how large is the touch
 */
fun findViewContainingPoint(list: List<View>, touchedPoint: Point, touchRadius: Int = 1): View? {
    for (view in list) {
        val viewRect = Rect()
        view.getGlobalVisibleRect(viewRect)
        val touchedRect =
            Rect(touchedPoint.x, touchedPoint.y, touchedPoint.x + touchRadius, touchedPoint.y + touchRadius)
        if (viewRect.intersect(touchedRect)) {
            return view
        }
    }

    return null
}

/**
 * Return all children views of the view
 */
val ViewGroup.allChildren: Sequence<View>
    get() {
        val list = ArrayList<View>()
        for (i in 0 until this.childCount) {
            list.add(this.getChildAt(i))
        }

        return list.asSequence()
    }
