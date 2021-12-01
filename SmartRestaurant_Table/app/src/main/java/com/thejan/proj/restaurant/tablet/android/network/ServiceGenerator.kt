package com.thejan.proj.restaurant.tablet.android.network

import com.google.gson.GsonBuilder
import com.thejan.proj.restaurant.tablet.android.helper.SharedPref
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Modifier
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Created by thejanthrimanna on 2020-02-05.
 */
object ServiceGenerator {
    private const val TIMEOUT = 10L
    fun getClient(): Retrofit {
        val x509TrustManager = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }

        val trustManagers = arrayOf<TrustManager>(x509TrustManager)
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustManagers, null)
        val networkCacheInterceptor = createCacheInterceptor()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
            .addNetworkInterceptor(networkCacheInterceptor)
            .addInterceptor(loggingInterceptor)
            .sslSocketFactory(sslContext.socketFactory, x509TrustManager)
            .build()
        val gson = GsonBuilder()
            .setLenient()
            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
            .create()

        return Retrofit.Builder()
//            .baseUrl(SharedPref.getString(SharedPref.DOMAIN, COMMON_URL)!!)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    private fun createCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            val response = chain.proceed(chain.request())

            var cacheControl = CacheControl.Builder()
                .maxAge(1, java.util.concurrent.TimeUnit.MINUTES)
                .build()

            response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
        }
    }
}
