#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/socket.h>
#include <errno.h>
#include <pthread.h>

 
#define TAILLE_MAX_BUF 1000
#define MAX_CLIENTS 10					 
 
 
void *fn_thread (void *arg)
{
	printf("thread %s: ok",(char *)arg);
	fflush(stdout);	
}
 
 
pthread_t thread[NB_THREAD];

 
int main(int argc, char *argv[])
{
 	// adresse socket coté client
  	static struct sockaddr_in addr_client;
  	// adresse socket locale
  	static struct sockaddr_in addr_serveur;
  	// longueur adresse
  	int lg_addr;
  	// socket d'écoute et de service
  	int socket_ecoute, socket_service;
  	// buffer qui contiendra le message reçu
  	char message[TAILLE_MAX_BUF];
  	// chaîne reçue du client
  	char *chaine_recue;
  	// chaîne renvoyée au client
  	char *reponse = "bien recu";
  	// nombre d'octets reçus ou envoyés
  	int nb_octets;
 
  
	// création socket TCP d'écoute
  	socket_ecoute = socket(AF_INET, SOCK_STREAM, 0);
  	if (socket_ecoute == -1)
	{
        	perror("creation socket");
          	exit(EXIT_FAILURE);
	}
  
	// liaison de la socket d'écoute sur le port 4000
  	bzero((char *) &addr_serveur, sizeof(addr_serveur));
  	addr_serveur.sin_family = AF_INET;
  	addr_serveur.sin_port = htons(4000);
  	addr_serveur.sin_addr.s_addr=htonl(INADDR_ANY);
  	if(bind(socket_ecoute,(struct sockaddr*)&addr_serveur,sizeof(addr_serveur)) == -1 )
	{
          	perror("erreur bind socket écoute");
          	exit(1);
 	}  

	// configuration socket écoute : MAX_CLIENTS connexions max en attente
 	if (listen(socket_ecoute,MAX_CLIENTS) == SOCKET_ERROR)
	{
        	perror("erreur listen");
        	exit(EXIT_FAILURE);
  	}
 
	int compteur=0;
 
	while(1)
	{
		lg_addr = sizeof(struct sockaddr_in);
		socket_service = accept(socket_ecoute,(struct sockaddr *)&addr_client,&lg_addr);
 
		if(socket_service == -1)
		{
			perror("erreur accept");
			exit(EXIT_FAILURE);
		}
	       
	int thread_client = pthread_create(&thread[compteur],NULL,fn_thread,(void *)socket_service);

	if(creation_thread!=0)
	{
		perror("pthread_create");
	}
 
	compteur++;				
	} 

	close(socket_ecoute);
}
