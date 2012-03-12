#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include "file.h"

struct file init_file(char* name,int file_size, int part_size, char* key){
  struct file f;
  strcpy(f.name,name);
  f.size=file_size;
  f.part_size=part_size;
  strcpy(f.key,key);
  return f;
}


void add_file(struct client* c,struct file f){
  if(c->nbr_fichiers==c->nbr_files_max){
    c->files=realloc(c->files,(sizeof(struct file)*(2*(c->nbr_files_max))));
    c->nbr_files_max *=2;
  }
  client->files[c->nbr_fichiers]=f;
  c->nbr_fichiers++;
  
}

void add_file(struct client* c,struct file* f,int n){
  for(int i=0;i<n;i++)
    add_file(c,f[i]);
  
}

void remove_file(struct client* c, struct file f){
  

}

void getfile(struct queue* queue,char *key){
  struct client *current = queue->first;
  printf("peers %s  [",key);
  while(current!=NULL){
    for(int i=0;i<current->nbr_fichiers_max;i++)
      if( strcmp(key,current->files[i].key)==0)
	;//printf de ip et n° port
    current=current->next;
  }
  printf("]");
}

struct file* look(struct queue* queue,char * name,int filesize,int op){
  printf("list [");
  struct client *current = queue->first;
  switch(op){
  case '0':
    while(current!=NULL){
      for(int i=0;i<current->nbr_fichiers_max;i++)
	if( strcmp(name,current->files[i])==0 && 
	    current->files[i].size == filesize)
	  printf("%s %d %d %s ",name,current->files[i].name,current->files[i].size,current->files[i].part_size,current->files[i].key);
      current=current->next;
    }
    printf(" ]");
    break;
  case '1':
    while(current!=NULL){
      for(int i=0;i<current->nbr_fichiers_max;i++)
	if( strcmp(name,current->files[i])==0 && 
	    current->files[i].size > filesize)
	  printf("%s %d %d %s ",name,current->files[i].name,current->files[i].size,current->files[i].part_size,current->files[i].key);
      current=current->next;
    }
    printf(" ]");
    break;
  case '2':
    while(current!=NULL){
      for(int i=0;i<current->nbr_fichiers_max;i++)
	if( strcmp(name,current->files[i])==0 && 
	    current->files[i].size < filesize)
	  printf("%s %d %d %s ",name,current->files[i].name,current->files[i].size,current->files[i].part_size,current->files[i].key);
      current=current->next;
    }
    printf(" ]");
    break;
  default:
    //pas de comparaison au niveau de la taille
    while(current!=NULL){
      for(int i=0;i<current->nbr_fichiers_max;i++)
	if( strcmp(name,current->files[i])==0)
	  printf("%s %d %d %s ",name,current->files[i].name,current->files[i].size,current->files[i].part_size,current->files[i].key);
      current=current->next;
    }
    printf(" ]");
    break;
  }
