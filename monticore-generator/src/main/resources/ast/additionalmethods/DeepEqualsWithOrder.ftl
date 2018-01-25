<#--
****************************************************************************
MontiCore Language Workbench, www.monticore.de
Copyright (c) 2017, MontiCore, All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

This software is provided by the copyright holders and contributors
"as is" and any express or implied warranties, including, but not limited
to, the implied warranties of merchantability and fitness for a particular
purpose are disclaimed. In no event shall the copyright holder or
contributors be liable for any direct, indirect, incidental, special,
exemplary, or consequential damages (including, but not limited to,
procurement of substitute goods or services; loss of use, data, or
profits; or business interruption) however caused and on any theory of
liability, whether in contract, strict liability, or tort (including
negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
****************************************************************************
-->
${tc.signature("ast","astType")}
   <#assign genHelper = glex.getGlobalVar("astHelper")>
   <#assign astName = genHelper.getPlainName(astType)>
   <#if astType.getCDAttributeList()?size == 0>
    return o instanceof ${astName};
   <#else>
      ${astName} comp;
    if ((o instanceof ${astName})) {
      comp = (${astName}) o;
    } else {
      return false;
    }
    if (!equalAttributes(comp)) {
      return false;
    }
    <#-- TODO: attributes of super class - use symbol table -->
    <#list astType.getCDAttributeList()  as attribute>  
       <#assign attrName = genHelper.getJavaConformName(attribute.getName())>
       <#if genHelper.isOptionalAstNode(attribute)>
    // comparing ${attrName}   
    if ( this.${attrName}.isPresent() != comp.${attrName}.isPresent() ||
      (this.${attrName}.isPresent() && !this.${attrName}.get().deepEquals(comp.${attrName}.get())) ) {
      return false;
    }
       <#elseif genHelper.isAstNode(attribute)>
    // comparing ${attrName}
    if ( (this.${attrName} == null && comp.${attrName} != null) || 
      (this.${attrName} != null && !this.${attrName}.deepEquals(comp.${attrName})) ) {
      return false;
    }
       <#elseif genHelper.isListAstNode(attribute)>
    // comparing ${attrName}
    if (this.${attrName}.size() != comp.${attrName}.size()) {
      return false;
    } else {
      <#assign astChildTypeName = genHelper.getAstClassNameForASTLists(attribute)>
      if (forceSameOrder) {
        Iterator<${astChildTypeName}> it1 = this.${attrName}.iterator();
        Iterator<${astChildTypeName}> it2 = comp.${attrName}.iterator();
        while (it1.hasNext() && it2.hasNext()) {
          if (!it1.next().deepEquals(it2.next())) {
            return false;
          }
        }
      } else {
        java.util.Iterator<${astChildTypeName}> it1 = this.${attrName}.iterator();
        while (it1.hasNext()) {
          ${astChildTypeName} oneNext = it1.next();
          boolean matchFound = false;
          java.util.Iterator<${astChildTypeName}> it2 = comp.${attrName}.iterator();
          while (it2.hasNext()) {
            if (oneNext.deepEquals(it2.next())) {
              matchFound = true;
              break;
            }
          }
          if (!matchFound) {
            return false;
          }
        }
      }
    }
       </#if>
     </#list>
    return true;     
   </#if>

