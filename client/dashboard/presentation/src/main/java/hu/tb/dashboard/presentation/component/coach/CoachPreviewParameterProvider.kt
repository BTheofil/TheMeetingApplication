package hu.tb.dashboard.presentation.component.coach

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import hu.tb.dashboard.domain.CoachItem

class CoachPreviewParameterProvider : PreviewParameterProvider<List<CoachItem>> {
    override val values = sequenceOf(
        listOf(
            CoachItem(id = "coach-anna", name = "Anna Kovács"),
            CoachItem(id = "coach-mark", name = "Márk Szabó"),
            CoachItem(id = "coach-julia", name = "Júlia Papp")
        ),
        listOf(
            CoachItem(id = "coach-anna", name = "Anna Kovács"),
        ),
        emptyList()
    )
}