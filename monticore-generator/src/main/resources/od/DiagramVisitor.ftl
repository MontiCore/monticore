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
${tc.signature("astType", "astPackage", "cd")}
<#assign genHelper = glex.getGlobalVar("odHelper")>

<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getPackageName()}._od;

import ${genHelper.getVisitorPackage()}.${genHelper.getCdName()}Visitor;
import ${genHelper.getPackageName()}._ast.${genHelper.getASTNodeBaseType()};
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import java.util.Iterator;

public class ${genHelper.getCdName()}2OD implements ${genHelper.getCdName()}Visitor {
      
  private ${genHelper.getCdName()}Visitor realThis = this;
  
  private IndentPrinter pp;
  
  private ReportingRepository reporting;
    
  public ${genHelper.getCdName()}2OD(IndentPrinter printer, ReportingRepository reporting) {
    this.reporting = reporting;
    this.pp = printer;
  }
    
  <#list cd.getTypes() as type>
    <#if type.isClass()>
      <#assign astName = genHelper.getJavaASTName(type)>
  
      @Override
      public void handle(${astName} node) {
        String name = StringTransformations.uncapitalize(reporting.getASTNodeNameFormatted(node));
        printObject(name, "${astName}");
        pp.indent();
        if (node.getSymbol().isPresent() && !node.getSymbol().get().getName().isEmpty()) {
          String symName = StringTransformations.uncapitalize(reporting.getSymbolNameFormatted(node.getSymbol().get()));
          pp.println("symbol = " + symName + ";");
        }
        <#list type.getAllVisibleFields() as field>
 
          <#if genHelper.isAstNode(field) || genHelper.isOptionalAstNode(field) >
            <#assign attrGetter = genHelper.getPlainGetter(field)>
            <#if genHelper.isOptional(field)>
              if (node.${attrGetter}().isPresent()) {
     			pp.print("${field.getName()}");
   			    pp.print(" = ");
                node.${attrGetter}().get().accept(getRealThis());
                pp.println(";");
              }
            <#else>
              if (null != node.${attrGetter}()) {          
      			pp.print("${field.getName()}");
   			    pp.print(" = ");
                node.${attrGetter}().accept(getRealThis());
                pp.println(";");
              }
            </#if>
          <#elseif genHelper.isListAstNode(field)>
            <#assign attrGetter = genHelper.getPlainGetter(field)>
            <#assign astChildTypeName = genHelper.getAstClassNameForASTLists(field)>
            {
              Iterator<${astChildTypeName}> iter_${field.getName()} = node.${attrGetter}().iterator();
              boolean isEmpty = true;
              if (iter_${field.getName()}.hasNext()) {
       			pp.print("${field.getName()}");
   			    pp.println(" = ");
   			    pp.indent();
   			    pp.println("// *size: " + node.${attrGetter}().size());
   			    isEmpty = false;
              }
              boolean isFirst = true;
              while (iter_${field.getName()}.hasNext()) {
                if (!isFirst) {
                  pp.println(",");
                }
                isFirst = false;
                iter_${field.getName()}.next().accept(getRealThis());
              }
              if (!isEmpty) {
              	pp.println(";");
              	pp.unindent();
              }
             
            }
          <#elseif genHelper.isOptional(field)>
            if (node.${genHelper.getPlainGetter(field)}().isPresent()) {
              printAttribute("${field.getName()}", "\"" + String.valueOf(node.${genHelper.getPlainGetter(field)}().get()) + "\"");
            }
          <#elseif genHelper.isListType(field.getType())>
            {
              String sep = "";
              <#if genHelper.isListOfString(field)>
                String str = "\"";
              <#else>
                String str = "";
              </#if>
              Iterator<?> it = node.${genHelper.getPlainGetter(field)}().iterator();
              boolean isEmpty = true;
              if (it.hasNext()) {
                pp.print("${field.getName()}" + " = [");
                isEmpty = false;
              }
              while (it.hasNext()) {
                pp.print(sep); 
                pp.print(str + String.valueOf(it.next()) + str);
                sep = ", ";
              }
              if (!isEmpty) {
              	pp.println("];");
              }
            }
          <#else>
            <#assign fieldType = field.getType()>
            <#if genHelper.isString(fieldType.getName())>
              printAttribute("${field.getName()}", "\"" + String.valueOf(node.${genHelper.getPlainGetter(field)}()) + "\"");
            <#else>
              printAttribute("${field.getName()}", String.valueOf(node.${genHelper.getPlainGetter(field)}()));
            </#if>
          </#if>
        </#list>
        pp.unindent();
        pp.print("}");
     } 

    </#if>
  </#list>  
  
  private void printAttribute(String name, String value) {
    pp.print(name);
    pp.print(" = ");
    pp.print(value);
    pp.println(";");
  }
  
  private void printObject(String objName, String objType) {
    pp.print(objName);
    pp.print(":");
    pp.print(Names.getSimpleName(objType));
    pp.println(" {");
  }
  
  public String printObjectDiagram(String modelName, ${genHelper.getASTNodeBaseType()} node) {
    pp.clearBuffer();
    pp.setIndentLength(2);
    pp.print("objectdiagram ");
    pp.print(modelName);
    pp.println(" {");
    pp.indent();
    node.accept(getRealThis());
    pp.unindent();
    pp.println("}");
    return pp.getContent();
  }
  
  @Override
  public void setRealThis(${genHelper.getCdName()}Visitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public ${genHelper.getCdName()}Visitor getRealThis() {
    return realThis;
  }
  
}
  