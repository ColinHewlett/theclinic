/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package _mail_merge_sample;

/**
 *
 * @author colin
 */
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MailMerge {

    public static void mainx(String[] args) {
        String templateFilePath = "path_to_template_file.docx";
        String outputFilePath = "path_to_output_file.docx";

        // Data source
        Map<String, String> data = new HashMap<>();
        data.put("<<NAME>>", "John Doe");
        data.put("<<EMAIL>>", "johndoe@example.com");
        // Add more key-value pairs for other data fields

        try {
            // Read the template file
            BufferedReader reader = new BufferedReader(new FileReader(templateFilePath));
            StringBuilder templateContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                templateContent.append(line).append("\n");
            }
            reader.close();

            // Replace placeholders with actual data
            String mergedContent = mergeData(templateContent.toString(), data);

            // Write the merged content to the output file
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
            writer.write(mergedContent);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String mergeData(String templateContent, Map<String, String> data) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String placeholder = entry.getKey();
            String value = entry.getValue();
            templateContent = templateContent.replace(placeholder, value);
        }
        return templateContent;
    }
}
