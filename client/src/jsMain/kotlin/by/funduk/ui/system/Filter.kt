package by.funduk.ui.general

import by.funduk.ui.system.tagBoard
import react.*
import react.dom.html.ReactHTML.div
import emotion.react.*
import web.cssom.*


external interface FilterFieldProps : Props {

}

val filterField = FC<FilterFieldProps> { props ->
    div {
        css {
            position = Position.sticky
            boxShadow = Common.Shadow
            borderRadius = Sizes.BoxBorderRadius
            background = Pallete.Web.Light
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
            padding = Sizes.RegularMargin
            width = Sizes.Filter.Width
            gap = Sizes.SmallGap
        }

        // search
        inputField {
        }

        // rate
        div {
            css {
                display = Display.flex;
                flexDirection = FlexDirection.column
                alignItems = AlignItems.center
                margin = 0.px
                padding = 0.px
                width = 100.pct
                gap = Sizes.MicroGap
            }

            textFrame {
                text = "rank"
                size = Font.Size.Regular
                margins = listOf(0.px, 0.px, 0.px, 0.px)
            }

            // content
            div {
                css {
                    margin = 0.px
                    display = Display.flex
                    flexDirection = FlexDirection.row
                    flexWrap = FlexWrap.nowrap
                }

                div {
                    css {
                        margin = 0.px
                        padding = 0.px
                        display = Display.flex
                        flexDirection = FlexDirection.row
                        justifyContent = JustifyContent.end
                        width = Sizes.MaxRankTagWidth
                        alignItems = AlignItems.center
                    }
                    tagFrame {
                        name = "Medium rare"
                    }
                }

                textFrame {
                    text = "to"
                    margins = listOf(0.px, 0.px, Sizes.RegularMargin, Sizes.RegularMargin)
                }

                div {
                    css {
                        margin = 0.px
                        padding = 0.px
                        display = Display.flex
                        flexDirection = FlexDirection.row
                        justifyContent = JustifyContent.start
                        width = Sizes.MaxRankTagWidth
                        alignItems = AlignItems.center
                    }
                    tagFrame {
                        name = "calf"
                    }
                }

            }


        }

        // tags
        div {
            css {
                display = Display.flex;
                flexDirection = FlexDirection.column
                alignItems = AlignItems.center
                margin = 0.px
                padding = 0.px
                width = 100.pct
                gap = Sizes.MicroGap
            }

            textFrame {
                text = "tags"
                size = Font.Size.Regular
                margins = listOf(0.px, 0.px, 0.px, 0.px)
            }

            tagBoard {
                padding = 0.px
                direction = FlexDirection.row
                align = AlignItems.start
            }

        }
    }
}