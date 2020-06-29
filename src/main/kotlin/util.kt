/**
 * Given a list of lines, removes leading whitespace from every line
 */
public fun List<String>.removeMargin() = this.map { it.trimStart() }

/**
 * Like above, but works on a single multiline string
 */
public fun String.removeMargin() = this.lines().removeMargin().joinToString("\n")

/**
 * Given a list of lines,
 * removes blank lines and lines starting directly with a // or ## (i.e. w/ no leading whitespace)
 * Recommended to call removeMargin() first
 */
fun List<String>.removeBlankAndCommentedLines() = this.filter {
    !(it.startsWith("//") || it.startsWith("##") || it.isEmpty())
}

/**
 * Given a list of lines, return a condensed string with as many newlines removed as possible. Specifically,
 * join lines that end in one of [';','{','}']
 * Recommended to call removeMargin and removeBlankAndCommentedLines first
 */
fun List<String>.toCondensedString() = this.reduce { acc, line ->
    if (acc.endsWith(";") || acc.endsWith("{") || acc.endsWith("}"))
        "$acc $line" else "$acc\n$line"
}

/**
 * Given a string of code, apply a standard condensing process:
 * - Remove margins
 * - Remove blank and commented out lines
 * - Remove unneeded line breaks (e.g. after semicolons)
 */
fun String.standardMinimize() = this.lines().removeMargin().removeBlankAndCommentedLines().toCondensedString()

/**
 * Given a language by name, return its compile instructions
 * Supports both the simple language templates in the LanguageTemplate enum as well as the base language
 */
fun getCompileInstructionsForLanguage(langName: String): String {
    return if (langName == BaseLanguageTemplate.langName)
        BaseLanguageTemplate.compileInstructions
    else
        SimpleWriterLanguageTemplate.values().first { it.langName == langName }.compileInstructions
}

/**
 * Given a language by name, return the corresponding language template's file name
 * Supports both the simple language templates in the LanguageTemplate enum as well as the base language
 */
fun getFileNameForLanguage(langName: String): String {
    return if (langName == BaseLanguageTemplate.langName)
        BaseLanguageTemplate.fileName
    else
        SimpleWriterLanguageTemplate.values().first { it.langName == langName }.fileName
}

/**
 * Given a language by name, return what output its program should print
 * This contains info about the current position in the relay as well as compilation instructions for the next program
 */
fun getOutputForLanguage(langName: String): String {
    // a list of all the language names in the relay
    val langNames = listOf(BaseLanguageTemplate.langName) + (SimpleWriterLanguageTemplate.values().map { it.langName })

    fun getLangNameByIndex(i: Int): String = langNames[(i + langNames.size) % langNames.size]
    val thisLangIndex = langNames.indexOf(langName)
    val nextLangName = getLangNameByIndex(thisLangIndex + 1)
    val prevLangName = getLangNameByIndex(thisLangIndex - 1)

    return """
        ... -> [$langName] -> $nextLangName -> ... -> $prevLangName -> $langName -> ...
        Current language: $langName. Next language: $nextLangName.
        Wrote ${getFileNameForLanguage(nextLangName)} to current directory.
        ${getCompileInstructionsForLanguage(nextLangName)}
    """.removeMargin()
}

/**
 * Given a string, turn every character into an hex escape sequence
 * Due to the circular nature of the quine, useful for avoiding replacing escape sequences
 * in the wrong places (e.g. in the "replace with" parameter of a replace function)
 */
fun String.unicodeEscaped(): String = this.map {
    "\\u00" + it.toByte().toString(16)
}.joinToString("")