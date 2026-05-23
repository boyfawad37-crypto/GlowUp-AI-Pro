package com.example.db

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Entity representing a historical GlowUp analyze and generation log.
 */
@Entity(tableName = "glowup_history")
data class GlowUpHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val styleSelected: String,
    val originalImagePath: String?,
    val resultImageUrl: String?,
    val jawlineScore: Int,
    val symmetryScore: Int,
    val glowScore: Int,
    val styleIndex: Int,
    val adviceTitle: String,
    val adviceGrooming: String,
    val adviceskincare: String,
    val adviceFashion: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Data Access Object for history.
 */
@Dao
interface GlowUpHistoryDao {
    @Query("SELECT * FROM glowup_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<GlowUpHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(entity: GlowUpHistoryEntity)

    @Query("DELETE FROM glowup_history WHERE id = :id")
    suspend fun deleteHistoryById(id: Int)

    @Query("DELETE FROM glowup_history")
    suspend fun clearAllHistory()
}

/**
 * App Room Database holder.
 */
@Database(entities = [GlowUpHistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun glowUpHistoryDao(): GlowUpHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "glowup_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * Abstract Repository to manage database queries.
 */
class GlowUpHistoryRepository(private val dao: GlowUpHistoryDao) {
    val allHistory: Flow<List<GlowUpHistoryEntity>> = dao.getAllHistory()

    suspend fun insert(entity: GlowUpHistoryEntity) {
        dao.insertHistory(entity)
    }

    suspend fun deleteById(id: Int) {
        dao.deleteHistoryById(id)
    }

    suspend fun clearAll() {
        dao.clearAllHistory()
    }
}
