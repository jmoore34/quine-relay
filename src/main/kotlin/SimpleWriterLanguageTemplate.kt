import java.io.File

enum class SimpleWriterLanguageTemplate(val langName: String, val fileName: String) {
    C("C", "SDQR.c") {
        override fun createWriterProgram(fileContent: String, fileName: String, printOutput: String): String {
            val backslashReplacement = "_bC-"
            val newlineReplacement = "_nC-"
            val doubleQuoteReplacement = "_qC-"
            val sanitizedContent = fileContent.replace("\\",backslashReplacement) // replace backslash first!
                .replace("\n",newlineReplacement)
                .replace("\"",doubleQuoteReplacement)
            val sanitizedOutput = printOutput.replace("\n",newlineReplacement)
            return """
                #include <stdio.h>
                #include <string.h>
                #include <stdlib.h>
                
                char * desanitize(const char *str) {
                  char *output = malloc(strlen(str));
                  char *output_current_pos = output;
                
                  for (char *ptr = (char*) str; *ptr != '\0'; ptr++) {
                    // by default, the character we want to add to the output
                    // is just the current character in the input string
                    char output_char = *ptr;
                
                    // however if we encounter an escape code for this language (C)
                    // i.e. in the format _nC- (newline), _qC- ("), or _bC- (\)
                    // then we will override the output char
                    if (*ptr == '_' && ptr[2] == 'C' && ptr[3] == '-') {
                      if (ptr[1] == 'b')
                        output_char = '\\';
                      else if (ptr[1] == 'q')
                        output_char = '"';
                      else if (ptr[1] == 'n')
                        output_char = '\n';
                
                      // advance the input pointer so that we ignore the rest
                      // of the escape sequence
                      ptr += 3;
                    }
                
                    // now we just insert the output character and then increment 
                    // the output pointer
                    *output_current_pos++ = output_char;
                  }
                
                  return output;
                }
                
                int main(void) {
                  FILE *file = fopen("$fileName", "w");
                  fprintf(file, "%s", desanitize("$sanitizedContent"));
                  printf("%s\n", desanitize("$sanitizedOutput"));
                }
            """.standardMinimize()
        }

        override val compileInstructions = """
            $compileStr gcc SDQR.c -o SDQR-c ${boldWhite("or")} clang SDQR.c -o SDQR-c
            $runStr ./SDQR-c
        """.trimIndent()
    },
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
                  std::cout << desanitize("$sanitizedOutput") << std::endl;
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
                        System.Console.Write("$sanitizedOutput".Replace("$newlineReplacement","\n") + "\n");
                    }
                }
            """.standardMinimize()
        }
        override val compileInstructions = """
            $packageStr sudo apt install mono-mcs
            $compileStr mcs -out:SDQR-C#.exe SDQR.cs
            $runStr mono SDQR-C#.exe
        """.trimIndent()
    },
    Lisp("Lisp", "SDQR.lisp") {
        override fun createWriterProgram(fileContent: String, fileName: String, printOutput: String): String {
            val backslashReplacement = "_bL"
            val newlineReplacement = "_nL"
            val doubleQuoteReplacement = "_qL"
            val sanitizedContent = fileContent.replace("\\",backslashReplacement) // replace backslash first!
                .replace("\n",newlineReplacement)
                .replace("\"",doubleQuoteReplacement)
            val sanitizedOutput = printOutput.replace("\n",newlineReplacement) + newlineReplacement
            return """
                ; gnu clisp  2.49.60
                
                ; modified replace-all from http://cl-cookbook.sourceforge.net/strings.html
                (defun replace-all (string part replacement-char &key (test #'char=))
                  (with-output-to-string (out)
                    (loop with part-length = (length part)
                          for old-pos = 0 then (+ pos part-length)
                          for pos = (search part string
                                            :start2 old-pos
                                            :test test)
                          do (write-string string out
                                           :start old-pos
                                           :end (or pos (length string)))
                          when pos do (write-char replacement-char out)
                          while pos)))
                
                
                (defun desanitize (string)
                    (replace-all (replace-all (replace-all string "$newlineReplacement" #\linefeed) "$doubleQuoteReplacement" #\") "$backslashReplacement" #\\))
                    
                (with-open-file (stream "next" :direction :output
                                               :if-exists :supersede)
                    (format stream "~d" (desanitize "$sanitizedContent")))

                (format t "~d" (desanitize "$sanitizedOutput")) 
            """.standardMinimize(true)
        }
        override val compileInstructions = """
            $packageStr sudo apt install sbcl
            $runStr sbcl --script SDQR.lisp
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
            println("---------------")
        }

        /**
         * Test the sanitizing capabilities of all the language templates.
         */
        fun testAll() {
            val testContent = """
                ${"""print("\n").substr("\"\"")""".rainbowize()}
                ${boldWhite("""\n\n\n 😊 \"\"\" @"" ${"\"\"\""}""")}
                ${termColors.underline("""${"\t"}<- \t""")}
             """.trimIndent()

            val nextFileName = "nextFile.txt"


            println(
                """
                --- Expected file output (to $nextFileName): ---
                $testContent
            """.removeMargin()
            )

            SimpleWriterLanguageTemplate.values().forEach {
                val file = File(it.fileName)
                file.writeText(it.createWriterProgram(testContent, nextFileName, getOutputForLanguage(it.langName)))
                println("Wrote ${file.absolutePath} to disk.")
            }
        }
    }
}

