#ifndef TRACKER_H_
#define TRACKER_H_
#include"linkedList/lk.h"
#include"hachage.h"
#include<sys/types.h>
#include<netinet/in.h>
#include<netdb.h>
#include<string.h>
#include<stdlib.h>
#include<stdio.h>
#include<errno.h>
#include<unistd.h>
#include<pthread.h>
#include"parser/myparser.h"
#include <sys/socket.h>
#include <arpa/inet.h>

#define HASH_MAX_VALUE 17 
#define THREADS_NUMBER 64 
#define BUFFER_SIZE 1024 




pthread_t tab_thread[THREADS_NUMBER];
int tab_thread_libre[THREADS_NUMBER]={0};
/*******************PROTOTYPES*********************/
int cree_socket_stream();
int thread_libre();
int serveur_tcp(void);

int affiche_adresse_socket(int sock);
void* traite_connexion(void* sock);
int quitter_le_serveur();
int announce_tracker(int sock,struct commandLine * clone,char  * addri);
int getFile_tracker(int sock,struct commandLine * clone,char  * addri);
int update_tracker(int sock,struct commandLine * clone,char  * addri);
int look_tracker(int sock,struct commandLine * clone,char  * addri);
#endif
