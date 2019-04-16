<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainClass")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
    value = new ${domainClass.getName()}();
<#list domainClass.getCDAttributeList() as attribute>
  <#assign methName = genHelper.getNativeAttributeName(attribute.getName())?cap_first>
  <#if genHelper.isListType(attribute.printType())>
    value.set${methName}List(this.${attribute.getName()});
  <#elseif genHelper.isOptional(attribute.getType())>
    value.set${methName}Opt(this.${attribute.getName()});
  <#else>
    value.set${methName}(this.${attribute.getName()});
  </#if>
</#list>
    value.set_SourcePositionEndOpt(this.sourcePositionEnd);
    value.set_SourcePositionStartOpt(this.sourcePositionStart);
    value.setEnclosingScopeOpt(this.enclosingScope);
    value.setSymbolOpt(this.symbol);
    value.setSpannedScopeOpt(this.spannedScope);
    value.set_PreCommentList(this.precomments);
    value.set_PostCommentList(this.postcomments);
