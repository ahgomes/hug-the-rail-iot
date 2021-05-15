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
