package ltd.mc233.bate

enum class Race(val id: Int) {
    SHEN(1),
    YAO(2),
    XIAN(3),
    ZHAN(4),
    REN(5);

    companion object {
        fun getById(id: Int): Race? {
            return entries.find { it.id == id }
        }
    }
}