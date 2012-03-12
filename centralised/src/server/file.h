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
void remove_file(struct client* c, struct file f);
struct client* getfile(struct queue* queue,char *key);
struct file* look(struct queue* queue,void* criterion);
 




#endif
