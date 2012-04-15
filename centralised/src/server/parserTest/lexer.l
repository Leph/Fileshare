%{
#include "y.tab.h"
#include <stdio.h>
#include <stdlib.h>

#undef YY_INPUT
#define YY_INPUT(b,r,s) readInputForLexer(b,&r,s)

%}


number                	[0-9]*
filename              	[a-zA-Z0-9_\-]+\.[a-zA-Z0-9]+
key                  	[a-zA-Z0-9]*[a-z][a-zA-Z0-9]*		      	
            
%%

"announce"		{ return TANNOUNCE; }
"listen"		{ return TLISTEN; }
"have"                  { return THAVE; }
"look"			{ return TLOOK; }
"filename"		{ return TFILENAME; }
"filesize"		{ return TFILESIZE; }
"getfile"		{ return TGETFILE; }
"update"		{ return TUPDATE; }
"seed"			{ return TSEED; }
"leech"			{ return TLEECH; }

{filename}              { yylval.stringValue=strdup(yytext); printf("filename\n"); return FILENAME; }
{number}                { yylval.intValue=atoi(yytext); printf("number\n"); return NUMBER; }
{key}                   { yylval.stringValue=strdup(yytext); printf("key\n"); return KEY; }

"<"		{ return('<'); }
">"		{ return('>'); }
"["     	{ return('['); }
"]"     	{ return(']'); }
"="		{ return('='); }
"\""		{ return('"'); }
"," 		{ return(','); }

[ \t\v\n\f]	{ /* ignore blanks */ }

%%

void yyerror(char* s) {
    fprintf(stderr,"%s\n",s);
}

int yywrap() {
    return -1;
}
