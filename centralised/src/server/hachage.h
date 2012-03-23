#ifndef HACHAGE_H_ 
#define HACHAGE_H_
#include"linkedList/lk.h"
#include<sys/types.h>
#include<netinet/in.h>
#include<netdb.h>
#include<string.h>
#include<unistd.h>
#include <sys/socket.h>
#include <arpa/inet.h>

#define HASH_MAX_VALUE 17 
#define MAX_WITHOUT_UPDATE_TIME 600 
#define PORT 60011 

struct mainTrackerElement{
	struct link* peers; // liste des differents peers sous forme de chaine de caratere du type [$IP1:$PORT1 $IP2:$PORT2]
	char *keyP;//la clef MD5
	int size;//taille en octect
	int pieceSize;//taille des pieces
	struct link* noms;// Liste des differents noms de ce fichier sous forme de chaine de caractere.
};

struct searchTrackerElement{
	int key;// clef generee a partir du nom du fichier a l aide de crc32
	char keyP[128];//la clef MD5
};

struct tracker{
	//cette table de hachage est la table de hachage principale.Elle contient l'ensemble des informations.Dans les maillons de la chaine les elements vont etre des structures mainTracker.
	struct link* hashi;
	//nameKey sert a faire le lien entre un nom de fichier et la clé de ce fichier generer a partir du fichier lui meme.En fait c'est une table qui sert uniquement pour al recherche.Les maillons des differents listes seront des structures searchTracker.
//	struct link * nameKey;
};

struct tracker *tracker;

int init_tracker();
//int add_key_nameKey(struct tracker *,char *,char *);
int init_searchTrackerElement(struct searchTrackerElement *);
struct lelement * search_mainTrackerElement(char *keyFile);

int add_key(char *key,char *peer,char *titre,int size,int pieceSize);
#endif
