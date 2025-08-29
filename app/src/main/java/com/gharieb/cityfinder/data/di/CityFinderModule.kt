package com.gharieb.cityfinder.data.di

import com.gharieb.cityfinder.data.repo.CityFinderRepoImpl
import com.gharieb.cityfinder.domain.repo.CityFinderIRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoBindings {
    @Binds
    abstract fun bindCityRepository(impl: CityFinderRepoImpl): CityFinderIRepo
}