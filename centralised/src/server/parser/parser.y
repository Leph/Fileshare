%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "myparser.h"


extern void yyerror(char* s);
extern int yylex();


struct commandLine command;

%}

%token NUMBER
%token FILENAME
%token KEY
%token TANNOUNCE
%token THAVE
%token TLISTEN
%token TLOOK
%token TFILENAME
%token TFILESIZE
%token TGETFILE
%token TUPDATE
%token TSEED
%token TLEECH
%union
{
	char *stringValue;;
	int intValue;
}	

%start commande
%%

commande
: announce_commande
| look_commande
| getfile_commande
| update_commande
;
 
announce_commande
: TANNOUNCE TLISTEN NUMBER THAVE '[' fileinfo_list ']'
{
	command.type = ANNOUNCE;
	command.port = $<intValue>3;
}
;

fileinfo_list
: fileinfo fileinfo_list
| fileinfo
;

fileinfo
: '"'FILENAME'"' NUMBER NUMBER KEY
{
	command.fileNames[command.filesNumber] = strdup($<stringValue>2);
        command.lengths[command.filesNumber] = $<intValue>4;
        command.pieceSize[command.filesNumber] = $<intValue>5;
        command.keys[command.filesNumber] = strdup($<stringValue>6);
        command.filesNumber++;
}
;


look_commande
: TLOOK criterion_list_declaration
{
	command.type = LOOK;
}
;

criterion_list_declaration
: '['criterion_list']'
;

criterion_list
: criterion criterion_list
| criterion
;

criterion
: name_criterion
| sup_size_criterion
| inf_size_criterion
;

name_criterion
: TFILENAME'='FILENAME
{
	command.fileName = strdup($<stringValue>3);
}
;

sup_size_criterion
: TFILESIZE'>'NUMBER
{
	command.supTo = $<intValue>3;
}
;

inf_size_criterion
: TFILESIZE'<'NUMBER
{
	command.infTo = $<intValue>3;
}
;








getfile_commande
: TGETFILE KEY
{
	command.type = GETFILE;
	command.getKey = strdup($<stringValue>2);
}
;






update_commande
: TUPDATE update_declaration
{
	command.type = UPDATE;
}
| TUPDATE
{
	command.type = UPDATE;
	printf("commande update vide reconnue.\n");
}
;

update_declaration
: seed_declaration leech_declaration
| leech_declaration seed_declaration
| seed_declaration
| leech_declaration 
;

seed_declaration
: TSEED '['seed_list']'
{
	command.isSeeder = 1;
}
| TSEED '[' ']'
;

leech_declaration
: TLEECH '['leech_list']'
{
	command.isLeecher = 1;
}
| TLEECH '[' ']'
;

seed_list
: KEY seed_list
{
        command.seedKeys[command.seedKeysNumber] = strdup($<stringValue>1);
        command.seedKeysNumber++;
}
| KEY
{
        command.seedKeys[command.seedKeysNumber] = strdup($<stringValue>1);
        command.seedKeysNumber++;

}
;

leech_list
: KEY leech_list
{
	command.leechKeys[command.leechKeysNumber] = strdup($<stringValue>1);
        command.leechKeysNumber++;
}
| KEY
{
	command.leechKeys[command.leechKeysNumber] = strdup($<stringValue>1);
        command.leechKeysNumber++;
}
;

%%

