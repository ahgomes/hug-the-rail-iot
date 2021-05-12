###############################################################################
#  Makefile for compiling, running, and testing IoT. You may
#  need to change CLASSPATH depending on location of JUNIT
#  jar file on your computer.
#
#  I pledge my honor that I have abided by the Stevens Honor System.
#
#  Authors  : Adrian Gomes, Aliya Iqbal, Amraiza Naz, and Matthew Cunningham
#  Version  : 1.0
#  Date     : Apr 26, 2021
###############################################################################

JC         = javac
J_FILE     = $(wildcard *.java)
CLASSPATH  = ".:./*:/Library/Java/*"
JFLAGS     = -cp $(CLASSPATH)

all:
	$(JC) $(JFLAGS) $(J_FILE)

run:
	java $(JFLAGS) IoT.java

test:
	java $(JFLAGS) IoTTest.java

clean:
	$(RM) *.class *.log
