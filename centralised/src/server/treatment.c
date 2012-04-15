#include"treatment.h"
#include<time.h>



int initTracker()
{
	printf("Creating data base... ");
	
	dataBaseList = malloc(sizeof(struct dataBaseList));

	printf("OK\n");
	printf("Initializing data base... ");

	if(dataBaseList != NULL)
	{
		dataBaseList->dataBase = lnk_init_empty2();
	}

	if(dataBaseList->dataBase != NULL)
	{
		printf("OK\n");
		return EXIT_SUCCESS;
	}
	printf("FAILED\nProcess aborted\n");
	return EXIT_FAILURE;
}



int addKey(char *key,char *peer,char *titre,int size,int pieceSize)
{
	struct lelement *temp = search_mainTrackerElement(key);
	int time2 = (int) time(NULL);
	if(temp != NULL)
	{
		//lelement existe deja on le met a jour
		llm_add_mainTrackerElement(temp,peer,key,titre,size,pieceSize,time2);
		return EXIT_SUCCESS;
	}
	else
	{
		//sinon on le creer et on la joute au debut de la liste
		temp=llm_init_empty2();
		llm_add_mainTrackerElement(temp,peer,key,titre,size,pieceSize,time2);
		//llm_debug_mainTrackerElement(temp);
		lnk_add_head(dataBaseList->dataBase,temp);
		return EXIT_SUCCESS;
	}
	return EXIT_FAILURE;
		
}



int updateKey(char *key,char * addr)
{
	struct lelement *temp=search_mainTrackerElement(key);
	int time2 = (int)time(NULL);

	if(temp!=NULL)
	{
		llm_add_mainTrackerElement(temp,addr,key,NULL,0,0,time2);
		return EXIT_SUCCESS;
	}
	return EXIT_FAILURE;
}



struct lelement *search_mainTrackerElement(char *keyFile){
	struct mainTrackerElement * ret;	
	struct lelement* temp=dataBaseList->dataBase->head;	
	while(temp->next != NULL)
	{
		ret = (struct mainTrackerElement *) temp->data;
		if(0==strcmp(ret->keyP,keyFile))
		{
			return temp;
			temp=temp->next;
		}
	}
	return NULL;
		
}



int init_searchTrackerElement(struct searchTrackerElement *elt){
	elt = malloc(sizeof(struct searchTrackerElement));
	return 0;
}



