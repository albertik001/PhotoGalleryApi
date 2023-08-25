package org.tredo.photogalleryapi.presentation.ui.extensions

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController


fun NavController.safeNavigation(@IdRes actionId: Int) {
    currentDestination?.getAction(actionId)?.let { navigate(actionId) }
}


fun NavController.safeNavigation(
    @IdRes actionId: Int,
    args: Bundle? = null,
    navOptionsBuilder: () -> NavOptions,
) {
    currentDestination?.getAction(actionId)?.let { navigate(actionId, args, navOptionsBuilder()) }
}

fun NavController.safeNavigation(
    @IdRes actionId: Int,
    args: Bundle,
) {
    currentDestination?.getAction(actionId)?.let { navigate(actionId, args) }
}

fun NavController.safeNavigation(
    @IdRes actionId: Int,
    navOptions: NavOptions
) {
    currentDestination?.getAction(actionId)
        ?.let {
            navigate(actionId, null, navOptions = navOptions)
        }
}

fun NavController.directionsSafeNavigation(directions: NavDirections) {
    currentDestination?.getAction(directions.actionId)?.let { navigate(directions) }
}

fun NavController.directionsSafeNavigation(
    directions: NavDirections,
    actionBeforeNavigation: () -> Unit
) {
    currentDestination?.getAction(directions.actionId)
        ?.let {
            navigate(directions)
            actionBeforeNavigation()
        }
}

fun Fragment.flowNavController(@IdRes navHostId: Int) = requireActivity().findNavController(
    navHostId
)

fun <T : Any> Fragment.setBackStackData(key: String, data: T?, doBack: Boolean) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, data)
    if (doBack) {
        findNavController().navigateUp()
    }
}

fun <T : Any> Fragment.getBackStackData(key: String, result: (T) -> (Unit)) {
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)
        ?.observe(viewLifecycleOwner) {
            result(it)
        }
}
