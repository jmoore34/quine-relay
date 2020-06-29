p="#include <iostream>^nP#include <regex>^nP#include <fstream>^nPstd::string desanitize(std::string str) { std::string tmp1 = std::regex^replace(str,std::regex(^qP^qCP^qP),^qP^bP^qP^qP); std::string tmp2 = std::regex^replace(tmp1,std::regex(^qP^nCP^qP),^qP^bPn^qP); return std::regex^replace(tmp2,std::regex(^qP^bCP^qP),^qP^bP^bP^qP); } int main() { std::ofstream outfile; outfile.open(^qPSDQR.cs^qP); outfile << desanitize(^qPclass MainClass { static void Main(string[] args) { System.IO.File.WriteAllText(^qCPSDQR.py^qCP, ^qCPp=^qC#^RWP^qC#; p2=p.replace(^qC#^bC#u005e^qC#,^qC#^bC#u005f^qC#).replace(^qC#^bC#u005f^bC#u0062^bC#u0050^qC#,^qC#^bC#^bC#^qC#).replace(^qC#^bC#u005f^bC#u006e^bC#u0050^qC#,^qC#^bC#n^qC#).replace(^qC#^bC#u005f^bC#u0071^bC#u0050^qC#,^qC#^bC#^qC#^qC#).replace(^qC#^bC#u005f^bC#u0052^bC#u0057^bC#u0050^qC#,p); f=open(^qC#SDQR.cpp^qC#,^qC#w+^qC#); f.write(p2); f.close(); print(^qC#@qP... -> [Python] -> C++ -> ... -> C# -> Python -> ...@qPCurrent language: Python. Next language: C++.@qPWrote SDQR.cpp to current directory.@qPCompile: g++ --std=c++11 SDQR.cpp -o SDQR-c++@qPRun: ./SDQR-c++@qP^qC#.replace(^qC#^bC#u0040^bC#u0071^bC#u0050^qC#,^qC#^bC#n^qC#))^qCP.Replace(^qCP^qC#^qCP, ^qCP^bCP^qCP^qCP).Replace(^qCP^nC#^qCP,^qCP^bCPn^qCP).Replace(^qCP^bC#^qCP,^qCP^bCP^bCP^qCP)); System.Console.Write(^qCP^nC#... -> [C#] -> Python -> ... -> C++ -> C# -> ...^nC#Current language: C#. Next language: Python.^nC#Wrote SDQR.py to current directory.^nC#Run: python3 SDQR.py^nC#^qCP.Replace(^qCP^nC#^qCP,^qCP^bCPn^qCP)); } }^qP); outfile.close(); std::cout << desanitize(^qP^nCP... -> [C++] -> C# -> ... -> Python -> C++ -> ...^nCPCurrent language: C++. Next language: C#.^nCPWrote SDQR.cs to current directory.^nCPCompile: mcs -out:SDQR-C#.exe SDQR.cs main.cs^nCPRun: mono SDQR-C#.exe^nCP^qP); }"; p2=p.replace("\u005e","\u005f").replace("\u005f\u0062\u0050","\\").replace("\u005f\u006e\u0050","\n").replace("\u005f\u0071\u0050","\"").replace("\u005f\u0052\u0057\u0050",p); f=open("SDQR.cpp","w+"); f.write(p2); f.close(); print("@qP... -> [Python] -> C++ -> ... -> C# -> Python -> ...@qPCurrent language: Python. Next language: C++.@qPWrote SDQR.cpp to current directory.@qPCompile: g++ --std=c++11 SDQR.cpp -o SDQR-c++@qPRun: ./SDQR-c++@qP".replace("\u0040\u0071\u0050","\n"))