#ifndef FCTS_TRACKER

#define FCTS_TRACKER

#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>


int make_tcp_ip_socket();

struct sockaddr_in info_sock(int sock);

void connexion_client(int sock);
#endif
