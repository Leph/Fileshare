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
#define PORT 60011 

int main(int argc,char **argv){
    int sock;
    struct sockaddr_in adresse;
    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        perror("socket");
        return -1;
    }
    struct hostent *hostinfo=NULL;
    memset(& adresse, 0, sizeof (struct sockaddr_in));
    gethostbyname(argv[1]);
    adresse.sin_family = AF_INET;
    adresse.sin_port = htons(PORT);
    adresse.sin_addr.s_addr = /*inet_addr("147.210.19.195");*/htonl(INADDR_ANY);
   if (connect(sock, (struct sockaddr *) & adresse,
                 sizeof(struct sockaddr_in)) < 0) {
        perror("connect");
        return -1;
    }
    char * buffer="announce listen 4444 have [  \"Le Seigneur Des Anneaux.avi\"   699000000  1024   hkj234f5s2h6fd7f        \"Pulp Fiction.mkv\"        701000000   1024   hkj234d5s2j6fd7p \"Batman - The Dark Knight.mp4\" 1400000000 2048 h4k2l6h6gf4oas9    \"rapportRE203.pdf\" 2300000 1024 h3p4n1c8p0l2  \"music.mp3\" 4380000 1024 j5l2bvfg34f9]"; 
//	printf("%s\n",buffer);
	if(send(sock,buffer,strlen(buffer),0)<0){
		perror("send");
		exit(errno);
	}
	char * bubu=malloc(5);
	recv(sock,bubu,5,0);
	
	printf("%s\n",bubu);
	 char *alInputText = "getfile hkj234f5s2h6fd7f";
	send(sock,alInputText,strlen(alInputText),0);
	char * caca = malloc(100);
	memset(caca,0,100);
	recv(sock,caca,100,0);
	printf(" %s\n",caca);
	return 0;
}
