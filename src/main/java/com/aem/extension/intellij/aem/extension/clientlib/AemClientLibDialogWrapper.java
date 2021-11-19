package com.aem.extension.intellij.aem.extension.clientlib;

import static com.aem.extension.intellij.aem.extension.clientlib.AemClientLibAction.CLIENT_LIBRARY;
import static com.aem.extension.intellij.aem.extension.component.AemComponentDialogWrapper.LEFT_AND_RIGHT_MARGIN;
import static com.aem.extension.intellij.aem.extension.component.AemComponentDialogWrapper.TOP_AND_BOTTOM_MARGIN;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.psi.PsiDirectory;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.JBUI.Borders;
import com.twelvemonkeys.lang.StringUtil;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jetbrains.annotations.Nullable;

/**
 * Client Library Dialog Wrapper Class.
 * <p>
 * This class is responsible for rendering the UI to generate Client Library for AEM component.
 * </p>
 *
 * @author Yury Raichonak
 */
public class AemClientLibDialogWrapper extends DialogWrapper {

  private static final String EMPTY_CATEGORIES_VALIDATION_MESSAGE = "Client lib categories can't be empty";
  private static final String CLIENT_LIB_DIALOG_TITLE = "Create new client library";
  private static final String CLIENT_LIB_EXISTS_VALIDATION_MESSAGE = "Client library in current component already exists";
  private static final String GENERATE_CSS_FILE = "Generate css file";
  private static final String GENERATE_LESS_FILE = "Generate less file";
  private static final String GENERATE_JS_FILE = "Generate js file";
  private static final String CLIENT_LIBRARY_CATEGORIES = "Client library categories";

  private static final int DIALOG_HEIGHT = 140;
  private static final int DIALOG_WIDTH = 400;

  private final PsiDirectory directory;

  private JTextField categories;
  private JCheckBox generateCssFile;
  private JCheckBox generateLessFile;
  private JCheckBox generateJsFile;

  protected AemClientLibDialogWrapper(PsiDirectory directory) {
    super(true);
    this.directory = directory;
    init();
    setTitle(CLIENT_LIB_DIALOG_TITLE);
  }

  /**
   * @return Swing panel with field and checkboxes;
   */
  @Override
  protected @Nullable JComponent createCenterPanel() {
    JPanel componentPanel = new JPanel(new GridBagLayout());
    JPanel fieldPanel = new JPanel(new GridBagLayout());
    JPanel checkBoxPanel = new JPanel(new GridBagLayout());
    componentPanel.setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.insets = JBUI.insets(5, 0);

    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.weightx = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    componentPanel.add(fieldPanel, constraints);

    constraints.gridy = 1;
    componentPanel.add(checkBoxPanel, constraints);

    categories = new JTextField();

    generateCssFile = new JCheckBox(GENERATE_CSS_FILE);
    generateLessFile = new JCheckBox(GENERATE_LESS_FILE);
    generateJsFile = new JCheckBox(GENERATE_JS_FILE);
    JLabel label = new JLabel(CLIENT_LIBRARY_CATEGORIES);

    label.setBorder(Borders.empty(TOP_AND_BOTTOM_MARGIN, LEFT_AND_RIGHT_MARGIN));

    constraints.gridy = 0;
    constraints.weightx = 0.1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    fieldPanel.add(label, constraints);

    constraints.gridx = 1;
    constraints.weightx = 1;
    fieldPanel.add(categories, constraints);

    constraints.gridx = 0;
    constraints.gridy = 2;
    checkBoxPanel.add(generateCssFile, constraints);

    constraints.gridy = 3;
    checkBoxPanel.add(generateLessFile, constraints);

    constraints.gridy = 4;
    checkBoxPanel.add(generateJsFile, constraints);

    return componentPanel;
  }

  /**
   * Method for validation of specified client library categories;
   * Also validate if client library of current AEM component already exists;
   * @return Validation info if client library categories is not valid, or client library for this component already exists;
   */
  @Override
  protected ValidationInfo doValidate() {
    if (StringUtil.isEmpty(categories.getText())) {
      return new ValidationInfo(EMPTY_CATEGORIES_VALIDATION_MESSAGE, categories);
    }
    if (directory.findSubdirectory(CLIENT_LIBRARY) != null) {
      return new ValidationInfo(CLIENT_LIB_EXISTS_VALIDATION_MESSAGE, categories);
    }
    return null;
  }

  /**
   * @return specified client library categories as string;
   */
  public String getCategories() {
    return categories.getText();
  }

  /**
   * @return true if Generate CSS file checkbox is selected, otherwise returns false;
   */
  public boolean getGenerateCssFile() {
    return generateCssFile.isSelected();
  }

  /**
   * @return true if Generate LESS file checkbox is selected, otherwise returns false;
   */
  public boolean getGenerateLessFile() {
    return generateLessFile.isSelected();
  }

  /**
   * @return true if Generate JS file checkbox is selected, otherwise returns false;
   */
  public boolean getGenerateJsFile() {
    return generateJsFile.isSelected();
  }
}
