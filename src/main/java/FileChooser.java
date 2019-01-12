import javax.swing.*;
import java.awt.*;

public class FileChooser {
    private String directoryName;
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void createFileDialog(String fileName) {
        FileDialog fileDialog = new FileDialog(new JFrame(), "Select file to save class document", FileDialog.SAVE);
        fileDialog.setDirectory("\\");
        fileDialog.setFile(fileName + ".pdf");
        fileDialog.setVisible(true);

        this.fileName = fileDialog.getFile();
        directoryName = fileDialog.getDirectory();
    }
}
