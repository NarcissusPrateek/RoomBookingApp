package com.example.conferenceroomapp.common.di

import android.content.Context
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.nineleaps.conferenceroombooking.BaseApplication
import com.nineleaps.conferenceroombooking.utils.Constants
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Singleton

@Singleton
@Module
class AppModule(private val application: BaseApplication) {
    private var objectMapper: ObjectMapper? = null
    private var mHttpLoggingInterceptor: HttpLoggingInterceptor? = null
    private var builder: OkHttpClient.Builder? = null

    /**
     * This method will return the application context
     *
     * @return Application instance mmjpApplication
     */
    @Provides
    @Singleton
    fun provideContext(): Context {
        return application
    }


    /**
     * This method will return the application instance
     *
     * @return applications instance mmjpApplication
     */

    @Singleton
    @Provides
    fun provideApplication(): BaseApplication {
        return application
    }


    /**
     * this method gives the retrofit instance to the application
     *
     * @return Retrofit object
     */

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        builder = provideOkHttpClient()
        builder!!.addInterceptor(provideHttpLoggingInterceptorInstance())
        return Retrofit.Builder()
            .client(builder!!.build())
            .addConverterFactory(JacksonConverterFactory.create(providesJacksonFactoryInstance()))
            .baseUrl(Constants.IP_ADDRESS)
            .build()

    }


    /**
     * This method returns the httpclient for logging requestURLs
     */

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
    }


    /**
     * This method returns object mapper instance
     */
    @Singleton
    @Provides
    fun provideObjectMapper(): ObjectMapper {
        return ObjectMapper()
    }

    /**
     * This method returns object mapper instance
     */
    @Singleton
    @Provides
    fun providesJacksonFactoryInstance(): ObjectMapper {
        objectMapper = provideObjectMapper()
        objectMapper!!.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        return objectMapper!!
    }


    /**
     * This method provides Httplogging Interceptor instance
     */

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptorInstance(): HttpLoggingInterceptor {
        mHttpLoggingInterceptor = HttpLoggingInterceptor()
        mHttpLoggingInterceptor!!.level = HttpLoggingInterceptor.Level.BODY
        return mHttpLoggingInterceptor!!
    }


}