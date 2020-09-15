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
all: target/testclasses
	@echo "[INFO]. Done with all."

# Activity 1: run MontiCore generator
target/automata: $(MODEL) $(MCJAR) 
	@echo "[INFO]. Generation of tool code from" $(MODEL)
	java -jar $(MCJAR) \
		$(MODEL) \
		-mp $(MCJAR) \
		-hcp src/main/java \
		-out target

# Activity 2: compile the tool
target/classes: target/automata $(SRC)
	@echo "[INFO]. Compile tool"
	# NUR vorübergehend aus der Not heraus:
	mkdir -p target/de/monticore/parser
	cp ~/monticore/./monticore-runtime/src/main/java/de/monticore/parser/Placeholder.java \
		target/de/monticore/parser/
	mkdir -p target/de/monticore/symboltable/serialization/
	cp ~/monticore/./monticore-runtime/src/main/java/de/monticore/symboltable/serialization/JsonDeSers.java \
		target/de/monticore/symboltable/serialization
	# Ende der Not 
	javac -cp $(MCJAR) \
	      -d target/classes/ \
	      -sourcepath ".$(SEP)target/$(SEP)src/main/java/" \
	      src/main/java/automata/AutomataTool.java

#KLAPPT NICHT# # Activity 3: build the tool jar
#KLAPPT NICHT# target/toolautomata.jar: target/classes
#KLAPPT NICHT# 	@echo "[INFO]. Create tool jar"
#KLAPPT NICHT# 	@jar --create \
#KLAPPT NICHT# 	     --file $@ \
#KLAPPT NICHT# 	     --main-class=src.main.java.automata.AutomataTool \
#KLAPPT NICHT# 	     $(wildcard target/classes/automata/**/*.class)

# Activity 4: compile the tests
# (which was on purpose not integrated in tool compilation)
target/testclasses: target/classes $(SRC)
	@echo "[INFO]. Compile tests"
	javac -cp ".$(SEP)$(MCJAR)$(SEP)hamcrest-core-1.3.jar$(SEP)junit-4.13.jar$(SEP)target/classes" \
	      -d target/testclasses/ \
	      -sourcepath ".$(SEP)target/$(SEP)src/test/java/" \
	      $(wildcard src/test/java/*Test.java)

# next steps:
# execute ToolTest
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

# Handle windows and unix separators
ifeq ($(OS),Windows_NT)
        SEP =;
else
        SEP =:
endif

