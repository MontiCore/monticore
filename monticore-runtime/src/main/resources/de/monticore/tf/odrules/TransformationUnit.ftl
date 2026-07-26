<#-- (c) https://github.com/MontiCore/monticore -->
package ${hierarchyHelper.getPackageName()};

import java.util.*;
import java.util.Optional;
import java.util.function.Consumer;

import de.monticore.ast.ASTNode;
import de.monticore.tf.runtime.FastLookupList;
import de.monticore.tf.runtime.ODRule;
import de.monticore.tf.runtime.ValueComparator;
import de.monticore.tf.runtime.inc.ModelAccessor;
import de.monticore.tf.runtime.inc.IModelAccessor;
import de.monticore.tf.runtime.inc.ModificationOp;
import com.google.common.collect.Lists;
import static com.google.common.collect.Lists.*;
import static de.se_rwth.commons.StringTransformations.*;
import java.util.stream.Collectors;
import com.google.common.base.*;
import de.se_rwth.commons.logging.Log;
import de.monticore.generating.templateengine.*;
<#--
import de.monticore.generating.templateengine.reporting.Reporting;
import static de.monticore.generating.templateengine.reporting.Reporting.*;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.TransformationReporter;
import de.monticore.generating.templateengine.reporting.commons.ASTNodeIdentHelper;
-->

<#list hierarchyHelper.getCustomImports() as customImport> ${customImport}</#list>

${tc.include("de.monticore.tf.odrules.TransformationClass")}
