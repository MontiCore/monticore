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

# jar for the MontiCore call
MCJAR=monticore-all.jar


# jar for Java compilation: runtime and grammars built using the snapshot generator
MCRT=monticore-rt.jar

# jar for grammars
MCG=monticore-g.jar

# model to be processed
MODEL=src/main/grammars/Automata.mc4

#############################################################
# Rules:

# initial rule depending on all sub-activities
all: target/f.executes target/f.tests
	@echo "[INFO]. Done with all."

#############################################################
# Generate and compile:

# Activity 1: run MontiCore generator
target/f.automata: $(MODEL) $(MCJAR) ${MCG}
	@echo "[INFO]. Generation of tool code from" $(MODEL)
	java -jar $(MCJAR) \
		$(MODEL) \
		-mp $(MCG) \
		-hcp src/main/java \
		-out target
	@touch $@

# Activity 2: compile the tool
target/f.classes: target/f.automata $(SRC) $(MCRT)
	@echo "[INFO]. Compile tool"
	@mkdir -p target/classes/
	javac -cp $(MCRT) \
	      -d target/classes/ \
	      -sourcepath ".;target/;src/main/java/" \
	      src/main/java/automata/AutomataTool.java
	@touch $@

# Activity 3: build the tool jar
target/toolautomata.jar: target/f.classes
	@echo "[INFO]. Create tool jar"
	jar cfe target/toolautomata.jar automata.AutomataTool -C target/classes .
# TODO toolautomatar.jar needs to be a fat jar?
#KLAPPT NICHT# 	--> das entstehende JAR ist wohl nicht geeignet?
#KLAPPT NICHT# 	--> es sollte nachfolgend zum Einsatz kommen (testen und processing)

#############################################################
# Testing

# Activity 4: compile the tests
# (which was on purpose not integrated in tool compilation)
target/f.testclasses: target/toolautomata.jar $(SRC)
	@echo "[INFO]. Compile tests"
	@mkdir -p target/testclasses/
	javac -cp "target/toolautomata.jar;.;$(MCRT);junit-4.13.jar" \
	      -d target/testclasses/ \
	      -sourcepath ".;target/;src/test/java/" \
	      $(wildcard src/test/java/*Test.java)
	@touch $@

# Activity 5: execute all tests
target/f.tests: target/f.testclasses
	@echo "[INFO]. Run tests"
	@java  -cp "target/testclasses;target/toolautomata.jar;.;$(MCRT);hamcrest-core-1.3.jar;junit-4.13.jar" \
	       org.junit.runner.JUnitCore \
	       $(patsubst target/testclasses/%.class,%,$(wildcard target/testclasses/*Test.class))
	@touch $@

#############################################################
# Usage Activities: processing Models:
# Each is handling one automaton:

target/f.executes: target/PingPong.result target/HierarchyPingPong.result target/Simple12.result

target/PingPong.result: src/test/resources/example/PingPong.aut target/f.classes
	@echo "[INFO]. Handling" $<
	java -cp "target/classes;.;$(MCJAR)" \
	     automata.AutomataTool $< target/symtab/

target/HierarchyPingPong.result: src/test/resources/example/HierarchyPingPong.aut target/f.classes
	@echo "[INFO]. Handling" $<
	java -cp "target/classes;.;$(MCJAR)" \
	     automata.AutomataTool $< target/symtab/

target/Simple12.result: src/test/resources/example/Simple12.aut target/f.classes
	@echo "[INFO]. Handling" $<
	java -cp "target/classes;.;$(MCJAR)" \
	     automata.AutomataTool $< target/symtab/



#
# next steps:
# generate something with the tool



#############################################################
# Auxiliary

# get the MontiCore lwb + test rte
$(MCJAR):
	cp ~/.m2/repository/./junit/junit/4.13/junit-4.13.jar .
	cp ~/.m2/repository/./org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar .
	cp ~/monticore/./monticore-cli/target/monticore-cli-*-with-dependencies.jar $@

# get the MontiCore rte
$(MCRT):
	cp ~/monticore/./monticore-grammar/target/monticore-grammar-*-with-dependencies.jar $@

# get the MontiCore grammars
$(MCG):
	cp ~/monticore/./monticore-grammar/target/monticore-grammar-*-grammars.jar $@

target:
	mkdir -p target

clean:
	rm -rf target
	rm -rf $(MCJAR)
	# NUR vorübergehend aus der Not heraus:
	rm -rf de
	rm hamcrest-core-1.3.jar junit-4.13.jar

.PHONY: clean

