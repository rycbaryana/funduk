package by.funduk.ui.general

import react.*
import react.dom.html.ReactHTML.span
import react.dom.html.ReactHTML.input
import emotion.react.*
import web.cssom.*

external interface TextFrameProps : Props {
    var text: String
    var forbbidSelection: Boolean
    var color: Color?
    var size: FontSize?
    var bold: Boolean
    var margins: List<Length>?
}

val textFrame = FC<TextFrameProps> { props ->
    span {
        val mcolor = props.color ?: Pallete.Web.DarkText
        val size = props.size ?: Sizes.Font.Regular
        val margins =
            props.margins ?: listOf(Sizes.RegularMargin, Sizes.RegularMargin, Sizes.RegularMargin, Sizes.RegularMargin)

        css {
            whiteSpace = WhiteSpace.nowrap
            position = Position.relative
            display = Display.inlineBlock
            justifyContent = JustifyContent.center
            alignItems = AlignItems.center
            fontFamily = Font.Family
            fontWeight = if (props.bold) FontWeight.bold else FontWeight.normal
            textAlign = TextAlign.center
            verticalAlign = VerticalAlign.middle
            marginLeft = margins[2]
            marginRight = margins[3]
            marginTop = margins[0]
            marginBottom = margins[1]
            padding = 0.px
            fontSize = size
            color = mcolor
            if (props.forbbidSelection) {
                userSelect = null
            }
        }
        +props.text
    }
}


external interface TagFrameProps : PropsWithChildren {
    var name: String
    var forbbidSelection: Boolean
    var color: Color?
    var back: Color?
    var size: FontSize?
    var margins: List<Length>?
    var onClickCallback: (() -> Unit)?
}

val tagFrame = FC<TagFrameProps> { props ->
    span {
        val mcolor = props.color ?: Pallete.Web.LightText
        val back = props.color ?: Pallete.Web.SecondPlan
        val size = props.size ?: Sizes.Font.Small
        val margins =
            props.margins ?: listOf(Sizes.SmallMargin, Sizes.SmallMargin, Sizes.SmallMargin, Sizes.SmallMargin)

        onClick = {
            props.onClickCallback?.invoke()
        }

        css {
            cursor = Cursor.pointer
            borderRadius = Sizes.TagBorderRadius
            whiteSpace = WhiteSpace.nowrap
            position = Position.relative
            display = Display.inlineBlock
            justifyContent = JustifyContent.center
            alignItems = AlignItems.center
            fontFamily = Font.Family
            textAlign = TextAlign.center
            verticalAlign = VerticalAlign.middle
            marginLeft = margins[2]
            marginRight = margins[3]
            marginTop = margins[0]
            marginBottom = margins[1]
            fontSize = size
            color = mcolor

            paddingBottom = Sizes.TagVPadding
            paddingTop = Sizes.TagVPadding

            paddingLeft = Sizes.TagHPadding
            paddingRight = Sizes.TagHPadding

            if (props.forbbidSelection) {
                userSelect = null
            }

            background = back
        }
        +props.name
    }
}

external interface InputFieldProps : Props {

}

val inputField = FC<InputFieldProps> { props ->
    input {
        css {
            display = Display.flex
            outline = 0.px
            margin = 0.px
            color = Pallete.Web.DarkText
            paddingLeft = Sizes.RegularMargin
            paddingRight = Sizes.RegularMargin
            background = Pallete.Web.SecondLight
            width = 100.pct - 2 * Sizes.RegularMargin
            height = Sizes.SearchHeight
            borderRadius = 0.5 * Sizes.SearchHeight
            borderStyle = LineStyle.hidden
        }
        placeholder = "Search..."
    }
}