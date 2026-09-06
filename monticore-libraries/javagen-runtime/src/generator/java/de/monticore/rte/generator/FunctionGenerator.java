/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.generator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDExtendUsage;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgument;
import de.monticore.types.typeparameters._ast.ASTTypeParameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FunctionGenerator extends InterfaceRteGenerator {

  public FunctionGenerator(File outputDirectory) {
    super(outputDirectory);
  }

  protected String getTypeName(int n) {
    return "Arg" + n;
  }

  protected String getParameterName(int n) {
    return "arg" + n;
  }

  protected String getClassName(int n) {
    return "Function" + n;
  }

  @Override
  protected List<ASTCDMethod> constructMethods(int n) {
    if (n == 1 || n == 2)
      return List.of();
    ASTCDMethod method = buildMethod("apply", CDModifier.PUBLIC_ABSTRACT.build(), "R", IntStream.range(0, n).mapToObj(i -> buildParameter(getParameterName(i), getTypeName(i))).toArray(ASTCDParameter[]::new)).build();
    if (n == 0) {
      ASTCDMethod acceptMethod = completeMethod(buildMethod("call", CDModifier.PUBLIC.build(), "R").setIsDefault(true), "de.monticore.rte.functions.FunctionApply", n);
      ASTCDMethod getMethod = completeMethod(buildMethod("get", CDModifier.PUBLIC.build(), "R").setIsDefault(true), "de.monticore.rte.functions.FunctionApply", n);
      return List.of(getMethod, acceptMethod, method);
    }
    return List.of(method);
  }

  @Override
  public ASTCDElement constructClass(int n) {
    ASTCDElement clazz = super.constructClass(n);
    setAnnotations(clazz, "@FunctionalInterface");
    return clazz;
  }

  @Override
  public ASTCDExtendUsage constructExtendUsage(int n) {
    List<String> names = null;
    if (n == 0) {
      names = List.of("java.util.concurrent.Callable", "Supplier");
    }
    else if (n == 1) {
      names = List.of("java.util.function.Function");
    }
    else if (n == 2) {
      names = List.of("java.util.function.BiFunction");
    }
    if (names == null) {
      return null;
    }

    List<ASTMCBasicTypeArgument> args = IntStream.range(0, n + 1).mapToObj(i -> CD4CodeMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(MCTypeFacade.getInstance().createQualifiedType(i >= n ? "R" : getTypeName(i))).build()).collect(Collectors.toList());
    List<ASTMCObjectType> superclasses = names.stream().map(e -> CD4CodeMill.mCBasicGenericTypeBuilder().addName(e).addAllMCTypeArguments(args).build()).collect(Collectors.toList());
    return CD4CodeMill.cDExtendUsageBuilder().addAllSuperclass(superclasses).build();
  }

  protected List<ASTTypeParameter> constructTypeParameterList(int n) {
    List<ASTTypeParameter> typeParameters = new ArrayList<>(n + 1);
    typeParameters.add(CD4CodeMill.typeParameterBuilder().setName("R").build());
    for (int i = 0; i < n; i++) {
      typeParameters.add(CD4CodeMill.typeParameterBuilder().setName(getTypeName(i)).build());
    }
    return typeParameters;
  }

  @Override
  protected List<String> buildPackageName() {
    return List.of("de", "monticore", "rte", "functions");
  }
}
