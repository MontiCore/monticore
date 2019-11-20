<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("domainClass")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
    value = new ${domainClass.getName()}();
<#list domainClass.getCDAttributeList() as attribute>
  <#assign methName = genHelper.getNativeAttributeName(attribute.getName())?cap_first>
  <#if genHelper.isListType(attribute.printType())>
    value.set${methName?remove_ending("s")}List(this.${attribute.getName()});
  <#elseif genHelper.isOptional(attribute.getMCType())>
    if (this.${attribute.getName()}.isPresent()) {
      value.set${methName}(this.${attribute.getName()}.get());
    } else {
      value.set${methName}Absent();
    }
  <#else>
    value.set${methName}(this.${attribute.getName()});
  </#if>
</#list>
    if (this.sourcePositionEnd.isPresent()) {
      value.set_SourcePositionEnd(this.sourcePositionEnd.get());
    } else {
      value.set_SourcePositionEndAbsent();
    }
    if (this.sourcePositionStart.isPresent()) {
      value.set_SourcePositionStart(this.sourcePositionStart.get());
    } else {
      value.set_SourcePositionStartAbsent();
    }
    value.set_PreCommentList(this.precomments);
    value.set_PostCommentList(this.postcomments);
