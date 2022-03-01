package com.example.currencycheckertestwork.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.currencycheckertestwork.BuildConfig
import com.example.currencycheckertestwork.data.api.ApiRetrofitService
import com.example.currencycheckertestwork.data.CommonRepositoryImpl
import com.example.currencycheckertestwork.data.storage.AppDatabase
import com.example.currencycheckertestwork.domain.interaction.GetCurrencyDataUseCase
import com.example.currencycheckertestwork.domain.interaction.RoomUseCase
import com.example.currencycheckertestwork.presentation.viewmodels.SharedViewModel
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Module
class UseCasesModule() {

    @Provides
    fun provideGetCurrencyDataUseCase(repository: CommonRepositoryImpl): GetCurrencyDataUseCase =
        GetCurrencyDataUseCase(repository)


    @Provides
    fun provideSaveInRoomUseCase(repository: CommonRepositoryImpl): RoomUseCase =
        RoomUseCase(repository)

}

@Module
class NetworkModule {

    companion object {
        const val TIMEOUT = 30 * 1000L
    }

    @Provides
    @ApplicationScope
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
        builder.readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
        builder.writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
        return builder.build()
    }

    @Provides
    @ApplicationScope
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @ApplicationScope
    fun provideApiRetrofitService(okHttpClient: OkHttpClient, moshi: Moshi): ApiRetrofitService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(ApiRetrofitService::class.java)
    }
}

@ApplicationScope
class ViewModelFactory @Inject constructor(
    private val viewModelsProvider: @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModelsProvider[modelClass]?.get() as T
    }
}

@Module
interface ViewModelModule {

    @IntoMap
    @ViewModelKey(SharedViewModel::class)
    @Binds
    fun bindSharedViewModel(impl: SharedViewModel): ViewModel

}

@Module
class RepositoryModule {

    private companion object {
        const val DATABASE_NAME = "currency_database"
    }

    @Provides
    @ApplicationScope
    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
    }

    @Provides
    fun provideCommonRepository(
        apiRetrofitService: ApiRetrofitService,
        appDatabase: AppDatabase
    ): CommonRepositoryImpl {
        return CommonRepositoryImpl(apiRetrofitService, appDatabase)
    }

    @Provides
    fun provideLinearLayoutManager(context: Context): LinearLayoutManager {
        return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

}
