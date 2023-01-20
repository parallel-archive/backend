package hu.codeandsoda.osa.email.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailTemplate {

    private static final String BLANK = "";
    private static Logger logger = LoggerFactory.getLogger(EmailTemplate.class);

    private String templateId;

    private String template;

    private Map<String, String> replacementParams;

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Map<String, String> getReplacementParams() {
        return replacementParams;
    }

    public void setReplacementParams(Map<String, String> replacementParams) {
        this.replacementParams = replacementParams;
    }

    public EmailTemplate(String templateId) {
        this.templateId = templateId;
        try {
            this.template = loadTemplate(templateId);
        } catch (Exception e) {
            this.template = BLANK;
        }
    }

    private String loadTemplate(String templateId) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("templates/" + templateId).getFile());
        String content = BLANK;
        try {
            content = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            String logMessage = new StringBuilder().append("action=loadEmailTemplate, status=error, templateId=").append(templateId).append(", error=").append(e.getMessage())
                    .toString();
            logger.error(logMessage);
            throw new Exception("Could not read template with ID = " + templateId);
        }
        return content;
    }

    public String getTemplate(Map<String, String> replacements) {
        String cTemplate = this.getTemplate();

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            cTemplate = cTemplate.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        return cTemplate;
    }

}
