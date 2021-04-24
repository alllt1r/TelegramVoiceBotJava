package telegram.Weather;

import lombok.SneakyThrows;
import org.json.JSONException;
import org.json.JSONObject;
import telegram.ReadProperties;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

public class Parsing {
    ReadProperties prop = new ReadProperties();
    @SneakyThrows
    String getTemperature(String city) {
        double current_temperature = 0;
        JSONObject temp = null;
        JSONObject json = readJsonFromUrl("https://api.openweathermap.org/data/2.5/weather?q=" + city.toLowerCase() + "&appid=" + prop.getProp("API.WEATHER"));
        current_temperature = (Float.parseFloat(json.getJSONObject("main").get("temp").toString()));
        current_temperature = Math.round(current_temperature*100.0)/100.0;
        return current_temperature + "";
    }
    @SneakyThrows
    public String getTemperatureCelsium(String city) {
        return Math.round((Float.parseFloat(getTemperature(city)) - 273.15) * 100.0) / 100.0 + "";
    }
    @SneakyThrows
    public String getTemperatureKelvin(String city) {
        return Math.round((Float.parseFloat(getTemperature(city))) * 100.0) / 100.0 + "";
    }
    @SneakyThrows
    public String getTemperatureFahrenheit(String city) {
        return Math.round(((Float.parseFloat(getTemperature(city)) - 273.15) * 9 / 5 + 32) * 100.0) / 100.0 + "";
    }
    @SneakyThrows
    private static JSONObject readJsonFromUrl(String url) throws JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
    @SneakyThrows
    private static String readAll(Reader rd) {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
