<#--
***************************************************************************************
Copyright (c) 2015, MontiCore
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software
without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
***************************************************************************************
-->
${tc.signature("ast","astType")}
   <#assign genHelper = glex.getGlobalValue("astHelper")>
   <#assign astName = genHelper.getPlainName(astType)>
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
    <#-- list astType.getCDAttributes()  as attribute> 
      <#assign attributeName = genHelper.getJavaConformName(attribute.getName())>
      <#if !genHelper.isAstNode(attribute)>
	// comparing ${attributeName} 
	    <#if genHelper.isPrimitive(attribute.getType())>
	if (!(this.${attributeName} == comp.${attributeName})) {
	  return false;
	}
	    <#else>
	if ( (this.${attributeName} == null && comp.${attributeName} != null) 
	  || (this.${attributeName} != null && !this.${attributeName}.equals(comp.${attributeName})) ) {
	  return false;
	}
	    </#if>
	  </#if>  
    </#list --> 
    // comparing comments
    if (get_PreComments().size() == comp.get_PreComments().size()) {
      java.util.Iterator<de.monticore.ast.Comment> one = get_PreComments().iterator();
      java.util.Iterator<de.monticore.ast.Comment> two = comp.get_PreComments().iterator();
      while (one.hasNext()) {
        if (!one.next().equals(two.next())) {
          return false;
        }
      }
    } else {
      return false;
    }
    
    if (get_PostComments().size() == comp.get_PostComments().size()) {
      java.util.Iterator<de.monticore.ast.Comment> one = get_PostComments().iterator();
      java.util.Iterator<de.monticore.ast.Comment> two = comp.get_PostComments().iterator();
      while (one.hasNext()) {
        if (!one.next().equals(two.next())) {
          return false;
        }
      }
    } else {
      return false;
    }
    return true;
