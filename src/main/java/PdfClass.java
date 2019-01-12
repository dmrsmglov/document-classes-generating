import java.util.List;

public class PdfClass {

    private final String name;
    private final String outerClass;
    private final String signature;
    private final String classAnnotations;
    private final List<String> extending;
    private final List<String> implementing;
    private final List<String> fields;
    private final List<String> constructors;
    private final List<String> methods;
    private final List<String> innerClasses;

    public String getName() {
        return name;
    }

    public String getClassAnnotations() {
        return classAnnotations;
    }

    public String getOuterClass() {
        return outerClass;
    }

    public String getSignature() {
        return signature;
    }

    public List<String> getExtending() {
        return extending;
    }

    public List<String> getImplementing() {
        return implementing;
    }

    public List<String> getFields() {
        return fields;
    }

    public List<String> getConstructors() {
        return constructors;
    }

    public List<String> getMethods() {
        return methods;
    }

    public List<String> getInnerClasses() {
        return innerClasses;
    }

    PdfClass(String name,
             String outerClass,
             String signature,
             String classAnnotations,
             List<String> extending,
             List<String> implementing,
             List<String> fields,
             List<String> constructors,
             List<String> methods,
             List<String> innerClasses) {

        this.name = name;
        this.outerClass = outerClass;
        this.signature = signature;
        this.classAnnotations = classAnnotations;
        this.extending = extending;
        this.implementing = implementing;
        this.fields = fields;
        this.constructors = constructors;
        this.methods = methods;
        this.innerClasses = innerClasses;
    }
}
