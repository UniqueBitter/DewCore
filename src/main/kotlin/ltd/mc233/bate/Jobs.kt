package ltd.mc233.bate

enum class Jobs(val id: Int) {
    ZHANSHI(1),
    YOUXIA(2),
    LIANDAN(3);

    companion object {
        // ✨ 根据ID查找对应的职业
        fun getById(id: Int): Jobs? {
            return entries.find { it.id == id }
        }
    }
}