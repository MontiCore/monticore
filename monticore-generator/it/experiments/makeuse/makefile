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
# The makefile handles two levels of generation and processing 
# Level
#   M2: MontiCore LWB generates Automata tool-code
#        & compiles & jars
#   M1: The resulting Automata tool is executed 
#   	on real automata

#############################################################
# M2 Configuration (files and used commands):

# M2-model to be processed (the grammar)
MODEL=src/main/grammars/Automata.mc4

# M2-Paths to handcoded part used for the TOP mechanism
HWC=src/main/java

# M2-input: All handwritten sources for the tool
# this is needed on M2 level to apply the TOP mechanism
SRC=$(wildcard src/**/*java)

# jar for the MontiCore call (M2), runtime (M1+M2), and grammars (M2)
MCJAR=monticore-all.jar
MCRTE=monticore-rt.jar
MCGRAMMARS=monticore-g.jar

#############################################################
# M2 Rules:

# initial rule depending on all relevant sub-activities
all: target/f.executes target/f.tests
	@echo "[INFO]. Done with all."

#############################################################
# Generate and compile:

# Activity 1 (M2): run MontiCore generator
target/f.automata: $(MODEL) $(MCJAR) $(MCGRAMMARS)
	@echo "[INFO]. Generation of tool code from" $(MODEL)
	java -jar $(MCJAR) \
		$(MODEL) \
		-mp $(MCGRAMMARS) \
		-hcp src/main/java \
		-out target
	@touch $@

# Activity 2 (M2): compile the tool
target/f.classes: target/f.automata $(SRC) $(MCRTE)
	@echo "[INFO]. Compile tool"
	@mkdir -p target/classes/
	javac -cp $(MCRTE) \
	      -d target/classes/ \
	      -sourcepath ".;target/;src/main/java/" \
	      src/main/java/automata/AutomataTool.java
	@touch $@

# Activity 3 (M2): build the tool jar
target/toolautomata.jar: target/f.classes
	@echo "[INFO]. Create tool jar"
	jar cfe target/toolautomata.jar automata.AutomataTool -C target/classes .
# TODO toolautomatar.jar needs to be a fat jar?
# BR: Könnte möglich sein, @KH: bitte erstellen; oder so erstellen, dass es gemeinsam mit 
# dem MCRTE einsetzbar ist.
# (und nicht mehr die   direkt angegeben werden müssen)

#############################################################
# Testing

# Activity 4 (M2): compile the tests
# (which was on purpose not integrated in tool compilation)
target/f.testclasses: target/toolautomata.jar $(SRC)
	@echo "[INFO]. Compile tests"
	@mkdir -p target/testclasses/
	javac -cp "target/toolautomata.jar;.;$(MCRTE);junit-4.13.jar" \
	      -d target/testclasses/ \
	      -sourcepath ".;target/;src/test/java/" \
	      $(wildcard src/test/java/*Test.java)
	@touch $@

# Activity 5 (M1): execute all tests
target/f.tests: target/f.testclasses
	@echo "[INFO]. Run tests"
	@java  -cp "target/testclasses;target/toolautomata.jar;.;$(MCRTE);hamcrest-core-1.3.jar;junit-4.13.jar" \
	       org.junit.runner.JUnitCore \
	       $(patsubst target/testclasses/%.class,%,$(wildcard target/testclasses/*Test.class))
	@touch $@

#############################################################
# Usage Activities (M1): processing Models:
# Each is handling one automaton: (producing one file)
#
# Because in these executions there is no additional (optional) input
# needed, we do not need to use an IncGenCheck here.

target/f.executes: target/PingPong.result target/HierarchyPingPong.result target/Simple12.result

target/PingPong.result: src/test/resources/example/PingPong.aut target/f.classes
	@echo "[INFO]. Handling" $<
	java -cp "target/classes;.;$(MCJAR)" \
	     automata.AutomataTool $< target/symtab/ > $@

target/HierarchyPingPong.result: src/test/resources/example/HierarchyPingPong.aut target/f.classes
	@echo "[INFO]. Handling" $<
	java -cp "target/classes;.;$(MCJAR)" \
	     automata.AutomataTool $< target/symtab/ > $@

target/Simple12.result: src/test/resources/example/Simple12.aut target/f.classes
	@echo "[INFO]. Handling" $<
	java -cp "target/classes;.;$(MCJAR)" \
	     automata.AutomataTool $< target/symtab/ > $@


#############################################################
# Auxiliary

# get the MontiCore lwb + test rte
$(MCJAR):
	cp ~/.m2/repository/./junit/junit/4.13/junit-4.13.jar .
	cp ~/.m2/repository/./org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar .
	cp ~/monticore/./monticore-cli/target/monticore-cli-*-with-dependencies.jar $@

# XXX BR: junit, etc. direkt nutzen

# get the MontiCore rte
$(MCRTE):
	# TODO: altre kruecke ersetzen 
	cp ~/monticore/./monticore-cli/target/monticore-cli-*-with-dependencies.jar $@
	#TODO: bei mir nicht existent
	#	cp ~/monticore/./monticore-grammar/target/monticore-grammar-*-with-dependencies.jar $@

# get the MontiCore grammars
$(MCGRAMMARS):
	cp ~/monticore/./monticore-grammar/target/monticore-grammar-*-grammars.jar $@

target:
	mkdir -p target

clean:
	rm -rf target
	rm hamcrest-core-1.3.jar junit-4.13.jar 
	rm -rf $(MCGRAMMARS) $(MCRTE) $(MCJAR)

.PHONY: clean

