package by.funduk.ui.general

import web.cssom.*

import by.funduk.general.*

sealed interface Pallete {
    sealed interface Web {
        companion object {
            val Light = NamedColor.white
            val SecondLight = Color("#F8F8F8")
            val SecondPlan = Color("#B3B3B3")
            val LightText = NamedColor.white
            val DarkText = NamedColor.black
            val Shadow = rgb(240, 240, 240)
            val LightShadow = rgb(246, 246, 246)
            val Notification = Color("#ABEFA9")
        }
    }

    sealed interface Status {
        companion object {
            val OK = Color("#41DC5E")
            val WA = Color("#DC4141")
            val TL = Color("#C241DC")
            val PE = Color("#FFF00F")
            val Fail = Color("#333333")
            val ML = NamedColor.brown
            val Running = NamedColor.blue
        }
    }

    sealed interface Rank {
        companion object {
            val Calf = Color("#8F8F8F")
            val Cow = Color("#40DC67")
            val Rare = Color("#40C7DC")
            val MediumRare = Color("#4540DC")
            val Medium = Color("#DC40A0")
            val MediumWell = Color("#DC4163")
            val WellDone = Color("#DCC841")
        }
    }

    sealed interface Debug {
        companion object {
            val Green = Color("green")
            val Blue = Color("blue")
            val Purple = Color("purple")
            val Red = Color("red")
        }
    }
}

sealed interface Font {
    companion object {
        val Family = FontFamily.sansSerif
    }

    sealed interface Size {
        companion object {
            val Bigger = 30.pt
            val Big = 16.pt
            val Regular = 11.pt
            val Small = 7.pt
        }
    }
}

sealed interface Common {
    companion object {
        val Shadow = BoxShadow(0.px, 0.px, 3 * Sizes.RegularMargin, 0.8 * Sizes.RegularMargin, Pallete.Web.Shadow)
    }
}

sealed interface Sizes {

    sealed interface Filter {
        companion object {
            val Width = 120.pt
        }
    }

    companion object {
        val SmallMargin = 2.pt
        val RegularMargin = 5.pt
        val BigMargin = 10.pt
        val BiggerMargin = 15.pt
        val MuchBiggerMargin = 20.pt
        val BottomMargin = 20.pt
        val BottomHeight = 300.pt

        val RegularGap = 10.pt
        val SmallGap = 5.pt
        val MicroGap = 2.pt

        val NavHeight = 28.pt
        val BoxBorderRadius = 10.pt

        val TaskViewWidth = 500.pt
        val TaskViewHeight = 38.pt

        val TaskViewStatusWidth = 50.pt

        val TagBorderRadius = 6.pt
        val TagVPadding = 2.pt
        val TagHPadding = 4.pt

        val RegularTagHeight = 16.pt
        val SearchHeight = 20.pt

        val MaxRankTagWidth = 50.pt

        val LoadMoreButtonHeight = 25.pt;
        val LoadMoreButtonWidth = 70.pt;
    }
}

sealed interface Counts {
    companion object {
        val NumberOfTagLinesInTaskView = 2;
    }
}

val kProjectName = "Funduk"

val colorOfRank = mapOf(
    Rank.Calf to Pallete.Rank.Calf,
    Rank.Cow to Pallete.Rank.Cow,
    Rank.Rare to Pallete.Rank.Rare,
    Rank.MediumRare to Pallete.Rank.MediumRare,
    Rank.Medium to Pallete.Rank.Medium,
    Rank.MediumWell to Pallete.Rank.MediumWell,
    Rank.WellDone to Pallete.Rank.WellDone,
)

val colorOfStatus = mapOf(
    Status.WA to Pallete.Status.WA,
    Status.TL to Pallete.Status.TL,
    Status.OK to Pallete.Status.OK,
    Status.PE to Pallete.Status.PE,
    Status.ML to Pallete.Status.ML,
    Status.Running to Pallete.Status.Running,
    Status.Fail to Pallete.Status.Fail,
)


