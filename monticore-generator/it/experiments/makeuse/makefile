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
all: target/mc2
	@echo "[INFO]. Done with all."

target/automata: $(MODEL) $(MCJAR) 
	@echo "[INFO]. Generation of tool code from" $(MODEL)
	java -jar $(MCJAR) \
		$(MODEL) \
		-mp $(MCJAR) \
		-hcp src/main/java \
		-out target
	@touch $@

SRCFILES=$(wildcard src/main/java/**/*.java)

target/mc2: target/automata $(SRC)
	@echo "[INFO]. Compile tool"
	# NUR vorübergehend aus der Not heraus:
	mkdir -p target/de/monticore/parser
	cp ~/monticore/./monticore-runtime/src/main/java/de/monticore/parser/Placeholder.java \
		target/de/monticore/parser/
	# Ende der Not 
	javac -cp $(MCJAR) \
	      -sourcepath ".$(SEP)target/$(SEP)src/main/java/" \
	      src/main/java/automata/AutomataTool.java




#############################################################
# Auxiliary

# get the MontiCore lwb + rte
$(MCJAR):
	cp ~/monticore/./monticore-cli/target/monticore-cli-*-with-dependencies.jar $@

target:
	mkdir -p target

clean:
	rm -rf target
	rm -rf $(MCJAR)

.PHONY: clean 

# Handle windows and unix separators
ifeq ($(OS),Windows_NT)
        SEP =;
else
        SEP =:
endif

