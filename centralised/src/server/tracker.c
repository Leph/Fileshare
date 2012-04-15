#include"tracker.h"

#define VERBOSE 1

pthread_mutex_t mutex_commandLine;
pthread_mutex_t mutex_tracker;


int yyparse();                                                                  
int cree_socket_stream ()
{
    int sock;
    struct sockaddr_in adresse;
    	#if VERBOSE
	printf("Creating socket... ");
	#endif	
    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
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
	printf("Binding socket to sockaddr...");
        #endif
   if (bind(sock, (struct sockaddr *) & adresse,
                 sizeof(struct sockaddr_in)) < 0) {
        close(sock);
        perror("bind");
        return -1;
    }
        #if VERBOSE
        printf("OK\n");
        #endif
    return sock;
}


int serveur_tcp (void){
    int sock_contact;
    int sock_connectee;
    int thread_courant=0;
    struct sockaddr_in adresse;
    socklen_t longueur;
    sock_contact = cree_socket_stream();
    if (sock_contact < 0)
        return -1;
//on autorise 5 connexions dans la liste d'attente.
    listen(sock_contact, 5);
    fprintf(stdout, "Listening >> ");
    affiche_adresse_socket(sock_contact);
    while (!quitter_le_serveur()) {
        longueur = sizeof(struct sockaddr_in);
        sock_connectee = accept(sock_contact,(struct sockaddr *)& adresse, & longueur);
        if (sock_connectee < 0){
            perror("accept");
            return -1;
        }
	thread_courant = thread_libre();	
	pthread_create(&tab_thread[thread_courant],NULL,traite_connexion,(void *)sock_connectee);
    }
    return 0;
}

int announce_tracker(int sock,struct commandLine * clone,char  * addri){
	
	int i;	
	char * port= malloc(strlen(addri)+6);
	sprintf(port,"%s",addri);
	sprintf(port+strlen(addri),":%d",clone->port);
	for(i=0;i<clone->filesNumber;i++){
		add_key(clone->keys[i],port,clone->fileNames[i],clone->lengths[i],clone->pieceSize[i]);
	}
//ICI ON REPOND OK AU CLIENT
	char * rett="ok\n";
	if(send(sock,rett,strlen(rett),0)<0){
                perror("send");
                exit(errno);
        }

	return 1;	

}

int getFile_tracker(int sock,struct commandLine * clone){

	struct lelement * temp=search_mainTrackerElement(clone->getKey);		
	if(temp!=NULL){	
		struct mainTrackerElement* ret=(struct mainTrackerElement *)temp->data;
		
	//ICI ON REPOND LA LISTE DES PEERS AU CLIENT
		char * retou = lnkPeer_to_string(ret->peers);	
		char * rere=malloc(MAX_STRING_SIZE+50);
		memset(rere,0,MAX_STRING_SIZE+50);
		strcat(rere,"peers ");
		strcat(rere,clone->getKey);
		strcat(rere," [");
		strcat(rere,retou);
		strcat(rere,"]");
	
		if(send(sock,rere,strlen(rere),0)<0){
	                perror("send");
        	        exit(errno);
  	      }
	free(retou);
	free(rere);

	}
	return 1;	

}

int look_tracker(int sock,struct commandLine * clone){
	//La fonction regarde dans la structure clone les differentes critere
	char * reponse = malloc(MAX_STRING_SIZE);
	memset(reponse,0,MAX_STRING_SIZE);
	strcat(reponse,"list [");
	if(clone->fileName!=NULL){
		char * conversion = malloc(64);
		memset(conversion,0,64);
		struct lelement *ptr=tracker->hashi->head;	
		struct mainTrackerElement * test=(struct mainTrackerElement *)ptr->data;
		while(ptr->next!=NULL){
			if(search_string(test->noms,clone->fileName)){
				strcat(reponse,clone->fileName);
				sprintf(conversion," %d ",test->size);
				strcat(reponse,conversion);
				memset(conversion,0,64);
				sprintf(conversion,"%d ",test->pieceSize);
				strcat(reponse,conversion);
				strcat(reponse,test->keyP);
				strcat(reponse," ");
					
			}		
			ptr=ptr->next;
			test=(struct mainTrackerElement *) ptr->data;
		}
	}
	strcat(reponse,"]");
	if(send(sock,reponse,strlen(reponse),0)<0){
		perror("send");
        	exit(errno);
	}
	return E_SUCCESS;
}

int update_tracker(int sock,struct commandLine * clone,char  * addri){
	int i;	
	char * port= malloc(strlen(addri)+6);
	sprintf(port,"%s",addri);
	sprintf(port+strlen(addri),":%d",clone->port);
	for(i=0;i<clone->leechKeysNumber;i++){
		update_key(clone->leechKeys[i],port);
	}
	for(i=0;i<clone->seedKeysNumber;i++){
		update_key(clone->seedKeys[i],port);
	}

//ICI ON REPOND OK AU CLIENT
	char * rett="ok\n";
	if(send(sock,rett,strlen(rett),0)<0){
                perror("send");
                exit(errno);
        }

	return 1;	

}

void* traite_connexion (void* socka){
    initCommandLine();
    int sock = (int) socka;
    struct sockaddr_in adresse;
    socklen_t           longueur;
    char                buffer[BUFFER_SIZE];
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
    struct commandLine * clone = cloneCommandLine();
    pthread_mutex_unlock(&mutex_commandLine);
    //FIN SECTION CRITIQUE	

    if (getpeername(sock,(struct sockaddr *)& adresse,& longueur) < 0) {
	perror("getpeername");
	int ret=-1;
         return (void*)ret;
    }
	//on garde l adresse  ip sous forme de chaine de caractere
	char *addri=strdup(inet_ntoa(adresse.sin_addr));
    //DEBUT SECTION CRITIQUE (tracker)	
	switch(clone->type){
		case ANNOUNCE:		
			announce_tracker(sock,clone,addri);	
			break;
		case LOOK:
			look_tracker(sock,clone);
			break;
		case UPDATE:
			update_tracker(sock,clone,addri);
			break;
		case GETFILE:
			getFile_tracker(sock,clone);
			break;
		default:
			printf("c'est pas normal");
	}
	//lnk_debug_mainTrackerElement(tracker->hashi);

    pthread_mutex_unlock(&mutex_tracker);
    //FIN SECTION CRITIQUE (tracker)    
	
    sprintf(buffer, "IP = %s, Port = %u \n",
             inet_ntoa(adresse.sin_addr),
             ntohs(adresse.sin_port));
    fprintf(stdout, "Connexion : locale ");
    affiche_adresse_socket(sock);
    fprintf(stdout, "           distante %s", buffer);
    //write(sock, "Votre adresse : ", 16);
    //write(sock, buffer, 10);
    int ret=1;
    //free(globalInputText);
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


int affiche_adresse_socket (int sock){
  
  	u_int32_t nip;
        getMyIP(sock,"eth0",&nip);
        struct in_addr in;
        in.s_addr = nip;
        char * cip =  inet_ntoa( in );

  fprintf(stdout, "IP = %s, Port = %u \n",cip,PORT);
  return 0;
}

int quitter_le_serveur(){
	return 0;
}


int thread_libre(){
	int i=0;
	for(i=0;i<THREADS_NUMBER;i++){
		if(tab_thread_libre[i]==0){
			tab_thread_libre[i]=1;	
			return i;
		}
	}
	return -1;
}




int main(void){
	pthread_mutex_init(&mutex_commandLine,NULL);
	pthread_mutex_init(&mutex_tracker,NULL);
	init_tracker();
	serveur_tcp();
	pthread_mutex_destroy(&mutex_commandLine);
	pthread_mutex_destroy(&mutex_tracker);
	return 0;
}
