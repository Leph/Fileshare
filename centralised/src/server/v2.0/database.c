#include "database.h"


extern struct dataBase *dataBase;


//initialise un fichier
struct fileData *initFileData()
{
	struct fileData *fileData = malloc(sizeof(struct fileData));
	fileData->key = NULL;
	//fileData->peersNumber = 0;
	//fileData->namesNumber = 0;
	/*
	int i;
	for(i=0;i<MAX_PEERS;i++)
	{
		fileData->peers[i] = NULL;
		fileData->names[i] = NULL;
	}
	*/
	fileData->peers = NULL;
	fileData->names = NULL;			
	fileData->size = 0;
	fileData->pieceSize = 0;
	return fileData;
}

//initialise la base
struct dataBase *initDataBase()
{
	struct dataBase *dataBase = malloc(sizeof(struct dataBase));
	dataBase->head = NULL;
	return dataBase;
}

struct charList *initCharList()
{
	struct charList *newCharList = malloc(sizeof(struct charList));
	newCharList->text = NULL;
	newCharList->next = NULL;
	return newCharList;
}

struct charList *addToCharList(char *new, struct charList *list)
{
	if(new == NULL)
		return list;
	
	if(list == NULL)
	{
		struct charList *newList = initCharList();
		newList->text = strdup(new);
		return newList;
	}
	
	struct charList *tmp;
	for(tmp = list ; tmp != NULL ; tmp = tmp->next)
	{
		if(tmp->text != NULL)
		{
			if(strcmp(tmp->text,new) == 0)
				return list;
		}
	}
	
	for(tmp = list ; tmp->next != NULL ; tmp = tmp->next)
        {}

	struct charList *toAdd = initCharList();
	tmp->next = toAdd;
	toAdd->text = strdup(new);
	return list;
}

struct charList *RemoveFromCharList(char *toRemove, struct charList *list)
{
	if(toRemove == NULL)
		return list;

	if(list == NULL)
		return NULL;

	if(list->next == NULL && strcmp(list->text,toRemove) == 0)
	{
		free(list);
		return NULL;
	}
	else if(list->next == NULL && strcmp(list->text,toRemove) != 0)
	{
		return list;
	} 
	
		
	struct charList *tmp;
	struct charList *previous;

	for(tmp = list,previous = tmp ; tmp != NULL ; tmp = tmp->next)
	{
		
		if(strcmp(toRemove,tmp->text) == 0)
		{
			//on traite le permier element
			if(previous == tmp)
			{
				list = list->next;
				
			}
			else
				previous->next = tmp->next;
			free(tmp);
			return list;
		}
		previous = tmp;
	}
	return list;
}
		






//effectue une copie de fileData2 dans fileData1 //attention: ne copie pas le champs "next"
void fileDataCopy(struct fileData *fileData1,struct fileData *fileData2)
{
        //key
        fileData1->key = strdup(fileData2->key);
        //peersNumber;
        //fileData1->peersNumber = fileData2->peersNumber; //vaut forcement 1
	//namesNumber;
	//fileData1->namesNumber = fileData2->namesNumber; //vaut forcement 1
        //peers


	/*
        int i;
        for(i=0;i<MAX_PEERS;i++)
        {
                //on copie la bonne valeur quand elle existe
                if(fileData2->peers[i] != NULL)
                        fileData1->peers[i] = strdup(fileData2->peers[i]);		
                //on copie NULL quand il n'y a rien a copier
                fileData1->peers[i] = NULL;
        }
	*/

		
	fileData1->peers = addToCharList(fileData2->peers->text,fileData1->peers);
	fileData1->names = addToCharList(fileData2->names->text,fileData1->names);

        //size
        fileData1->size = fileData2->size;
        //pieceSize
        fileData1->pieceSize = fileData2->pieceSize;
}



//ajoute un fichier a la base
void addFileData(struct fileData *fileData)
{
	//cas ou la base est vide
	if(dataBase->head == NULL)
	{
		dataBase->head = initFileData();
		fileDataCopy(dataBase->head,fileData);
		dataBase->head->next = NULL;
		//fileData->peersNumber++;
		//fileData->namesNumber++;
		return;
	}

	//cas ou la base n'est pas vide
	struct fileData *tmp;
	for(tmp = dataBase->head ; tmp->next != NULL ; tmp = tmp->next)
	{
		//cas ou la cle est trouve dans la base
		if(strcmp(tmp->key,fileData->key) == 0)
		{
			addToCharList(fileData->peers->text,tmp->peers);
			addToCharList(fileData->names->text,tmp->names);
			return;
		}
	}
	//on traite le dernier fichier de la base car non traite dans la boucle
	if(strcmp(tmp->key,fileData->key) == 0)
      	{
		addToCharList(fileData->peers->text,tmp->peers);
          	addToCharList(fileData->names->text,tmp->names);
		return;
    	}
	//si on arrive ici, le fichier est ajoute en queue de liste car il n'existait pas dans la base
	struct fileData *newFileData = initFileData();
	fileDataCopy(newFileData,fileData);
	newFileData->next = NULL;
	tmp->next = newFileData;
	return;
}

	
	
/*	
//ajoute un peer pour un fichier donne (intervient pdt ajout de fichier)		
void addPeerToFileData(char *peer, struct fileData *fileData)
{
	int i;
	for(i=0;i<MAX_PEERS;i++)
	{
		if(fileData->peers[i] != NULL)
		{
			if(strcmp(peer,fileData->peers[i]) == 0)
				return;
		}
	}
	for(i=0;i<MAX_PEERS;i++)
        {
                if(fileData->peers[i] == NULL)
		{
			fileData->peers[i] = strdup(peer);
			fileData->peersNumber++;
                        return;
		}
        }
	printf("La liste de peers pour le fichier %s:%p est pleine\n",fileData->names[0],fileData);
	return;
}
*/

/*
//ajoute un nom pour un fichier donne (intervient pdt ajout de fichier)
void addNameToFileData(char *name, struct fileData *fileData)
{
	int i;
	for(i=0;i<MAX_FILENAMES;i++)
        {
		if(fileData->names[i] != NULL)
                	if(strcmp(name,fileData->names[i]) == 0)
                        	return;
        }
        for(i=0;i<MAX_FILENAMES;i++)
        {
                if(fileData->names[i] == NULL)
                {
                        fileData->names[i] = strdup(name);
			fileData->namesNumber++;
                        return;
                }
        }
        printf("La liste de noms pour le fichier %s:%p est pleine\n",fileData->names[0],fileData);
        return;
}
*/

/*
//supprime un peer d'un fichier donne
void removePeerFromFileData(char *peer, struct fileData *fileData)
{
	int i;
	for(i=0;i<MAX_PEERS;i++)
	{
		if(fileData->peers[i] != NULL)
			if(strcmp(peer,fileData->peers[i]) == 0)
			{
				fileData->peers[i] = NULL;
				fileData->peersNumber--;
				return;
			}	
	}
	return;
}
*/


/*
//supprime un des noms d'un fichier donne
void removeNameFromFileData(char *name, struct fileData *fileData)
{
        int i;
        for(i=0;i<MAX_FILENAMES;i++)
        {
		if(fileData->names[i] != NULL)
                	if(strcmp(name,fileData->names[i]) == 0)
                	{
                        	fileData->names[i] = NULL;
                        	return;
                	}
	}
        return;
}
*/



//vaut 1 si name est un nom de fileData, 0 sinon
int isNameOf(char *name, struct fileData *fileData)
{
	struct charList *tmp;
	for(tmp = fileData->names ; tmp != NULL ; tmp = tmp->next)
	{
		if(tmp->text != NULL)
		{
			if(strcmp(name,tmp->text) == 0)
				return 1;
		}
	}
	return 0;
}

//vaut 1 si name est un peer de fileData, 0 sinon
int isPeerOf(char *peer, struct fileData *fileData)
{
	struct charList *tmp;
        for(tmp = fileData->peers ; tmp != NULL ; tmp = tmp->next)
        {
                if(tmp->text != NULL)
                {
                        if(strcmp(peer,tmp->text) == 0)
                                return 1;
                }
        }
        return 0;
}



struct fileData *getFileDataByKey(char *key)
{
	struct fileData *tmp;
	for(tmp = dataBase->head ; tmp != NULL ; tmp = tmp->next)
	{
		if(strcmp(tmp->key,key) == 0)
		{
			return tmp;
		}
	}
	return NULL;
}


#define PEERSCAT 500

char *catFileDataPeers(struct fileData *fileData)
{
	char *peersCat = malloc(PEERSCAT);
	memset(peersCat,0,PEERSCAT);
	struct charList *tmp;
	for(tmp = fileData->peers ; tmp != NULL ; tmp = tmp->next)
	{
		if(tmp->text != NULL)
		{
			printf("catFileDataPeers[k]: %s\n",tmp->text);
			strcat(peersCat,tmp->text);
			strcat(peersCat," ");
		}
	}

	if(peersCat != NULL)
		if(strlen(peersCat) > 0)
		{
			int k = strlen(peersCat)-1;
			peersCat[k] = '\0';
		}

	printf("peersCat: %s\n",peersCat);

	return peersCat;
}
		
/*
char *firstPeer(struct fileData *fileData)
{
        int i;
        for(i=0;i<MAX_FILENAMES;i++)
                if(fileData->peers[i] != NULL)
                        return fileData->peers[i];
        return NULL;
}	
*/

/*
char *firstName(struct fileData *fileData)
{
	int i;
	for(i=0;i<MAX_FILENAMES;i++)
		if(fileData->names[i] != NULL)
			return fileData->names[i];
	return NULL;
}
*/		

void debugFileData(struct fileData *fileData)
{
	if(fileData->names != NULL)
		printf("filename(1): %s\n",fileData->names->text);
	if(fileData->peers != NULL)
                printf("peer(1): %s\n",fileData->peers->text);
	printf("key: %s\n",fileData->key);
}


void debugDataBase()
{
	struct fileData *tmp;
	for(tmp = dataBase->head ; tmp != NULL ; tmp = tmp->next)
		debugFileData(tmp);
}

























































