import com.github.ajalt.mordant.TermColors
import kotlin.math.floor


/**
 * Utility constants
 * Formatted strings for use in each language's compile insturctions
 */
val termColors = TermColors(TermColors.Level.ANSI256)
val boldYellow = (termColors.bold + termColors.brightYellow)
val white = termColors.rgb(255,255,255)
val boldWhite = (termColors.bold + white)
val packageStr = boldYellow("[Package]")
val compileStr = boldYellow("[Compile]")
val runStr = boldYellow("[Run]")


// a list of all the language names in the relay
val langNames = listOf(BaseLanguageTemplate.langName) + (SimpleWriterLanguageTemplate.values().map { it.langName })

/**
 * Given a list of lines, removes leading whitespace from every line
 */
fun List<String>.removeMargin() = this.map { it.trimStart() }

/**
 * Like above, but works on a single multiline string
 */
fun String.removeMargin() = this.lines().removeMargin().joinToString("\n")

/**
 * Given a list of lines,
 * removes blank lines and lines starting directly with a // or ## or ; (i.e. w/ no leading whitespace)
 * (## instead of # used to allow for hashbangs)
 * Recommended to call removeMargin() first
 */
fun List<String>.removeBlankAndCommentedLines() = this.filter {
    !(it.startsWith("//") || it.startsWith("##") || it.startsWith(";") || it.isEmpty())
}

/**
 * Given a list of lines, return a condensed string with as many newlines removed as possible. Specifically,
 * join lines that end in one of [';','{','}',')']
 * Alternatively, if 'aggressive' is specified, all lines will be joined no matter what they end with
 * Recommended to call removeMargin and removeBlankAndCommentedLines first
 */
fun List<String>.toCondensedString(aggressive: Boolean = false) = this.reduce { acc, line ->
    if (acc.endsWith(";") || acc.endsWith("{") || acc.endsWith("}") || acc.endsWith(')') || aggressive)
        "$acc $line" else "$acc\n$line"
}

/**
 * Given a string of code, apply a standard condensing process:
 * - Remove margins
 * - Remove blank and commented out lines
 * - Remove unneeded line breaks (e.g. after semicolons)
 * If 'aggressive' is specified, all line breaks will be removed
 */
fun String.standardMinimize(aggressive: Boolean = false) = this.lines().removeMargin().removeBlankAndCommentedLines().toCondensedString(aggressive)

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

fun printAllCompileInstructions() = langNames.forEach {
    println(getCompileInstructionsForLanguage(it))
    println("---------------")
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

    fun getLangNameByIndex(i: Int): String = langNames[(i + langNames.size) % langNames.size]
    val thisLangIndex = langNames.indexOf(langName)
    val nextLangName = getLangNameByIndex(thisLangIndex + 1)
    val prevLangName = getLangNameByIndex(thisLangIndex - 1)

    return """
        ${boldWhite("[Self-Documenting Quine Relay]")} ${termColors.gray("~")} ${"jmoore34.github.io".rainbowize()}
        ... -> ${"[$langName]".colorByLangName(TextStyle.STRONGER)} -> ${nextLangName.colorByLangName()} -> ... -> ${prevLangName.colorByLangName()} -> ${langName.colorByLangName()} -> ...
        Current language: ${langName.colorByLangName()}. Next language: ${nextLangName.colorByLangName()}.
        Wrote ${white(getFileNameForLanguage(nextLangName))} to current directory.
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
fun String.colorByLangName(style: TextStyle = TextStyle.STRONG): String {
    val langName = this.filter { it != '[' && it != ']' }
    val index = langNames.indexOfFirst { it == langName }
    val percentage = 1.0 * index / (langNames.size)
    val hue = (percentage * 360).toInt()
    val saturation = 100
    val lightness = if (style == TextStyle.LIGHT) 94 else 80

    val base = termColors.hsl(hue, saturation, lightness)(this)
    return if (style == TextStyle.STRONGER) termColors.bold(base) else base
}


fun String.rainbowize(): String = this.mapIndexed { index, char ->
    val percentage = 1.0 * index / this.length
    val hue = (360 * percentage).toInt()
    val saturation = 100
    val lightness = 92
    termColors.hsl(hue, saturation, lightness)("$char")
}.joinToString("")

/**
 * Given a string, turn every character into an hex escape sequence
 * Due to the circular nature of the quine, useful for avoiding replacing escape sequences
 * in the wrong places (e.g. in the "replace with" parameter of a replace function)
 */
fun String.unicodeEscaped(): String = this.map {
    "\\u00" + it.toByte().toString(16)
}.joinToString("")