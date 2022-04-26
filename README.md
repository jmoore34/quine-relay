# Self Documenting Quine Relay

[![Build Status](https://img.shields.io/github/stars/jmoore34/quine-relay.svg)](https://github.com/jmoore34/quine-relay)
[![Activity](https://img.shields.io/github/last-commit/jmoore34/quine-relay.svg)](https://github.com/jmoore34/quine-relay/commits)

## What is it?

A Python program that outputs a C program that outputs a C++ program that outputs a C# program that outputs a Lisp program that outputs the exact original Python program.

![Flowchart](https://user-images.githubusercontent.com/1783464/158309575-ff9f9360-1959-4407-8f4a-390862b50711.png)

### Quine Rules

This quine relay follows quine rules. That is, each of the programs in the chain may not take any input. This means:

* no commandline arguments
* no reading files from disk (especially not source code files)
* no accessing the internet

### Self-documenting

Each program in the chain outputs helpful documentation about the current position in the cycle and how to compile and run the next program in the cycle.

## Demo

View a terminal recording of the quine relay [here](https://bit.ly/quinerelay).

[![asciicast](https://asciinema.org/a/361022.svg)](https://asciinema.org/a/361022)

## Running it

Run the following in the UNIX terminal of your choice:
```bash
curl https://raw.githubusercontent.com/jmoore34/quine-relay/master/SDQR.py
python3 SDQR.py
```
Each program generated will offer
compilation instructions for the next program in the chain.
