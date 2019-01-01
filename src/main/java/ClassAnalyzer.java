import com.intellij.psi.PsiClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassAnalyzer {
    PsiClass psiClass;
    Map<String, List<String>> classInfo = new HashMap<>();

    public ClassAnalyzer(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    private void analyze() {

    }

    public Map<String, List<String>> getClassInfo() {
        analyze();
        return classInfo;
    }
}
