package by.funduk.general

enum class Rank {
    Calf,
    Cow,
    Rare,
    MediumRare,
    Medium,
    MediumWell,
    WellDone
}

enum class Tag(val text: String) {
    Dp("dp"),
    Greed("greed"),
    Ds("ds"),
    Fft("fft"),
    Binsearch("bin search"),
    Twosat("2-sat"),
}

enum class Status {
    WA,
    TL,
    ML,
    Fail,
    Running,
    PE,
    OK
}