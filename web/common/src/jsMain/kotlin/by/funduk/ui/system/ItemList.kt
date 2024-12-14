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
import web.html.HTMLInputElement


data class Searcher<Item>(val data: List<Pair<String, Item>>) {
    fun Search(pref: String): List<Pair<String, Item>> {
        return data.filter { it.first.lowercase().startsWith(pref) }
    }
}

external interface ItemListFieldProps<Item> : PropsWithRef<HTMLDivElement> {
    var items: List<Pair<String, Item>>
    var onClickCallback: ((Item) -> Unit)?
}

fun <Item> GetItemList():
        ForwardRefExoticComponent<ItemListFieldProps<Item>> {
    return ForwardRef { props ->
        val refSearch = useRef<HTMLInputElement>(null)
        val searcher = Searcher(props.items)
        var current_items by useState(props.items)
        div {
            ref = props.ref
            tabIndex = 0
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
                outline = 0.px
            }

            inputField {
                ref = refSearch
                onChangeCallback = { str ->
                    current_items = searcher.Search(str.lowercase())
                }

                onEnterCallback = {
                    val items = current_items
                    if (items.isNotEmpty()) {
                        refSearch.current?.value = ""
                        props.onClickCallback?.invoke(items[0].second)
                        current_items = props.items
                    }
                }
            }

            onFocus = {e ->
                refSearch.current?.focus()
                e.stopPropagation()
            }

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
                                refSearch.current?.value = ""
                                current_items = props.items
                                props.onClickCallback?.invoke(item)
                            }
                        }
                    }
                }
            }
        }
    }
}