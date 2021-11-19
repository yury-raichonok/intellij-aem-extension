package com.aem.extension.intellij.aem.extension.component;

import static com.aem.extension.intellij.aem.extension.utils.FileContentFetcherUtil.getFileContentFromTemplate;
import static io.netty.util.internal.StringUtil.EMPTY_STRING;

import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * AEM Component Generation Action Class.
 * <p>
 * This class generates AEM component with developer-selected files from template.
 * </p>
 * @author Yury Raichonak
 */
public class AemComponentAction extends CreateElementActionBase {

  public static final String CONTENT_XML_FILE_NAME = ".content.xml";
  public static final String CARRIAGE_RETURN = "\r";

  private static final String ACTION_NAME = "Create AEM Component";
  private static final String CQ_DIALOG_DIRECTORY_NAME = "_cq_dialog";
  private static final String CQ_DIALOG_XML_TEMPLATE_PATH = "templates/DialogTemplate.txt";
  private static final String CQ_EDIT_CONFIG_DIRECTORY_NAME = "_cq_editConfig";
  private static final String CQ_TEMPLATE_DIRECTORY_NAME = "_cq_template";
  private static final String CONTENT_XML_TEMPLATE_PATH = "templates/ComponentContextTemplate.txt";
  private static final String COMPONENT_GROUP_NAME_FILE_SPECIFIER = "ComponentGroupName";
  private static final String COMPONENT_TITLE_FILE_SPECIFIER = "ComponentTitle";
  private static final String DIALOG_DIRECTORY_NAME = "dialog";
  private static final String ERROR_TITLE = "Cannot create AEM component";

  public static final int DIALOG_EXIT_CODE = 1;

  private boolean generateDialogXmlFile;
  private boolean generateCqDialogXmlFile;
  private boolean generateCqEditConfigXmlFile;
  private boolean generateCqTemplateXmlFile;
  private Project currentProject;
  private String componentGroupName;

  /**
   * @param project current project;
   * @param directory where create AEM component;
   * @param elementsConsumer Psi elements;
   */
  @Override
  protected void invokeDialog(@NotNull Project project, @NotNull PsiDirectory directory,
      @NotNull Consumer<PsiElement[]> elementsConsumer) {
    currentProject = project;
    var aemComponentDialogWrapper = new AemComponentDialogWrapper(directory);

    if (aemComponentDialogWrapper.showAndGet()) {
      String componentTitle = aemComponentDialogWrapper.getComponentTitle();
      componentGroupName = aemComponentDialogWrapper.getComponentGroupName();
      generateDialogXmlFile = aemComponentDialogWrapper.getGenerateDialogXmlFile();
      generateCqDialogXmlFile = aemComponentDialogWrapper.getGenerateCqDialogXmlFile();
      generateCqEditConfigXmlFile = aemComponentDialogWrapper.getGenerateCqEditConfigXmlFile();
      generateCqTemplateXmlFile = aemComponentDialogWrapper.getGenerateCqTemplateXmlFile();
      create(componentTitle, directory);
      aemComponentDialogWrapper.close(DIALOG_EXIT_CODE);
    }
  }

  /**
   * @param componentTitle title of AEM component;
   * @param directory where create AEM component;
   * @return new PsiElement;
   */
  @Override
  protected PsiElement @NotNull [] create(@NotNull String componentTitle, PsiDirectory directory) {
    Application application = ApplicationManager.getApplication();
    application.runWriteAction(() -> {
      PsiDirectory componentDirectory = directory.createSubdirectory(componentTitle);
      generateRequiredFiles(componentTitle, componentDirectory);
    });
    return new PsiElement[0];
  }

  /**
   * @return constant error title;
   */
  @Override
  protected String getErrorTitle() {
    return ERROR_TITLE;
  }

  /**
   * @param directory of AEM component;
   * @param newName name of action;
   * @return constant name of action;
   */
  @Override
  public String getActionName(PsiDirectory directory, String newName) {
    return ACTION_NAME;
  }

  /**
   * Method for generation files under newly created directory;
   * @param componentTitle title of AEM component, required for files generation;
   * @param directory of AEM component
   */
  private void generateRequiredFiles(String componentTitle, PsiDirectory directory) {
    PsiFileFactory factory = PsiFileFactory.getInstance(currentProject);

    PsiFile componentHtmlFile = factory.createFileFromText(String.format("%s.html", componentTitle),
        HTMLLanguage.INSTANCE, EMPTY_STRING);
    directory.add(componentHtmlFile);

    String contentXmlFileContent = getFileContentFromTemplate(CONTENT_XML_TEMPLATE_PATH)
        .replace(CARRIAGE_RETURN, EMPTY_STRING)
        .replace(COMPONENT_TITLE_FILE_SPECIFIER, componentTitle)
        .replace(COMPONENT_GROUP_NAME_FILE_SPECIFIER, componentGroupName);
    PsiFile contentXmlFile = factory.createFileFromText(CONTENT_XML_FILE_NAME, XMLLanguage.INSTANCE,
        contentXmlFileContent);
    directory.add(contentXmlFile);

    if (generateCqDialogXmlFile) {
      PsiDirectory cqDialogDirectory = directory.createSubdirectory(CQ_DIALOG_DIRECTORY_NAME);
      String cqDialogFileContent = getFileContentFromTemplate(CQ_DIALOG_XML_TEMPLATE_PATH)
          .replace(CARRIAGE_RETURN, EMPTY_STRING)
          .replace(COMPONENT_TITLE_FILE_SPECIFIER, componentTitle);
      PsiFile cqDialogXmlFile = factory.createFileFromText(CONTENT_XML_FILE_NAME,
          XMLLanguage.INSTANCE, cqDialogFileContent);
      cqDialogDirectory.add(cqDialogXmlFile);
    }

    if (generateCqEditConfigXmlFile) {
      PsiDirectory cqEditConfigDirectory = directory.createSubdirectory(
          CQ_EDIT_CONFIG_DIRECTORY_NAME);
      PsiFile cqEditConfigXmlFile = factory.createFileFromText(CONTENT_XML_FILE_NAME,
          XMLLanguage.INSTANCE, EMPTY_STRING);
      cqEditConfigDirectory.add(cqEditConfigXmlFile);
    }
    if (generateCqTemplateXmlFile) {
      PsiDirectory cqTemplateDirectory = directory.createSubdirectory(CQ_TEMPLATE_DIRECTORY_NAME);
      PsiFile cqTemplateXmlFile = factory.createFileFromText(CONTENT_XML_FILE_NAME,
          XMLLanguage.INSTANCE, EMPTY_STRING);
      cqTemplateDirectory.add(cqTemplateXmlFile);
    }
    if (generateDialogXmlFile) {
      PsiDirectory dialogDirectory = directory.createSubdirectory(DIALOG_DIRECTORY_NAME);
      PsiFile dialogXmlFile = factory.createFileFromText(CONTENT_XML_FILE_NAME,
          XMLLanguage.INSTANCE,
          EMPTY_STRING);
      dialogDirectory.add(dialogXmlFile);
    }
  }
}
