package com.aconno.blesniffer.data.sync

import android.content.SharedPreferences
import com.aconno.blesniffer.data.remote.FormatApiService
import com.aconno.blesniffer.data.remote.mappers.DeserializerMapper
import com.aconno.blesniffer.data.remote.model.BeaconFormat
import com.aconno.blesniffer.data.remote.model.LatestVersion
import com.aconno.blesniffer.data.repository.DeserializerDao
import com.aconno.blesniffer.data.repository.DeserializerEntity
import com.aconno.blesniffer.data.repository.mappers.DeserializerEntityMapper
import com.aconno.blesniffer.domain.sync.SyncRepository
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber


class SyncRepositoryImpl(
    private val formatApiService: FormatApiService,
    private val sharedPreferences: SharedPreferences,
    private val deserializerMapper: DeserializerMapper,
    private val deserializerEntityMapper: DeserializerEntityMapper,
    private val deserializerDao: DeserializerDao

) : SyncRepository {

    private val currentVersion = sharedPreferences.getLong(LATEST_VERSION, LATEST_ASSETS_VERSION)

    override fun syncDeserializers(): Completable {
        return formatApiService.getLatestVersion(currentVersion.toString())
            .flatMapCompletable { latestVersion -> updateFormats(latestVersion) }
    }

    private fun updateFormats(latestVersion: LatestVersion): Completable {
        return if (latestVersion.isUpdateNeeded) {
            val downloadSingles = downloadFormats(latestVersion.filesToBeUpdated)
            getCompletableFromDownloadSingles(downloadSingles).doOnComplete {
                updateVersion(latestVersion.latestVersion.toLong())
            }
        } else {
            Completable.create { emitter ->
                emitter.onComplete()
            }
        }
    }

    private fun downloadFormats(filesToBeUpdated: List<LatestVersion.File>): List<Single<BeaconFormat>> {
        val formatSinglesList = arrayListOf<Single<BeaconFormat>>()

        filesToBeUpdated.filter { isFormat(it.path) }
            .forEach { file ->
                formatSinglesList.add(formatApiService.getFormat(file.path).doOnError {
                    updateVersion(file.fileLastModifiedDate)
                    Timber.e(it)
                })
            }

        return formatSinglesList.toList()
    }

    private fun getCompletableFromDownloadSingles(downloadSingles: List<Single<BeaconFormat>>): Completable {
        val formatsToInsert = arrayListOf<DeserializerEntity>()

        return Single.zip(downloadSingles) { parameters ->
            formatsToInsert.addAll(getBeaconFormatsToInsert(parameters))
        }.flatMapCompletable {
            deserializerDao.insertAll(formatsToInsert)
        }
    }

    private fun getBeaconFormatsToInsert(parameters: Array<out Any>): List<DeserializerEntity> {
        return parameters.filterIsInstance<BeaconFormat>()
            .map { mapBeaconToEntity(it) }
    }

    private fun mapBeaconToEntity(beaconFormat: BeaconFormat): DeserializerEntity {
        val deserializer = deserializerMapper.map(beaconFormat)
        return deserializerEntityMapper.toEntity(deserializer)
    }

    private fun isFormat(fileName: String) =
        fileName.endsWith(JSON_FORMAT) && fileName.contains(FORMATS_KEYWORD)

    private fun updateVersion(fileLastModifiedDate: Long) {
        sharedPreferences.edit()
            .putLong(LATEST_VERSION, fileLastModifiedDate)
            .apply()
    }


    companion object {
        private const val JSON_FORMAT = ".json"
        private const val FORMATS_KEYWORD = "formats"
        const val LATEST_VERSION = "LATEST_VERSION"
        const val LATEST_ASSETS_VERSION = 0L
    }
}