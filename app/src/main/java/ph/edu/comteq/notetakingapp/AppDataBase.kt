package ph.edu.comteq.notetakingapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Note::class,
        Tag::class,
        NoteTagCrossRef::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDAO

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE notes ADD COLUMN category TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE notes ADD COLUMN updated_at INTEGER NOT NULL DEFAULT 0")
                database.execSQL("UPDATE notes SET updated_at = created_at")
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS tags (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        color TEXT NOT NULL DEFAULT '#6200EE'
                    )
                """)
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS note_tag_cross_ref (
                        note_id INTEGER NOT NULL,
                        tag_id INTEGER NOT NULL,
                        PRIMARY KEY(note_id, tag_id)
                    )
                """)
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notes_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                this.instance = instance
                instance
            }
        }
    }
}
