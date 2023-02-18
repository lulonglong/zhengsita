package share;

import io.lettuce.core.output.SocketAddressOutput;
import org.openjdk.jol.info.ClassLayout;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class CrmTechApplication {

    public static void main(String[] args) {
        //SpringApplication.run(CrmTechApplication.class, args);

        parent ch=new child();
        System.out.println(ch.i);
        System.out.println(((child)ch).i);

        ClassLayout classLayout=ClassLayout.parseInstance(ch);
        System.out.println(classLayout.toPrintable());

        ClassLayout classLayout1=ClassLayout.parseClass(child.class);
        System.out.println(classLayout1.toPrintable());
    }
}

class parent{
    public int i=1;
    public void getValue(){
        System.out.println(i);
    }
}

class child extends parent{
    public int i=2;

    public void getValue1(){
        System.out.println(i);
    }
}