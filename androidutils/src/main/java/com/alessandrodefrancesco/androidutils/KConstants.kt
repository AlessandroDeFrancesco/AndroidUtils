package com.alessandrodefrancesco.androidutils

import com.google.gson.Gson
import java.util.*

/** GSON global instance */
val GSON = Gson()

/** The current year */
val CURRENT_YEAR get() = Calendar.getInstance().get(Calendar.YEAR)
/** The current month as number from 0 (January) to 12 (December) */
val CURRENT_MONTH get() = Calendar.getInstance().get(Calendar.MONTH)
/** The current day as number from 1 (Sunday) to 7 (Saturday) */
val CURRENT_DAY_OF_WEEK get() = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

/** A list containing latin alphabet and arabic numbers */
val alphanumeric = ('a'..'z') + ('A'..'Z') + ('0'..'9')