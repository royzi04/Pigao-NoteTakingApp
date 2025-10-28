package ph.edu.comteq.notetakingapp

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class NoteWithClass(
    @Embedded
    val note: Note,

    @Relation(
        parentColumn = "id",    // Note's ID
        entityColumn = "id",    // Tag's noteId
        associateBy = Junction(
            value = NoteTagCrossRef::class,
            parentColumn = "note_id",
            entityColumn = "tag_id"
        )
    )
    val tags: List<Tag>
)
