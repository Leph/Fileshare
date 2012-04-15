CC=gcc -g
CFLAGS= -Wall -W  -g

exec :	main.o   lk.o  lk.h
	$(CC) -o exec main.o  lk.o -g 


main.o:	main.c   
	$(CC)   -c main.c $(CFLAGS)


lk.o:	lk.c lk.h
	$(CC) -c lk.c $(CFLAGS)

clean:
	rm *.o

