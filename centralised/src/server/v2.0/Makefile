CC=gcc 
CFLAGS= -Wall -W -g -lpthread

exec: tracker.o treatment.o database.o parser.o database.h
	$(CC) -o exec tracker.o treatment.o y.tab.o lex.yy.o parser.o database.o -lpthread

tracker.o: tracker.c treatment.o lex.yy.o y.tab.o tracker.h database.h parser.o parser.h
	$(CC) -c tracker.c $(CFLAGS)

treatment.o: treatment.c treatment.h database.h parser.h
	$(CC) -c treatment.c $(CFLAGS)

parser.o: parser.c parser.h
	$(CC) -c parser.c $(CFLAGS)

y.tab.o: y.tab.c y.tab.h
	$(CC) -c y.tab.c $(CFLAGS)

lex.yy.o: lex.yy.c
	$(CC) -w -c lex.yy.c $(CFLAGS)

database.o: database.c database.h 
	$(CC) -c database.c $(CFLAGS)

clean:
	rm *.o
	rm exec

