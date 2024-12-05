package by.funduk.ui.system

import by.funduk.ui.general.*
import react.*
import react.dom.html.ReactHTML.div
import emotion.react.*
import web.html.HTMLElement
import org.w3c.dom.events.MouseEvent
import web.cssom.*
import web.events.EventType
import web.events.addEventListener
import web.html.HTMLDivElement
import web.window.window
import web.dom.document


external interface ItemListFieldProps<Item> : Props {
    var items: List<Pair<String, Item>>
    var onClickCallback: ((Item) -> Unit)?
}

fun<Item> GetItemList(): FC<ItemListFieldProps<Item>> {

    return FC { props ->
        div {
//            val ref_this = useRef<HTMLDivElement>(null)

//            props.ref = ref_this
            css {
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.center
                width = Sizes.ItemList.Width
                boxShadow = Common.Shadow
                borderRadius = Sizes.BoxBorderRadius
                background = Pallete.Web.Light
                padding = Sizes.SmallMargin
                gap = Sizes.SmallMargin
            }

            inputField {
            }

            var current_items by useState(props.items)

//            val handleClickOutside = { event: MouseEvent ->
//                val target = event.target as? HTMLElement
//                val block = document.getElementById("toggleBlock")
//                if (block != null && !block.contains(target)) {
//                    ref_this.current?.style?.visibility = "hidden"
//                }
//            }
//
//            useEffectWithCleanup {
//                window.addEventListener(EventType("click"), handleClickOutside)
//
//                onCleanup {
//                    window.removeEventListener(EventType("click"), handleClickOutside)
//                }
//            }

            if (current_items.isEmpty()) {
                textFrame {
                    text = "nothing was found"
                    color = Pallete.Web.SecondPlan
                }
            } else {
                div {
                    css {
                        display = Display.flex
                        flexDirection = FlexDirection.row
                        width = 100.pct
                        flexWrap = FlexWrap.wrap

                    }

                    for ((title, item) in current_items) {
                        tagFrame {
                            name = title
                            onClickCallback = {
                                props.onClickCallback?.invoke(item)
                            }
                        }
                    }
                }
            }
        }
    }
}