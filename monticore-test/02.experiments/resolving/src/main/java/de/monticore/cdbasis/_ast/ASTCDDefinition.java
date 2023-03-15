/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._ast;

import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis.MCQualifiedNameFacade;
import de.monticore.cdbasis._visitor.CDBasisTraverser;
import de.monticore.cdbasis._visitor.CDElementVisitor;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ASTCDDefinition extends ASTCDDefinitionTOP {

  protected Optional<ASTCDPackage> defaultPackage = Optional.empty();
  protected String defaultPackageName = "";

  public <T extends ASTCDElement> List<T> getCDElementListBy(CDElementVisitor.Options... options) {
    final CDElementVisitor cdElementVisitor = new CDElementVisitor(options);
    CDBasisTraverser t = CDBasisMill.traverser();
    t.add4CDBasis(cdElementVisitor);
    this.accept(t);
    return cdElementVisitor.getElements();
  }

  public <T extends ASTCDElement> Iterator<T> iterateCDElementsBy(
    CDElementVisitor.Options... options) {
    return this.<T>getCDElementListBy(options).iterator();
  }

  public <T extends ASTCDElement> Stream<T> streamCDElementsBy(
    CDElementVisitor.Options... options) {
    return this.<T>getCDElementListBy(options).stream();
  }

  public int sizeCDElementsBy(CDElementVisitor.Options... options) {
    return getCDElementListBy(options).size();
  }

  public List<ASTCDPackage> getCDPackagesList() {
    final CDElementVisitor cdElementVisitor = new CDElementVisitor(CDElementVisitor.Options.PACKAGES);
    CDBasisTraverser t = CDBasisMill.traverser();
    t.add4CDBasis(cdElementVisitor);
    this.accept(t);
    return cdElementVisitor.getElements();
  }

  public List<ASTCDClass> getCDClassesList() {
    final CDElementVisitor cdElementVisitor = new CDElementVisitor(CDElementVisitor.Options.CLASSES);
    CDBasisTraverser t = CDBasisMill.traverser();
    t.add4CDBasis(cdElementVisitor);
    this.accept(t);
    return cdElementVisitor.getElements();
  }

  public List<String> getDefaultPackageNameList() {
    return MCQualifiedNameFacade.createPartList(this.defaultPackageName);
  }

  public String getDefaultPackageName() {
    return this.defaultPackageName;
  }

  public void setDefaultPackageName(String defaultPackageName) {
    this.defaultPackageName = defaultPackageName;
  }

  public Optional<ASTCDPackage> getPackageWithName(String packageName) {
    return getPackageWithName(getCDPackagesList(), packageName);
  }

  public static Optional<ASTCDPackage> getPackageWithName(
    List<ASTCDPackage> packages, String packageName) {
    return packages.stream()
      .filter(p -> p.getMCQualifiedName().getQName().equals(packageName))
      .findAny();
  }

  public Optional<ASTCDPackage> getDefaultPackage(List<ASTCDPackage> packages) {
    return getPackageWithName(packages, getDefaultPackageName());
  }

  public ASTCDPackage getOrCreatePackage(String packageName) {
    final Optional<ASTCDPackage> pkg = getPackageWithName(packageName);
    if (pkg.isPresent()) {
      return pkg.get();
    }
    final ASTCDPackage createdPkg =
      CDBasisMill.cDPackageBuilder()
        .setMCQualifiedName(
          MCBasicTypesMill.mCQualifiedNameBuilder()
            .setPartsList(MCQualifiedNameFacade.createPartList(packageName))
            .build())
        .build();
    super.addCDElement(createdPkg);
    createdPkg.setEnclosingScope(this.getEnclosingScope());
    return createdPkg;
  }

  public boolean addCDElementToPackageWithName(ASTCDElement element) {
    return addCDElementToPackageWithName(element, this.defaultPackageName);
  }

  public boolean addCDElementToPackageWithName(int index, ASTCDElement element) {
    return addCDElementToPackageWithName(index, element, defaultPackageName);
  }

  public boolean addCDElementToPackageWithName(ASTCDElement element, String packageName) {
    return addCDElementToPackageWithName(-1, element, packageName);
  }

  public boolean addCDElementToPackageWithName(
    int index, ASTCDElement element, String packageName) {
    // should be added to the package
    final ASTCDPackage specificPackage = getOrCreatePackage(packageName);
    if (index < 0) {
      return specificPackage.addCDElement(element);
    } else {
      specificPackage.addCDElement(index, element);
      return true;
    }
  }

  public boolean addCDElementToPackage(ASTCDElement element, String packageName) {
    return addCDElementToPackageWithName(element, packageName);
  }

  @Override
  public void addCDElement(int index, ASTCDElement element) {
    if (isPresentDefaultPackage()) {
      getDefaultPackage().addCDElement(index, element);
    } else {
      super.addCDElement(index, element);
    }
  }

  @Override
  public boolean addCDElement(de.monticore.cdbasis._ast.ASTCDElement element) {
    if (isPresentDefaultPackage()) {
      return getDefaultPackage().addCDElement(element);
    } else {
      return super.addCDElement(element);
    }
  }

  @Override
  public boolean addAllCDElements(int index, Collection<? extends ASTCDElement> collection) {
    if (isPresentDefaultPackage()) {
      return getDefaultPackage().addAllCDElements(index, collection);
    } else {
      return super.addAllCDElements(index, collection);
    }
  }

  @Override
  public boolean addAllCDElements(Collection<? extends ASTCDElement> collection) {
    if (isPresentDefaultPackage()) {
      return getDefaultPackage().addAllCDElements(collection);
    } else {
      return super.addAllCDElements(collection);
    }
  }

  public boolean isPresentDefaultPackage() {
    return defaultPackage.isPresent();
  }

  public ASTCDPackage getDefaultPackage() {
    if (!isPresentDefaultPackage()) {
      Log.error("0xA7106 Could not get default package as it is not specified.");
    }
    return defaultPackage.get();
  }

  public void setDefaultPackage(ASTCDPackage pkg) {
    defaultPackage = Optional.of(pkg);
  }


}
