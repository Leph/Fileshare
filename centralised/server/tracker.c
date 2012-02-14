#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <unistd.h>
#include <arpa/inet.h>
#include "fcts_tracker.h"

#define FILE_DATTENTE 5 // des mutex pourront par la suite être mis en place

//squelette du serveur

int main(){
  int sock_serv=make_tcp_ip_socket();
  int sock_connexion;
  struct sockaddr_in addr_connexion;
  pid_t p;
  socklen_t length=sizeof(struct sockaddr_in);
  if(sock_serv < 0){
    fprintf(stderr,"echec creation socket serveur");
    exit(-1);
  }
  listen(sock_serv,FILE_DATTENTE);
  //on affiche les infos du serveur sur le terminal
  struct sockaddr_in addr=info_sock(sock_serv);
  printf("adresse du serveur: %s\n Numero port: %d\n",inet_ntoa(addr.sin_addr),
	 ntohs(addr.sin_port));
  //boucle infinie
  while(1){
    //on attend une  connexion venant d'1 client
    sock_connexion=accept(sock_serv,(struct sockaddr*) &addr_connexion,
			  &length);
    if(sock_connexion < 0){
      fprintf(stderr,"echec creation socket serveur");
      exit(-1);
    }
    //la mise en place de thread sera privilegiée par la suite
    p=fork();
    switch(p){
    case -1:
      fprintf(stderr,"echec fork");
      break;
    case 0://processus fils on y gérera la connexion client/serveur
      close(sock_serv);
      connexion_client(sock_connexion);
      break;
    default://processus pere
      close(sock_connexion);
      break;
    }
  }
  
 return 0;
}
