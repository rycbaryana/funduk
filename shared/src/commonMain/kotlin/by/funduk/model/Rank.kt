package by.funduk.model

enum class Rank(val points: Int, val threshold: Int) {
    Calf(25, 0),
    Cow(50, 250),
    Rare(75, 500),
    MediumRare(100, 750),
    Medium(125, 1000),
    MediumWell(150, 1250),
    WellDone(175, 1500)
}

