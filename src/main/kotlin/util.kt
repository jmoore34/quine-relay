import com.github.ajalt.mordant.TermColors
import kotlin.math.floor


/**
 * Utility constants
 * Formatted strings for use in each language's compile insturctions
 */
val termColors = TermColors(TermColors.Level.ANSI256)
val boldYellow = (termColors.bold + termColors.brightYellow)
val compileStr = boldYellow("[Compile]")
val runStr = boldYellow("[Run]")

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
        [Self-Documenting Quine Relay] ${termColors.gray("~")} ${termColors.dim("jmoore34.github.io")}
        ... -> ${"[$langName]".colorByLangName(TextStyle.STRONGER)} -> ${nextLangName.colorByLangName(TextStyle.STRONG)} -> ... -> ${prevLangName.colorByLangName(TextStyle.STRONG)} -> ${langName.colorByLangName(TextStyle.STRONG)} -> ...
        Current language: ${langName.colorByLangName()}. Next language: ${nextLangName.colorByLangName()}.
        Wrote ${getFileNameForLanguage(nextLangName)} to current directory.
        ${getCompileInstructionsForLanguage(nextLangName)}
    """.removeMargin()
            // add in some more colors
        .replace("...", termColors.gray("..."))
        .replace("->", termColors.dim("->"))
}

enum class TextStyle {LIGHT, STRONG, STRONGER}

/**
 * Given a string containing a language name, colors it using ANSI escape codes
 * Each language has a corresponding color related to its order in the sequence
 */
fun String.colorByLangName(style: TextStyle = TextStyle.LIGHT): String {
    // a list of all the language names in the relay
    val langNames = listOf(BaseLanguageTemplate.langName) + (SimpleWriterLanguageTemplate.values().map { it.langName })

    val index = langNames.indexOfFirst { this.contains(it) } // get the index of the language name contained in this string
    val percentage = 1.0 * index / (langNames.size)
    val hue = (percentage * 360).toInt()
    val saturation = 100
    val lightness = if (style == TextStyle.LIGHT) 94 else 80

    val base = termColors.hsl(hue, saturation, lightness)(this)
    return if (style == TextStyle.STRONGER) termColors.bold(base) else base
}

/**
 * Given a string, turn every character into an hex escape sequence
 * Due to the circular nature of the quine, useful for avoiding replacing escape sequences
 * in the wrong places (e.g. in the "replace with" parameter of a replace function)
 */
fun String.unicodeEscaped(): String = this.map {
    "\\u00" + it.toByte().toString(16)
}.joinToString("")