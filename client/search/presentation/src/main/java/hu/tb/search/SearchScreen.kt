package hu.tb.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.design_system.Icons
import hu.tb.design_system.modifier.screenPadding
import hu.tb.design_system.theme.MeetingTheme
import hu.tb.search.component.SearchResults
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    SearchScreen(
        state = viewModel.state.collectAsStateWithLifecycle().value,
        onBackClick = onBackClick,
        onSearch = {},
        onCoachClick = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@TraceRecomposition
@Composable
private fun SearchScreen(
    state: SearchState,
    onBackClick: () -> Unit = {},
    onSearch: (String) -> Unit,
    onCoachClick: (String) -> Unit = {}
) {
    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberSearchBarState()

    val query by remember { derivedStateOf { textFieldState.text.toString().trim() } }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .screenPadding()
        ) {
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.fillMaxHeight()
                        .width(46.dp),
                    onClick = onBackClick
                ) {
                    Icon(
                        painter = painterResource(Icons.arrow_back),
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(Modifier.width(4.dp))
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    state = searchBarState,
                    inputField = {
                        SearchBarDefaults.InputField(
                            textFieldState = textFieldState,
                            searchBarState = searchBarState,
                            onSearch = {},
                            placeholder = { Text(text = "Search coaches") },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(Icons.search),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                )
            }
            SearchResults(
                modifier = Modifier.fillMaxSize(),
                coaches = state.searchResult,
                query = query,
                onCoachClick = onCoachClick
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SearchScreenEmptyPreview() {
    MeetingTheme {
        SearchScreen(state = SearchState(), onBackClick = {}, onSearch = {}, onCoachClick = {})
    }
}
