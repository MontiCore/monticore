<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast","type")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  <#list type.getAllVisibleFields() as field>
    <#assign attrGetter = genHelper.getPlainGetter(field)>
    <#assign attrSetter = genHelper.getPlainSetter(field)>
    <#if genHelper.isAstNode(field)>
      if (${attrGetter}() == child) {
        ${attrSetter}(null);
      }
    <#elseif genHelper.isOptionalAstNode(field)>
      if (${attrGetter}().isPresent() && ${attrGetter}().get() == child) {
        ${attrSetter}(null);
      }
    <#elseif genHelper.isListAstNode(field)>
      if (${attrGetter}().contains(child)) {
        ${attrGetter}().remove(child);
      }
    </#if>
  </#list>
