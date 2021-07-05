/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cd4codebasis._ast.ASTCDThrowsDeclaration;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/******************************************************************************
 * AST specific helper to print AST nodes.
 *****************************************************************************/
public class CdUtilsPrinter {

  public static final String EMPTY_STRING = "";

  /**
   * Print the string of a ASTModifier type, e.g. abstract private final static
   *
   * @param modifier the ASTModifier object
   * @return a string, e.g. abstract private final static
   */
  public String printModifier(ASTModifier modifier) {
    CD4CodeFullPrettyPrinter printer = new CD4CodeFullPrettyPrinter(new IndentPrinter());
    return printer.prettyprint(modifier);
  }

  public String printSimpleModifier(ASTModifier modifier) {
    StringBuilder modifierStr = new StringBuilder();
    if (modifier.isAbstract()) {
      modifierStr.append(" abstract ");
    }
    if (modifier.isPublic()) {
      modifierStr.append(" public ");
    } else if (modifier.isPrivate()) {
      modifierStr.append(" private ");
    } else if (modifier.isProtected()) {
      modifierStr.append(" protected ");
    }
    if (modifier.isFinal()) {
      modifierStr.append(" final ");
    }
    if (modifier.isStatic()) {
      modifierStr.append(" static ");
    }

    return modifierStr.toString();
  }

  /**
   * Converts a list of import statements to a string list.
   *
   * @param importStatements the list of import statements
   * @return a string list of all import statements
   */
  public Collection<String> printImportList(
          List<ASTMCImportStatement> importStatements) {

    return Collections2.transform(importStatements,
            new Function<ASTMCImportStatement, String>() {

              @Override
              public String apply(ASTMCImportStatement arg0) {
                return arg0.getQName();
              }

            });
  }

  /**
   * Converts a list of enum constants to a string list of enum constants
   *
   * @param enumConstants list of enum constants
   * @return a string list of enum constants
   */
  public String printEnumConstants(List<ASTCDEnumConstant> enumConstants) {

    checkNotNull(enumConstants);

    return Joiner.on(",").join(

            Collections2.transform(enumConstants,
                    new Function<ASTCDEnumConstant, String>() {

                      @Override
                      public String apply(ASTCDEnumConstant arg0) {
                        return arg0.getName();
                      }

                    }

            ));
  }

  /**
   * Prints an ASTType
   *
   * @param type an ASTType
   * @return String representation of the ASTType
   */
  public String printType(ASTMCType type) {
    return new CD4CodeFullPrettyPrinter().prettyprint(type);
  }

  public String printType(ASTMCReturnType type) {
    return new CD4CodeFullPrettyPrinter().prettyprint(type); //TODO CollectionTypesPrinter
  }

  /**
   * Prints the parameter declarations that can be used in methods,
   * constructors, etc.
   *
   * @param parameterList a list of all parameters
   * @return a string list of parameter declarations, e.g. type name
   */
  public String printCDParametersDecl(List<ASTCDParameter> parameterList) {
    checkNotNull(parameterList);

    return Joiner.on(",").join(
            Collections2.transform(parameterList,
                    new Function<ASTCDParameter, String>() {

                      @Override
                      public String apply(ASTCDParameter arg0) {
                        return printType(arg0.getMCType()) + " " + arg0.getName();
                      }

                    }));
  }

  /**
   * Prints the throws declaration for methods, constructors, etc.
   *
   * @param throwsDecl a list of all qualified exceptions
   * @return a string list of all exceptions
   */
  public String printThrowsDecl(ASTCDThrowsDeclaration throwsDecl) {
    StringBuilder str = new StringBuilder();

    str.append("throws ");

    return str.append(
            Joiner.on(",").join(
                    Collections2.transform(throwsDecl.getExceptionList(),
                            new Function<ASTMCQualifiedName, String>() {

                              @Override
                              public String apply(ASTMCQualifiedName arg0) {
                                return Joiner.on(".").join(arg0.getPartsList());
                              }

                            }))).toString();
  }

  /**
   * Prints a list of extends declarations.
   *
   * @param extendsList a list of extends declarations
   * @return a string list of all extends declarations
   */
  public String printObjectTypeList(List<ASTMCObjectType> extendsList) {
    checkNotNull(extendsList);

    return Joiner.on(",").join(
            Collections2.transform(extendsList,
                    new Function<ASTMCObjectType, String>() {

                      @Override
                      public String apply(ASTMCObjectType arg0) {
                        return printType(arg0);
                      }
                    }));
  }

}
