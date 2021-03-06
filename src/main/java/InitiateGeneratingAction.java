import com.intellij.navigation.ItemPresentation;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import di.Injector;

import javax.inject.Inject;


public class InitiateGeneratingAction extends AnAction {

    @Inject
    private ClassAnalyzer analyzer;
    @Inject
    private PdfComposer pdfComposer;

    @Override
    public void update(AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        Presentation presentation = e.getPresentation();

        if (psiFile == null || !psiFile.getName().endsWith(".java")) {
            presentation.setEnabled(false);
            presentation.setVisible(false);
        } else {
            presentation.setVisible(true);
            presentation.setEnabled(true);
        }
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Injector.getInstance()
                .getInjector()
                .injectMembers(this);

        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        Project project = e.getProject();
        PsiDirectory psiFileParent = psiFile.getParent();
        ItemPresentation presentation = PsiDirectory.class.cast(psiFileParent).getPresentation();
        String locationString = presentation.getLocationString();
        if (locationString.endsWith("src")) {
            locationString = "";
        } else {
            locationString += ".";
        }
        String fileName = psiFile.getName();
        String className = fileName.substring(0, fileName.length() - 5);
        PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(locationString + className, GlobalSearchScope.allScope(project));

        if (aClass != null) {
            pdfComposer.compose(analyzer.getClassInfo(aClass));
            for (PsiClass innerClass : aClass.getInnerClasses()) {
                pdfComposer.compose(analyzer.getClassInfo(innerClass));
            }
        } else {
            Notifications.Bus.notify(new Notification("Error", "Cannot generate documentation",
                    "Java class not found " + locationString + "." + className, NotificationType.ERROR));
        }
    }
}
