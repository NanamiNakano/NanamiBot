package utils

object Bilibili {
    private val bvMatcher = Regex("(BV.*?).{10}")

    fun isBV(bv: String):Boolean {
        return bvMatcher.matches(bv)
    }

}
