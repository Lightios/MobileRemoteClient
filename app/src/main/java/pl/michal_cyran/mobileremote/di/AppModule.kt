package pl.michal_cyran.mobileremote.di

import io.ktor.client.engine.cio.CIO
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import pl.michal_cyran.mobileremote.core.data.networking.HttpClientFactory
import pl.michal_cyran.mobileremote.remote.data.RemoteDataSourceImpl
import pl.michal_cyran.mobileremote.remote.domain.RemoteDataSource
import pl.michal_cyran.mobileremote.remote.presentation.RemoteViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import pl.michal_cyran.mobileremote.remote.data.IpProviderImpl

val appModule = module {
    single { HttpClientFactory.create(CIO.create()) }

    single {
        IpProviderImpl(
            context = get()
        )
    }

    single<RemoteDataSource> {
        RemoteDataSourceImpl(
            httpClient = get(),
            ipProvider = get<IpProviderImpl>()
        )
    }

    viewModel {
        RemoteViewModel(
            remoteDataSource = get()
        )
    }

}