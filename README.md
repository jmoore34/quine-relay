# Self Documenting Quine Relay

## What is it?

A Python program that outputs a C program that outputs a C++ program (through several more languages) that outputs a Lisp program that outputs the original Python program.

Full list: Python, C, C++, C#, Lisp

### Quine Rules

This quine relay follows quine rules. That is, each of the programs in the chain may not take any input. This means:

* no commandline arguments
* no reading files from disk (especially not source code files)
* no accessing the internet

### Self-documenting

Each program in the chain outputs helpful documentation about the current position in the cycle and how to compile and run the next program in the cycle.

## Demo

View a terminal recording of the quine relay [here](https://bit.ly/quinerelay).

## Running it

Run the following in the UNIX terminal of your choice:
```bash
curl https://raw.githubusercontent.com/jmoore34/quine-relay/master/SDQR.py
python3 SDQR.py
```
Each program generated will offer
compilation instructions for the next program in the chain.
