#include <stdlib.h>
#include <stdio.h>
#include "myparser.h"


int yyparse();
extern struct commandLine command;

extern int globalReadOffset;

// Chaîne à lire:
const char *globalInputText = "announce listen 4444 have [  \"Le Seigneur Des Anneaux.avi\"   699000000  1024   hkj234f5s2h6fd7f        \"Pulp Fiction.mkv\"        701000000   1024   hkj234d5s2j6fd7p \"Batman - The Dark Knight.mp4\" 1400000000 2048 h4k2l6h6gf4oas9    \"rapportRE203.pdf\" 2300000 1024 h3p4n1c8p0l2  \"music.mp3\" 4380000 1024 j5l2bvfg34f9]";

//const char *globalInputText = "look [filesize>200000000 filename=HIMYM-S07EP15.avi filesize<500000000]";

//const char *globalInputText = "getfile h4jl6v1gf3jp0q8lj1nc4kp0se54cv4dg5h7";

//const char *globalInputText = "update seed [gh3kl1g3 df56g2gbl7 fg4g2l6ggn2] leech [f4sg5fs1sdg4 gr4f3v5gh7]";

//const char *globalInputText = "update leech [f4sg5fs1sdg4 gr4f3v5gh7] seed [gh3kl1g3 df56g2gbl7 fg4g2l6ggn2]";



int main()
{
        globalReadOffset = 0;
	initCommandLine();
        yyparse();
	struct commandLine * clone= cloneCommandLine();
        printf("\n");
        affiche(&command);
        affiche(clone);
        return 0;
}
