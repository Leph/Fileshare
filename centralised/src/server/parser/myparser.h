#ifndef _MYPARSER_H_
#define _MYPARSER_H_

#include <stdlib.h>

#define MAX_FILES_NUMBER 20

enum type {ANNOUNCE=1,LOOK=2,GETFILE=3,UPDATE=4};

struct commandLine
{
        enum type type;

        int port;
        int filesNumber;
        char* fileNames[MAX_FILES_NUMBER];
        int lengths[MAX_FILES_NUMBER];
        int pieceSize[MAX_FILES_NUMBER];
        char *keys[MAX_FILES_NUMBER];

        char *fileName;
        int supTo;
        int infTo;

        char *getKey;

        int seedKeysNumber;
        int leechKeysNumber;
        int isSeeder;
        int isLeecher;
        char* seedKeys[MAX_FILES_NUMBER];
        char* leechKeys[MAX_FILES_NUMBER];
};


int readInputForLexer(char* buffer,int *numBytesRead,int maxBytesToRead);
void affiche();
int free_commandLine();

#endif
