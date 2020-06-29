import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe


class LanguageTemplateTests : FunSpec({
    context("utility function tests") {
        test("remove margin") {
            """
                l1
                   l2
                      l3
                   l4
            """.trimIndent().lines().removeMargin() shouldBe listOf("l1","l2","l3","l4")
        }
        test("standard minimize") {
            """
                #include <iostream>
                #include <regex>
                #include <fstream>

                std::string desanitize(std::string str) {
                  // undo backslash sanitization
                  return std::regex_replace(tmp2,std::regex("BS"),"\\");
                }

                int main() {
                  std::ofstream outfile;
                  //TODO: change to actual file name
                  outfile.open("out");
                  outfile << desanitize("[str]");
                  outfile.close();
                }
            """.trimIndent().standardMinimize() shouldBe """
                #include <iostream>
                #include <regex>
                #include <fstream>
                std::string desanitize(std::string str) { return std::regex_replace(tmp2,std::regex("BS"),"\\"); } int main() { std::ofstream outfile; outfile.open("out"); outfile << desanitize("[str]"); outfile.close(); }
            """.trimIndent()
        }
        context("getFileNameForLanguage") {
            test("in LanguageTemplate enum") {
                getFileNameForLanguage("C#") shouldBe "SDQR.cs"
                getFileNameForLanguage("C++") shouldBe "SDQR.cpp"
            }
            test("for base language") {
                getFileNameForLanguage("Python") shouldBe "SDQR.py"
            }
        }
    }
})