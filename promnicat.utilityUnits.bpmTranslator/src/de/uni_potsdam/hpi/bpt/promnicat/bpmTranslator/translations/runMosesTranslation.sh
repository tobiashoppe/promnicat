#!/bin/bash

# script to invoke the moses decoder given config file (first param)
# produces a specified number (second param) of possible translations of content of 'inputFile' in 'outputFile'
./bin/translations/moses -i bin/translations/inputFile -n-best-list bin/translations/outputFile $2 distinct -f $1
rm bin/translations/inputFile
