import java.io.File

enum class SimpleWriterLanguageTemplate(val langName: String, val fileName: String) {
    Cpp("C++","SDQR.cpp") {
        override fun createWriterProgram(fileContent: String, fileName: String, printOutput: String): String {
            val backslashReplacement = "_bCP"
            val newlineReplacement = "_nCP"
            val doubleQuoteReplacement = "_qCP"
            val sanitizedContent = fileContent.replace("\\",backslashReplacement) // replace backslash first!
                .replace("\n",newlineReplacement)
                .replace("\"",doubleQuoteReplacement)
            val sanitizedOutput = printOutput.replace("\n",newlineReplacement)
            return """
                #include <iostream>
                #include <regex>
                #include <fstream>

                std::string desanitize(std::string str) {
                  std::string tmp1 = std::regex_replace(str,std::regex("$doubleQuoteReplacement"),"\"");
                  std::string tmp2 = std::regex_replace(tmp1,std::regex("$newlineReplacement"),"\n");
                  return std::regex_replace(tmp2,std::regex("$backslashReplacement"),"\\");
                }

                int main() {
                  std::ofstream outfile;
                  outfile.open("$fileName");
                  outfile << desanitize("$sanitizedContent");
                  outfile.close();
                  std::cout << desanitize("$sanitizedOutput");
                }
            """.standardMinimize()

        }

        override val compileInstructions = """
            $compileStr g++ --std=c++11 SDQR.cpp -o SDQR-c++
            $runStr ./SDQR-c++
        """.trimIndent()
    },
    Cs("C#","SDQR.cs") {
        override fun createWriterProgram(fileContent: String, fileName: String, printOutput: String): String {
            val backslashReplacement = "_bC#"
            val newlineReplacement = "_nC#"
            val doubleQuoteReplacement = "_qC#"
            val sanitizedContent = fileContent.replace("\\",backslashReplacement) // replace backslash first!
                .replace("\n",newlineReplacement)
                .replace("\"",doubleQuoteReplacement)
            val sanitizedOutput = printOutput.replace("\n",newlineReplacement)
            return """
                class MainClass {
                    static void Main(string[] args) {
                        System.IO.File.WriteAllText("$fileName", "$sanitizedContent".Replace("$doubleQuoteReplacement", "\"").Replace("$newlineReplacement","\n").Replace("$backslashReplacement","\\"));
                        System.Console.Write("$sanitizedOutput".Replace("$newlineReplacement","\n"));
                    }
                }
            """.standardMinimize()
        }
        override val compileInstructions = """
            $compileStr mcs -out:SDQR-C#.exe SDQR.cs main.cs
            $runStr mono SDQR-C#.exe
        """.trimIndent()
    };

    abstract fun createWriterProgram(fileContent: String, fileName: String, printOutput: String): String
    abstract val compileInstructions: String

    companion object { // static methods
        val langNames = listOf(BaseLanguageTemplate.langName) + (SimpleWriterLanguageTemplate.values().map { it.langName })

        /**
         * Print the associated colors of each language
         * Used for testing
         */
        fun testColors() = langNames.forEach {
            println(
                "Light: ${it.colorByLangName(TextStyle.LIGHT)} Strong: ${it.colorByLangName(TextStyle.STRONG)} Stronger: ${it.colorByLangName(
                    TextStyle.STRONGER
                )} "
            )
        }

        /**
         * Print the output for each language template
         * Used for testing
         */
        fun testOutputs() = langNames.forEach {
            println(getOutputForLanguage(it))
        }

        /**
         * Test the sanitizing capabilities of all the language templates.
         */
        fun testAll() {
            val testContent = """
                print("\n").substr("\"\"")
                \n\n\n 😊 \"\"\" @"" ${"\"\"\""}
                ${"\t"}<- \t
             """.trimIndent()

            val nextFileName = "nextFile.txt"

            val testMsg = """
                ... -> [lang1] -> lang2 -> ... -> lang1
                compile & run instructions
            """.trimIndent()

            println(
                """
                --- Expected file output (to $nextFileName): ---
                $testContent
                
                --- Expected console output: ---
                $testMsg
            """.removeMargin()
            )

            SimpleWriterLanguageTemplate.values().forEach {
                val file = File(it.fileName)
                file.writeText(it.createWriterProgram(testContent, nextFileName, testMsg))
                println("Wrote ${file.absolutePath} to disk.")
            }
        }
    }
}

