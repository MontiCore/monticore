<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("prodname", "grammarname", "package")}
this.ret = new boolean[]{false};
this.traverser = ${package}tagdefinition.${grammarname}TagDefinitionMill.traverser();
this.traverser.add4${grammarname}TagDefinition(new ${package}tagdefinition._visitor.${grammarname}TagDefinitionVisitor2() {
    @Override
    public void visit(${package}tagdefinition._ast.AST${prodname}Identifier node) {
        if (node.get${prodname}().deepEquals(element)) {
            ret[0] = true;
        }
    }
});
