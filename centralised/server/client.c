#include "client.h"


struct client *init_client_empty()
{
	struct client *client = malloc(sizeof(struct client));
	client->next = NULL;
	//remplir les champs..
	return client;
}


struct queue *init_queue_empty()
{
	struct queue *queue = malloc(sizeof(struct queue));
	queue->last = NULL;
	queue->first = NULL;
}


void print_queue(struct queue *queue)
{
	struct client *current = queue->last;
	printf("> ");
	while(current!=NULL)
	{
		printf(/* infos */);
		current=current->next;
	}
	printf("<\n");
}


struct queue *queue_add_last(struct queue *queue, struct client *c)
{
	queue_add_after(queue,NULL,c);
	return queue;
}


struct queue *queue_remove_last(struct queue *queue)
{
	queue_remove_after(queue,NULL);
	return queue;
}


struct queue *queue_add_first(struct queue *queue, struct client *client)
{
	queue_add_after(queue,queue->first,client);
	client->first = queue->first->next;
	return queue;
}


struct queue *queue_remove_first(struct queue *queue)
{
	struct client *current = queue->last;
	while(current!=NULL && current->next!=queue->first)
	{
		current = current->next;
	}
	queue_remove_after(queue,current);
	queue->first = current;
	return queue;
}


struct queue *queue_add_after(struct queue *queue,struct client *client1, struct client * client2)
{
	struct client *last = queue->last;
	if(client1 == NULL)
	{
		if(queue->last == NULL)
		{
			queue->first = client2;
		}
		queue->last = client2;
		client2->next = last;
	}
	else
	{
		client2->next = client1->next;
		if(client2->next != NULL)
		{
			client2->next->next->prev = client2->next;
		}
		client2->prev = client1;
		client1->next = client2;
	}
	return queue;
}


struct queue *queue_remove_after(struct queue *queue,struct client *client)
{
	struct client *current;
	if(client == NULL)
	{
		current = queue->last;
		if(current != NULL)
		{
			queue->last = queue->last->next;
			free(current);
			if(queue->last != NULL && queue->last->next != NULL)
			{
				queue->last->next->prev = queue->last;
			}
		}
	}
	else
	{
		current = client->next;
		if(client->next != NULL)
		{
			client->next = client->next->next;
			
			if(client->next != NULL)
			{
				client->next->next->prev = client->next;
				free(current);
			}
		}
	}
	return queue;
}


