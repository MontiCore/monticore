/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.codegen.cd2python.ast;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast_emf.AstEmfGeneratorHelper;
import de.monticore.codegen.cd2python.visitor.PythonVisitorGeneratorHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.io.paths.IterablePath;
import de.monticore.literals.literals._ast.ASTNullLiteral;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.TrustAnchor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.se_rwth.commons.StringTransformations.uncapitalize;

public class AstPythonGeneratorHelper extends AstEmfGeneratorHelper {

    protected static final String AST_BUILDER = "Builder";
    private static final String PYTHON_EXTENSION = ".py";
    public static final String PARAMETER_PREFIX = "_";

    public AstPythonGeneratorHelper(ASTCDCompilationUnit topAst, GlobalScope symbolTable) {
        super(topAst, symbolTable);
    }

    public String getAstAttributeValue(ASTCDAttribute attribute, ASTCDType clazz) {
        return getAstAttributeValue(attribute);
    }

    public String getAstAttributeValue(ASTCDAttribute attribute) {
        if (attribute.getValue().isPresent() && attribute.getValue().get().getSignedLiteral() instanceof ASTNullLiteral){
            return "None";
        }
        if (attribute.getValue().isPresent()) {
            return attribute.printValue();
        }
        if (isOptional(attribute)) {
            return "None";
        }
        String typeName = TypesPrinter.printType(attribute.getType());
        if (isListType(typeName)) {
            return "list()";
        }
        if (isMapType(typeName)) {
            return "dict()";
        }
        return "None";
    }

    public String getAstAttributeValueForBuilder(ASTCDAttribute attribute) {
        if (isOptional(attribute)) {
            return "None";
        }
        return getAstAttributeValue(attribute);
    }

    public String getAstPackage() {
        return getPackageName(getPackageName(), getAstPackageSuffix());
    }

    public static String getAstPackageSuffix() {
        return GeneratorHelper.AST_PACKAGE_SUFFIX;
    }

    public Optional<ASTCDClass> getASTBuilder(ASTCDClass clazz) {
        return getCdDefinition().getCDClasses().stream()
                .filter(c -> c.getName().equals(getNameOfBuilderClass(clazz))).findAny();
    }

    public static String getNameOfBuilderClass(ASTCDClass astClass) {
        return getASTClassNameWithoutPrefix(astClass) + AST_BUILDER;
    }


    /**
     * Returns all super classes and interfaces of the handed over class.
     * @param astcdClass
     * @return
     */
    @SuppressWarnings("unused")//used in the template
    public String getSuperClassesAsString(ASTCDClass astcdClass){
        StringBuilder builder = new StringBuilder();
        if (astcdClass.getSuperclass().isPresent()){
            builder.append(astcdClass.getSuperclass().get().toString());
            builder.append(',');
        }
        builder.append(super.getASTNodeBaseType());
        return builder.toString();
    }

    @SuppressWarnings("unused")//used in the template
    public List<String> getSuperClasses(ASTCDClass astcdClass){
        List<String> ret = new ArrayList<>();
        if (astcdClass.getSuperclass().isPresent()){
            ret.add(astcdClass.getSuperclass().get().toString());
        }
        ret.add(super.getASTNodeBaseType());
        return ret;
    }


    /**
     * Returns a list of all interfaces a single class implements.
     * @param astcdClass a single class object
     * @return a list of implemented interfaces
     */
    public static List<String> getInterfaces(ASTCDClass astcdClass){
        List<String> ret = new ArrayList<>();
        for (ASTReferenceType type:astcdClass.getInterfaces()){
            ret.add(type.toString());
        }
        return ret;
    }

    /**
     * The are no actual modifiers in python but some guidelines. In order to mark something as private,
     * we extend the name by __, e.g., __isPrivate
     * @param astcdAttribute a single attribute object
     * @return the modifier if required
     */
    @SuppressWarnings("unused")//used in the template
    public static String printModifier(ASTCDAttribute astcdAttribute){
        //TODO: there are no real privates or publics, but only conventions. indicate those
        if(astcdAttribute.getModifier().isPresent() && astcdAttribute.getModifier().get().isPrivate()){
            return "";
        }
        else{
            return "";
        }
    }

    /**
     * There are not actual modifiers in python but only some guidelines. In order to mark something as "private"
     * we have to use the prefix __.
     * @param astcdMethod a single method object
     * @return the corresponding prefix
     */
    @SuppressWarnings("unused")//used in the template
    public static String printModifier(ASTCDMethod astcdMethod){
        //Caution: This is just a python convention, not a real private.
        if(astcdMethod.getModifier().isPrivate()){
            return "__";
        }
        else{
            return "";
        }
    }


    /**
     * Indicates whether a handed over class uses abstract methods or not.
     * @param astcdClass a single class
     * @return True if class has abstract methods, otherwise false.
     */
    @SuppressWarnings("unused")//used in the template
    public static boolean classHasAbstracts(ASTCDClass astcdClass){
        for (ASTCDMethod method :astcdClass.getCDMethods()){
            if (method.getModifier().isAbstract()){
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unused")//used in the template
    public static boolean isStaticAttribute(ASTCDAttribute astcdAttribute){
        return astcdAttribute.getModifier().isPresent() && astcdAttribute.getModifier().get().isStatic();
    }

    @SuppressWarnings("unused")//used in the template
    public static String getPythonConformName(String name){
        return PythonNamesHelper.pythonAttribute(name);
    }

    @SuppressWarnings("unused")//used in the template
    public static String printParametersDeclaration(ASTCDMethod astcdMethod){
        StringBuilder builder = new StringBuilder();
        for(ASTCDParameter parameter:astcdMethod.getCDParameters()){
            builder.append(" "+(PARAMETER_PREFIX + parameter.getName()));
            builder.append(("="));
            builder.append(("None"));
            builder.append(",");
        }
        if (astcdMethod.getCDParameters().size() > 0){
            // the last ', ' is not required
            builder.deleteCharAt(builder.length()-1);
        }
        return builder.toString();
    }

    @SuppressWarnings("unused")//used in the template
    public static String printInitParameters(ASTCDClass astcdClass){
        StringBuilder builder = new StringBuilder();
        for (ASTCDAttribute attribute:astcdClass.getCDAttributes()){
            builder.append(printPrefixedNamed(attribute));
            builder.append((String)(" = "));
            builder.append((String)("None"));
            builder.append(", ");
        }
        if (builder.length() > 0){
            // the last ',' is not required
            builder.setLength(builder.length()-2);
        }
        return builder.toString();
    }

    @SuppressWarnings("unused")//used in the template
    public static String printPrefixedNamed(ASTCDAttribute astcdAttribute){
        return PARAMETER_PREFIX + astcdAttribute.getName();
    }

    @SuppressWarnings("unused")//used in the template
    public static String printPrefixedNamed(ASTCDParameter astcdParameter){
        return PARAMETER_PREFIX + astcdParameter.getName();
    }

    @SuppressWarnings("unused")//used in the template
    public static boolean hasBooleanReturn(ASTCDMethod astcdMethod){
        return astcdMethod.getReturnType().getSymbol().isPresent() && astcdMethod.getReturnType().getSymbol().get().getName().equals("boolean");
    }

    @SuppressWarnings("unused")//used in the template
    public static boolean hasParameters(ASTCDMethod astcdMethod){
        return astcdMethod.getCDParameters().size() > 0;
    }

    @SuppressWarnings("unused")//used in the template
    public static boolean isStaticMethod(ASTCDMethod astcdMethod){
        return astcdMethod.getModifier().isStatic();
    }

    @SuppressWarnings("unused")//used in the template
    public static boolean isAbstractMethod(ASTCDMethod astcdMethod){return astcdMethod.getModifier().isAbstract();}

    @SuppressWarnings("unused")//used in the template
    public static boolean isBoolean(ASTCDAttribute astcdAttribute){
        return astcdAttribute.getType() instanceof ASTPrimitiveType &&((ASTPrimitiveType) astcdAttribute.getType()).isBoolean();
    }

    @SuppressWarnings("unused")//used in the template
    public static boolean isAbstract(ASTCDClass astcdClass){
        return astcdClass.getModifier().isPresent() && astcdClass.getModifier().get().isAbstract();
    }

    @SuppressWarnings("unused")//used in the template
    public boolean isListAttribute(ASTCDAttribute astcdAttribute){
        return isListType(TypesPrinter.printType(astcdAttribute.getType()));
    }


    public static String getSimpleTypeNameToGenerate(String simpleName, String packageName,
                                                     IterablePath targetPath) {
        if (existsHandwrittenClass(simpleName, packageName, targetPath)) {
            return simpleName + TransformationHelper.GENERATED_CLASS_SUFFIX;
        }
        return simpleName;
    }

    public static boolean existsHandwrittenClass(String simpleName, String packageName,
                                                 IterablePath targetPath) {
        return existsHandwrittenClass(targetPath,
                getDotPackageName(packageName) + simpleName);
    }

    /**
     * Checks if a handwritten class with the given qualifiedName (dot-separated)
     * exists on the target path
     *
     * @param qualifiedName name of the class to search for
     * @return true if a handwritten class with the qualifiedName exists
     */
    public static boolean existsHandwrittenClass(IterablePath targetPath,
                                                 String qualifiedName) {
        Path handwrittenFile = Paths.get(Names
                .getPathFromPackage(qualifiedName)
                + PYTHON_EXTENSION);
        Log.debug("Checking existence of handwritten class " + qualifiedName
                + " by searching for "
                + handwrittenFile.toString(), TransformationHelper.class.getName());
        boolean result = targetPath.exists(handwrittenFile);
        if (result) {
            Reporting.reportUseHandwrittenCodeFile(targetPath.getResolvedPath(handwrittenFile).get(),
                    handwrittenFile);
        }
        return result;
    }


    /**
     * Returns the name of the start rule of a given grammar.
     * @param grammarSymbol a grammar stored as a grammar object.
     * @return the name of the rule
     */
    String getStartRuleName(MCGrammarSymbol grammarSymbol){
        if (grammarSymbol.getStartProd().isPresent()) {
            return uncapitalize(grammarSymbol.getStartProd().get().getName());
        }
        for (MCGrammarSymbol g: grammarSymbol.getSuperGrammarSymbols()) {
            if (g.getStartProd().isPresent()) {
                return uncapitalize(g.getStartProd().get().getName().toLowerCase());
            }
        }
        return "";
    }

    /**
     * Removes the AST prefix as required to be conform to antlr.
     * @param name the name of a class
     * @return the name without prefix
     */
    public static String getAntlrConformName(String name){
        return uncapitalize(PythonVisitorGeneratorHelper.getAntlrConformName(name));
    }

}





