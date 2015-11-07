/*
 *  The scanner definition for COOL.
 */

import java_cup.runtime.Symbol;
%%

%{


/*  Stuff enclosed in %{ %} is copied verbatim to the lexer class
 *  definition, all the extra variables/functions you want to use in the
 *  lexer actions should go here.  Don't remove or modify anything that
 *  was there initially.  */
    
    // Max size of string constants
    static int MAX_STR_CONST = 1025;
    // For assembling string constants
    StringBuffer string_buf = new StringBuffer();

    private int curr_lineno = 1;
    private int num_comment = 0;

    int get_curr_lineno() {
	return curr_lineno;
    }

    private AbstractSymbol filename;

    void set_filename(String fname) {
	filename = AbstractTable.stringtable.addString(fname);
    }

    AbstractSymbol curr_filename() {
	return filename;
    }
%}

%init{

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */

    // empty for now
%init}

%eofval{

/*  Stuff enclosed in %eofval{ %eofval} specifies java code that is
 *  executed when end-of-file is reached.  If you use multiple lexical
 *  states and want to do something special if an EOF is encountered in
 *  one of those states, place your code in the switch statement.
 *  Ultimately, you should return the EOF symbol, or your lexer won't
 *  work.  */

    switch(yy_lexical_state) {
    case YYINITIAL:
	/* nothing special to do in the initial state */
        
	break;
	case COMMENT:
            curr_lineno = yyline;
            yybegin(YYINITIAL);
            return new Symbol(TokenConstants.ERROR, "EOF in comment");
    case SINGLECOMMENT:
        yybegin(YYINITIAL);
        break;
    case STRING:
         curr_lineno = yyline;
        yybegin(YYINITIAL);
        return new Symbol(TokenConstants.ERROR, "EOF in string constant");
    }
    curr_lineno = yyline;
    return new Symbol(TokenConstants.EOF);
%eofval}

%line
%cup
%class CoolLexer
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
BlockCommentStart = "(*"
%state STRING COMMENT SINGLECOMMENT MIDNULL
%%


<YYINITIAL>{BlockCommentStart}  {num_comment++;yybegin(COMMENT);}
<COMMENT>[^*])  {curr_lineno=yyline;}
<COMMENT>"*"  {curr_lineno = yyline;}
<COMMENT>{BlockCommentStart}  {curr_lineno = yyline;num_comment++;}
<COMMENT>"*)"    {  curr_lineno =yyline;num_comment--;
                    if(num_comment==0) 
                        {curr_lineno=yyline;yybegin(YYINITIAL);}
                }

<YYINITIAL>"--" {yybegin(SINGLECOMMENT);}
<SINGLECOMMENT>[^\n\r]* {}
<SINGLECOMMENT>\n {yybegin(YYINITIAL);}

<YYINITIAL>[0-9]+ 
                                {
                                curr_lineno = yyline;
                                Symbol foo = new Symbol(TokenConstants.INT_CONST,AbstractTable.inttable.addString((yytext()))); 
                                return foo;
                                 
                                }
<YYINITIAL>\"      {curr_lineno =yyline;string_buf.setLength(0); yybegin(STRING); }

<YYINITIAL>"*)"      {curr_lineno=yyline;return new Symbol(TokenConstants.ERROR, "Unmatched "+yytext());}
<YYINITIAL>[cC][lL][aA][sS][sS]     {curr_lineno = yyline;return new Symbol(TokenConstants.CLASS);}
<YYINITIAL>[iI][fF]     {curr_lineno = yyline;return new Symbol(TokenConstants.IF);}

<YYINITIAL>"/"     {curr_lineno = yyline;return new Symbol(TokenConstants.DIV);}
<YYINITIAL>"*"     {curr_lineno = yyline;return new Symbol(TokenConstants.MULT);}
<YYINITIAL>"{"     {curr_lineno = yyline;return new Symbol(TokenConstants.LBRACE);}
<YYINITIAL>"}"     {curr_lineno = yyline;return new Symbol(TokenConstants.RBRACE);}
<YYINITIAL>"@"     {curr_lineno = yyline;return new Symbol(TokenConstants.AT);}
<YYINITIAL>"-"     {curr_lineno = yyline;return new Symbol(TokenConstants.MINUS);}
<YYINITIAL>";"     {curr_lineno = yyline;return new Symbol(TokenConstants.SEMI);}
<YYINITIAL>":"     {curr_lineno = yyline;return new Symbol(TokenConstants.COLON);}
<YYINITIAL>","     {curr_lineno = yyline;return new Symbol(TokenConstants.COMMA);}
<YYINITIAL>"<-"     {curr_lineno = yyline;return new Symbol(TokenConstants.ASSIGN);}
<YYINITIAL>"<"     {curr_lineno = yyline;return new Symbol(TokenConstants.LT);}
<YYINITIAL>"<="     {curr_lineno = yyline;return new Symbol(TokenConstants.LE);}
<YYINITIAL>"("     {curr_lineno = yyline;return new Symbol(TokenConstants.LPAREN);}
<YYINITIAL>")"     {curr_lineno = yyline;return new Symbol(TokenConstants.RPAREN);}
<YYINITIAL>[fF][iI]     {curr_lineno = yyline;return new Symbol(TokenConstants.FI);}
<YYINITIAL>[Ee][lL][sS][eE]     {curr_lineno = yyline;return new Symbol(TokenConstants.ELSE);}
<YYINITIAL>[iI][nN]     {curr_lineno = yyline;return new Symbol(TokenConstants.IN);}
<YYINITIAL>[iI][nN][hH][eE][rR][iI][tT][sS]     {curr_lineno = yyline;return new Symbol(TokenConstants.INHERITS);}
<YYINITIAL>[iI][sS][vV][oO][iI][dD]     {curr_lineno = yyline;return new Symbol(TokenConstants.ISVOID);}
<YYINITIAL>[lL][eE][tT]     {curr_lineno = yyline;return new Symbol(TokenConstants.LET);}
<YYINITIAL>[lL][oO][oO][pP]     {curr_lineno = yyline;return new Symbol(TokenConstants.LOOP);}
<YYINITIAL>[pP][oO][oO][lL]     {curr_lineno = yyline;return new Symbol(TokenConstants.POOL);}
<YYINITIAL>[tT][hH][eE][nN]     {curr_lineno = yyline;return new Symbol(TokenConstants.THEN);}
<YYINITIAL>[wW][hH][iI][lL][eE]     {curr_lineno = yyline;return new Symbol(TokenConstants.WHILE);}
<YYINITIAL>[nN][eE][wW]     {curr_lineno = yyline;return new Symbol(TokenConstants.NEW);}
<YYINITIAL>[oO][fF]     {curr_lineno = yyline;return new Symbol(TokenConstants.OF);}
<YYINITIAL>[nN][oO][tT]     {curr_lineno = yyline;return new Symbol(TokenConstants.NOT);}
<YYINITIAL>[cC][aA][sS][eE]     {curr_lineno = yyline;return new Symbol(TokenConstants.CASE);}
<YYINITIAL>[eE][sS][aA][cC]     {curr_lineno = yyline;return new Symbol(TokenConstants.ESAC);}
<YYINITIAL>[t][rR][uU][eE]     {curr_lineno = yyline;return new Symbol(TokenConstants.BOOL_CONST, java.lang.Boolean.TRUE);}
<YYINITIAL>[f][aA][lL][sS][eE]     {curr_lineno = yyline;return new Symbol(TokenConstants.BOOL_CONST, java.lang.Boolean.FALSE);}
<YYINITIAL>"=>"			{curr_lineno = yyline;return new Symbol(TokenConstants.DARROW); }
<YYINITIAL>"~"			{curr_lineno = yyline;return new Symbol(TokenConstants.NEG); }
<YYINITIAL>"="			{curr_lineno = yyline;return new Symbol(TokenConstants.EQ); }
<YYINITIAL>"."			{curr_lineno = yyline;return new Symbol(TokenConstants.DOT); }
<YYINITIAL>"+"			{curr_lineno = yyline;return new Symbol(TokenConstants.PLUS); }

<YYINITIAL>[A-Z][_a-zA-Z0-9]* {
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
<YYINITIAL>[a-z][_a-zA-Z0-9]* {
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
<YYINITIAL>[\t \n\f\r\x0B] {curr_lineno=yyline;}
<YYINITIAL>.        {curr_lineno = yyline;return new Symbol(TokenConstants.ERROR,yytext());}

<STRING>\"              { yybegin(YYINITIAL) ; 
                        curr_lineno = yyline;
                        if(string_buf.length()>MAX_STR_CONST)
                            {return new Symbol(TokenConstants.ERROR, "String constant too long");}
                        if(string_buf.toString().length()>0 && string_buf.toString().charAt(string_buf.toString().length()-1) =='\0'){
                            return new Symbol(TokenConstants.ERROR, "String contains null character");
                        }
                        return new Symbol(TokenConstants.STR_CONST, AbstractTable.stringtable.addString(string_buf.toString()));}
<STRING>[^\n\"\\]              {if(yytext().charAt(0)=='\0') {string_buf.setLength(0);yybegin(MIDNULL);return new Symbol(TokenConstants.ERROR,"String contains null character");}string_buf.append(yytext()); }
                                
<STRING>\\t                 { string_buf.append('\t'); }
<STRING>\\f                 {string_buf.append('\f'); }
<STRING>\\b                 { string_buf.append('\b'); }
<STRING>\n                  {curr_lineno = yyline;string_buf.setLength(0);yybegin(YYINITIAL);return new Symbol(TokenConstants.ERROR,"Unterminated string constant");}
<STRING>\\n                 { string_buf.append("\n"); }
<STRING>\\\n                 { curr_lineno=yyline;string_buf.append("\n"); }
<STRING>\\\"                {  string_buf.append('\"'); }
<STRING>\\                  {}
<STRING>\\\\                { string_buf.append("\\");}


<MIDNULL>[^\"\n\r]   {}
<MIDNULL>\"   {yybegin(YYINITIAL);}
<MIDNULL>\n   {yybegin(YYINITIAL);}
