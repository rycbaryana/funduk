package by.funduk.model

enum class Language(val text: String, val extensions: List<String>) {
    CPP23_GCC14("C++23 (GCC 14)", listOf("cpp", "cc", "cxx")),
    Python3("python 3.13", listOf("py"))
}