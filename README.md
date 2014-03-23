AnalyzeThisBook
===============

Simple Text Analyzer for English Language Books in Java

INTRO:

This is a simply a small tool that I wrote to analyze In Search of Lost Time, written by Marcel Proust and one of the longest novels in history. When I finished the book, given how dense and free-flowing it is, and the mind-bogglingly log and nested with sub-clauses, most of the sentences are, I thought to try to parse it from a more detached aspect of numbers and patterns. I did find a couple of places where this kind of things was done but mostly 3rd-party tools like MATLAB or R were being used. I instead wrote something from scratch in Java. This is right now, very basic. In addition to general improvements, I am thinking of adding more methods to it for gradually. One idea was to use the 'Guide To Proust' supplement but I am unable to find any copy for it. Let us see.


A Java program that takes, as input, that expects a text file of a novel or a book and parses that to find:

1- Number of times each words was used and generates 2 files:
		a) a list of words in alphabetical order and count for each
		b) a list of words sorted in descending order by the number of times each was used.
	
	This is invoked by 'wc' operation.
	You can optionally pass a parameter x which will exclude commonly used words from the processing above and the would not be shown or used in the whoel process and the final files.
	You can further on after using this parameter specify a filename of a list of comma separated words if you hwant to override the default list of common words and supply your own.
	
	Files generated are:
		a) wordcount.txt
		b) invertedwordcount.txt
	
2- List of longest sentences in the book. You can specify top N longest sentences you want to see. If there are more than one sentences of the same size then all of these are printed. It also prints the following metrics fo rhe sentence lengths (need more testing though):
		-Mean, Median, Mode
	
	This is invoked by 'ls' operation.
	You can optionally pass a number specifying how many top N longest sentences you want to see.
	
	Files generated are:
		a) sentencelength.txt

HOW TO RUN:

First you need to compile it:

    In the directory <application-home>/javac *.java

This will compile the code and generate class files. From this same directory you can run the program as shown in the example that follow. 

You can then after compilation, invoke it as:

    AnalyzeThisBook <filename> <operation>

where filename is the source text file name and operation is one of these [wc, ls]

Examples:

    1- AnalyzeThisBook book/insearchoflosttime.txt wc

    2- AnalyzeThisBook book/insearchoflosttime.txt wc x
   (the first example is simple word count and the other with exclude default list of commonly used words option turned on.)	

    3- AnalyzeThisBook book/insearchoflosttime.txt x <excludefilename>
   (invoke word count and exclude list of words provided in the file excludefilename.)

    4- AnalyzeThisBook book/insearchoflosttime.txt ls 10
   (invoke longest sentences analysis and show me top 10 longest sentences.)

The order of the parameters matter.

NOTES:

1- The sentence terminators are considered if anything ends at one of the following AND the next word or piece of text is either a newline or starts with an uppercase letter 
	{	! ,
	, . 
	,	.)
	,	!)
	,	."
	,	.'
	,	.’
	,	.”   }
	Although this is applicable in other books as well but in case of Proust it was especially interesting to find end of a sentence because there are some characters whose saluations are abbreviations of long form and used a '.' For example M. Legrandin, Mme. Guermantes etc. To handle that, I have created a list of abbreviations, that if occur in the sentence then that point should NOT be considered an end of sentence. It is easily editable and right now have only 3 values {Mme., M., Mlle.}
	
2- The source text of In Search of Last Time that I used for consolidated and cleaned by Nathan Brixius (http://nathanbrixius.wordpress.com/2013/05/09/text-analytics-on-prousts-in-search-of-lost-time/). Thanks to him.

4- This will work only with English language books. It could be used with any other books.

5- Special characters are handled and the file is read in UTF-16.

6- Default commonly used words to be excluded are: {that,than,was,at,do,the,in,to,and,by,be,are,of,on,he,she,is,they,it,then,him,his,her,their,them,will,has,had,have,did,a}

7- Right now it will in Mac/Unix platforms where I have only tested it yet. Reason being that the special characters that are used for parsing can cause a problem. Looking into it to make it cross platform compatible.

8- Wherever you are running this program, that user should have rights to write/create files there.

9- When counting words the following characters are stripped-off ={ (,),!,',",.,‘,,,’,?,”,;,“,:,&,*,—,# }. I have not excluded - in this list yet as it is used in some cases as part of a one word.

10- There are some special characters that are used a lot in the book (accents and such), especially in names which I have not differentiated between and they might seem funny in the Unix shell. They should come up fine in GUI interfaces.
Due to this and note# 9, the calculations might not 100% accurate but the effect should be minimal given how they are used and what is the goal at this point.

CODE:

There are only two classes:
	- AnalyzeThisBook, the main class which is the driver and had the logic
	- Util, a helper class for common auxiliary methods and declarations, mostly static.
