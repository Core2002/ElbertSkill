package fun.fifu.elbertskill;

public class Test {
    @org.junit.jupiter.api.Test
    public void reflection(){
        ClassUtils.getClassName ("fun.fifu.elbertskill.stands",true).forEach(p-> {

            System.out.println(p);
        });
    }
}
