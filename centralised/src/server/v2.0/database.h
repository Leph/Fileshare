#ifndef _DATABASE_H_
#define _DATABASE_H_

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#define MAX_PEERS 100
#define MAX_FILENAMES 100

struct charList
{
	char *text;
	struct charList *next;
};

struct fileData
{
	char *key;
	//int peersNumber;
	struct charList *peers;
	//char *peers[MAX_PEERS];
	//int namesNumber;
	struct charList *names;
	//char *names[MAX_FILENAMES];
	long size;
	int pieceSize;
	struct fileData *next;
};

struct dataBase
{
	struct fileData *head;
};


struct fileData *initFileData();
struct dataBase *initDataBase();
struct charList *addToCharList(char *new, struct charList *list);

struct charList *RemoveFromCharList(char *toRemove, struct charList *list);

void fileDataCopy(struct fileData *fileData1,struct fileData *fileData2);
void addFileData(struct fileData *fileData);

//void addPeerToFileData(char *peer, struct fileData *fileData);
//void addNameToFileData(char *name, struct fileData *fileData);

//void removePeerFromFileData(char *peer, struct fileData *fileData);
//void removeNameFromFileData(char *name, struct fileData *fileData);

int isNameOf(char *name, struct fileData *fileData);
int isPeerOf(char *peer, struct fileData *fileData);

struct fileData *getFileDataByKey(char *key);

char *catFileDataPeers(struct fileData *fileData);

//char *firstName(struct fileData *fileData);

void debugFileData();
void debugDataBase();

#endif
