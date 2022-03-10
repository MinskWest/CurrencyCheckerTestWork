package com.example.currencycheckertestwork.di

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.currencycheckertestwork.BuildConfig
import com.example.currencycheckertestwork.constants.DATABASE_NAME
import com.example.currencycheckertestwork.constants.MAIN_LAY_MANAGER
import com.example.currencycheckertestwork.constants.TIMEOUT
import com.example.currencycheckertestwork.data.CommonRepositoryImpl
import com.example.currencycheckertestwork.data.SchedulerProviderImpl
import com.example.currencycheckertestwork.data.api.ApiRetrofitService
import com.example.currencycheckertestwork.data.storage.AppDatabase
import com.example.currencycheckertestwork.domain.CommonRepository
import com.example.currencycheckertestwork.domain.interaction.GetCurrencyDataUseCase
import com.example.currencycheckertestwork.domain.interaction.RoomUseCase
import com.example.currencycheckertestwork.domain.scheduler.SchedulerProvider
import com.example.currencycheckertestwork.presentation.viewmodels.SharedViewModel
import com.example.currencysymbols.CurrencySymbolsManager
import com.example.currencysymbols.CurrencySymbolsManagerImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


val useCasesModule = module {

    factory {
        GetCurrencyDataUseCase(commonRepository = get())
    }

    factory {
        RoomUseCase(commonRepository = get())
    }

}

val networkModule = module {

    single<OkHttpClient> {
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
            .build()
    }

    single<Moshi> {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    single<ApiRetrofitService> {

        val okHttpClient: OkHttpClient = get()
        val moshi: Moshi = get()

        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRetrofitService::class.java)
    }

}

val viewModelModule = module {

    viewModel<SharedViewModel> {
        SharedViewModel(
            getCurrencyDataUseCase = get(),
            roomUseCase = get()
        )
    }

}

val repositoryModule = module {

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    single<CommonRepository> {
        CommonRepositoryImpl(
            apiRetrofitService = get(),
            appDatabase = get()
        )
    }

    factory(named(MAIN_LAY_MANAGER)) {
        LinearLayoutManager(
            androidContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    single<SchedulerProvider> {
        SchedulerProviderImpl()
    }

}

val currencySymbolModule = module {

    single<CurrencySymbolsManager> {
        CurrencySymbolsManagerImpl(context = get())
    }

}