package by.funduk.ui.system

import by.funduk.ui.SubmissionView
import by.funduk.ui.general.*
import by.funduk.model.Submission.*
import by.funduk.model.Status
import react.*
import react.dom.html.ReactHTML.div
import emotion.react.*
import web.html.HTMLElement
import web.cssom.*
import org.w3c.dom.events.MouseEvent
import web.events.EventType
import web.events.addEventListener
import web.html.HTMLDivElement
import web.window.window
import web.dom.document
import web.html.HTMLInputElement
import kotlin.math.min
import kotlin.math.roundToInt

external interface SubmissionViewProps : Props {
    var backgroundColor: Color?
    var submission: SubmissionView?
}

val submissionView = FC<SubmissionViewProps> { props ->
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.row
            gap = Sizes.RegularGap
            flexWrap = FlexWrap.nowrap
            padding = Sizes.SmallMargin
            alignItems = AlignItems.center
            borderRadius = Sizes.BoxBorderRadius
            background = props.backgroundColor ?: NamedColor.transparent
        }

        //id
        div {
            css {
                display = Display.flex
                minWidth = Sizes.SubmissionView.IdWidth
                maxWidth = Sizes.SubmissionView.IdWidth
            }
            textFrame {
                text = props.submission?.id?.toString() ?: "no data"
                margins = listOf(0.px, 0.px, Sizes.RegularMargin, Sizes.RegularMargin)
            }
        }

        //date
        div {
            css {
                display = Display.flex
                minWidth = Sizes.SubmissionView.DateWidth
                maxWidth = Sizes.SubmissionView.DateWidth
            }
            textFrame {
                text =
                    "${props.submission?.submitTime?.date.toString()} ${props.submission?.submitTime?.time?.hour}:${props.submission?.submitTime?.time?.minute}"
                margins = listOf(0.px, 0.px, 0.px, 0.px)
            }
        }

        //lang
        div {
            css {
                display = Display.flex
                minWidth = Sizes.SubmissionView.LanguageWidth
                maxWidth = Sizes.SubmissionView.LanguageWidth
            }
            textFrame {
                text = props.submission?.language?.text ?: "no data"
                margins = listOf(0.px, 0.px, 0.px, 0.px)
            }
        }

        //space
        div {
            css {
                display = Display.flex
                width = 100.pct
            }
        }

        val status = props.submission?.testInfo?.status
        var doWriteMemAndTime = false

        if (status != null) {
            if (status != Status.Fail && status != Status.Running && status != Status.Queued) {
                doWriteMemAndTime = true;
            }
        }

        //time
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.row
                justifyContent = JustifyContent.end
                alignItems = AlignItems.center
                minWidth = Sizes.SubmissionView.TimeWidth
                maxWidth = Sizes.SubmissionView.TimeWidth
            }

            if (doWriteMemAndTime) {

                textFrame {
                    val time = props.submission?.testInfo?.time ?: 0
                    text = "${time}ms"
                    margins = listOf(0.px, 0.px, 0.px, 0.px)
                }
            }
        }

        //memory
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.row
                justifyContent = JustifyContent.end
                alignItems = AlignItems.center
                minWidth = Sizes.SubmissionView.MemoryWidth
                maxWidth = Sizes.SubmissionView.MemoryWidth
            }

            if (doWriteMemAndTime) {
                textFrame {
                    val mem = props.submission?.testInfo?.memory ?: 0
                    var mem_mb = mem.toDouble() / 1048576
                    mem_mb = (mem_mb * 10).roundToInt().toDouble() / 10.0
                    text = "${mem_mb}mb"
                    margins = listOf(0.px, 0.px, 0.px, 0.px)
                }
            }
        }

        //status
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.row
                justifyContent = JustifyContent.end
                alignItems = AlignItems.center
                minWidth = Sizes.SubmissionView.StatusWidth
                maxWidth = Sizes.SubmissionView.StatusWidth
            }
            var stat_text = status?.name ?: "in queue"

            if (status != null && status != Status.Fail && status != Status.OK && status != Status.Queued) {
                stat_text += " ${props.submission?.testInfo?.test}"
            }

            tagFrame {
                name = stat_text
                back = colorOfStatus[status] ?: Pallete.Web.SecondPlan
            }
        }
    }
}

external interface SubmissionTableProps : Props {
    var width: Length?
    var height: Length?
    var submissions: List<SubmissionView>
}

val submissionTable = FC<SubmissionTableProps> { props ->
    div {
        css {
            width = props.width
            height = props.height
            display = Display.flex
            flexDirection = FlexDirection.column
        }

        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.column
                width = 100.pct
            }
            for (i in 0 until props.submissions.size) {
                var background = NamedColor.transparent
                if (i % 2 == 1) {
                    background = Pallete.Web.SecondLight
                }

                submissionView {
                    backgroundColor = background
                    submission = props.submissions[i]
                }
            }
        }

    }
}
