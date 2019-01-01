import com.intellij.psi.PsiClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassAnalyzer {
    private PsiClass aClass;
    private Map<String, List<String>> classInfo = new HashMap<>();

    ClassAnalyzer(PsiClass aClass) {
        this.aClass = aClass;
    }

    private void analyze() {

    }

    public Map<String, List<String>> getClassInfo() {
        analyze();
        return classInfo;
    }
}
