package hu.tb.search.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.skydoves.compose.stability.runtime.TraceRecomposition
import hu.tb.design_system.theme.MeetingTheme
import hu.tb.search.domain.Coach

@TraceRecomposition
@Composable
internal fun SearchResults(
    modifier: Modifier = Modifier,
    coaches: List<Coach>,
    query: String,
    isLoading: Boolean,
    onCoachClick: (String) -> Unit
) {
    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(64.dp))
        }
    } else {
        if (coaches.isEmpty()) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (query.isBlank()) {
                        "No coaches to show yet."
                    } else {
                        "No coaches matches"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = modifier.fillMaxWidth(),
            ) {
                items(items = coaches, key = { it.id }) { coach ->
                    CoachResultRow(
                        coach = coach,
                        onClick = { onCoachClick(coach.id) }
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SearchResultsPreview() {
    MeetingTheme {
        SearchResults(
            coaches = emptyList(),
            isLoading = true,
            query = "an",
            onCoachClick = {}
        )
    }
}
