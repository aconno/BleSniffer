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
    }

    override fun onItemClick(item: Deserializer) {
        startActivityForResult(Intent(this, EditDeserializerActivity::class.java).apply {
            putExtra("filter", item.filter)
            putExtra("type", item.filterType.name)
        }, 0x01)
    }

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

        getAllDeserializersUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(deserializerAdapter::setDeserializers)
    }
}