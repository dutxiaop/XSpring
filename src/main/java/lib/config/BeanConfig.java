package lib.config;

/**
 * Created by xiaoP on 2017/2/25.
 */
public class BeanConfig {
    private String id;
    private String className;
    private String initMethod;

    public BeanConfig() {
        this.id = null;
        this.className = null;
        this.initMethod = null;
    }

    public BeanConfig(String id, String className) {
        this.id = id;
        this.className = className;
        this.initMethod = null;
    }

    public BeanConfig(String id, String className, String initMethod) {
        this.id = id;
        this.className = className;
        this.initMethod = initMethod;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getInitMethod() {
        return initMethod;
    }

    public void setInitMethod(String initMethod) {
        this.initMethod = initMethod;
    }
}
