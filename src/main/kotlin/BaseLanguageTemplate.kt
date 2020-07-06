
object BaseLanguageTemplate {
    val langName = "Python"
    val PAYLOAD_PLACEHOLDER = "_RWP"
    val fileName = "SDQR.py"
    val compileInstructions = """
        $runStr python3 SDQR.py
    """.trimIndent()

    fun createQuineProgram(fileContent: String, printOutput: String): String {
        val backslashReplacement = "_bP"
        val newlineReplacement = "_nP"
        val contentDoubleQuoteReplacement = "_qP"
        val outputDoubleQuoteReplacement = "@qP"

        val sanitizedContent = fileContent.replace("\\", backslashReplacement)
            .replace("\n", newlineReplacement)
            .replace("\"", contentDoubleQuoteReplacement)
            .replace("_","^") // Deactivate all escape codes (incl. for other languages) in the payload

        val sanitizedOutput = printOutput.replace("\n",outputDoubleQuoteReplacement)

        return """
            ## Note: We use semicolons here because the minimizer will remove line breaks
            
            ## Payload is the fully sanitized (escape codes disabled) file content
            p="$sanitizedContent";
            
            ## Desanitized version of the payload
            ## This is what we actually print to file
            ## Replaces ^ with _ to make the escape codes (incl. for other languages) active
            ## Also replaces in newlines, etc.
            p2=p.replace("${"^".unicodeEscaped()}","${"_".unicodeEscaped()}").replace("${backslashReplacement.unicodeEscaped()}","\\").replace("${newlineReplacement.unicodeEscaped()}","\n").replace("${contentDoubleQuoteReplacement.unicodeEscaped()}","\"").replace("${PAYLOAD_PLACEHOLDER.unicodeEscaped()}",p);
            f=open("${SimpleWriterLanguageTemplate.values().first().fileName}","w+");
              f.write(p2);
            f.close();
            print("$sanitizedOutput".replace("${outputDoubleQuoteReplacement.unicodeEscaped()}","\n"))
        """.standardMinimize()
    }

    fun createPlaceholderProgram(output: String): String = createQuineProgram(PAYLOAD_PLACEHOLDER, output)
}