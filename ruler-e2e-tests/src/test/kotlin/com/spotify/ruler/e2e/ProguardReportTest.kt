/*
 * Copyright 2021 Spotify AB
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

package com.spotify.ruler.e2e

import com.google.common.truth.Truth.assertThat
import com.spotify.ruler.e2e.testutil.Correspondence
import com.spotify.ruler.e2e.testutil.FileMatcher
import com.spotify.ruler.e2e.testutil.parseReport
import com.spotify.ruler.models.FileType
import org.junit.jupiter.api.Test

class ProguardReportTest {

    // Use the report generated by the sample ProGuard app for verification
    private val report = parseReport("proguard", "release")

    @Test
    fun `Classes are de-obfuscated correctly`() {
        val lib = report.components.single { component -> component.name == ":sample:lib" }

        assertThat(lib.files).comparingElementsUsing(Correspondence.file()).contains(
            FileMatcher("com.spotify.ruler.sample.lib.ClassToObfuscate", FileType.CLASS)
        )
    }
}