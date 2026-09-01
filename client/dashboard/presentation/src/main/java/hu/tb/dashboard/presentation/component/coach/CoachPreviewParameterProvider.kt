package hu.tb.dashboard.presentation.component.coach

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import hu.tb.dashboard.presentation.model.CoachItem

class CoachPreviewParameterProvider : PreviewParameterProvider<List<CoachItem>> {
    override val values = sequenceOf(
        listOf(
            CoachItem(id = "coach-anna", name = "Anna Kovács", openHourCount = 3),
            CoachItem(id = "coach-mark", name = "Márk Szabó", openHourCount = 4),
            CoachItem(id = "coach-julia", name = "Júlia Papp", openHourCount = 0)
        ),
        listOf(
            CoachItem(id = "coach-anna", name = "Anna Kovács", openHourCount = 3),
        ),
        emptyList()
    )
}