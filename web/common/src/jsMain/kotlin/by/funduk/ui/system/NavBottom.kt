package by.funduk.ui.system

import by.funduk.ui.general.*
import by.funduk.ui.general.Font
import by.funduk.ui.general.Pallete
import by.funduk.ui.general.Sizes
import by.funduk.ui.general.kProjectName

import react.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.a
import emotion.react.*
import web.cssom.*

external interface NavProps : Props {
}

val nav = FC<NavProps> { props ->
    div {
        css {
            top = 0.px
            transition = Transition(TransitionProperty.all, 0.5.s, 0.s)
            left = 0.px
            background = Pallete.Web.Light
            display = Display.block
            display = Display.flex
            justifyItems = JustifyItems.center
            position = Position.fixed
            top = 0.px
            left = 0.px
            width = 100.pct
            height = Sizes.NavHeight
            margin = 0.px
        }

        div {
            css {
                position = Position.relative
                display = Display.flex
                flexDirection = FlexDirection.row
                maxWidth = 1500.pt
                width = 100.pct
                height = 100.pct
            }

            textFrame {
                forbbidSelection = true
                text = kProjectName
                color = Pallete.Web.DarkText
                size = Font.Size.Big
                margins = listOf(Sizes.RegularMargin, Sizes.RegularMargin, Sizes.BiggerMargin, Sizes.BiggerMargin)
            }

            div {
                css {
                    width = 100.pct
                    height = 100.pct
                }
            }

            textFrame {
                forbbidSelection = true
                text = "Me"
                color = Pallete.Web.DarkText
                size = Font.Size.Big
                margins = listOf(Sizes.RegularMargin, Sizes.RegularMargin, Sizes.BiggerMargin, Sizes.BiggerMargin)
            }
        }
    }
}


external interface BottomProps : Props {
}

external interface BottomColumnProps : Props {
    var title: String
    var data: Array<Pair<String, String?>>
}

val bottomColumn = FC<BottomColumnProps> { props ->
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
            gap = 1.pt
        }

        textFrame {
            text = props.title
            size = Font.Size.Big
        }

        for ((name, ref) in props.data) {
            a {
                href = ref
                css {
                    textDecoration = TextDecoration.solid
                }

                textFrame {
                    text = name
                    margins = listOf(0.px, 0.px, 0.px, 0.px)
                }
            }
        }
    }

}

val bottom = FC<BottomProps> { props ->
    div {
        css {
            background = Pallete.Web.SecondLight
            display = Display.flex
            justifyContent = JustifyContent.center
            width = 100.pct
            height = Sizes.BottomHeight
            paddingTop = Sizes.BottomMargin
            paddingBottom = Sizes.BottomMargin
        }

        //content
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.center
                gap = Sizes.RegularGap
            }

            //name + discript
            div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    alignItems = AlignItems.center
                }

                textFrame {
                    text = kProjectName
                    color = Pallete.Web.DarkText
                    size = Font.Size.Bigger
                    margins = listOf(0.pt, 0.pt, Sizes.RegularMargin, Sizes.RegularMargin)
                }

                textFrame {
                    text = "competitive programming platform"
                    color = Pallete.Web.DarkText
                    size = Font.Size.Regular
                    margins = listOf(0.pt, 0.pt, Sizes.RegularMargin, Sizes.RegularMargin)
                }
            }

            // columns
            div {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.row
                    alignItems = AlignItems.start
                    justifyContent = JustifyContent.center
                    gap = Sizes.RegularGap
                }

                // thanks to
                bottomColumn {
                    title = "title of column"
                    data = listOf(Pair("First", null), Pair("Second", null), Pair("Third", null)).toTypedArray()
                }

                // contact us
                bottomColumn {
                    title = "Contact us"
                    data = listOf(
                        Pair("Kirill Zarovskiy", "https://t.me/Lospec"),
                        Pair("Dariya Gnedko", "https://t.me/DashaGnedko"),
                        Pair("Vladislav Katok", "https://t.me/katokvladislav"),
                        Pair("Maksim Koval", "https://t.me/ocmuko")
                    ).toTypedArray()
                }
            }

        }
    }
}
