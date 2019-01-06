import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class PdfComposer {
    private Document document;
    private File file = new File("kek");
    private Path directoryPath = Paths.get("kekus");

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

    private void createDirectoryForDocuments(String basePath) {
        directoryPath = Paths.get(basePath + "/documents");
        try {
            if (!Files.exists(directoryPath)) {
                Files.createDirectory(directoryPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        classInfo.get(methodName + "Modifiers").stream().map(x -> x + " ").forEach(paragraph::add);
        if (classInfo.get(methodName + "ReturnType") != null) {
            paragraph.add(classInfo.get(methodName + "ReturnType").get(0));
        }
        paragraph.add(" " + methodName);
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
        classInfo.get(fieldName + "Modifiers").stream().map(x -> x + " ").forEach(paragraph::add);
        paragraph.add(classInfo.get(fieldName + "Type").get(0));
        paragraph.add(" " + fieldName);
        return paragraph;
    }


    void compose(Map<String, List<String>> classInfo, String basePath) {
        initialize(classInfo.get("name") + ".pdf");
        try {
            document.addTitle(classInfo.get("name").get(0));
            Paragraph modifiers = composeSimpleParagraph(classInfo.get("modifiers"));
            modifiers.add(classInfo.get("name").get(0));
            document.add(modifiers);

            if (!classInfo.get("extends").isEmpty()) {
                document.add(new Chunk("Extends"));
                document.add(composeSimpleParagraph(classInfo.get("extends")));
            }

            if (!classInfo.get("implements").isEmpty()) {
                document.add(new Chunk("Implements"));
                document.add(composeSimpleParagraph(classInfo.get("implements")));
            }

            if (!classInfo.get("constructors").isEmpty()) {
                document.add(new Chunk("Constructors"));
                for (String constructor : classInfo.get("constructors")) {
                    document.add(composeSignature(constructor, classInfo));
                }
            }

            if (!classInfo.get("fields").isEmpty()) {
                document.add(new Chunk("Fields"));
                for (String fieldName : classInfo.get("fields")) {
                    for (String annotationName : classInfo.get(fieldName + "Annotations")) {
                        document.add(new Chunk(annotationName));
                    }
                    document.add(composeFieldDeclaration(fieldName, classInfo));
                }
            }

            if (!classInfo.get("methods").isEmpty()) {
                document.add(new Chunk("Methods"));
                for (String methodName : classInfo.get("methods")) {
                    for (String annotationName : classInfo.get(methodName + "Annotations")) {
                        document.add(new Chunk(annotationName));
                    }
                    document.add(composeSignature(methodName, classInfo));
                }
            }
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }
        document.close();
        createDirectoryForDocuments(basePath);
        file.renameTo(new File(directoryPath.toString() + "/" + classInfo.get("name").get(0) + ".pdf"));
        System.out.println("*****************ready*******************");
    }
}
