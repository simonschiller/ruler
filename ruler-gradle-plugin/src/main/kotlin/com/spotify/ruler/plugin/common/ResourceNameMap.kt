package com.spotify.ruler.plugin.common

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.Reader
import java.text.ParseException

/**
 * Responsible for de-obfuscating resource file names. This class was made with
 * DexGuard's resource name mapping in mind.
 */
class ResourceNameMap {

    private val obfuscatedToClearNameMap: HashMap<String, String> by lazy { HashMap() }

    fun getResourceName(resourceName: String): String {
        return obfuscatedToClearNameMap[resourceName] ?: resourceName
    }

    fun readFromFile(mapFile: File?) {
        readFromReader(FileReader(mapFile))
    }

    fun readFromReader(mapReader: Reader) {
        obfuscatedToClearNameMap.clear()
        BufferedReader(mapReader).use { reader ->
            var line = reader.readLine()
            while (line != null) {
                // We're only interested in resource file name mappings for now.
                val trimmed = line.trim()
                if (trimmed.isEmpty() || !trimmed.startsWith("res/")) {
                    line = reader.readLine()
                    continue
                }

                // Resource file name mappings are of the form:
                //    res/anim/abc_grow_fade_in_from_bottom.xml -> [res/raw/a.xml]
                val sep = line.indexOf(" -> ")
                if (sep == -1 || sep + 5 >= line.length) {
                    throw ParseException("Error parsing class line: '$line'", 0)
                }
                val clearName = line.substring(0, sep)
                val obfuscatedName = line.substring(sep + 4 + (1 /* ..for the opening bracket */), line.length - 1)

                // We want resource names to mimic the APK structure i.e.,
                // with a leading '/' as the "res" directory is placed at
                // APK root.
                obfuscatedToClearNameMap["/$obfuscatedName"] = "/$clearName"

                line = reader.readLine()
            }
        }
    }
}
