package by.funduk.ui.system

import by.funduk.model.Tag
import by.funduk.ui.general.*

import react.*
import react.dom.html.ReactHTML.div
import emotion.react.*
import web.cssom.*

external interface StaticTagBoardProps : Props {
    var direction: FlexDirection
    var align: AlignItems
    var justify: JustifyContent
    var padding: Length?
    var tags: List<Tag>
    var height: Height
    var width: Width
}

val staticTagBoard = FC<StaticTagBoardProps> { props ->
    div {
        val padding = props.padding ?: Sizes.SmallMargin
        css {
            display = Display.flex
            height = props.height
            width = props.width
        }
        div {
            css {
                display = Display.flex
                flexDirection = props.direction
                alignItems = props.align
                justifyContent = props.justify
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

