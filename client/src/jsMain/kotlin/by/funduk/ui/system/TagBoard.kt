package by.funduk.ui.system

import by.funduk.general.*
import by.funduk.ui.general.*

import react.*
import react.dom.html.ReactHTML.div
import emotion.react.*
import web.cssom.*

external interface StaticTagBoardProps : Props {
    var direction: FlexDirection
    var padding: Length?
    var tags: List<Tag>
}

val staticTagBoard = FC<StaticTagBoardProps> { props ->
    div {
        val padding = props.padding ?: Sizes.SmallMargin
        css {
            display = Display.flex
            height = 100.pct
            width = 100.pct
        }
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.end
                alignContent = AlignContent.end
                justifyContent = JustifyContent.start
                flexWrap = FlexWrap.wrapReverse
                margin = padding
                width = 100.pct
            }

            for (tag in props.tags) {
                tagFrame {
                    name = tag.text
                }
            }
        }
    }
}

external interface TagFieldProps: Props {
    var direction: FlexDirection
    var align: AlignItems?
    var padding: Length?
    var tags: List<Tag>
}

val tagBoard = FC<TagFieldProps> { props ->
    div {
        val padding = props.padding ?: Sizes.SmallMargin
        val align = props.align ?: AlignItems.end
        css {
            display = Display.flex
            width = 100.pct
        }
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = align
                justifyContent = JustifyContent.start
                flexWrap = FlexWrap.wrapReverse
                margin = padding
                width = 100.pct
            }

            // here ll be tags

            tagFrame {
                name = "+"
            }
        }
    }
}

