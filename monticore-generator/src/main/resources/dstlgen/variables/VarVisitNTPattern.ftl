<#-- (c) https://github.com/MontiCore/monticore -->
    @Override
    public void visit(AST${ast.getName()}_Pat node) {
    <#if grammarInfo.getStringAttrs(ast.getName())?? >
      ${tc.include(collect_string_attrs, grammarInfo.getStringAttrs(ast.getName()))}
      ${tc.include(collect_stringlist_attrs, grammarInfo.getStringListAttrs(ast.getName()))}
    </#if>
    }
