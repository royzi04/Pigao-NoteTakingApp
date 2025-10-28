package ph.edu.comteq.notetakingapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val noteDao: NoteDAO = AppDatabase.getDatabase(application).noteDao()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val allNotes: Flow<List<Note>> = searchQuery.flatMapLatest { query ->
        val category = _selectedCategory.value
        when {
            query.isNotBlank() && category != null -> noteDao.searchNotes(query)
            query.isNotBlank() -> noteDao.searchNotes(query)
            category != null -> noteDao.getNotesByCategory(category)
            else -> noteDao.getAllNotes()
        }
    }

    val allNotesWithTags: Flow<List<NoteWithTags>> = noteDao.getAllNotesWithTags()
    val allCategories: Flow<List<String>> = noteDao.getAllCategories()
    val allTags: Flow<List<Tag>> = noteDao.getAllTags()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun filterByCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun clearCategoryFilter() {
        _selectedCategory.value = null
    }

    fun insert(note: Note) = viewModelScope.launch {
        noteDao.insertNote(note)
    }

    fun update(note: Note) = viewModelScope.launch {
        val updatedNote = note.copy(updatedAt = System.currentTimeMillis())
        noteDao.updateNote(updatedNote)
    }

    fun delete(note: Note) = viewModelScope.launch {
        noteDao.deleteNote(note)
    }

    suspend fun getNoteById(id: Int): Note? {
        return noteDao.getNoteById(id)
    }

    suspend fun getNoteWithTags(noteId: Int): NoteWithTags? {
        return noteDao.getNoteWithTags(noteId)
    }

    fun insertTag(tag: Tag) = viewModelScope.launch {
        noteDao.insertTag(tag)
    }

    fun updateTag(tag: Tag) = viewModelScope.launch {
        noteDao.updateTag(tag)
    }

    fun deleteTag(tag: Tag) = viewModelScope.launch {
        noteDao.deleteTag(tag)
    }

    fun addTagToNote(noteId: Int, tagId: Int) = viewModelScope.launch {
        noteDao.insertNoteTagCrossRef(NoteTagCrossRef(noteId, tagId))
    }

    fun removeTagFromNote(noteId: Int, tagId: Int) = viewModelScope.launch {
        noteDao.deleteNoteTagCrossRef(NoteTagCrossRef(noteId, tagId))
    }

    fun getNotesWithTag(tagId: Int): Flow<List<Note>> {
        return noteDao.getNotesWithTag(tagId)
    }
}
