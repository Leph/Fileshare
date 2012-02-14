#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <string.h>
#include <arpa/inet.h>
#include <unistd.h>

#define NB_PORT 3000 //à definir ou à mettre en parametre
#define ADDR_IP "127.0.0.1"//idem. on recuperera l'@ de l'hote

//création d'un socket utilisant le protocole TCP/IP
int make_tcp_ip_socket(){
  
  int sock=socket(AF_INET,SOCK_STREAM,0);
  if(sock<0){
    fprintf(stderr,"erreur creation socket");
    exit(-1);
  }
  struct sockaddr_in addr;
  memset(&addr,0,sizeof(struct sockaddr_in));
  addr.sin_family=AF_INET;
  addr.sin_port=htons(NB_PORT);
  addr.sin_addr.s_addr=htons(INADDR_ANY);
  // inet_aton(ADDR_IP,&addr.sin_addr);
  //on relit l'@ et le port au socket
  if(bind(sock,(struct sockaddr*)&addr,sizeof(struct sockaddr_in)) <0){
    close(sock);
    fprintf(stderr,"echec de bind");
  }
  return sock;
}

/*retourne une structure contenant les informations(@,port) du socket
  à appeler après bind*/

struct sockaddr_in info_sock(int sock){
  struct sockaddr_in addr;
  socklen_t length=sizeof(struct sockaddr_in);
  if(getsockname(sock,(struct sockaddr*)&addr,&length)<0){
    fprintf(stderr,"echec de getsockname");
  }
  return addr;
}

void connexion_client(int sock){
  // communication via des tubes
  printf("connexion établie identifient socket %d",sock);
}
