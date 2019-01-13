import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import di.Injector;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.util.List;
import java.util.stream.Collectors;

public class PdfComposer {

    @Inject
    private FileChooser fileChooser;

    PdfComposer() {
        Injector.getInstance()
                .getInjector()
                .injectMembers(this);
    }

    private Document initialize(String fileName) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
        } catch (FileNotFoundException | DocumentException ex) {
            ex.printStackTrace();
        }

        document.open();
        return document;
    }

    private Paragraph composeSimpleParagraph(List<String> values) {
        Paragraph paragraph = new Paragraph();
        paragraph.setIndentationLeft(10);
        paragraph.add(values.stream().collect(Collectors.joining(", ")));
        return paragraph;
    }

    private Paragraph composeSimpleParagraph(String value) {
        Paragraph paragraph = new Paragraph(value);
        paragraph.setIndentationLeft(10);
        return paragraph;
    }

    void compose(PdfClass pdfClass) {
        String fileName = pdfClass.getName() + ".pdf";
        File file = new File(fileName);
        Document document = initialize(fileName);
        try {
            document.addTitle(pdfClass.getName());
            Paragraph classAnnotations = new Paragraph(pdfClass.getClassAnnotations());
            document.add(classAnnotations);
            Paragraph modifiers = new Paragraph(pdfClass.getSignature());
            document.add(modifiers);
            if (pdfClass.getOuterClass() != null) {
                document.add(new Chunk("It's an inner class of " + pdfClass.getOuterClass() + "\n"));
            }
            if (!pdfClass.getInnerClasses().isEmpty()) {
                document.add(new Chunk("Inner classes\n"));
                document.add(composeSimpleParagraph(pdfClass.getInnerClasses()));
            }
            if (!pdfClass.getExtending().isEmpty()) {
                document.add(new Chunk("Extends\n"));
                document.add(composeSimpleParagraph(pdfClass.getExtending()));
            }

            if (!pdfClass.getImplementing().isEmpty()) {
                document.add(new Chunk("Implements\n"));
                document.add(composeSimpleParagraph(pdfClass.getImplementing()));
            }

            if (!pdfClass.getConstructors().isEmpty()) {
                document.add(new Chunk("Constructors\n"));
                for (String constructor : pdfClass.getConstructors()) {
                    document.add(composeSimpleParagraph(constructor));
                }
            }

            if (!pdfClass.getFields().isEmpty()) {
                document.add(new Chunk("Fields\n"));
                for (String field : pdfClass.getFields()) {
                    document.add(composeSimpleParagraph(field));
                }
            }

            if (!pdfClass.getMethods().isEmpty()) {
                document.add(new Chunk("Methods\n"));
                for (String method : pdfClass.getMethods()) {
                    document.add(composeSimpleParagraph(method));
                }
            }
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }
        document.close();

        fileChooser.createFileDialog(pdfClass.getName());
        file.renameTo(new File(fileChooser.getDirectoryName() + "/" + fileChooser.getFileName()));
    }
}
