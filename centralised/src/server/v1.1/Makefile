CC=gcc 
CFLAGS= -Wall -W -g -lpthread

exec :	tracker.o list.o treatment.o myparser.o list.h
	$(CC) -o exec treatment.o tracker.o y.tab.o lex.yy.o list.o myparser.o -lpthread

myparser.o: myparser.h 
	$(CC) -c myparser.c $(CFLAGS)

tracker.o: treatment.o myparser.o lex.yy.o y.tab.o tracker.c myparser.h 
	$(CC) -c tracker.c $(CFLAGS)

treatment.o: treatment.c treatment.h list.o
	$(CC) -c treatment.c $(CFLAGS)


y.tab.o : 
	$(CC) -c y.tab.c $(CFLAGS)

lex.yy.o :
	$(CC) -w -c lex.yy.c $(CFLAGS)

lk.o:	list.c list.h
	$(CC) -c list.c $(CFLAGS)

clean:
	rm *.o
	rm exec

