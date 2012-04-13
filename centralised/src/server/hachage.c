#include"hachage.h"
#include<time.h>

int init_tracker(){
	tracker = malloc(sizeof(struct tracker));
	if(NULL!=tracker){
		tracker->hashi = lnk_init_empty2();
//		tracker->nameKey = malloc(sizeof(struct link*)*HASH_MAX_VALUE);
	}
	if(tracker->hashi!=NULL)
		return E_SUCCESS;
	return E_FAILURE;
}


int add_key(char *key,char *peer,char *titre,int size,int pieceSize){
	struct lelement *temp=search_mainTrackerElement(key);
	int time2 = (int)time(NULL);
	if(temp!=NULL){
	//lelement existe deja on le met a jour
		llm_add_mainTrackerElement(temp,peer,key,titre,size,pieceSize,time2);
		return E_SUCCESS;
	}
	else{
	//sinon on le creer et on la joute au debut de la liste
		temp=llm_init_empty2();
		llm_add_mainTrackerElement(temp,peer,key,titre,size,pieceSize,time2);
	//	llm_debug_mainTrackerElement(temp);
		lnk_add_head(tracker->hashi,temp);
		return EXIT_SUCCESS;
	}
	return EXIT_FAILURE;
		
}
int update_key(char *key,char * addr){
	struct lelement *temp=search_mainTrackerElement(key);
	int time2 = (int)time(NULL);
	if(temp!=NULL){
		llm_add_mainTrackerElement(temp,addr,key,NULL,0,0,time2);
		return E_SUCCESS;
	}
	return E_FAILURE;
}
struct lelement * search_mainTrackerElement(char *keyFile){
	struct mainTrackerElement * ret;	
	struct lelement* temp=tracker->hashi->head;	
	while(temp->next!=NULL){
		ret=(struct mainTrackerElement *)temp->data;
		if(0==strcmp(ret->keyP,keyFile))
			return temp;
		temp=temp->next;
		}
	return NULL;
		
}

/*void init_mainTrackerElement(struct mainTrackerElement * elt){
	elt = malloc(sizeof(struct mainTrackerElement));
	if(NULL!= elt){
		lst_init_empty(&(elt->peers));
		lst_init_empty(&(elt->noms));
	}
}*/
int init_searchTrackerElement(struct searchTrackerElement *element){
	element = malloc(sizeof(struct searchTrackerElement));
	return 0;
}



