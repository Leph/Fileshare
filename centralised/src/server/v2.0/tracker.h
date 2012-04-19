#ifndef TRACKER_H_
#define TRACKER_H_

#include "database.h"
#include "treatment.h"
#include <sys/types.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <errno.h>
#include <unistd.h>
#include <pthread.h>
#include "parser.h"
#include <sys/ioctl.h>
#include <sys/socket.h>
#include <arpa/inet.h>

#define THREADS_NUMBER 64 
#define BUFFER_SIZE 1024 


/********************VARIABLE GLOBALE*********************/
//pthread_mutext_t mutex_commandLine=PTHREAD_MUTEX_INITIALIZER;
//pthread_mutext_t mutex_hachage=PTHREAD_MUTEX_INITIALIZER;



pthread_t tab_thread[THREADS_NUMBER];
int tab_thread_libre[THREADS_NUMBER]={0};



/*******************PROTOTYPES*********************/
int createSocketStream();
int pickNewThread();
int serverTcp(void);

int displaySocketAdress(int sock);
void *clientTreatment(void* sock);
int quitServer();

#endif
