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

# Handcoded part used for the TOP mechanism
HWC=$(wildcard src/**/*java)
# All sources for the tool (may be more, but includes than HWC)
HWC=$(SRC) 

# commands: MontiCore call, Java compilation and execution
MC=java -jar monticore-all.jar
JAVAC=javac -cp monticore-all.jar
JAVA=java

# model to be processed
MODEL=src/main/grammars/Automata.mc4

#############################################################
# Rules:

# initial rule depending on all sub-activities 
all: monticore-all.jar \
     target/mc1
	@echo "** Done with all."

target/mc1:  $(MODEL) 
	@echo "** Generation of tool code from" $(MODEL)
	$(MC) $(MODEL) \
		-hcp $(HWC) \
		-out target
	@echo "** Generation done."

#############################################################
# Auxiliary

# get the MontiCore lwb + rte
monticore-all.jar:
	cp ~/monticore/./monticore-cli/target/monticore-cli-*-with-dependencies.jar $@

target:
	mkdir -p target

clean:
	rm -rf target
	rm -rf monticore-all.jar

.PHONY: clean 

