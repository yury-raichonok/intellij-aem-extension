package com.aem.extension.intellij.aem.extension.component;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.psi.PsiDirectory;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.JBUI.Borders;
import com.twelvemonkeys.lang.StringUtil;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jetbrains.annotations.Nullable;

/**
 * AEM Component Dialog Wrapper Class.
 * <p>
 * This class is responsible for rendering the UI to generate AEM component.
 * </p>
 *
 * @author Yury Raichonak
 */
public class AemComponentDialogWrapper extends DialogWrapper {

  private static final String COMPONENT_GROUP = "Component group";
  private static final String COMPONENT_TITLE = "Component title";
  private static final String COMPONENT_DIALOG_TITLE = "Create new component";
  private static final String EMPTY_TITLE_VALIDATION_MESSAGE = "Component title can't be empty";
  private static final String GENERATE_DIALOG_XML_FILE = "Generate dialog.xml file";
  private static final String GENERATE_CQ_DIALOG_XML_FILE = "Generate _cq_dialog.xml file";
  private static final String GENERATE_CQ_EDIT_CONFIG_XML_FILE = "Generate _cq_editConfig.xml file";
  private static final String GENERATE_CQ_TEMPLATE_XML_FILE = "Generate _cq_template.xml file";
  private static final String SAME_TITLE_VALIDATION_MESSAGE = "Component with such title already exists";
  private static final String EMPTY_GROUP_VALIDATION_MESSAGE = "Group title can't be empty";

  public static final int TOP_AND_BOTTOM_MARGIN = 2;
  public static final int LEFT_AND_RIGHT_MARGIN = 0;

  private static final int DIALOG_HEIGHT = 140;
  private static final int DIALOG_WIDTH = 400;

  private final PsiDirectory directory;

  private JTextField componentTitle;
  private JTextField componentGroup;
  private JCheckBox generateDialogXmlFile;
  private JCheckBox generateCqDialogXmlFile;
  private JCheckBox generateCqEditConfigXmlFile;
  private JCheckBox generateCqTemplateXmlFile;

  protected AemComponentDialogWrapper(PsiDirectory directory) {
    super(true);
    this.directory = directory;
    init();
    setTitle(COMPONENT_DIALOG_TITLE);
  }

  /**
   * @return Swing panel with fields and checkboxes;
   */
  @Override
  protected @Nullable JComponent createCenterPanel() {
    JPanel componentPanel = new JPanel(new GridBagLayout());
    JPanel fieldsPanel = new JPanel(new GridBagLayout());
    JPanel checkBoxPanel = new JPanel(new GridBagLayout());
    componentPanel.setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.insets = JBUI.insets(5, 0);

    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.weightx = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    componentPanel.add(fieldsPanel, constraints);

    constraints.gridy = 1;
    componentPanel.add(checkBoxPanel, constraints);

    componentTitle = new JTextField();
    componentGroup = new JTextField();
    generateDialogXmlFile = new JCheckBox(GENERATE_DIALOG_XML_FILE);
    generateCqDialogXmlFile = new JCheckBox(GENERATE_CQ_DIALOG_XML_FILE);
    generateCqEditConfigXmlFile = new JCheckBox(GENERATE_CQ_EDIT_CONFIG_XML_FILE);
    generateCqTemplateXmlFile = new JCheckBox(GENERATE_CQ_TEMPLATE_XML_FILE);

    constraints.gridy = 0;
    constraints.weightx = 0.1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    fieldsPanel.add(createLabel(COMPONENT_TITLE), constraints);

    constraints.gridx = 1;
    constraints.weightx = 1;
    fieldsPanel.add(componentTitle, constraints);

    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.weightx = 0.1;
    fieldsPanel.add(createLabel(COMPONENT_GROUP), constraints);

    constraints.gridx = 1;
    constraints.weightx = 1;
    fieldsPanel.add(componentGroup, constraints);

    constraints.gridx = 0;
    constraints.gridy = 2;
    checkBoxPanel.add(generateDialogXmlFile, constraints);

    constraints.gridy = 3;
    checkBoxPanel.add(generateCqDialogXmlFile, constraints);

    constraints.gridy = 4;
    checkBoxPanel.add(generateCqEditConfigXmlFile, constraints);

    constraints.gridy = 5;
    checkBoxPanel.add(generateCqTemplateXmlFile, constraints);

    return componentPanel;
  }

  /**
   * Method for validation of specified component title and group;
   * @return Validation info if component title and/or group are not valid;
   */
  @Override
  protected ValidationInfo doValidate() {
    if (StringUtil.isEmpty(componentTitle.getText())) {
      return new ValidationInfo(EMPTY_TITLE_VALIDATION_MESSAGE, componentTitle);
    }
    if (StringUtil.isEmpty(componentGroup.getText())) {
      return new ValidationInfo(EMPTY_GROUP_VALIDATION_MESSAGE, componentGroup);
    }
    if (directory.findSubdirectory(componentTitle.getText()) != null) {
      return new ValidationInfo(SAME_TITLE_VALIDATION_MESSAGE, componentTitle);
    }
    return null;
  }

  /**
   * @param text on label
   * @return Swing label component with applied styles;
   */
  private JComponent createLabel(String text) {
    var label = new JBLabel(text);
    label.setBorder(Borders.empty(TOP_AND_BOTTOM_MARGIN, LEFT_AND_RIGHT_MARGIN));
    return label;
  }

  /**
   * @return specified component title as string;
   */
  public String getComponentTitle() {
    return componentTitle.getText();
  }

  /**
   * @return specified component group name as string;
   */
  public String getComponentGroupName() {
    return componentGroup.getText();
  }

  /**
   * @return true if Generate dialog.xml file checkbox is selected, otherwise returns false;
   */
  public boolean getGenerateDialogXmlFile() {
    return generateDialogXmlFile.isSelected();
  }

  /**
   * @return true if Generate _cq_dialog.xml file checkbox is selected, otherwise returns false;
   */
  public boolean getGenerateCqDialogXmlFile() {
    return generateCqDialogXmlFile.isSelected();
  }

  /**
   * @return true if Generate _cq_editConfig.xml file checkbox is selected, otherwise returns false;
   */
  public boolean getGenerateCqEditConfigXmlFile() {
    return generateCqEditConfigXmlFile.isSelected();
  }

  /**
   * @return true if Generate _cq_template.xml file checkbox is selected, otherwise returns false;
   */
  public boolean getGenerateCqTemplateXmlFile() {
    return generateCqTemplateXmlFile.isSelected();
  }
}
