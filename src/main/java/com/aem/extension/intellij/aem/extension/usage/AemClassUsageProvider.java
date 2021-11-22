package com.aem.extension.intellij.aem.extension.usage;

import static java.util.Arrays.asList;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.daemon.ImplicitUsageProvider;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * AEM Class Usage Provider.
 * <p>
 * This class marks classes and fields as implicitly used.
 * </p>
 * @author Yury Raichonak
 */
public class AemClassUsageProvider implements ImplicitUsageProvider {

  private static final String CHILD_RESOURCE_ANNOTATION = "org.apache.sling.models.annotations.injectorspecific.ChildResource";
  private static final String COMPONENT_ANNOTATION = "org.osgi.service.component.annotations.Component";
  private static final String INJECT_ANNOTATION = "javax.inject.Inject";
  private static final String MODEL_ANNOTATION = "org.apache.sling.models.annotations.Model";
  private static final String OSGI_SERVICE_ANNOTATION = "org.apache.sling.models.annotations.injectorspecific.OSGiService";
  private static final String REFERENCE_ANNOTATION = "org.osgi.service.component.annotations.Reference";
  private static final String REQUEST_ATTRIBUTE_ANNOTATION = "org.apache.sling.models.annotations.injectorspecific.RequestAttribute";
  private static final String RESOURCE_PATH_ANNOTATION = "org.apache.sling.models.annotations.injectorspecific.ResourcePath";
  private static final String SELF_ANNOTATION = "org.apache.sling.models.annotations.injectorspecific.Self";
  private static final String SLING_OBJECT_ANNOTATION = "org.apache.sling.models.annotations.injectorspecific.SlingObject";
  private static final String VALUE_MAP_VALUE_ANNOTATION = "org.apache.sling.models.annotations.injectorspecific.ValueMapValue";

  private static final int FLAGS = 0;

  private static final List<String> CLASS_ANNOTATIONS = asList(
      COMPONENT_ANNOTATION,
      MODEL_ANNOTATION
  );
  private static final List<String> MODEL_FIELD_ANNOTATIONS = asList(
      CHILD_RESOURCE_ANNOTATION,
      INJECT_ANNOTATION,
      OSGI_SERVICE_ANNOTATION,
      REFERENCE_ANNOTATION,
      REQUEST_ATTRIBUTE_ANNOTATION,
      RESOURCE_PATH_ANNOTATION,
      SELF_ANNOTATION,
      SLING_OBJECT_ANNOTATION,
      VALUE_MAP_VALUE_ANNOTATION
  );

  /**
   * @param element used Psi element;
   * @return true if element is used implicitly, otherwise return false;
   */
  @Override
  public boolean isImplicitUsage(@NotNull PsiElement element) {
    if (element instanceof PsiClass) {
      return AnnotationUtil.isAnnotated((PsiClass) element, CLASS_ANNOTATIONS, FLAGS);
    }
    return false;
  }

  /**
   * @param element element used Psi element;
   * @return false;
   */
  @Override
  public boolean isImplicitRead(@NotNull PsiElement element) {
    return false;
  }

  /**
   * @param element element used Psi element;
   * @return true if element is used implicitly, otherwise return false;
   */
  @Override
  public boolean isImplicitWrite(@NotNull PsiElement element) {
    if (element instanceof PsiField
        && ((PsiField) element).hasModifierProperty(PsiModifier.PRIVATE)
        && !((PsiField) element).hasModifierProperty(PsiModifier.STATIC)) {
      if (AnnotationUtil.isAnnotated((PsiClass) element.getParent(), MODEL_ANNOTATION, FLAGS)) {
        return AnnotationUtil.isAnnotated((PsiField) element, MODEL_FIELD_ANNOTATIONS, FLAGS);
      }
      if (AnnotationUtil.isAnnotated((PsiClass) element.getParent(), COMPONENT_ANNOTATION, FLAGS)) {
        return AnnotationUtil.isAnnotated((PsiField) element, REFERENCE_ANNOTATION, FLAGS);
      }
    }
    return false;
  }
}
