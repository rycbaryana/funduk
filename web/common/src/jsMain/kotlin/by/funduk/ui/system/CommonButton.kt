package by.funduk.ui.system

import by.funduk.ui.general.Pallete
import by.funduk.ui.general.Sizes
import by.funduk.ui.general.textFrame
import emotion.react.*
import org.w3c.dom.events.Event
import react.*
import react.dom.html.ReactHTML.div
import react.ForwardRef
import react.dom.events.MouseEvent
import web.cssom.*
import web.html.HTMLDivElement

external interface CommonButtonProps : PropsWithRef<HTMLDivElement> {
    var text: String
    var onClick: ((MouseEvent<HTMLDivElement, *>) -> Unit)?
}

val commonButton = ForwardRef<CommonButtonProps> { props ->
    div {
        css {
            display = Display.flex
            justifyContent = JustifyContent.center
            alignItems = AlignItems.center

            borderRadius = Sizes.Button.Height / 2
            height = Sizes.Button.Height

            background = Pallete.Web.Light
            border = Sizes.Button.Border
            borderStyle = LineStyle.solid
            borderColor = Pallete.Web.Button.Border
            cursor = Cursor.pointer
        }

        onClick = props.onClick

        textFrame {
            text = props.text
            margins = listOf(0.px, 0.px, Sizes.RegularMargin, Sizes.RegularMargin)
        }
    }
}