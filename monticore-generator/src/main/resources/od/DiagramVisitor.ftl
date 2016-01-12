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
<#assign genHelper = glex.getGlobalValue("odHelper")>

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
      public void visit(${astName} node) {
        String name = StringTransformations.uncapitalize(reporting.getASTNodeNameFormatted(node));
        printObject(name, "${astName}");
        pp.indent();
        <#list type.getAllVisibleFields() as field>
          <#if !genHelper.isAstNode(field)>
            <#if genHelper.isOptional(field) && !genHelper.isOptionalAstNode(field)>
              if (node.${genHelper.getPlainGetter(field)}().isPresent()) {
               printAttribute("${field.getName()}", String.valueOf(node.${genHelper.getPlainGetter(field)}().get()));
              }
            <#elseif genHelper.isListType(field.getType())  && !genHelper.isListAstNode(field)>
            {
              String sep = "";
              pp.print("${field.getName()}" + " = {");
              <#if genHelper.isListOfString(field)>
                String str = "\"";
              <#else>
                String str = "";
              </#if>
              Iterator<?> it = node.${genHelper.getPlainGetter(field)}().iterator();
              while (it.hasNext()) {
                pp.print(sep); 
                pp.print(str + String.valueOf(it.next()) + str);
                sep = ", ";
              }
              pp.println("};");
            }
            <#elseif !genHelper.isListType(field.getType()) && !genHelper.isOptional(field)>
              <#assign fieldType = field.getType()>
              <#if genHelper.isString(fieldType.getName())>
                printAttribute("${field.getName()}", "\"" + String.valueOf(node.${genHelper.getPlainGetter(field)}()) + "\"");
              <#else>
                printAttribute("${field.getName()}", String.valueOf(node.${genHelper.getPlainGetter(field)}()));
              </#if>
            </#if>
          </#if> 
        </#list>
      }
      
      @Override
      public void endVisit(${astName} node) {
        pp.unindent();
        pp.println("}");
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
    pp.print(": ");
    pp.print(Names.getSimpleName(objType));
    pp.println(" {");
  }
  
  public String printObjectDiagram(String modelName, ${genHelper.getASTNodeBaseType()} node) {
    pp.clearBuffer();
    pp.setIndentLength(2);
    pp.print("astobjectdiagram ");
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
  

