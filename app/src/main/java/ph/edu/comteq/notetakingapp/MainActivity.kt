package ph.edu.comteq.notetakingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ph.edu.comteq.notetakingapp.ui.theme.NoteTakingAppTheme
import ph.edu.comteq.notetakingapp.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val viewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteTakingAppTheme {
                NoteApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteApp(viewModel: NoteViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val notes by viewModel.allNotes.collectAsState(initial = emptyList())

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (isSearchActive) {
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = searchQuery,
                            onQueryChange = {
                                searchQuery = it
                                viewModel.updateSearchQuery(it)
                            },
                            onSearch = {},
                            expanded = true,
                            onExpandedChange = { shouldExpand ->
                                if (!shouldExpand) {
                                    isSearchActive = false
                                    searchQuery = ""
                                    viewModel.clearSearch()
                                }
                            },
                            placeholder = { Text("Search notes...") },
                            leadingIcon = {
                                IconButton(onClick = {
                                    isSearchActive = false
                                    searchQuery = ""
                                    viewModel.clearSearch()
                                }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Close search"
                                    )
                                }
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = {
                                        searchQuery = ""
                                        viewModel.clearSearch()
                                    }) {
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = "Clear search"
                                        )
                                    }
                                }
                            }
                        )
                    },
                    expanded = true,
                    onExpandedChange = { shouldExpand ->
                        if (!shouldExpand) {
                            isSearchActive = false
                            searchQuery = ""
                            viewModel.clearSearch()
                        }
                    }
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        if (notes.isEmpty()) {
                            item {
                                Text(
                                    text = "No notes found",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        } else {
                            items(notes) { note ->
                                NoteItem(note)
                            }
                        }
                    }
                }
            } else {
                TopAppBar(
                    title = { Text("Notes") },
                    actions = {
                        IconButton(onClick = {
                            isSearchActive = true
                        }) {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { }) {
                Icon(Icons.Filled.Add, contentDescription = "Add note")
            }
        }
    ) { innerPadding ->
        if (!isSearchActive) {
            NoteListScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun NoteListScreen(viewModel: NoteViewModel, modifier: Modifier = Modifier) {
    val notes by viewModel.allNotes.collectAsState(initial = emptyList())
    LazyColumn(modifier = modifier) {
        items(notes) { note ->
            NoteItem(note)
        }
    }
}

@Composable
fun NoteItem(note: Note) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = DateUtils.getTimeAgo(note.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
