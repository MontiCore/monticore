package de.monticore.codegen.cd2java;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public final class DecoratorTestUtil {

  private DecoratorTestUtil() {}

  public static ASTCDClass getClassBy(String name, ASTCDCompilationUnit ast) {
    List<ASTCDClass> filteredMethods = ast.getCDDefinition().getCDClassList().stream()
        .filter(c -> name.equals(c.getName()))
        .collect(Collectors.toList());
    assertEquals(String.format("Expected find 1 method, but found '%s'", filteredMethods.size()), 1, filteredMethods.size());
    return filteredMethods.get(0);
  }

  public static List<ASTCDMethod> getMethodsBy(String name, ASTCDClass clazz) {
    return getMethodsBy(name, clazz.getCDMethodList());
  }

  public static List<ASTCDMethod> getMethodsBy(String name, int parameterSize, ASTCDClass clazz) {
    return getMethodsBy(name, parameterSize, clazz.getCDMethodList());
  }

  public static ASTCDMethod getMethodBy(String name, ASTCDClass clazz) {
    return getMethodBy(name, clazz.getCDMethodList());
  }

  public static ASTCDMethod getMethodBy(String name, int parameterSize, ASTCDClass clazz) {
    return getMethodBy(name, parameterSize, clazz.getCDMethodList());
  }

  public static List<ASTCDMethod> getMethodsBy(String name, List<ASTCDMethod> methods) {
    return filterMethods(methods, Collections.singletonList(
        m -> name.equals(m.getName())));
  }

  public static List<ASTCDMethod> getMethodsBy(String name, int parameterSize, List<ASTCDMethod> methods) {
    return filterMethods(methods, Arrays.asList(
        m -> name.equals(m.getName()),
        m -> parameterSize == m.getCDParameterList().size()));
  }

  public static ASTCDMethod getMethodBy(String name, List<ASTCDMethod> methods) {
    return filterMethodsOrFail(methods, Collections.singletonList(
        m -> name.equals(m.getName())));
  }

  public static ASTCDMethod getMethodBy(String name, int parameterSize, List<ASTCDMethod> methods) {
    return filterMethodsOrFail(methods, Arrays.asList(
        m -> name.equals(m.getName()),
        m -> parameterSize == m.getCDParameterList().size()));
  }

  private static ASTCDMethod filterMethodsOrFail(List<ASTCDMethod> methods, List<Predicate<ASTCDMethod>> predicates) {
    List<ASTCDMethod> filteredMethods = filterMethods(methods, predicates);
    assertEquals(String.format("Expected find 1 method, but found '%s'", filteredMethods.size()), 1, filteredMethods.size());
    return filteredMethods.get(0);
  }

  private static List<ASTCDMethod> filterMethods(List<ASTCDMethod> methods, List<Predicate<ASTCDMethod>> predicates) {
    Predicate<ASTCDMethod> composedPredicate = predicates.stream()
        .reduce(m -> true, Predicate::and);
    return methods.stream()
        .filter(composedPredicate)
        .collect(Collectors.toList());
  }

  public static ASTCDAttribute getAttributeBy(String name, ASTCDClass clazz) {
    List<ASTCDAttribute> filterAttributes = clazz.getCDAttributeList().stream()
        .filter(attribute -> name.equals(attribute.getName()))
        .collect(Collectors.toList());
    assertEquals(String.format("Expected find 1 method, but found '%s'", filterAttributes.size()), 1, filterAttributes.size());
    return filterAttributes.get(0);
  }
}
