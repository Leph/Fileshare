CC=gcc 
CFLAGS= -Wall -W -g -lpthread 

exec :	tracker.o   lk.o   hachage.o myparser.o linkedList/lk.h
	$(CC) -o  exec hachage.o tracker.o y.tab.o lex.yy.o lk.o myparser.o  -lpthread

myparser.o: parser/myparser.h 
	$(CC)  -c parser/myparser.c $(CFLAGS)

tracker.o: hachage.o 	myparser.o lex.yy.o	y.tab.o	tracker.c  parser/myparser.h 
	$(CC) hachage.o myparser.o y.tab.o lex.yy.o -c tracker.c $(CFLAGS)

hachage.o: hachage.h lk.o
	$(CC) lk.o -c hachage.c $(CFLAGS)


y.tab.o : 
	$(CC)   -c parser/y.tab.c $(CFLAGS)

lex.yy.o :
	$(CC)   -c parser/lex.yy.c $(CFLAGS)

lk.o:	linkedList/lk.c linkedList/lk.h
	$(CC) -c linkedList/lk.c $(CFLAGS)

clean:
	rm *.o
	rm exec

