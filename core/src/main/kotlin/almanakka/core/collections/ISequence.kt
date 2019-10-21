package almanakka.core.collections

interface ISequence<T> {

    val size: Int

    /**
     * [createSideRangeIfIndexNotCreated] is within target index.
     * So, if value is 1, it mean create 1 size segment.
     * if value is 4, it mean create 7 or fewer size segment
     */
    fun create(index: Int, createSideRangeIfIndexNotCreated: Int)

    operator fun get(index: Int): T

    /**
     * [createSideRangeIfIndexNotCreated] is within target index.
     * So, if value is 1, it mean create 1 size segment.
     * if value is 4, it mean create 7 or fewer size segment
     */
    fun getOrCreate(index: Int, createSideRangeIfIndexNotCreated: Int): T
}