/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.compiler.plugins.kotlin

import androidx.compose.compiler.plugins.kotlin.analysis.StabilityConfigParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class StabilityConfigurationParserTests {
    private fun testConfigParsing(config: String, expectedClasses: Set<String>) {
        val parser = StabilityConfigParser.fromLines(config.lines())
        assertEquals(expectedClasses, parser.stableTypeMatchers.map { it.pattern }.toSet())
    }

    private fun testConfigParsingThrows(config: String) {
        assertThrows(IllegalStateException::class.java) {
            StabilityConfigParser.fromLines(config.lines())
        }
    }

    @Test
    fun testSingleLineClassName() = testConfigParsing(
        """
            com.foo.bar
        """.trimIndent(),
        setOf("com.foo.bar")
    )

    @Test
    fun testSingleLineClassNameWithNonAlphaCharacters() = testConfigParsing(
        """
            com.foo_1.bar
        """.trimIndent(),
        setOf("com.foo_1.bar")
    )

    @Test
    fun testMultipleClassNames() = testConfigParsing(
        """
            com.foo.bar
            com.bar.foo
        """.trimIndent(),
        setOf("com.foo.bar", "com.bar.foo")
    )

    @Test
    fun testCommentsAreIgnored() = testConfigParsing(
        """
            // Comment first line
            com.foo.bar
            // Comment last line
        """.trimIndent(),
        setOf("com.foo.bar")
    )

    @Test
    fun whitespaceIgnored() = testConfigParsing(
        """

               com.foo.bar

        """.trimIndent(),
        setOf("com.foo.bar")
    )

    @Test
    fun testWildcardsAreParsed() = testConfigParsing(
        """
            // Comment first line
            com.*
            // Comment last line
        """.trimIndent(),
        setOf("com.*")
    )

    @Test
    fun testWildcardInMiddle() = testConfigParsing(
        """
            com.*.bar
        """.trimIndent(),
        setOf("com.*.bar")
    )

    @Test
    fun testMultipleWildcardInMiddle() = testConfigParsing(
        """
            com.*.bar.*
        """.trimIndent(),
        setOf("com.*.bar.*")
    )

    @Test
    fun testWhitespaceThrows() = testConfigParsingThrows(
        """
            com. ab.*
        """.trimIndent()
    )

    @Test
    fun testInlineCommentThrows() = testConfigParsingThrows(
        """
            com.foo.* //comment
        """.trimIndent()
    )

    @Test
    fun testIllegalCharacterThrows() = testConfigParsingThrows(
        """
            com.foo!.bar //comment
        """.trimIndent()
    )
}
