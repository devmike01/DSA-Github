package dev.gbenga.dsagithub.nav

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import kotlin.reflect.KType

inline fun <reified T : Any> NavGraphBuilder.composables(typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
                                                        deepLinks: List<NavDeepLink> = emptyList(),
                                                        noinline enterTransition:
                                                        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
                                                        EnterTransition?)? =
                                                            null,
                                                        noinline exitTransition:
                                                        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
                                                        ExitTransition?)? =
                                                            null,
                                                        noinline popEnterTransition:
                                                        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
                                                        EnterTransition?)? =
                                                            enterTransition,
                                                        noinline popExitTransition:
                                                        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
                                                        ExitTransition?)? =
                                                            exitTransition,
                                                        noinline sizeTransform:
                                                        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
                                                        SizeTransform?)? =
                                                            null,
                                                        noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit){
    //composable<T>()
}