import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;


public class InitiateGeneratingAction extends AnAction {

    private String composePackage(String currentPackage, PsiDirectory psiDirectory) {
        if (psiDirectory.getName().startsWith("src")) {
            return currentPackage;
        }
        return composePackage(psiDirectory.getName() + "." + currentPackage, psiDirectory.getParentDirectory());
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        Project project = e.getProject();
        if (psiFile != null && psiFile.getName().endsWith(".java")) {
            String packageName = composePackage("", psiFile.getContainingDirectory());
            String fileName = psiFile.getName();
            String className = fileName.substring(0, fileName.length() - 5);
            PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(packageName + className, GlobalSearchScope.allScope(project));
            if (aClass != null) {
                ClassAnalyzer analyzer = new ClassAnalyzer(aClass);
                PdfComposer pdfComposer = new PdfComposer();
                pdfComposer.compose(analyzer.getClassInfo(), project.getBasePath());

            } else {
                Notifications.Bus.notify(new Notification("Error", "Cannot generate documentation",
                        "Java class not found " + aClass.getQualifiedName(), NotificationType.ERROR));
            }
        } else {
            Notifications.Bus.notify(new Notification("Error", "Cannot generate documentation",
                    "Java file not found " + psiFile.getName(), NotificationType.ERROR));
        }
    }
}
