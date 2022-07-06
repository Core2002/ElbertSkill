package fun.fifu.elbertskill;

public class Test {
    @org.junit.jupiter.api.Test
    public void reflection() {
        ClassUtils.instance.getClasses("fun.fifu.elbertskill.stands").forEach(p -> {

            System.out.println(p);
        });
    }
}
