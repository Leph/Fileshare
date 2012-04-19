#ifndef _TREATMENT_H_
#define _TREATMENT_H_

#include "database.h"
#include "parser.h"

#include <sys/types.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <errno.h>
#include <unistd.h>
#include <pthread.h>
#include <sys/ioctl.h>
#include <sys/socket.h>
#include <arpa/inet.h>



int announceTreatment(int sock, struct commandLine *clone, char *addri);
int getFileTreatment(int sock, struct commandLine *clone);
int lookTreatment(int sock, struct commandLine *clone);
int updateTreatment(int sock, struct commandLine *clone, char *addri);


#endif
