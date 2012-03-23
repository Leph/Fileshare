#ifndef _CLIENT_H_
#define _CLIENT_H_

#include<stdlib.h>
#include<stdio.h>

#include <sys/types.h>  /* for Socket data types */
#include <sys/socket.h> /* for socket(), connect(), send(), and recv() */
#include <netinet/in.h> /* for IP Socket data types */
#include <arpa/inet.h>  /* for sockaddr_in and inet_addr() */
#include <string.h>     /* for memset() */
#include <unistd.h>     /* for close() */

#include "file.h"

#define NBR_FICHIERS_MAX 1000

struct client
{
	struct file files[NBR_FICHIERS_MAX];
	int nbr_fichiers;
        int nbr_files_max;
	struct sockaddr_in addr_client;
	struct client *prev;
	struct client *next;
};


struct queue
{
	struct client *last;
	struct client *first;
};


struct client *init_client_empty();
struct queue *init_queue_empty();
void print_queue(struct queue *);
struct queue *queue_add_last(struct queue *, struct client *);
struct queue *queue_remove_last(struct queue *);
struct queue *queue_add_first(struct queue *, struct client *);
struct queue *queue_remove_first(struct queue *);
struct queue *queue_add_after(struct queue *,struct client *, struct client *);
struct queue *queue_remove_after(struct queue *,struct client *);

#endif
