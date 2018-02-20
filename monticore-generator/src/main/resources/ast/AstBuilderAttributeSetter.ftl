<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("ast", "name")}
    <#assign genHelper = glex.getGlobalVar("astHelper")>
    <#assign attributeName = genHelper.getJavaConformName(ast.getName())>
    <#assign typeHelper = tc.instantiate("de.monticore.types.TypesHelper")>
    <#assign plainName = genHelper.getPlainName(ast)>
    public ${name} ${attributeName}(${typeHelper.printSimpleRefType(ast.getType())} ${attributeName}) {
      this.${attributeName} = ${attributeName};
      return this;
    }
    
<#if genHelper.isListAstNode(ast)>
    public ${name} ${attributeName}Add(${genHelper.printTypeArgumentOfAstList(ast.getType())} element) {
      this.${attributeName}.add(element);
      return this;
    }
    
    public ${name} ${attributeName}AddAll(${typeHelper.printSimpleRefType(ast.getType())} ${attributeName}) {
      this.${attributeName}.addAll(${attributeName});
      return this;
    }
</#if>    
