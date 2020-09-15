# (c) https://github.com/MontiCore/monticore

#
# This makefile shows how to embedd a MontiCore 
# code generation and a tool execution in a 
# makefile using make for efficient incremental generation
#
# Incremental == makefile itself checks whether to regenerate
# and therefore uses MontiCore's IncGenCheck.sh
# 

#############################################################
# Configuration (files and used commands):

# Paths to handcoded part used for the TOP mechanism
HWC=src/main/java

# All sources for the tool (includes HWC)
SRC=$(wildcard src/**/*java)

# jar for all commands: MontiCore call, Java compilation and execution
MCJAR=monticore-all.jar

# model to be processed
MODEL=src/main/grammars/Automata.mc4

#############################################################
# Rules:

# initial rule depending on all sub-activities 
all: target/f.tests
	@echo "[INFO]. Done with all."

# Activity 1: run MontiCore generator
target/f.automata: $(MODEL) $(MCJAR) 
	@echo "[INFO]. Generation of tool code from" $(MODEL)
	java -jar $(MCJAR) \
		$(MODEL) \
		-mp $(MCJAR) \
		-hcp src/main/java \
		-out target
	@touch $@

# Activity 2: compile the tool
target/f.classes: target/f.automata $(SRC)
	@echo "[INFO]. Compile tool"
	# NUR vorübergehend aus der Not heraus:
	mkdir -p target/de/monticore/parser
	cp ~/monticore/./monticore-runtime/src/main/java/de/monticore/parser/Placeholder.java \
		target/de/monticore/parser/
	mkdir -p target/de/monticore/symboltable/serialization/
	cp ~/monticore/./monticore-runtime/src/main/java/de/monticore/symboltable/serialization/JsonDeSers.java \
		target/de/monticore/symboltable/serialization
	mkdir -p de/monticore/modelloader/
	cp ~/monticore/./monticore-runtime/src/main/java/de/monticore/modelloader/IModelLoader.java \
		de/monticore/modelloader/
	# Ende der Not 
	javac -cp $(MCJAR) \
	      -d target/classes/ \
	      -sourcepath ".;target/;src/main/java/" \
	      src/main/java/automata/AutomataTool.java
	@touch $@

#KLAPPT NICHT# # Activity 3: build the tool jar
#KLAPPT NICHT# target/toolautomata.jar: target/classes
#KLAPPT NICHT# 	@echo "[INFO]. Create tool jar"
#KLAPPT NICHT# 	@jar --create \
#KLAPPT NICHT# 	     --file $@ \
#KLAPPT NICHT# 	     --main-class=src.main.java.automata.AutomataTool \
#KLAPPT NICHT# 	     $(wildcard target/classes/automata/**/*.class)
#KLAPPT NICHT# 	--> das entstehende JAR ist wohl nicht geeignet?

# Activity 4: compile the tests
# (which was on purpose not integrated in tool compilation)
target/f.testclasses: target/f.classes $(SRC)
	@echo "[INFO]. Compile tests"
	javac -cp "target/classes;.;$(MCJAR);hamcrest-core-1.3.jar;junit-4.13.jar" \
	      -d target/testclasses/ \
	      -sourcepath ".;target/;src/test/java/" \
	      $(wildcard src/test/java/*Test.java)
	@touch $@

# Activity 5: execute all tests
target/f.tests: target/f.testclasses makefile
	@echo "[INFO]. Run tests"
	@java  -cp "target/testclasses;target/classes;.;$(MCJAR);hamcrest-core-1.3.jar;junit-4.13.jar" \
	      org.junit.runner.JUnitCore \
	      $(patsubst target/testclasses/%.class,%,$(wildcard target/testclasses/*Test.class))
	@touch $@


# 
# next steps:
# generate something with the tool 



#############################################################
# Auxiliary

# get the MontiCore lwb + rte
$(MCJAR): 
	cp ~/.m2/repository/./junit/junit/4.13/junit-4.13.jar .
	cp ~/.m2/repository/./org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar .
	cp ~/monticore/./monticore-cli/target/monticore-cli-*-with-dependencies.jar $@

target:
	mkdir -p target

clean:
	rm -rf target
	rm -rf $(MCJAR)
	# NUR vorübergehend aus der Not heraus:
	rm -rf de
	rm hamcrest-core-1.3.jar junit-4.13.jar

.PHONY: clean 

