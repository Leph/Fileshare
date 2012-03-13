#include <stdio.h>
#include <string.h>
#include "myparser.h"

int yyparse();
int readInputForLexer( char *buffer, int *numBytesRead, int maxBytesToRead );
extern struct commandLine command;

static int globalReadOffset;
// Text to read:
static const char *globalInputText = "announce listen 4444 have [  \"Le Seigneur Des Anneaux.avi\"   699000000  1024   hkj234f5s2h6fd7f        \"Pulp Fiction.mkv\"        701000000   1024   hkj234d5s2j6fd7p \"Batman - The Dark Knight.mp4\" 1400000000 2048 h4k2l6h6gf4oas9 ]";
//static const char *globalInputText = "look [filename=HIMYM-S07EP15.avi filesize>200000000]";
//static const char *globalInputText = "look [filesize>200000000 filename=HIMYM-S07EP15.avi]";
//static const char *globalInputText = "getfile h4jl6v1gf3jp0q8";
//static const char *globalInputText = "update seed [gh3kl1g3 df56g2gbl7 fg4g2l6ggn2] leech [f4sg5fs1sdg4 gr4f3v5gh7]";
//static const char *globalInputText = "update leech [f4sg5fs1sdg4 gr4f3v5gh7] seed [gh3kl1g3 df56g2gbl7 fg4g2l6ggn2]";

int main()
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
}

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


void affiche()
{
	enum type type = command.type;
	switch (type)
	{
		case ANNOUNCE:
			printf("commande de type ANNOUNCE:\n\n");
			printf("port de connexion du client: %d\n",command.port);
			printf("nombre de fichiers: %d\n\n",command.filesNumber);
			int i;
			if(command.filesNumber)
			for(i=0;i<command.filesNumber;i++)
			{
				printf("fichier: %s\n",command.fileNames[i]);
				printf("taille: %d Mo\n",(int)((double)command.lengths[i]/1000000));
                                printf("découpage: %d octets\n",command.pieceSize[i]);
                                printf("md5: %s\n",command.keys[i]);
				printf("\n");
			}
			break;

		case LOOK:
			printf("commande de type LOOK:\n\n");
			if(command.fileName)
				printf("critère: nom_fichier=%s\n",command.fileName);
			if(command.supTo)
				printf("critère: taille>%dMo\n",(int)((double)command.supTo/1000000));
			if(command.infTo)
				printf("critère: taille<%dMo\n",(int)((double)command.infTo/1000000));
			break;

		case GETFILE:
			printf("commande de type GETFILE:\n\n");
			printf("clé recherchée: %s\n",command.getKey);
			break;

		case UPDATE:
			printf("commande de type UPDATE:\n\n");
			if(command.isSeeder)
				printf("le client possède %d fichiers complets",command.seedKeysNumber);
				printf(" de clés:\n");
				int j;
				for(j=0;j<command.seedKeysNumber;j++)
					printf("%s\n",command.seedKeys[j]);
			if(command.isLeecher)
                                printf("le client possède %d fichiers incomplets",command.leechKeysNumber);
                                printf(" de clés:\n");
                                for(j=0;j<command.leechKeysNumber;j++)
                                        printf("%s\n",command.leechKeys[j]);
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
	






























