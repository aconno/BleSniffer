package com.aconno.acnsensa.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.adapter.DeserializerAdapter
import com.aconno.acnsensa.adapter.ItemClickListener
import com.aconno.acnsensa.adapter.LongItemClickListener
import com.aconno.acnsensa.dagger.deserializerlistactivity.DaggerDeserializerListActivityComponent
import com.aconno.acnsensa.dagger.deserializerlistactivity.DeserializerListActivityComponent
import com.aconno.acnsensa.dagger.deserializerlistactivity.DeserializerListActivityModule
import com.aconno.acnsensa.domain.deserializing.Deserializer
import com.aconno.acnsensa.domain.interactor.deserializing.DeleteDeserializerUseCase
import com.aconno.acnsensa.domain.interactor.deserializing.GetAllDeserializersUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_deserializer_list.*
import javax.inject.Inject

const val REQUEST_CODE_EDIT: Int = 0x00
const val REQUEST_CODE_EDIT_QUIT_ON_RESULT: Int = 0x01

class DeserializerListActivity : AppCompatActivity(), ItemClickListener<Deserializer>, LongItemClickListener<Deserializer> {

    private var snackbar: Snackbar? = null
    private val deserializerAdapter: DeserializerAdapter = DeserializerAdapter(mutableListOf(), this, this)

    @Inject
    lateinit var getAllDeserializersUseCase: GetAllDeserializersUseCase
    @Inject
    lateinit var deleteDeserializerUseCase: DeleteDeserializerUseCase

    val editDeserializerActivityComponent: DeserializerListActivityComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication
        DaggerDeserializerListActivityComponent.builder()
                .appComponent(acnSensaApplication?.appComponent)
                .deserializerListActivityModule(DeserializerListActivityModule(this))
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deserializer_list)

        editDeserializerActivityComponent.inject(this)

        custom_toolbar.title = getString(R.string.app_name)
        deserializer_list.layoutManager = LinearLayoutManager(this)
        deserializer_list.adapter = deserializerAdapter
        setSupportActionBar(custom_toolbar)

        getAllDeserializersUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(deserializerAdapter::setDeserializers)

        add_deserializer.setOnClickListener {
            startActivityForResult(Intent(this@DeserializerListActivity, EditDeserializerActivity::class.java).apply {
            }, 0x01)
        }

        intent.extras?.let {
            if (it.containsKey(EXTRA_FILTER_MAC)) {
                startEditActivity(it.getString(EXTRA_FILTER_MAC), Deserializer.Type.MAC, true)
            }
        }
    }

    override fun onItemClick(item: Deserializer) {
        startEditActivity(item.filter, item.filterType)
    }

    private fun startEditActivity(filter: String, type: String, quitOnResult: Boolean = false) {
        startActivityForResult(Intent(this, EditDeserializerActivity::class.java).apply {
            putExtra("filter", filter)
            putExtra("type", type)
        }, if (quitOnResult) REQUEST_CODE_EDIT_QUIT_ON_RESULT else REQUEST_CODE_EDIT)
    }

    private fun startEditActivity(filter: String,
                                  type: Deserializer.Type,
                                  quitOnResult: Boolean = false) =
            startEditActivity(filter, type.name, quitOnResult)

    override fun onLongItemClick(item: Deserializer): Boolean {
        AlertDialog.Builder(this)
                .setMessage("Delete deserializer?")
                .setPositiveButton("Delete") { dialog, _ ->
                    deleteDeserializerUseCase.execute(item)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                getAllDeserializersUseCase.execute()
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(deserializerAdapter::setDeserializers)
                            }
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_EDIT -> getAllDeserializersUseCase.execute()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(deserializerAdapter::setDeserializers)
            REQUEST_CODE_EDIT_QUIT_ON_RESULT -> finish()
        }
    }
}