#!/bin/bash

yacc -d parser.y
lex lexer.l
gcc -Wall -o parser ./*.c
