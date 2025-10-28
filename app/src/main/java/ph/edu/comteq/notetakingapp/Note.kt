package ph.edu.comteq.notetakingapp

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val category: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tags")
data class Tag(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    val color: String = "#6200EE"
)

@Entity(
    tableName = "note_tag_cross_ref",
    primaryKeys = ["note_id", "tag_id"]
)
data class NoteTagCrossRef(
    @ColumnInfo(name = "note_id") val noteId: Int,
    @ColumnInfo(name = "tag_id") val tagId: Int
)

data class NoteWithTags(
    @Embedded val note: Note,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = NoteTagCrossRef::class,
            parentColumn = "note_id",
            entityColumn = "tag_id"
        )
    )
    val tags: List<Tag>
)
