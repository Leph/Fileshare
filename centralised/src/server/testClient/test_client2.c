#include<sys/types.h>
#include<netinet/in.h>
#include<netdb.h>
#include<string.h>
#include<errno.h>
#include<stdio.h>
#include<stdlib.h>
#include<unistd.h>
#include <sys/socket.h>
#include <arpa/inet.h>

#define HASH_MAX_VALUE 1777 
#define PORT 60034 

int main(int argc,char **argv){
    int sock;
    struct sockaddr_in adresse;
    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        perror("socket");
        return -1;
    }
    //struct hostent *hostinfo=NULL;
    memset(& adresse, 0, sizeof (struct sockaddr_in));
    //gethostbyname(argv[1]);
    adresse.sin_family = AF_INET;
    adresse.sin_port = htons(PORT);
    adresse.sin_addr.s_addr =htonl(INADDR_ANY);
   if (connect(sock, (struct sockaddr *) & adresse,
                 sizeof(struct sockaddr_in)) < 0) {
        perror("connect");
        return -1;
    }
	 char *alInputText = "getfile hkj234f5s2h6fd7f";
	send(sock,alInputText,strlen(alInputText),0);
	char * caca = malloc(100);
	memset(caca,0,100);
	recv(sock,caca,100,0);
	printf(" %s\n",caca);
	return 0;
}
