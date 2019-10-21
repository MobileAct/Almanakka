package almanakka.core.collections

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SequenceSegmentTest {

    @Test
    fun testCreate_failValidArgument() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            SequenceSegment.create(emptyArray<Int>())
        }
    }
}