# (c) https://github.com/MontiCore/monticore

#
# This makefile shows how to embedd a MontiCore 
# code generation and a tool execution in a 
# makefile, such that efficient incremental generation
# becomes possible
#
# Incremental == makefile checks whether to regenerate
# (with some help by MontiCore)
# 

# For demonstration reasons, we use two 
# sub-projects with individual sources
# (but both are merged in the same project structure)

#########
# automata project
# tooljava1 carries all handwritten Java sources
# that will be added to the generated tool.
# Some of those are used in the TOP mechanism
tooljava1=$(shell find src/main/java/automata/ -name "*.java" -type f)

TODO: complete this makefile
SEE also RefMan chapter 16: dort wird ein Teil davon erbracht

#############################################
# ab hier das unbearbeitete Original:
# (aus dem svn projekt:
# https://sselab.de/lab9/private/svn/IncGen/SkriptReport/makefile
#
#
src1files=$(shell find src1 -name "*.java" -type f)
hwc1files=$(shell find hwc1 -name "*.java" -type f)
src2files=$(shell find src2 -name "*.java" -type f)
hwc2files=$(shell find hwc2 -name "*.java" -type f)

igf1Check=gen1/reports/IncGenCheck.sh
igf2Check=gen2/reports/IncGenCheck.sh

.PHONY: clean force

all: Result.txt 

.igf1: force
	@[ -e .igf1 ] || touch .igf1
	@[ -e $(igf1Check) ] && sh $(igf1Check) .igf1 || true
  
.igf2: force
	@[ -e .igf2 ] || touch .igf2
	@[ -e $(igf2Check) ] && sh $(igf2Check) .igf2 || true

gen1: grammars/Automaton.mc4 .igf1
	java -jar monticore-cli.jar grammars/Automaton.mc4 -out $@ -hcp hwc1 -f

automaton-tool.jar: $(src1files) $(hwc1files) gen1
	rm -rf build1
	mkdir -p build1
	javac -cp monticore-cli.jar -d build1 \
		-sourcepath "src1;gen1;hwc1" src1/automaton/AutomatonTool.java
	jar -cf $@ -C build1 .

gen2: grammars/Trafo.mc4 .igf2
	java -jar monticore-cli.jar grammars/Trafo.mc4 -out $@ -hcp hwc2 -f

trafo-tool.jar: $(src2files) $(hwc2files) gen2 automaton-tool.jar
	rm -rf build2
	mkdir -p build2
	javac -cp "monticore-cli.jar;automaton-tool.jar" -d build2 -sourcepath "src2;gen2;hwc2" src2/trafo/TrafoTool.java
	jar -cf $@ -C build2 .
  
PingPongTF.aut: trafo-tool.jar automaton-tool.jar models/PingPong.aut models/RenameState.tf 
	java -cp "trafo-tool.jar;automaton-tool.jar;monticore-cli.jar" trafo.TrafoTool models/PingPong.aut models/RenameState.tf $@

Result.txt: automaton-tool.jar PingPongTF.aut
	java -cp "automaton-tool.jar;monticore-cli.jar" automaton.AutomatonTool PingPongTF.aut > $@

clean:
	rm -rf .igf1 gen1 build1 automaton-tool.jar 
	rm -rf .igf2 gen2 build2 trafo-tool.jar
	rm -f PingPongTF.aut Result.txt
	rm -f *.log
