package dev.gbenga.dsagithub.features

import androidx.lifecycle.SavedStateHandle
import dev.gbenga.dsagithub.features.details.DetailRepository
import dev.gbenga.dsagithub.features.details.DetailRepositoryImpl
import dev.gbenga.dsagithub.features.details.DetailViewModel
import dev.gbenga.dsagithub.features.home.FavouriteRepository
import dev.gbenga.dsagithub.features.home.FavouriteRepositoryImpl
import dev.gbenga.dsagithub.features.home.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val featureModule = module{
    single { CoroutineScope(Dispatchers.IO + SupervisorJob()) }
    singleOf(::FavouriteRepositoryImpl) bind FavouriteRepository::class
    singleOf(::DetailRepositoryImpl) bind DetailRepository::class
    viewModel { (handle: SavedStateHandle) -> HomeViewModel(get(), handle) }
    viewModel{ DetailViewModel(get(), get()) }
}