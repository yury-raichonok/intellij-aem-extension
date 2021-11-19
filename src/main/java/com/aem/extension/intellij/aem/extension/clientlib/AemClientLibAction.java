package com.aem.extension.intellij.aem.extension.clientlib;

import static com.aem.extension.intellij.aem.extension.component.AemComponentAction.CARRIAGE_RETURN;
import static com.aem.extension.intellij.aem.extension.component.AemComponentAction.CONTENT_XML_FILE_NAME;
import static com.aem.extension.intellij.aem.extension.component.AemComponentAction.DIALOG_EXIT_CODE;
import static com.aem.extension.intellij.aem.extension.utils.FileContentFetcherUtil.getFileContentFromTemplate;
import static io.netty.util.internal.StringUtil.EMPTY_STRING;

import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.ide.highlighter.custom.SyntaxTable;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.impl.AbstractFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public class AemClientLibAction extends CreateElementActionBase {

  public static final String CLIENT_LIBRARY = "clientLibrary";

  private static final String ACTION_NAME = "Create new client library";
  private static final String ERROR_TITLE = "Cannot create new client library";
  private static final String CONTENT_XML_TEMPLATE_PATH = "templates/ClientLibContentTemplate.txt";
  private static final String CLIENT_LIBRARY_CATEGORIES_SPECIFIER = "ClientLibsCategories";
  private static final String CSS_DIRECTORY_NAME = "css";
  private static final String CSS_FILE_NAME = "style.css";
  private static final String CSS_TXT_FILE_NAME = "css.txt";
  private static final String FILE_PATH_FORMAT_TEMPLATE = "%s/%s";
  private static final String JS_DIRECTORY_NAME = "js";
  private static final String JS_FILE_NAME = "script.js";
  private static final String JS_TXT_FILE_NAME = "js.txt";
  private static final String LESS_FILE_NAME = "style.less";

  private Project currentProject;
  private boolean generateCssFile;
  private boolean generateLessFile;
  private boolean generateJsFile;

  @Override
  protected void invokeDialog(@NotNull Project project, @NotNull PsiDirectory directory,
      @NotNull Consumer<PsiElement[]> elementsConsumer) {
    currentProject = project;
    var aemClientLibDialogWrapper = new AemClientLibDialogWrapper(directory);

    if (aemClientLibDialogWrapper.showAndGet()) {
      String categories = aemClientLibDialogWrapper.getCategories();
      generateCssFile = aemClientLibDialogWrapper.getGenerateCssFile();
      generateLessFile = aemClientLibDialogWrapper.getGenerateLessFile();
      generateJsFile = aemClientLibDialogWrapper.getGenerateJsFile();
      create(categories, directory);
      aemClientLibDialogWrapper.close(DIALOG_EXIT_CODE);
    }
  }

  @Override
  protected PsiElement @NotNull [] create(@NotNull String categories, PsiDirectory directory) {
    Application application = ApplicationManager.getApplication();
    application.runWriteAction(() -> {
      PsiDirectory clientLibDirectory = directory.createSubdirectory(CLIENT_LIBRARY);
      generateRequiredFiles(categories, clientLibDirectory);
    });
    return new PsiElement[0];
  }

  @Override
  protected String getErrorTitle() {
    return ERROR_TITLE;
  }

  @Override
  protected String getActionName(PsiDirectory directory, String newName) {
    return ACTION_NAME;
  }

  private void generateRequiredFiles(String componentTitle, PsiDirectory directory) {
    PsiFileFactory factory = PsiFileFactory.getInstance(currentProject);

    String contentXmlFileContent = getFileContentFromTemplate(CONTENT_XML_TEMPLATE_PATH)
        .replace(CARRIAGE_RETURN, EMPTY_STRING)
        .replace(CLIENT_LIBRARY_CATEGORIES_SPECIFIER, componentTitle);
    PsiFile contentXmlFile = factory.createFileFromText(CONTENT_XML_FILE_NAME, XMLLanguage.INSTANCE,
        contentXmlFileContent);
    directory.add(contentXmlFile);

    if (generateCssFile) {
      PsiDirectory cssDirectory = directory.createSubdirectory(CSS_DIRECTORY_NAME);
      PsiFile cssFile = factory.createFileFromText(CSS_FILE_NAME,
          new AbstractFileType(new SyntaxTable()), EMPTY_STRING);
      cssDirectory.add(cssFile);

      PsiFile cssTxtFile = factory.createFileFromText(CSS_TXT_FILE_NAME,
          new AbstractFileType(new SyntaxTable()),
          String.format(FILE_PATH_FORMAT_TEMPLATE, CSS_DIRECTORY_NAME, CSS_FILE_NAME));
      directory.add(cssTxtFile);
    }

    if (generateLessFile) {
      PsiDirectory cssDirectory = directory.findSubdirectory(CSS_DIRECTORY_NAME);
      if (cssDirectory == null) {
        cssDirectory = directory.createSubdirectory(CSS_DIRECTORY_NAME);
        PsiFile lessFile = factory.createFileFromText(LESS_FILE_NAME,
            new AbstractFileType(new SyntaxTable()), EMPTY_STRING);
        cssDirectory.add(lessFile);

        PsiFile cssTxtFile = factory.createFileFromText(CSS_TXT_FILE_NAME,
            new AbstractFileType(new SyntaxTable()),
            String.format(FILE_PATH_FORMAT_TEMPLATE, CSS_DIRECTORY_NAME, LESS_FILE_NAME));
        directory.add(cssTxtFile);
      } else {
        PsiFile lessFile = factory.createFileFromText(LESS_FILE_NAME,
            new AbstractFileType(new SyntaxTable()), EMPTY_STRING);
        cssDirectory.add(lessFile);

        PsiFile cssTxtFile = directory.findFile(CSS_TXT_FILE_NAME);
        if (cssTxtFile != null) {
          cssTxtFile.delete();
          PsiFile newCssTxtFile = factory.createFileFromText(CSS_TXT_FILE_NAME,
              new AbstractFileType(new SyntaxTable()),
              CSS_DIRECTORY_NAME + "/" + CSS_FILE_NAME + "\n" + CSS_DIRECTORY_NAME + "/"
                  + LESS_FILE_NAME);
          directory.add(newCssTxtFile);
        }
      }
    }

    if (generateJsFile) {
      PsiDirectory jsDirectory = directory.createSubdirectory(JS_DIRECTORY_NAME);
      PsiFile jsFile = factory.createFileFromText(JS_FILE_NAME,
          new AbstractFileType(new SyntaxTable()), EMPTY_STRING);
      jsDirectory.add(jsFile);

      PsiFile jsTxtFile = factory.createFileFromText(JS_TXT_FILE_NAME,
          new AbstractFileType(new SyntaxTable()),
          String.format(FILE_PATH_FORMAT_TEMPLATE, JS_DIRECTORY_NAME, JS_FILE_NAME));
      directory.add(jsTxtFile);
    }
  }
}
