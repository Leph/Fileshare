#ifndef FILE_H

#define FILE_H
#include "client.h"
struct file
{
  char* name;
  int size;
  int part_size;
  char* key;
};

struct file init_file(char* name,int file_size, int part_size, char* key);
void add_file(struct client* c,struct file f);
void add_files(struct client* c,struct file* f,int n);
//n correspond au nombre de fichier à ajouter
//void remove_file(struct client* c, struct file f);
void getfile(struct queue* queue,char *key,char* buff);
struct file* look(struct queue* queue,char* name,int filesize,int op,char* buff);;
 




#endif
