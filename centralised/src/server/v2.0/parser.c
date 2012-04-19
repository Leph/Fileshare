#include <stdio.h>
#include <string.h>
#include "parser.h"

//int yyparse();
//int readInputForLexer( char *buffer, int *numBytesRead, int maxBytesToRead );
//extern struct commandLine command;

/*static*///int globalReadOffset;
// Text to read:
//extern/*static*/ const char *globalInputText;/* = "announce listen 4444 have [  \"Le Seigneur Des Anneaux.avi\"   699000000  1024   hkj234f5s2h6fd7f        \"Pulp Fiction.mkv\"        701000000   1024   hkj234d5s2j6fd7p \"Batman - The Dark Knight.mp4\" 1400000000 2048 h4k2l6h6gf4oas9 ]";*/
//static const char *globalInputText = "look [filename=HIMYM-S07EP15.avi filesize>200000000]";
//static const char *globalInputText = "look [filesize>200000000 filename=HIMYM-S07EP15.avi]";
//static const char *globalInputText = "getfile h4jl6v1gf3jp0q8";
//static const char *globalInputText = "update seed [gh3kl1g3 df56g2gbl7 fg4g2l6ggn2] leech [f4sg5fs1sdg4 gr4f3v5gh7]";
//static const char *globalInputText = "update leech [f4sg5fs1sdg4 gr4f3v5gh7] seed [gh3kl1g3 df56g2gbl7 fg4g2l6ggn2]";

/*int main()
{
    	globalReadOffset = 0;
	
	command.type = 0;
	command.port = 0;
	command.filesNumber = 0;
	command.seedKeysNumber = 0;
	command.leechKeysNumber = 0;
	command.isSeeder = 0;
	command.isLeecher = 0;
    	yyparse();
	printf("\n");
	affiche();
    	return 0;
}*/

int readInputForLexer( char *buffer, int *numBytesRead, int maxBytesToRead ) {
    int numBytesToRead = maxBytesToRead;
    int bytesRemaining = strlen(globalInputText)-globalReadOffset;
    int i;
    if ( numBytesToRead > bytesRemaining ) { numBytesToRead = bytesRemaining; }
    for ( i = 0; i < numBytesToRead; i++ ) {
        buffer[i] = globalInputText[globalReadOffset+i];
    }
    *numBytesRead = numBytesToRead;
    globalReadOffset += numBytesToRead;
    return 0;
}


void affiche(struct commandLine *comman)
{
	enum type type = comman->type;
	switch (type)
	{
		case ANNOUNCE:
			printf("commande de type ANNOUNCE:\n\n");
			printf("port de connexion du client: %d\n",comman->port);
			printf("nombre de fichiers: %d\n\n",comman->filesNumber);
			int i;
			if(comman->filesNumber)
			for(i=0;i<comman->filesNumber;i++)
			{
				printf("fichier: %s\n",comman->fileNames[i]);
				printf("taille: %d Mo\n",(int)((double)comman->lengths[i]/1000000));
                                printf("découpage: %d octets\n",comman->pieceSize[i]);
                                printf("md5: %s\n",comman->keys[i]);
				printf("\n");
			}
			break;

		case LOOK:
			printf("commande de type LOOK:\n\n");
			if(comman->fileName)
				printf("critère: nom_fichier=%s\n",comman->fileName);
			if(comman->supTo)
				printf("critère: taille>%dMo\n",(int)((double)comman->supTo/1000000));
			if(comman->infTo)
				printf("critère: taille<%dMo\n",(int)((double)comman->infTo/1000000));
			break;

		case GETFILE:
			printf("commande de type GETFILE:\n\n");
			printf("clé recherchée: %s\n",comman->getKey);
			break;

		case UPDATE:
			printf("commande de type UPDATE:\n\n");
			if(comman->isSeeder)
			{
				printf("le client possède %d fichiers complets",comman->seedKeysNumber);
				printf(" de clés:\n");
				int j;
				for(j=0;j<comman->seedKeysNumber;j++)
					printf("%s\n",comman->seedKeys[j]);
			}
			if(comman->isLeecher)
			{
                                printf("le client possède %d fichiers incomplets",comman->leechKeysNumber);
                                printf(" de clés:\n");
				int j;
                                for(j=0;j<comman->leechKeysNumber;j++)
                                        printf("%s\n",comman->leechKeys[j]);
			}
			break;
	
		default:
			printf("type de commande non affecté.\n");
	}
	printf("\n");
}


int free_commandLine()
{
	return 1;
}


struct commandLine *cloneCommandLine()
{
	struct commandLine *clone = malloc(sizeof(struct commandLine));

	switch(command.type)
	{
	
	case ANNOUNCE:
		clone->type = command.type;
		clone->port = command.port;
		clone->filesNumber = command.filesNumber;
	
		int i = 0;
		for(i=0;i<command.filesNumber;i++)
		{
			clone->fileNames[i] = strdup(command.fileNames[i]);
			clone->lengths[i] = command.lengths[i];
			clone->pieceSize[i] = command.pieceSize[i];
			clone->keys[i] = strdup(command.keys[i]);
		}
		break;

	case LOOK:
		clone->type = command.type;
		clone->fileName = strdup(command.fileName);
		clone->supTo = command.supTo;
		clone->infTo = command.infTo;
		break;

	case GETFILE:
		clone->type = command.type;
		clone->getKey = strdup(command.getKey);		
		break;

	case UPDATE:
		clone->type = command.type;
		clone->seedKeysNumber = command.seedKeysNumber;
		clone->leechKeysNumber = command.leechKeysNumber;
		clone->isSeeder = command.isSeeder;
		clone->isLeecher = command.isLeecher;

		int j;
                for(j=0;j<clone->seedKeysNumber;j++)
                	clone->seedKeys[j] = strdup(command.seedKeys[j]);

                for(j=0;j<clone->leechKeysNumber;j++)
                        clone->leechKeys[j] = strdup(command.leechKeys[j]);
		break;

	default:
		printf("error: clonage d'une commande erronée.\n");
	}
	globalReadOffset=0;
	return clone;
}
		



void freeCommandLine(){
        int i=0;
	command.type = 0;
	command.port = 0;
        command.filesNumber = 0;

	for(i=0;i<MAX_FILES_NUMBER;i++){
		free(command.fileNames[i]);
        	command.fileNames[i] = NULL;
		free(command.keys[i]);
        	command.keys[i] = NULL;


	}
	free(command.fileName);
        command.fileName = NULL;
        command.supTo = 0;
        command.infTo = 0;

	free(command.getKey);
        command.getKey = NULL;

        command.isSeeder = 0;
        command.seedKeysNumber = 0;
        command.isLeecher = 0;
        command.leechKeysNumber = 0;

}
void initCommandLine()
{
        command.type = 0;
	command.port = 0;
        command.filesNumber = 0;

        command.fileName = NULL;
        command.supTo = 0;
        command.infTo = LONG_MAX;

        command.getKey = NULL;

        command.isSeeder = 0;
        command.seedKeysNumber = 0;
        command.isLeecher = 0;
        command.leechKeysNumber = 0;
}






















