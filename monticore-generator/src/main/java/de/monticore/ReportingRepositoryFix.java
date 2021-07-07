// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import com.google.common.collect.Sets;
import de.monticore.generating.templateengine.reporting.commons.IASTNodeIdentHelper;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.se_rwth.commons.logging.LogStub;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ReportingRepositoryFix extends ReportingRepository {

  private Set<String> allTemplateNames = Sets.newLinkedHashSet();


  @Override
  public void initAllTemplates() {
    // it's a kind of magic
    ClassLoader loader = ClasspathHelper.contextClassLoader();
    List<URL> urls = ClasspathHelper.forClassLoader().stream().filter(u -> !u.getFile().contains("groovy")).collect(Collectors.toList());
    Reflections helper = new Reflections(new ConfigurationBuilder()
            .addClassLoader(loader)
            .setUrls(urls)
            .setScanners(new ResourcesScanner()));

    this.allTemplateNames = helper.getResources(Pattern.compile(".*\\.ftl"));
  }

  public ReportingRepositoryFix(IASTNodeIdentHelper astNodeIdentHelper) {
    super(astNodeIdentHelper);
  }

  public Set<String> getAllTemplateNames() {
    return allTemplateNames;
  }

}
