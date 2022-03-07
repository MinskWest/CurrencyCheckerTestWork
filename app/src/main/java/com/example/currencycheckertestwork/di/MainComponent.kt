package com.example.currencycheckertestwork.di

import android.content.Context
import com.example.currencycheckertestwork.presentation.activities.MainActivity
import com.example.currencycheckertestwork.presentation.fragments.CommonFragment
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(
    modules = [UseCasesModule::class, NetworkModule::class, ViewModelModule::class,
        RepositoryModule::class, CurrencySymbolsModule::class]
)
interface MainComponent {

    fun inject(activity: MainActivity)

    fun inject(commonFragment: CommonFragment)

    @Component.Factory
    interface ApplicationComponentFactory {
        fun create(
            @BindsInstance context: Context,
        ): MainComponent
    }
}