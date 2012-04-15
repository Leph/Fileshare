#include"lk.h"
#include<stdio.h>
#include<stdlib.h>

int main(){

	struct link *maList=lnk_init_empty2();
	char * key=malloc(128);
	char * nom=malloc(128);
	char * nom2=malloc(128);
	char * peer=malloc(128);
	char * peer2=malloc(128);
	nom="le roi lion";
	nom2="Lion King";
	peer="128.165.125.12:60111";
	peer2="128.165.12.122:61";
	key="azedazeazeazeazeazeaze";
//	lnk_debug_mainTrackerElement(maList);
//	lnk_debug_string(maList);
	struct lelement * temp=llm_init_empty2();
	struct lelement * temp2=llm_init_empty2();
//	llm_add_mainTrackerElement(temp,peer,key,nom);
//	llm_add_mainTrackerElement(temp,peer2,key,nom2);
//	llm_add_string(temp,peer);
//	llm_add_string(temp2,peer2);
//	lnk_add_head(maList,temp);
//	lnk_add_head(maList,temp2);
//	char * tttt=lnkString_to_string(maList);
//	printf("%s\n",tttt);
//	lnk_debug_string(maList);

	llm_add_peer(temp,peer,1214512);
	
	lnk_add_head(maList,temp);
	lnk_debug_peer(maList);



  return EXIT_SUCCESS;
}
