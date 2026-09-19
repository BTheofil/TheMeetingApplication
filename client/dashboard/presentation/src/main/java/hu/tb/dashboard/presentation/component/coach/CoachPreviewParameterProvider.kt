package hu.tb.dashboard.presentation.component.coach

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import hu.tb.dashboard.domain.CoachItem

class CoachPreviewParameterProvider : PreviewParameterProvider<List<CoachItem>> {
    override val values = sequenceOf(
        listOf(
            CoachItem(id = 1, name = "Anna Kovács"),
            CoachItem(id = 2, name = "Márk Szabó"),
            CoachItem(id = 3, name = "Júlia Papp")
        ),
        listOf(
            CoachItem(id = 1, name = "Anna Kovács"),
        ),
        emptyList()
    )
}