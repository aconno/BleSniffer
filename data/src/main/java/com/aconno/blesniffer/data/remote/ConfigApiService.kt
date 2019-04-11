package com.aconno.blesniffer.data.remote

import com.aconno.blesniffer.data.remote.model.BeaconFormat
import com.aconno.blesniffer.data.remote.model.LatestVersion
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ConfigApiService {

    companion object {
        const val BASE_URL = "http://playground.simvelop.de:8095/"
    }

    @GET("sensroics/api/(getLatestVersion.php")
    fun getLatestVersion(@Query("version") version: String): Single<LatestVersion>


    @GET
    fun getFormat(formatPath: String): Single<BeaconFormat>
}