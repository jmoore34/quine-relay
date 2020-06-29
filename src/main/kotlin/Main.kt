import java.io.File



fun main() {

    println("Generating quine relay...")

    /** The list of language templates, in *reversed* order
     *  That is, the opposite order of the order they appear to the user in the relay
     *  Reverse order is needed because we start with the base program, then we build our way up.
     *  That is, we create a program that just prints the base program. Then, we create a program
     *  that prints *that* program, and we continue doing that until we get back to the base language.
     *  Then, we wrap the base language around that program, but substitute in the program that makes a program...etc
     *  as the payload which it writes.
     */
    val languageTemplates = SimpleWriterLanguageTemplate.values().reversed()

    val firstLangAfterBase = SimpleWriterLanguageTemplate.values().first()

    /** The raw, unsubstituted code of the base language. The parameter ensures it prints the compile
     *  instructions for compiling the next language.
     */
    val baseLanguageRawCode: String = BaseLanguageTemplate.createPlaceholderProgram(getOutputForLanguage(BaseLanguageTemplate.langName))

    /**
     * Compute the payload.
     * For example, in the quine relay Python (base) -> C -> Java -> Kotlin (-> Python),
     * we start with the base (Python), then we create a Kotlin program to write that Python
     * program, supplying it with the existing payload (the Python source) as well as the compilation
     * instructions and file name for Python. Then, we create a Java program to write that Kotlin
     * program, supplying it with the current payload (the Kotlin program we just made) as well
     * as the file name and compilation instructions for that Kotlin program. Then we make a C
     * program that writes the Java program, giving it Java's compile instructions and file name.
     */
    val payload = languageTemplates.foldIndexed(baseLanguageRawCode) { index, acc: String, languageTemplate ->
        val currentLanguageTemplate = languageTemplates[index]

        // The previous language is the last language that was generated
        // Remember that we a generating the sequence in reverse, so the previous lang in the
        // loop would actually come next in the sequence from the user's POV, so that's why we use its
        // file name as we generate the current language
        val prevLangName = if (index==0) BaseLanguageTemplate.langName else languageTemplates[index-1].langName

        currentLanguageTemplate.createWriterProgram(acc, getFileNameForLanguage(prevLangName), getOutputForLanguage(currentLanguageTemplate.langName))
    }


    /**
     * Now that we have the payload, we complete the circuit by substituting in the payload in the
     * base (Python) source. Hence, we have our finished quine, which looks something like this (pseudocode):
     *
     * p (payload variable) = <payload gets sanitized and put here>
     * write payload.substitute(<payload placeholder>, p) to file
     * print <output (i.e. compilation instructions for next program) >
     */
    val quineRelay = BaseLanguageTemplate.createQuineProgram(payload, getOutputForLanguage(BaseLanguageTemplate.langName))

    val file = File(BaseLanguageTemplate.fileName)
    file.writeText(quineRelay)

    println("Wrote quine relay to ${file.absolutePath}")
}



