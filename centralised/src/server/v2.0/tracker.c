#include"tracker.h"

#define VERBOSE 1
#define PORT 60034

pthread_mutex_t mutex_commandLine;
pthread_mutex_t mutex_dataBase;


struct dataBase *dataBase;


int yyparse();                                                                  


int createSocketStream ()
{
    	int sock;
    	struct sockaddr_in adresse;

    	#if VERBOSE
	printf("Creating socket... ");
	#endif	

    	if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0)
	{
        	perror("socket");
        	return -1;
    	}

	#if VERBOSE
        printf("OK\n");
	printf("Initializing socket... ");
        #endif
    	memset(& adresse, 0, sizeof (struct sockaddr_in));
    	adresse.sin_family = AF_INET;
   	adresse.sin_port = htons(PORT);
    	adresse.sin_addr.s_addr =htonl(INADDR_ANY);

        #if VERBOSE
        printf("OK\n");
	printf("Binding socket to host machine...");
        #endif

        if (bind(sock, (struct sockaddr *) & adresse,sizeof(struct sockaddr_in)) < 0)
	{
        	close(sock);
        	perror("bind");
        	return -1;
    	}

        #if VERBOSE
        printf("OK\n");
        #endif
	return sock;
}


int serverTcp (void)
{
    	int sock_contact;
    	int sock_connectee;
    	int thread_courant=0;
    	struct sockaddr_in adresse;
    	socklen_t longueur;
    	sock_contact = createSocketStream();
    	if (sock_contact < 0)
        	return -1;

	//on autorise 5 connexions dans la liste d'attente.
    	listen(sock_contact, 5);
    	fprintf(stdout, "Listening >> ");
    	displaySocketAdress(sock_contact);


    	while (!quitServer())
	{
        	longueur = sizeof(struct sockaddr_in);
        	sock_connectee = accept(sock_contact,(struct sockaddr *)& adresse, & longueur);
        	if (sock_connectee < 0)
		{
            		perror("accept");
            		return -1;
        	}

		thread_courant = pickNewThread();	
		pthread_create(&tab_thread[thread_courant],NULL,clientTreatment,(void *)sock_connectee);
    	}

	return 0;
}




void *clientTreatment(void *socka)
{
	initCommandLine();
    	int sock = (int) socka;
    	struct sockaddr_in adresse;
    	socklen_t longueur;
    	char buffer[BUFFER_SIZE];
    	memset(buffer,0,strlen(buffer));
    	memset(&adresse,0,sizeof(struct sockaddr_in));
    	longueur = sizeof(struct sockaddr_in);
    	recv(sock,buffer,BUFFER_SIZE,0);

    	//DEBUT SECTION CRITIQUE (commandLine)
    	pthread_mutex_lock(&mutex_commandLine);
    	initCommandLine();
    	globalInputText = strdup(buffer);
    	yyparse();
    	free(globalInputText);
    	struct commandLine *clone = cloneCommandLine();
    	pthread_mutex_unlock(&mutex_commandLine);
    	//FIN SECTION CRITIQUE (commandLine)	

    	if (getpeername(sock,(struct sockaddr *)& adresse,& longueur) < 0)
	{
		perror("getpeername");
		int ret=-1;
        	return (void*)ret;
    	}
	//on garde l'adresse ip sous forme de chaine de caractere
	char *addri=strdup(inet_ntoa(adresse.sin_addr));

    	//DEBUT SECTION CRITIQUE (tracker)	
	switch(clone->type){
		case ANNOUNCE:		
			announceTreatment(sock,clone,addri);	
			break;
		case LOOK:
			lookTreatment(sock,clone);
			break;
		case UPDATE:
			updateTreatment(sock,clone,addri);
			break;
		case GETFILE:
			getFileTreatment(sock,clone);
			break;
		default:
			printf("Bad request\n");
	}

    	pthread_mutex_unlock(&mutex_dataBase);
    	//FIN SECTION CRITIQUE (tracker)    
	
    	sprintf(buffer, "IP = %s, Port = %u \n",inet_ntoa(adresse.sin_addr),ntohs(adresse.sin_port));
    	fprintf(stdout, "Connexion : locale ");
    	displaySocketAdress(sock);
    	fprintf(stdout, "	distante %s", buffer);
    	int ret=1;
    	free(clone);
    	free(addri);
   	freeCommandLine();
    	close(sock);
   	return (void*)ret;
}



int getMyIP(int fd, char* ifname,  u_int32_t * ipaddr)
{
/* fd: opened file descriptor
 * ifname: if name eg: "eth0"
 */

	#define IFNAMSIZ 16
        struct ifreq
        {
                char ifr_name[IFNAMSIZ];
                struct sockaddr ifr_addr;
        };

        struct ifreq ifr;
        memcpy(ifr.ifr_name,ifname,IFNAMSIZ);
        if(ioctl(fd,SIOCGIFADDR,&ifr) == -1)
        {
                perror("getMyIP: ioctl" );
                exit(-1);
        }
        memcpy(ipaddr,&ifr.ifr_addr.sa_data[2],4);
        return 0;
}



int displaySocketAdress(int sock)
{  
  	u_int32_t nip;
        getMyIP(sock,"eth0",&nip);
        struct in_addr in;
        in.s_addr = nip;
        char * cip =  inet_ntoa( in );

  	fprintf(stdout, "IP = %s, Port = %u \n",cip,PORT);
  	return 0;
}



int quitServer(){
	return 0;
}



int pickNewThread(){
	int i=0;
	for(i=0;i<THREADS_NUMBER;i++){
		if(tab_thread_libre[i]==0){
			tab_thread_libre[i]=1;	
			return i;
		}
	}
	return -1;
}



int main(void)
{
	pthread_mutex_init(&mutex_commandLine,NULL);
	pthread_mutex_init(&mutex_dataBase,NULL);
	dataBase = initDataBase();
	serverTcp();
	pthread_mutex_destroy(&mutex_commandLine);
	pthread_mutex_destroy(&mutex_dataBase);
	return 0;
}
