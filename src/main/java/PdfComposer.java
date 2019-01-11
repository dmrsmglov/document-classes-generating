import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import gherkin.lexer.Pa;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PdfComposer {
    private Document document;
    private File file = new File("kek");

    PdfComposer() {
        document = new Document();
    }

    private void initialize(String fileName) {
        file = new File(fileName);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
        } catch (FileNotFoundException | DocumentException ex) {
            ex.printStackTrace();
        }
        document.open();
    }

    private Paragraph composeSimpleParagraph(List<String> values) {
        Paragraph paragraph = new Paragraph();
        paragraph.setIndentationLeft(10);
        values.stream().map(x -> x + " ").forEach(paragraph::add);
        return paragraph;
    }

    private Paragraph composeSignature(String methodName, Map<String, List<String>> classInfo) {
        Paragraph paragraph = new Paragraph();
        paragraph.setIndentationLeft(10);
        classInfo.get(methodName + "Modifiers").stream()
                                                .filter(x -> x.startsWith("@"))
                                                .forEach(paragraph::add);
        classInfo.get(methodName + "Modifiers").stream()
                                                .filter(x -> !x.startsWith("@") && !x.equals(""))
                                                .map(x -> x + " ")
                                                .forEach(paragraph::add);
        if (classInfo.get(methodName + "ReturnType") != null) {
            paragraph.add(classInfo.get(methodName + "ReturnType").get(0));
        }
        paragraph.add(" " + methodName.split("#")[0]);
        String parameters = classInfo.get(methodName + "Parameters").stream()
                .reduce("(", (x, y) -> x + y + ", ");
        if (parameters.length() > 1) {
            parameters = parameters.substring(0, parameters.length() - 2) + ")";
        } else {
            parameters += ")";
        }
        paragraph.add(parameters);
        return paragraph;
    }

    private Paragraph composeFieldDeclaration(String fieldName, Map<String, List<String>> classInfo) {
        Paragraph paragraph = new Paragraph();
        paragraph.setIndentationLeft(10);
        classInfo.get(fieldName + "Modifiers").stream()
                                                .filter(x -> x.startsWith("@"))
                                                .forEach(paragraph::add);
        classInfo.get(fieldName + "Modifiers").stream()
                                                .filter(x -> !x.startsWith("@") && !x.equals(""))
                                                .map(x -> x + " ")
                                                .forEach(paragraph::add);
        paragraph.add(classInfo.get(fieldName + "Type").get(0));
        paragraph.add(" " + fieldName);
        return paragraph;
    }


    void compose(Map<String, List<String>> classInfo) {
        initialize(classInfo.get("name") + ".pdf");
        try {
            document.addTitle(classInfo.get("name").get(0));
            Paragraph classAnnotations = new Paragraph();
            classInfo.get("modifiers").stream()
                    .filter(x -> x.startsWith("@"))
                    .forEach(classAnnotations::add);
            document.add(classAnnotations);
            Paragraph modifiers = composeSimpleParagraph(classInfo.get("modifiers").stream()
                                                                        .filter(x -> !x.equals("") && !x.startsWith("@"))
                                                                        .collect(Collectors.toList()));
            modifiers.add(classInfo.get("name").get(0));
            document.add(modifiers);

            if (classInfo.get("containClass") != null) {
                document.add(new Chunk("It's a inner class of " + classInfo.get("containClass").get(0) + "\n"));
            }
            if (classInfo.get("innerClasses") != null) {
                document.add(new Chunk("Inner classes\n"));
                document.add(composeSimpleParagraph(classInfo.get("innerClasses")));
            }
            if (!classInfo.get("extends").isEmpty()) {
                document.add(new Chunk("Extends\n"));
                document.add(composeSimpleParagraph(classInfo.get("extends")));
            }

            if (!classInfo.get("implements").isEmpty()) {
                document.add(new Chunk("Implements\n"));
                document.add(composeSimpleParagraph(classInfo.get("implements")));
            }

            if (!classInfo.get("constructors").isEmpty()) {
                document.add(new Chunk("Constructors\n"));
                for (String constructor : classInfo.get("constructors")) {
                    document.add(composeSignature(constructor, classInfo));
                }
            }

            if (!classInfo.get("fields").isEmpty()) {
                document.add(new Chunk("Fields\n"));
                for (String fieldName : classInfo.get("fields")) {
                    document.add(composeFieldDeclaration(fieldName, classInfo));
                }
            }

            if (!classInfo.get("methods").isEmpty()) {
                document.add(new Chunk("Methods\n"));
                for (String methodName : classInfo.get("methods")) {
                    document.add(composeSignature(methodName, classInfo));
                }
            }
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }
        document.close();

        FileChooser fileChooser = new FileChooser(classInfo.get("name").get(0));
        file.renameTo(new File(fileChooser.getDirectoryName() + "/" + fileChooser.getFileName()));
    }
}
