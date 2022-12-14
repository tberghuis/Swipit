package dev.tberghuis.swipit.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Database(entities = [Subreddit::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
  abstract fun subredditDao(): SubredditDao
}

@Entity
data class Subreddit(
  @PrimaryKey val subreddit: String
)

@Dao
interface SubredditDao {
  @Query("SELECT * FROM subreddit")
  fun getAll(): Flow<List<Subreddit>>

  @Insert
  fun insertAll(vararg subreddits: Subreddit)

  @Delete
  fun delete(subreddit: Subreddit)
}