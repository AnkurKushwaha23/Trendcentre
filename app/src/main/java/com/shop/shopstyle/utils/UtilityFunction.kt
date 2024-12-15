package com.shop.shopstyle.utils

import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

fun Fragment.navigateToNextScreen(action: Int, popUpToId: Int) {
    findNavController().navigate(
        action,
        null,
        NavOptions.Builder()
            .setPopUpTo(popUpToId, true)
            .build()
    )
}

fun View.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionMessage: String? = null,
    action: ((View) -> Unit)? = null
) {
    val snackbar = Snackbar.make(this, message, duration)
    if (actionMessage != null && action != null) {
        snackbar.setAction(actionMessage) { action(this) }
    }
    snackbar.show()
}