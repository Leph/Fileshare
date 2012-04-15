#ifndef LIST_H_
#define LIST_H_

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include"treatment.h" 
#define DATA_INIT 0.0
#define E_SUCCESS 1
#define E_FAILURE 0
#define MAX_STRING_SIZE 2048
/*
maillon de l a liste de base qui ne possede qu'une champs void*
*/
struct lelement{
  void * data;
  struct lelement *next;
};

struct peer{
	char * add;
	int temps;
};

struct link{
  struct lelement *head;
  struct lelement *tail;
};

struct lelement * llm_init_empty(int taille);
struct link* lnk_init_empty(int taille);
struct lelement *llm_init_empty2();
struct link*lnk_init_empty2();
//void lnk_debug_data(const struct link *);


int llm_add_mainTrackerElement(struct lelement * lelement,char *peer,char* key,char *titre,int size,int pieceSize,int time);

int llm_add_peer(struct lelement * lelement,char * peer,int temps);

void llm_debug_peer(struct lelement * lelement);
void lnk_debug_peer(struct link* link );


void llm_debug_mainTrackerElement(struct lelement * lelement);
void lnk_debug_mainTrackerElement(struct link * link);
void llm_debug_string(struct lelement * lelement);
void lnk_debug_string(struct link * link);

int llm_add_string(struct lelement * lelement,char *peer);

int lnk_add_head (struct link *,struct lelement *);
int lnk_remove_head (struct link *);


int lnk_add_tail (struct link *,struct lelement *);
int lnk_remove_tail(struct link *);

int lnk_add_after(struct link *,struct lelement *,struct lelement *);
int lnk_remove_after(struct link *,struct lelement *);

int set_next(struct lelement **, struct lelement *);
int unset_next(struct lelement **);
int search_string(struct link*  tracker,char *keyFile);
struct lelement * search_string_structPeer(struct link*  tracker,char *keyFile);
char * lnkString_to_string(struct link * link);
char * lnkPeer_to_string(struct link * link);


#endif
