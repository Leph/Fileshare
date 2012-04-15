#include "lk.h"

struct lelement *llm_init_empty(int taille){
	struct lelement * lelement=malloc(sizeof(struct lelement));
	if(lelement!=NULL){
		lelement->data=malloc(taille);
	}
	return lelement;
}

struct link* lnk_init_empty(int taille){
	struct link* link =malloc(sizeof(struct link));
	if(link!=NULL){
		link->head=llm_init_empty(taille);
		link->tail=llm_init_empty(taille);
	}
	return link;
}
struct lelement *llm_init_empty2(){
	struct lelement * lelement=malloc(sizeof(struct lelement));
	lelement->data=NULL;
	lelement->next=NULL;
	return lelement;
}

struct link* lnk_init_empty2(){
	struct link* link =malloc(sizeof(struct link));
	link->head=llm_init_empty2();
	return link;
}
/*
La fonction debug est propre a chaque type de donne que l'on va mettre dans le void *.
Celle ci est pour un liste d echaine de caratere.
*/
//void lnk_debug_data(const struct link *sent){
//  struct lelement *curs=sent->head;
//  printf("> ");
//  while (curs!=NULL){
//    printf("%s ",curs->data);
//    curs=curs->next;
//  }
//  printf("<\n");
//  return;
//}

int lnk_add_head (struct link *sent,struct lelement *element){
  return lnk_add_after(sent,NULL,element);
}

int lnk_remove_head (struct link *sent){
return lnk_remove_after(sent,NULL);
}



int lnk_add_after(struct link *sent,struct lelement *curs,struct lelement *element){
  if(sent==NULL || element==NULL)
    return E_FAILURE;
  if(curs==NULL){
    set_next(&sent->head,element);
    //if(element->next==NULL)
     // sent->tail=element;
    return E_SUCCESS;
  }
  else{
    set_next(&curs->next,element);
    if(element->next==NULL)
      sent->tail=element;
    return E_SUCCESS;
  }
}


int lnk_remove_after(struct link *sent,struct lelement *curs){
  if(sent==NULL)
    return E_FAILURE;
  if(curs==NULL){
    unset_next(&sent->head);
   // if(sent->head==NULL)
     // sent->tail=NULL;
  }
  else if(curs->next!=NULL){
    unset_next(&curs->next);;
    //if(curs->next==NULL)
      //sent->tail=curs;
  }
  return E_SUCCESS;
}

int set_next(struct lelement **curs, struct lelement * element){
  if(*curs==NULL || element==NULL)
    return E_FAILURE;
  element->next=*curs;
  *curs=element;
  return E_SUCCESS;
}

int unset_next(struct lelement **curs){
  if(*curs==NULL)
    return E_FAILURE;
  *curs=(*curs)->next;
  return E_SUCCESS;
}

int llm_add_peer(struct lelement * lelement,char * peer,int temps){
	//Si on rentre dans le if ça signifie que l element existe deja et on met a jour le temps
	if(NULL!=lelement->data){
		struct peer *ptr=(struct peer *)lelement->data;
//		lelement->data->peers=lnk_init_empty2();
		ptr->temps=temps;
		}
		
		
	
	else 	{
		lelement->data=malloc(sizeof(struct peer));
		if(NULL!=lelement->data){
			struct peer *ptr=(struct peer *)lelement->data;
			ptr->add=strdup(peer);
			ptr->temps=temps;
			
		}	
		else{
			printf("erreur d'allocations du peer\n");
			return E_FAILURE;
		}
	}
	return E_SUCCESS;


}

struct lelement * search_string_structPeer(struct link*  tracker,char *File){
	struct lelement* temp=tracker->head;	
	struct peer *peer;
	while(temp->next!=NULL){
		peer=(struct peer *)temp->data;
		if(0==strcmp(peer->add,File))
			return temp;
		temp=temp->next;
		}
	return NULL;
		
}

int llm_add_mainTrackerElement(struct lelement * lelement,char *peer,char* key,char *titre,int size,int pieceSize,int time){
	//Si on rentre dans le if ça signifie que l element existe deja
	if(NULL!=lelement->data){
		struct mainTrackerElement*ptr=(struct mainTrackerElement*)lelement->data;
//		lelement->data->peers=lnk_init_empty2();
//		struct lelement *temp=llm_init_empty2();
		if(NULL!=search_string_structPeer(ptr->peers,peer)){
			struct lelement *update=search_string_structPeer(ptr->peers,peer);
			llm_add_peer(update,peer,time);
		}
		if(titre!=NULL){
		if(E_SUCCESS!=search_string(ptr->noms,titre)){
		//on copie de le nom
//		lelement->data->noms=lnk_init_empty2();
		struct lelement *temp2=llm_init_empty2();
		llm_add_string(temp2,titre);
		lnk_add_head(ptr->noms,temp2);
		}}
		
		
	}
	else 	{
		lelement->data=malloc(sizeof(struct mainTrackerElement));
		if(NULL!=lelement->data){
			struct mainTrackerElement*ptr=(struct mainTrackerElement*)lelement->data;
			ptr->peers=lnk_init_empty2();
			struct lelement *temp=llm_init_empty2();
			llm_add_peer(temp,peer,time);
			lnk_add_head(ptr->peers,temp);
			//on copie de le nom
			ptr->noms=lnk_init_empty2();
			struct lelement *temp2=llm_init_empty2();
			llm_add_string(temp2,titre);
			lnk_add_head(ptr->noms,temp2);
			//on copie la clef 
			ptr->keyP=malloc(sizeof(char)*128);
			strcpy(ptr->keyP,key);
			ptr->size=size;
			ptr->pieceSize=pieceSize;
		}	
		else{
			printf("erreur d'allocations du mainTrackerElement\n");
			return E_FAILURE;
		}
	}
	return E_SUCCESS;
}
int search_string(struct link*  tracker,char *keyFile){
	struct lelement* temp=tracker->head;	
	char * chaine;
	while(temp->next!=NULL){
		chaine=(char *)temp->data;
		if(0==strcmp(chaine,keyFile))
			return E_SUCCESS;
		temp=temp->next;
		}
	return E_FAILURE;
		
}
int llm_add_string(struct lelement * lelement,char *string){
	lelement->data=malloc(sizeof(char)*(strlen(string)+1));
	if(NULL!=lelement->data){
		char * ptr=(char*)lelement->data;
		strcpy(ptr,string);
	}
	else{
		printf("erruer dans llm_add_string");
		return E_FAILURE;
	}

	return E_SUCCESS;
}

void llm_debug_peer(struct lelement * lelement){
	if(lelement==NULL)
		printf("\n");
	else{
		if(lelement->data==NULL)
			printf("\n");
		else{
			struct peer* ptr = (struct peer* )lelement->data;
			printf("Le peer est %s\n",ptr->add);
			printf("Le temps est  %d \n",ptr->temps);
		}
	}
		
}



void llm_debug_mainTrackerElement(struct lelement * lelement){
	if(lelement==NULL)
		printf("\n");
	else{
		if(lelement->data==NULL)
			printf("\n");
		else{
			struct mainTrackerElement * ptr = (struct mainTrackerElement* )lelement->data;
			printf("La clef est %s\n",ptr->keyP);
			printf("La taille est %d Mo \n",ptr->size/1000000);
			printf("La taille des pieces est %d Ko\n",ptr->pieceSize);
			printf("les different peers sont :");
			lnk_debug_peer(ptr->peers);	
			printf("les different titres sont :");
			lnk_debug_string(ptr->noms);	
			printf("\n");

		}
	}
		
}

void lnk_debug_peer(struct link * link){
	if(link==NULL||link->head==NULL)
		 printf(" ");
	else{
		
		struct lelement *pt=link->head;
		while(pt->next!=NULL){
			if(pt->data!=NULL){
				llm_debug_peer(pt);
				//printf(", ");
			}
			pt=pt->next;
		}
		//printf("\n");
	}
}
void lnk_debug_mainTrackerElement(struct link * link){
	if(link==NULL||link->head==NULL)
		 printf(" ");
	else{
		
		struct lelement *pt=link->head;
		while(pt->next!=NULL){
			if(pt->data!=NULL){
				llm_debug_mainTrackerElement(pt);
				//printf(", ");
			}
			pt=pt->next;
		}
		//printf("\n");
	}
}

void llm_debug_string(struct lelement * lelement){
	if(lelement==NULL)
		printf("\n");
	else{
		if(lelement->data!=NULL){
			char *pt=(char*) lelement->data;
			printf(" %s",pt);
		}
	}
	
}

void lnk_debug_string(struct link * link){
	if(link==NULL||link->head==NULL)
		printf("\n");
	else{
		
		struct lelement *pt=link->head;
		while(pt->next!=NULL){
			llm_debug_string(pt);
			if(pt->next->next==NULL)
				printf(". ");
			else
				printf(", ");
			pt=pt->next;
		}
		printf("\n");
	}
}
	
char * lnkPeer_to_string(struct link * link){
	
	struct lelement* ptr= (struct lelement *)link->head;
	char * data=malloc(MAX_STRING_SIZE);
	memset(data,0,MAX_STRING_SIZE);
	struct peer *cast = (struct peer *)ptr->data;
	strcat(data,cast->add);
	ptr = ptr->next;
	cast = (struct peer *)ptr->data;
	while(ptr->next!=NULL){		
		if((time(NULL)-MAX_WITHOUT_UPDATE_TIME)<cast->temps)
		{		
			strcat(data," ");
			strcat(data,cast->add);
		}	
		//SINON ON SUPPRIME DE LA LISTE A FAIRE/.................
			ptr=ptr->next;	
			cast=(struct peer *)ptr->data;
	
	}
	return data;
}
char * lnkString_to_string(struct link * link){
	struct lelement* ptr= (struct lelement *)link->head;
	char * data=malloc(MAX_STRING_SIZE);
	memset(data,0,MAX_STRING_SIZE);
	char * cast = (char *)ptr->data;
	while(ptr->next!=NULL){
		strcat(data," ");
		strcat(data,cast);
		ptr=ptr->next;	
		cast=(char *)ptr->data;
	
	}
	return data;


}
