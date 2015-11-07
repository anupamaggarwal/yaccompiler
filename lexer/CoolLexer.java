/*
 *  The scanner definition for COOL.
 */
import java_cup.runtime.Symbol;


class CoolLexer implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

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
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	CoolLexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	CoolLexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private CoolLexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */
    // empty for now
	}

	private boolean yy_eof_done = false;
	private final int STRING = 1;
	private final int YYINITIAL = 0;
	private final int SINGLECOMMENT = 3;
	private final int COMMENT = 2;
	private final int MIDNULL = 4;
	private final int yy_state_dtrans[] = {
		0,
		69,
		91,
		64,
		94
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NOT_ACCEPT,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NOT_ACCEPT,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NOT_ACCEPT,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR,
		/* 139 */ YY_NO_ANCHOR,
		/* 140 */ YY_NO_ANCHOR,
		/* 141 */ YY_NO_ANCHOR,
		/* 142 */ YY_NO_ANCHOR,
		/* 143 */ YY_NO_ANCHOR,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NO_ANCHOR,
		/* 147 */ YY_NO_ANCHOR,
		/* 148 */ YY_NO_ANCHOR,
		/* 149 */ YY_NO_ANCHOR,
		/* 150 */ YY_NO_ANCHOR,
		/* 151 */ YY_NO_ANCHOR,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NO_ANCHOR,
		/* 154 */ YY_NO_ANCHOR,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR,
		/* 159 */ YY_NO_ANCHOR,
		/* 160 */ YY_NO_ANCHOR,
		/* 161 */ YY_NO_ANCHOR,
		/* 162 */ YY_NO_ANCHOR,
		/* 163 */ YY_NO_ANCHOR,
		/* 164 */ YY_NO_ANCHOR,
		/* 165 */ YY_NO_ANCHOR,
		/* 166 */ YY_NO_ANCHOR,
		/* 167 */ YY_NO_ANCHOR,
		/* 168 */ YY_NO_ANCHOR,
		/* 169 */ YY_NO_ANCHOR,
		/* 170 */ YY_NO_ANCHOR,
		/* 171 */ YY_NO_ANCHOR,
		/* 172 */ YY_NO_ANCHOR,
		/* 173 */ YY_NO_ANCHOR,
		/* 174 */ YY_NO_ANCHOR,
		/* 175 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"61:9,60,7,60:2,3,61:18,60,61,9,61:5,1,4,2,41,22,5,40,16,8:10,21,20,23,24,38" +
",61,19,42,43,44,45,46,15,43,47,48,43:2,49,43,50,51,52,43,53,54,29,55,56,57," +
"43:3,61,6,61:2,58,61,12,62,10,32,25,37,59,27,14,59:2,11,59,26,31,33,59,28,1" +
"3,35,36,30,34,59:3,17,61,18,39,61,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,176,
"0,1,2,3,1:2,4,1,5,1,6,7,1:7,8,9,1:6,10,11,12,1:3,10:8,12,10:7,1,13,1:9,14,1" +
"5,1:2,16,1:4,17,18,19,12,20,10,12:8,10,12:5,1,21,22,23,24,25,26,27,28,29,30" +
",31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55" +
",56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80" +
",81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,10,12,96,97,98,99,100,101,102" +
",103,104")[0];

	private int yy_nxt[][] = unpackFromString(105,63,
"1,2,3,4,5,6,7,4,8,9,10,125,165:2,70,11,12,13,14,15,16,17,18,19,20,167,127,1" +
"65:2,71,165,92,165,169,171,173,165,95,7,21,22,23,166:2,168,166,170,166,93,1" +
"26,128,96,172,166:4,174,7,165,4,7,165,-1:65,24,-1:64,25,-1:63,26,-1:65,8,-1" +
":62,165,-1,165,175,129,165:3,-1:9,165:13,-1:4,129,165:6,175,165:10,-1:2,165" +
",-1:8,166,-1,166:4,29,166,-1:9,166:13,-1:4,166:6,29,166:11,-1:2,166,-1:5,30" +
",-1:18,31,-1:76,32,-1:32,165,-1,165:6,-1:9,165:13,-1:4,165:18,-1:2,165,-1:8" +
",165,-1,165:6,-1:9,165:2,153,165:10,-1:4,165:5,153,165:12,-1:2,165,-1:8,166" +
",-1,166:6,-1:9,166:13,-1:4,166:18,-1:2,166,-1:6,53,54,-1,55,-1:16,56,-1:8,5" +
"7,-1,58,-1:24,59,-1:2,62,-1:64,63,-1:58,1,90:2,-1,90:3,65,90:55,1,49:5,50,5" +
"1,49,52,49:53,-1:8,165,-1,165:3,133,165,27,-1:9,165,28,165:10,27,-1:4,165:8" +
",28,165:3,133,165:5,-1:2,165,-1:8,166,-1,166:6,-1:9,166:2,130,166:10,-1:4,1" +
"66:5,130,166:12,-1:2,166,-1:8,166,-1,166:6,-1:9,166:2,152,166:10,-1:4,166:5" +
",152,166:12,-1:2,166,-1,90:2,-1,90:3,-1,90:55,1,60,61,89:60,-1:8,165,-1,165" +
":5,33,-1:9,165:12,33,-1:4,165:18,-1:2,165,-1:8,166,-1,166:3,140,166,72,-1:9" +
",166,73,166:10,72,-1:4,166:8,73,166:3,140,166:5,-1:2,166,1,66:2,-1,66:3,67," +
"66,68,66:53,-1:8,165,-1,165:2,147,165,74,165,-1:9,165:13,-1:4,147,165:5,74," +
"165:11,-1:2,165,-1:8,166,-1,166:5,75,-1:9,166:12,75,-1:4,166:18,-1:2,166,-1" +
":8,165,-1,165:6,-1:9,165:4,34,165:5,34,165:2,-1:4,165:18,-1:2,165,-1:8,166," +
"-1,166:6,-1:9,166:4,76,166:5,76,166:2,-1:4,166:18,-1:2,166,-1:8,165,-1,165:" +
"6,-1:9,165:9,35,165:3,-1:4,165:15,35,165:2,-1:2,165,-1:8,166,-1,166:6,-1:9," +
"166:9,77,166:3,-1:4,166:15,77,166:2,-1:2,166,-1:8,165,-1,165:6,-1:9,165:4,3" +
"6,165:5,36,165:2,-1:4,165:18,-1:2,165,-1:8,166,-1,166:6,-1:9,166:4,78,166:5" +
",78,166:2,-1:4,166:18,-1:2,166,-1:8,165,-1,165:6,-1:9,37,165:12,-1:4,165:4," +
"37,165:13,-1:2,165,-1:8,166,-1,166:6,-1:9,166,41,166:11,-1:4,166:8,41,166:9" +
",-1:2,166,-1:8,165,-1,165:6,-1:9,165:8,38,165:4,-1:4,165:10,38,165:7,-1:2,1" +
"65,-1:8,166,-1,166:6,-1:9,79,166:12,-1:4,166:4,79,166:13,-1:2,166,-1:8,165," +
"-1,165:6,-1:9,39,165:12,-1:4,165:4,39,165:13,-1:2,165,-1:8,166,-1,166:6,-1:" +
"9,81,166:12,-1:4,166:4,81,166:13,-1:2,166,-1:8,165,-1,40,165:5,-1:9,165:13," +
"-1:4,165:2,40,165:15,-1:2,165,-1:8,166,-1,82,166:5,-1:9,166:13,-1:4,166:2,8" +
"2,166:15,-1:2,166,-1:8,165,-1,165,42,165:4,-1:9,165:13,-1:4,165:7,42,165:10" +
",-1:2,165,-1:8,166,-1,166:6,-1:9,166:8,80,166:4,-1:4,166:10,80,166:7,-1:2,1" +
"66,-1:8,165,-1,165:6,-1:9,165,83,165:11,-1:4,165:8,83,165:9,-1:2,165,-1:8,1" +
"66,-1,166,84,166:4,-1:9,166:13,-1:4,166:7,84,166:10,-1:2,166,-1:8,165,-1,16" +
"5:6,-1:9,43,165:12,-1:4,165:4,43,165:13,-1:2,165,-1:8,166,-1,166:3,85,166:2" +
",-1:9,166:13,-1:4,166:12,85,166:5,-1:2,166,-1:8,165,-1,165:3,44,165:2,-1:9," +
"165:13,-1:4,165:12,44,165:5,-1:2,165,-1:8,166,-1,166:6,-1:9,86,166:12,-1:4," +
"166:4,86,166:13,-1:2,166,-1:8,165,-1,165:6,-1:9,45,165:12,-1:4,165:4,45,165" +
":13,-1:2,165,-1:8,166,-1,166:6,-1:9,166:7,87,166:5,-1:4,166:3,87,166:14,-1:" +
"2,166,-1:8,165,-1,165:6,-1:9,46,165:12,-1:4,165:4,46,165:13,-1:2,165,-1:8,1" +
"66,-1,166:3,88,166:2,-1:9,166:13,-1:4,166:12,88,166:5,-1:2,166,-1:8,165,-1," +
"165:6,-1:9,165:7,47,165:5,-1:4,165:3,47,165:14,-1:2,165,-1:8,165,-1,165:3,4" +
"8,165:2,-1:9,165:13,-1:4,165:12,48,165:5,-1:2,165,-1:8,165,-1,165:6,-1:9,97" +
",165:5,131,165:6,-1:4,165:4,97,165:4,131,165:8,-1:2,165,-1:8,166,-1,166:6,-" +
"1:9,98,166:5,142,166:6,-1:4,166:4,98,166:4,142,166:8,-1:2,166,-1:8,165,-1,1" +
"65:6,-1:9,99,165:5,101,165:6,-1:4,165:4,99,165:4,101,165:8,-1:2,165,-1:8,16" +
"6,-1,166:6,-1:9,100,166:5,102,166:6,-1:4,166:4,100,166:4,102,166:8,-1:2,166" +
",-1:8,165,-1,165:3,103,165:2,-1:9,165:13,-1:4,165:12,103,165:5,-1:2,165,-1:" +
"8,166,-1,166:6,-1:9,104,166:12,-1:4,166:4,104,166:13,-1:2,166,-1:8,165,-1,1" +
"65:6,-1:9,165:6,105,165:6,-1:4,165:9,105,165:8,-1:2,165,-1:8,166,-1,166:2,1" +
"48,166:3,-1:9,166:13,-1:4,148,166:17,-1:2,166,-1:8,165,-1,165:6,-1:9,165:5," +
"151,165:7,-1:4,165:14,151,165:3,-1:2,165,-1:8,166,-1,166:3,106,166:2,-1:9,1" +
"66:13,-1:4,166:12,106,166:5,-1:2,166,-1:8,165,-1,165:3,107,165:2,-1:9,165:1" +
"3,-1:4,165:12,107,165:5,-1:2,165,-1:8,166,-1,166:3,108,166:2,-1:9,166:13,-1" +
":4,166:12,108,166:5,-1:2,166,-1:8,165,-1,165:2,109,165:3,-1:9,165:13,-1:4,1" +
"09,165:17,-1:2,165,-1:8,166,-1,166:2,110,166:3,-1:9,166:13,-1:4,110,166:17," +
"-1:2,166,-1:8,165,-1,165:6,-1:9,165:6,111,165:6,-1:4,165:9,111,165:8,-1:2,1" +
"65,-1:8,166,-1,166:6,-1:9,166:5,150,166:7,-1:4,166:14,150,166:3,-1:2,166,-1" +
":8,165,-1,165:4,155,165,-1:9,165:13,-1:4,165:6,155,165:11,-1:2,165,-1:8,166" +
",-1,166:6,-1:9,166:6,112,166:6,-1:4,166:9,112,166:8,-1:2,166,-1:8,165,-1,16" +
"5:6,-1:9,113,165:12,-1:4,165:4,113,165:13,-1:2,165,-1:8,166,-1,166:6,-1:9,1" +
"66:6,114,166:6,-1:4,166:9,114,166:8,-1:2,166,-1:8,165,-1,165:6,-1:9,165:11," +
"115,165,-1:4,165:13,115,165:4,-1:2,165,-1:8,166,-1,166:4,154,166,-1:9,166:1" +
"3,-1:4,166:6,154,166:11,-1:2,166,-1:8,165,-1,165,157,165:4,-1:9,165:13,-1:4" +
",165:7,157,165:10,-1:2,165,-1:8,166,-1,166:3,116,166:2,-1:9,166:13,-1:4,166" +
":12,116,166:5,-1:2,166,-1:8,165,-1,165:3,117,165:2,-1:9,165:13,-1:4,165:12," +
"117,165:5,-1:2,165,-1:8,166,-1,166:6,-1:9,166:6,156,166:6,-1:4,166:9,156,16" +
"6:8,-1:2,166,-1:8,165,-1,165:6,-1:9,165:6,159,165:6,-1:4,165:9,159,165:8,-1" +
":2,165,-1:8,166,-1,166:6,-1:9,158,166:12,-1:4,166:4,158,166:13,-1:2,166,-1:" +
"8,165,-1,165:6,-1:9,161,165:12,-1:4,165:4,161,165:13,-1:2,165,-1:8,166,-1,1" +
"66,118,166:4,-1:9,166:13,-1:4,166:7,118,166:10,-1:2,166,-1:8,165,-1,165,119" +
",165:4,-1:9,165:13,-1:4,165:7,119,165:10,-1:2,165,-1:8,166,-1,166:4,120,166" +
",-1:9,166:13,-1:4,166:6,120,166:11,-1:2,166,-1:8,165,-1,165:3,121,165:2,-1:" +
"9,165:13,-1:4,165:12,121,165:5,-1:2,165,-1:8,166,-1,166:6,-1:9,166:3,160,16" +
"6:9,-1:4,166:11,160,166:6,-1:2,166,-1:8,165,-1,165:4,123,165,-1:9,165:13,-1" +
":4,165:6,123,165:11,-1:2,165,-1:8,166,-1,166:4,162,166,-1:9,166:13,-1:4,166" +
":6,162,166:11,-1:2,166,-1:8,165,-1,165:6,-1:9,165:3,163,165:9,-1:4,165:11,1" +
"63,165:6,-1:2,165,-1:8,166,-1,166:6,-1:9,166:4,122,166:5,122,166:2,-1:4,166" +
":18,-1:2,166,-1:8,165,-1,165:4,164,165,-1:9,165:13,-1:4,165:6,164,165:11,-1" +
":2,165,-1:8,165,-1,165:6,-1:9,165:4,124,165:5,124,165:2,-1:4,165:18,-1:2,16" +
"5,-1:8,165,-1,165,135,165,137,165:2,-1:9,165:13,-1:4,165:7,135,165:4,137,16" +
"5:5,-1:2,165,-1:8,166,-1,166,132,134,166:3,-1:9,166:13,-1:4,134,166:6,132,1" +
"66:10,-1:2,166,-1:8,165,-1,165:6,-1:9,165:6,139,165:6,-1:4,165:9,139,165:8," +
"-1:2,165,-1:8,166,-1,166,136,166,138,166:2,-1:9,166:13,-1:4,166:7,136,166:4" +
",138,166:5,-1:2,166,-1:8,165,-1,165:6,-1:9,165:2,141,165:10,-1:4,165:5,141," +
"165:12,-1:2,165,-1:8,166,-1,166:6,-1:9,166:6,144,166:6,-1:4,166:9,144,166:8" +
",-1:2,166,-1:8,165,-1,165:6,-1:9,165:2,143,145,165:9,-1:4,165:5,143,165:5,1" +
"45,165:6,-1:2,165,-1:8,166,-1,166:6,-1:9,166:2,146,166:10,-1:4,166:5,146,16" +
"6:12,-1:2,166,-1:8,165,-1,165:2,149,165:3,-1:9,165:13,-1:4,149,165:17,-1:2," +
"165");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

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
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{curr_lineno = yyline;return new Symbol(TokenConstants.LPAREN);}
					case -3:
						break;
					case 3:
						{curr_lineno = yyline;return new Symbol(TokenConstants.MULT);}
					case -4:
						break;
					case 4:
						{curr_lineno=yyline;}
					case -5:
						break;
					case 5:
						{curr_lineno = yyline;return new Symbol(TokenConstants.RPAREN);}
					case -6:
						break;
					case 6:
						{curr_lineno = yyline;return new Symbol(TokenConstants.MINUS);}
					case -7:
						break;
					case 7:
						{curr_lineno = yyline;return new Symbol(TokenConstants.ERROR,yytext());}
					case -8:
						break;
					case 8:
						{
                                curr_lineno = yyline;
                                Symbol foo = new Symbol(TokenConstants.INT_CONST,AbstractTable.inttable.addString((yytext()))); 
                                return foo;
                                }
					case -9:
						break;
					case 9:
						{curr_lineno =yyline;string_buf.setLength(0); yybegin(STRING); }
					case -10:
						break;
					case 10:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -11:
						break;
					case 11:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -12:
						break;
					case 12:
						{curr_lineno = yyline;return new Symbol(TokenConstants.DIV);}
					case -13:
						break;
					case 13:
						{curr_lineno = yyline;return new Symbol(TokenConstants.LBRACE);}
					case -14:
						break;
					case 14:
						{curr_lineno = yyline;return new Symbol(TokenConstants.RBRACE);}
					case -15:
						break;
					case 15:
						{curr_lineno = yyline;return new Symbol(TokenConstants.AT);}
					case -16:
						break;
					case 16:
						{curr_lineno = yyline;return new Symbol(TokenConstants.SEMI);}
					case -17:
						break;
					case 17:
						{curr_lineno = yyline;return new Symbol(TokenConstants.COLON);}
					case -18:
						break;
					case 18:
						{curr_lineno = yyline;return new Symbol(TokenConstants.COMMA);}
					case -19:
						break;
					case 19:
						{curr_lineno = yyline;return new Symbol(TokenConstants.LT);}
					case -20:
						break;
					case 20:
						{curr_lineno = yyline;return new Symbol(TokenConstants.EQ); }
					case -21:
						break;
					case 21:
						{curr_lineno = yyline;return new Symbol(TokenConstants.NEG); }
					case -22:
						break;
					case 22:
						{curr_lineno = yyline;return new Symbol(TokenConstants.DOT); }
					case -23:
						break;
					case 23:
						{curr_lineno = yyline;return new Symbol(TokenConstants.PLUS); }
					case -24:
						break;
					case 24:
						{num_comment++;yybegin(COMMENT);}
					case -25:
						break;
					case 25:
						{curr_lineno=yyline;return new Symbol(TokenConstants.ERROR, "Unmatched "+yytext());}
					case -26:
						break;
					case 26:
						{yybegin(SINGLECOMMENT);}
					case -27:
						break;
					case 27:
						{curr_lineno = yyline;return new Symbol(TokenConstants.IF);}
					case -28:
						break;
					case 28:
						{curr_lineno = yyline;return new Symbol(TokenConstants.IN);}
					case -29:
						break;
					case 29:
						{curr_lineno = yyline;return new Symbol(TokenConstants.FI);}
					case -30:
						break;
					case 30:
						{curr_lineno = yyline;return new Symbol(TokenConstants.ASSIGN);}
					case -31:
						break;
					case 31:
						{curr_lineno = yyline;return new Symbol(TokenConstants.LE);}
					case -32:
						break;
					case 32:
						{curr_lineno = yyline;return new Symbol(TokenConstants.DARROW); }
					case -33:
						break;
					case 33:
						{curr_lineno = yyline;return new Symbol(TokenConstants.OF);}
					case -34:
						break;
					case 34:
						{curr_lineno = yyline;return new Symbol(TokenConstants.LET);}
					case -35:
						break;
					case 35:
						{curr_lineno = yyline;return new Symbol(TokenConstants.NEW);}
					case -36:
						break;
					case 36:
						{curr_lineno = yyline;return new Symbol(TokenConstants.NOT);}
					case -37:
						break;
					case 37:
						{curr_lineno = yyline;return new Symbol(TokenConstants.CASE);}
					case -38:
						break;
					case 38:
						{curr_lineno = yyline;return new Symbol(TokenConstants.LOOP);}
					case -39:
						break;
					case 39:
						{curr_lineno = yyline;return new Symbol(TokenConstants.ELSE);}
					case -40:
						break;
					case 40:
						{curr_lineno = yyline;return new Symbol(TokenConstants.ESAC);}
					case -41:
						break;
					case 41:
						{curr_lineno = yyline;return new Symbol(TokenConstants.THEN);}
					case -42:
						break;
					case 42:
						{curr_lineno = yyline;return new Symbol(TokenConstants.POOL);}
					case -43:
						break;
					case 43:
						{curr_lineno = yyline;return new Symbol(TokenConstants.BOOL_CONST, java.lang.Boolean.TRUE);}
					case -44:
						break;
					case 44:
						{curr_lineno = yyline;return new Symbol(TokenConstants.CLASS);}
					case -45:
						break;
					case 45:
						{curr_lineno = yyline;return new Symbol(TokenConstants.WHILE);}
					case -46:
						break;
					case 46:
						{curr_lineno = yyline;return new Symbol(TokenConstants.BOOL_CONST, java.lang.Boolean.FALSE);}
					case -47:
						break;
					case 47:
						{curr_lineno = yyline;return new Symbol(TokenConstants.ISVOID);}
					case -48:
						break;
					case 48:
						{curr_lineno = yyline;return new Symbol(TokenConstants.INHERITS);}
					case -49:
						break;
					case 49:
						{if(yytext().charAt(0)=='\0') {string_buf.setLength(0);yybegin(MIDNULL);return new Symbol(TokenConstants.ERROR,"String contains null character");}string_buf.append(yytext()); }
					case -50:
						break;
					case 50:
						{}
					case -51:
						break;
					case 51:
						{curr_lineno = yyline;string_buf.setLength(0);yybegin(YYINITIAL);return new Symbol(TokenConstants.ERROR,"Unterminated string constant");}
					case -52:
						break;
					case 52:
						{ yybegin(YYINITIAL) ; 
                        curr_lineno = yyline;
                        if(string_buf.length()>MAX_STR_CONST)
                            {return new Symbol(TokenConstants.ERROR, "String constant too long");}
                        if(string_buf.toString().length()>0 && string_buf.toString().charAt(string_buf.toString().length()-1) =='\0'){
                            return new Symbol(TokenConstants.ERROR, "String contains null character");
                        }
                        return new Symbol(TokenConstants.STR_CONST, AbstractTable.stringtable.addString(string_buf.toString()));}
					case -53:
						break;
					case 53:
						{ string_buf.append("\\");}
					case -54:
						break;
					case 54:
						{ curr_lineno=yyline;string_buf.append("\n"); }
					case -55:
						break;
					case 55:
						{  string_buf.append('\"'); }
					case -56:
						break;
					case 56:
						{ string_buf.append("\n"); }
					case -57:
						break;
					case 57:
						{ string_buf.append('\t'); }
					case -58:
						break;
					case 58:
						{string_buf.append('\f'); }
					case -59:
						break;
					case 59:
						{ string_buf.append('\b'); }
					case -60:
						break;
					case 60:
						{curr_lineno=yyline;}
					case -61:
						break;
					case 61:
						{curr_lineno = yyline;}
					case -62:
						break;
					case 62:
						{curr_lineno = yyline;num_comment++;}
					case -63:
						break;
					case 63:
						{  curr_lineno =yyline;num_comment--;
                    if(num_comment==0) 
                        {curr_lineno=yyline;yybegin(YYINITIAL);}
                }
					case -64:
						break;
					case 64:
						{}
					case -65:
						break;
					case 65:
						{yybegin(YYINITIAL);}
					case -66:
						break;
					case 66:
						{}
					case -67:
						break;
					case 67:
						{yybegin(YYINITIAL);}
					case -68:
						break;
					case 68:
						{yybegin(YYINITIAL);}
					case -69:
						break;
					case 70:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -70:
						break;
					case 71:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -71:
						break;
					case 72:
						{curr_lineno = yyline;return new Symbol(TokenConstants.IF);}
					case -72:
						break;
					case 73:
						{curr_lineno = yyline;return new Symbol(TokenConstants.IN);}
					case -73:
						break;
					case 74:
						{curr_lineno = yyline;return new Symbol(TokenConstants.FI);}
					case -74:
						break;
					case 75:
						{curr_lineno = yyline;return new Symbol(TokenConstants.OF);}
					case -75:
						break;
					case 76:
						{curr_lineno = yyline;return new Symbol(TokenConstants.LET);}
					case -76:
						break;
					case 77:
						{curr_lineno = yyline;return new Symbol(TokenConstants.NEW);}
					case -77:
						break;
					case 78:
						{curr_lineno = yyline;return new Symbol(TokenConstants.NOT);}
					case -78:
						break;
					case 79:
						{curr_lineno = yyline;return new Symbol(TokenConstants.CASE);}
					case -79:
						break;
					case 80:
						{curr_lineno = yyline;return new Symbol(TokenConstants.LOOP);}
					case -80:
						break;
					case 81:
						{curr_lineno = yyline;return new Symbol(TokenConstants.ELSE);}
					case -81:
						break;
					case 82:
						{curr_lineno = yyline;return new Symbol(TokenConstants.ESAC);}
					case -82:
						break;
					case 83:
						{curr_lineno = yyline;return new Symbol(TokenConstants.THEN);}
					case -83:
						break;
					case 84:
						{curr_lineno = yyline;return new Symbol(TokenConstants.POOL);}
					case -84:
						break;
					case 85:
						{curr_lineno = yyline;return new Symbol(TokenConstants.CLASS);}
					case -85:
						break;
					case 86:
						{curr_lineno = yyline;return new Symbol(TokenConstants.WHILE);}
					case -86:
						break;
					case 87:
						{curr_lineno = yyline;return new Symbol(TokenConstants.ISVOID);}
					case -87:
						break;
					case 88:
						{curr_lineno = yyline;return new Symbol(TokenConstants.INHERITS);}
					case -88:
						break;
					case 89:
						{curr_lineno=yyline;}
					case -89:
						break;
					case 90:
						{}
					case -90:
						break;
					case 92:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -91:
						break;
					case 93:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -92:
						break;
					case 95:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -93:
						break;
					case 96:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -94:
						break;
					case 97:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -95:
						break;
					case 98:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -96:
						break;
					case 99:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -97:
						break;
					case 100:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -98:
						break;
					case 101:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -99:
						break;
					case 102:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -100:
						break;
					case 103:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -101:
						break;
					case 104:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -102:
						break;
					case 105:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -103:
						break;
					case 106:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -104:
						break;
					case 107:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -105:
						break;
					case 108:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -106:
						break;
					case 109:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -107:
						break;
					case 110:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -108:
						break;
					case 111:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -109:
						break;
					case 112:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -110:
						break;
					case 113:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -111:
						break;
					case 114:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -112:
						break;
					case 115:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -113:
						break;
					case 116:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -114:
						break;
					case 117:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -115:
						break;
					case 118:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -116:
						break;
					case 119:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -117:
						break;
					case 120:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -118:
						break;
					case 121:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -119:
						break;
					case 122:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -120:
						break;
					case 123:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -121:
						break;
					case 124:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -122:
						break;
					case 125:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -123:
						break;
					case 126:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -124:
						break;
					case 127:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -125:
						break;
					case 128:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -126:
						break;
					case 129:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -127:
						break;
					case 130:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -128:
						break;
					case 131:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -129:
						break;
					case 132:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -130:
						break;
					case 133:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -131:
						break;
					case 134:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -132:
						break;
					case 135:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -133:
						break;
					case 136:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -134:
						break;
					case 137:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -135:
						break;
					case 138:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -136:
						break;
					case 139:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -137:
						break;
					case 140:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -138:
						break;
					case 141:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -139:
						break;
					case 142:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -140:
						break;
					case 143:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -141:
						break;
					case 144:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -142:
						break;
					case 145:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -143:
						break;
					case 146:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -144:
						break;
					case 147:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -145:
						break;
					case 148:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -146:
						break;
					case 149:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -147:
						break;
					case 150:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -148:
						break;
					case 151:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -149:
						break;
					case 152:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -150:
						break;
					case 153:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -151:
						break;
					case 154:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -152:
						break;
					case 155:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -153:
						break;
					case 156:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -154:
						break;
					case 157:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -155:
						break;
					case 158:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -156:
						break;
					case 159:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -157:
						break;
					case 160:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -158:
						break;
					case 161:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -159:
						break;
					case 162:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -160:
						break;
					case 163:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -161:
						break;
					case 164:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -162:
						break;
					case 165:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -163:
						break;
					case 166:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -164:
						break;
					case 167:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -165:
						break;
					case 168:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -166:
						break;
					case 169:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -167:
						break;
					case 170:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -168:
						break;
					case 171:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -169:
						break;
					case 172:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -170:
						break;
					case 173:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -171:
						break;
					case 174:
						{
                            curr_lineno = yyline;
                            return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext()));
                              }
					case -172:
						break;
					case 175:
						{
                            curr_lineno = yyline;    
                            return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext()));
                              }
					case -173:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
