package com.aconno.blesniffer.data.remote

import com.aconno.blesniffer.data.remote.model.BeaconFormat
import com.aconno.blesniffer.data.remote.model.LatestVersion
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface FormatApiService {

    companion object {
        const val BASE_URL = "https://aconno.de/"
    }

    @GET("/sensorics/api/getLatestVersion_debug.php")
    fun getLatestVersion(@Query("version") version: String): Single<LatestVersion>


    @GET
    fun getFormat(@Url formatPath: String): Single<BeaconFormat>
}