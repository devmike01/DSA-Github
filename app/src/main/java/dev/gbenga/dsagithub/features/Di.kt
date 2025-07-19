package dev.gbenga.dsagithub.features

import dev.gbenga.dsagithub.features.details.DetailRepository
import dev.gbenga.dsagithub.features.details.DetailRepositoryImpl
import dev.gbenga.dsagithub.features.details.DetailViewModel
import dev.gbenga.dsagithub.features.home.HomeRepository
import dev.gbenga.dsagithub.features.home.HomeRepositoryImpl
import dev.gbenga.dsagithub.features.home.HomeViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featureModule = module{
    singleOf(::HomeRepositoryImpl) bind HomeRepository::class
    singleOf(::DetailRepositoryImpl) bind DetailRepository::class
    viewModel { HomeViewModel(get()) }
    viewModel{ DetailViewModel(get()) }
}