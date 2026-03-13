/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theclinic.model.message_transmission_model;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.nio.file.Path;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 *
 * @author colin
 */
public class TemplateRenderer {
    private final Configuration cfg;

    public TemplateRenderer(Path templateFolder) throws IOException {

        cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setDirectoryForTemplateLoading(templateFolder.toFile());
        cfg.setDefaultEncoding("UTF-8");
    }

    public String renderTemplate(String templateName, Map<String,Object> model)
            throws Exception {

        Template template = cfg.getTemplate(templateName);

        StringWriter writer = new StringWriter();
        template.process(model, writer);

        return writer.toString();
    }
}
