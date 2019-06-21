package com.alessandrodefrancesco.androidutils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList

/**
 * The distance between two points
 */
fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double {

    return Math.sqrt(((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)))
}

/**
 * The distance between two points
 */
fun distance(x1: Int, y1: Int, x2: Int, y2: Int): Double {
    return distance(x1.toDouble(), y1.toDouble(), x2.toDouble(), y2.toDouble())
}

/**
 * The distance between two points
 */
fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Double {
    return distance(x1.toDouble(), y1.toDouble(), x2.toDouble(), y2.toDouble())
}

/**
 * Return the fibonacci sequence numbers until [upperBound]
 */
fun fibonacci(upperBound: Int): List<Int>{
    val list = ArrayList<Int>()
    list.add(0)
    list.add(1)
    for (i in 1..upperBound) {
        list.add(list[i-2] + list[i-1])
    }
    return list
}


/**
 * Parse the [json] string into the specified class
 */
inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)

/**
 * Encode the object as JSON string
 */
fun Any.toJson(): String {
    return GSON.toJson(this)
}

/**
 * Returns an exact copy of the object
 * WARNING: VERY SLOW
 */
fun <T : Any> T.deepCopy(): T {
    return GSON.fromJson(GSON.toJson(this), this.javaClass)
}

/**
 * Capitalize each word in the string
 * Ex: "hi all, i'm a repository" -> "Hi All, I'm A Repository"
 */
fun String.capitalizeEachWord(): String {
    return this.toLowerCase(Locale.UK).split(' ').joinToString(" ") { it.capitalize() }
}

/**
 * Return [min] if the number is < [min]
 * [max] if the number is > [max]
 * the number itself otherwise
 */
fun Int.clamp(min: Int, max: Int): Int {
    return when {
        this < min -> min
        this > max -> max
        else -> this
    }
}

/**
 * Return [min] if the number is < [min]
 * [max] if the number is > [max]
 * the number itself otherwise
 */
fun Double.clamp(min: Double, max: Double): Double {
    return when {
        this < min -> min
        this > max -> max
        else -> this
    }
}

/**
 * The TAG to use for debug purposes in Log.d(TAG, "message")
 */
val Any?.TAG: String
    get() {
        var className = "null"
        this?.let {
            className = this::class.java.name.substringAfterLast(".")
        }
        return className
    }

/** Remove the chars in [toRemove] from this String (e.g. "abcd".removeChars("bc") == "ad") */
fun String.removeChars(toRemove: String): String {
    return this.replace(Regex("[$toRemove]"), "")
}