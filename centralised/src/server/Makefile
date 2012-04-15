CC=gcc 
CFLAGS= -Wall -W -g -lpthread

exec :	tracker.o lk.o hachage.o myparser.o lk.h
	$(CC) -o  exec hachage.o tracker.o y.tab.o lex.yy.o lk.o myparser.o -lpthread

myparser.o: myparser.h 
	$(CC)  -c myparser.c $(CFLAGS)

tracker.o: hachage.o myparser.o lex.yy.o y.tab.o tracker.c myparser.h 
	$(CC)  -c tracker.c $(CFLAGS)

hachage.o: hachage.c hachage.h lk.o
	$(CC) -c hachage.c $(CFLAGS)


y.tab.o : 
	$(CC) -c y.tab.c $(CFLAGS)

lex.yy.o :
	$(CC) -c lex.yy.c $(CFLAGS)

lk.o:	lk.c lk.h
	$(CC) -c lk.c $(CFLAGS)

clean:
	rm *.o
	rm exec

