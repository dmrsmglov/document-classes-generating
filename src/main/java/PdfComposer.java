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
    private Path directoryPath = Paths.get("kek");

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

    private Paragraph composeParagraph(List<String> values) {
        Paragraph paragraph = new Paragraph();
        values.stream().map(x -> x + " ").forEach(paragraph::add);
        return paragraph;
    }

    void compose(Map<String, List<String>> classInfo, String basePath) {
        initialize(classInfo.get("name") + ".pdf");
        try {
            document.addTitle(classInfo.get("name").get(0));
            Paragraph modifiers = composeParagraph(classInfo.get("modifiers"));
            modifiers.add(classInfo.get("name").get(0));
            document.add(modifiers);
            document.add(new Chunk("Extends"));
            document.add(composeParagraph(classInfo.get("extends")));
            if (!classInfo.get("implements").isEmpty()) {
                document.add(new Chunk("Implements"));
                document.add(composeParagraph(classInfo.get("implements")));
            }
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }
        document.close();
        createDirectoryForDocuments(basePath);
        file.renameTo(new File(directoryPath.toString() + "/" + classInfo.get("name") + ".pdf"));
    }
}
