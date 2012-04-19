#include "treatment.h"


extern struct dataBase *dataBase;


int announceTreatment(int sock, struct commandLine *clone, char *addri)
{
        int i;
        char *peer = malloc(strlen(addri)+8);
        sprintf(peer,"%s",addri);
        sprintf(peer+strlen(addri),":%d",clone->port);
	printf("peer %s\n",peer);
	struct fileData *fileData;

	//ajout des fichiers a la base
        for(i=0;i<clone->filesNumber;i++)
	{
		fileData = initFileData();
		fileData->key = strdup(clone->keys[i]);
		fileData->peers = addToCharList(peer,fileData->peers);
		fileData->names = addToCharList(clone->fileNames[i],fileData->names);
		fileData->size = clone->lengths[i];
		fileData->pieceSize = clone->pieceSize[i];
		addFileData(fileData);
		free(fileData);
	}
	
        //on repond au client
        char *answer = "ok\n";
        if(send(sock,answer,strlen(answer)+1,0)<0)
	{
                perror("send in announce response");
                exit(errno);
        }

	debugDataBase();
	
        return 1;

}




int getFileTreatment(int sock, struct commandLine *clone)
{
	printf("clone->getKey: %s\n",clone->getKey);
	
	struct fileData *fileData = getFileDataByKey(clone->getKey);

	debugFileData(fileData);

	if(fileData == NULL)
	{
		char *emptyPeers = malloc(100);
        	memset(emptyPeers,0,100);
        	strcat(emptyPeers,"peers ");
        	strcat(emptyPeers,clone->getKey);
        	strcat(emptyPeers," []");
        	if(send(sock,emptyPeers,strlen(emptyPeers),0)<0)
                {
                	perror("send getfile");
                	exit(errno);
                }
		return 0;
	}	

	char *answer = malloc(300);
	memset(answer,0,300);
	char *peersCat = catFileDataPeers(fileData);
	if(peersCat != NULL)
	{
		printf("%s\n",peersCat);
		memset(answer,0,300);
		strcat(answer,"peers ");
               	strcat(answer,clone->getKey);
               	strcat(answer," [");
               	strcat(answer,peersCat);
               	strcat(answer,"]\n");
		
		if(send(sock,answer,strlen(answer),0)<0)
               	{
                       	perror("send getfile");
                       	exit(errno);
               	}
               	free(answer);
               	free(peersCat);
	}
	return 1;
}




#define LOOK_ANSWER 1000

int lookTreatment(int sock, struct commandLine *clone)
{
/*
	char *answer = malloc(LOOK_ANSWER);
	memset(answer,0,LOOK_ANSWER);
	strcat(answer,"list [");
	char convert[50];

	//cas avec filename
	if(clone->fileName != NULL)
	{
		struct fileData *tmp;
		memset(convert,0,50);
        	for(tmp = dataBase->head ; tmp != NULL ; tmp = tmp->next)
        	{
                	if(isNameOf(clone->fileName,tmp) && tmp->size>clone->supTo && tmp->size<clone->infTo)
			{	
				strcat(answer,firstName(tmp));
				strcat(answer," ");
				sprintf(convert,"%ld",tmp->size);
				strcat(answer,convert);
				strcat(answer," ");
				memset(convert,0,50);
				sprintf(convert,"%d",tmp->pieceSize);
				strcat(answer,convert);
				strcat(answer," ");
				strcat(answer,tmp->key);
				strcat(answer," ");
				memset(convert,0,50);
        		}
		}
		int k = strlen(answer)-1;
                answer[k] = ']'; answer[k+1] = '\0';

		if(send(sock,answer,strlen(answer),0)<0)
		{
                	perror("send");
                	exit(errno);
        	}
		return 1;
	}

	//cas sans filename
	struct fileData *tmp2;
        for(tmp2 = dataBase->head ; tmp2 != NULL ; tmp2 = tmp2->next)
        {
        	if(tmp2->size>clone->supTo && tmp2->size<clone->infTo)
                {
                        strcat(answer,tmp2->names[0]);
                        strcat(answer," ");
			sprintf(convert,"%ld",tmp2->size);			
                        strcat(answer,convert);
                        strcat(answer," ");
			memset(convert,0,50);
			sprintf(convert,"%d",tmp2->pieceSize);
                        strcat(answer,convert);
                        strcat(answer," ");
                        strcat(answer,tmp2->key);
                        strcat(answer," ");
			memset(convert,0,50);
              	}
    	}
        int k2 = strlen(answer)-1;
        answer[k2] = ']'; answer[k2+1] = '\0';

	if(send(sock,answer,strlen(answer),0)<0)
                {
                        perror("send");
                        exit(errno);
                }	
		
  */      return 1;
}






int updateTreatment(int sock, struct commandLine *clone, char *addri)
{/*
     	int i;
        char *peer = malloc(strlen(addri)+8);
        sprintf(peer,"%s",addri);
        sprintf(peer+strlen(addri),":%d",clone->port);

	

	struct fileData *tmp;
	struct fileData *newFile;

	if(clone->isLeecher)
	{
        	for(i=0;i<clone->leechKeysNumber;i++)
		{
			tmp = getFileDataByKey(clone->leechKeys[i]);
			
			if(tmp != NULL)
			{
				if(!isPeerOf(peer,tmp))
					addPeerToFileData(peer,tmp);
			}
			
			newFile = initFileData();
			addPeerToFileData(peer,newFile);
			newFile->key = strdup(clone->leechKeys[i]);
			addFileData(newFile);						
		}
        }

	if(clone->isSeeder)
	{
        	for(i=0;i<clone->seedKeysNumber;i++)
		{
			tmp = getFileDataByKey(clone->seedKeys[i]);
			
			if(tmp != NULL)
                        {
                                if(!isPeerOf(peer,tmp))
                                        addPeerToFileData(peer,tmp);
                        }

                        newFile = initFileData();
                        addPeerToFileData(peer,newFile);
                        newFile->key = strdup(clone->seedKeys[i]);
                        addFileData(newFile);
		}
     	}


        //ICI ON REPOND OK AU CLIENT
        char *updateAnswer="ok\n";
        if(send(sock,updateAnswer,strlen(updateAnswer),0)<0){
                perror("send");
                exit(errno);
        }

   */     return 1;

}

































