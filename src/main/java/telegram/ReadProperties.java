package telegram;

import java.io.FileInputStream;
import java.util.Properties;

import lombok.SneakyThrows;

public class ReadProperties {
    @SneakyThrows
    public String getProp(String proper) {
        FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
        Properties prop = new Properties();
        prop.load(fis);
        return prop.getProperty(proper);
    }
}
