package com.example.moneytrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** База Room: users, expenses, subscriptions + кэш курсов currency_rates */
@Database(
    entities = [UserEntity::class, ExpenseEntity::class, SubscriptionEntity::class, CurrencyRateEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun currencyDao(): CurrencyDao

    companion object {
        /** Версия 1 → 2: добавилась таблица курсов. Аккаунты и траты пользователя сохраняются. */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `currency_rates` (" +
                        "`code` TEXT NOT NULL, `name` TEXT NOT NULL, `nominal` INTEGER NOT NULL, " +
                        "`value` REAL NOT NULL, `previous` REAL NOT NULL, `cbr_date` TEXT NOT NULL, " +
                        "`updated_at` INTEGER NOT NULL, PRIMARY KEY(`code`))"
                )
            }
        }

        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "moneytrack_room.db")
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}
