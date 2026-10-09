package com.example.moneytrack.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun findById(id: Long): UserEntity?

    @Query("UPDATE users SET monthly_limit = :limit WHERE id = :id")
    suspend fun updateLimit(id: Long, limit: Long)
}

@Dao
interface ExpenseDao {
    /** REPLACE — чтобы «Отменить» могло вернуть трату с тем же id */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity): Long

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM expenses WHERE user_id = :userId ORDER BY date DESC")
    suspend fun forUser(userId: Long): List<ExpenseEntity>

    @Query("SELECT COUNT(*) FROM expenses WHERE user_id = :userId")
    suspend fun count(userId: Long): Int
}

@Dao
interface SubscriptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subscription: SubscriptionEntity): Long

    @Update
    suspend fun update(subscription: SubscriptionEntity)

    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM subscriptions WHERE user_id = :userId ORDER BY next_charge ASC")
    suspend fun forUser(userId: Long): List<SubscriptionEntity>

    @Query("UPDATE subscriptions SET next_charge = :nextCharge WHERE id = :id")
    suspend fun updateNextCharge(id: Long, nextCharge: Long)

    @Query("SELECT COUNT(*) FROM subscriptions WHERE user_id = :userId")
    suspend fun count(userId: Long): Int
}

@Dao
interface CurrencyDao {
    /** Flow: экран узнаёт о новых курсах сам, как только они записаны в базу */
    @Query("SELECT * FROM currency_rates")
    fun observeAll(): Flow<List<CurrencyRateEntity>>

    @Upsert
    suspend fun upsert(rates: List<CurrencyRateEntity>)

    @Query("SELECT MAX(updated_at) FROM currency_rates")
    suspend fun lastUpdated(): Long?
}
