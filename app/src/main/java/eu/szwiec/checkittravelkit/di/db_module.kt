package eu.szwiec.checkittravelkit.di

import android.content.Context
import androidx.room.Room
import eu.szwiec.checkittravelkit.db.CountryDao
import eu.szwiec.checkittravelkit.db.CountryDb
import org.koin.dsl.module.module

val dbModule = module {
    single { createDb(get()) }
    single { createDao(get()) }
}

fun createDb(context: Context): CountryDb {
    return Room
            .databaseBuilder(context, CountryDb::class.java, "country.db")
            .build()
}

fun createDao(db: CountryDb): CountryDao {
    return db.countryDao()
}
