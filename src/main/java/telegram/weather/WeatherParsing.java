package telegram.weather;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import telegram.ReadProperties;

import java.io.*;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;

public class WeatherParsing {
    ReadProperties prop = new ReadProperties();

    @SneakyThrows
    String getTemperature(String city) {
        double current_temperature = 0;
        JSONObject json = new JSONObject(IOUtils.toString(new URL("https://api.openweathermap.org/data/2.5/weather?q=" + city.toLowerCase(Locale.ROOT) + "&appid=" + prop.getProp("API.WEATHER.2")), Charset.forName("UTF-8")));
        current_temperature = (Float.parseFloat(json.getJSONObject("main").get("temp").toString()));
        current_temperature = Math.round(current_temperature*100.0)/100.0;
        System.out.println(json);
        return current_temperature + "";
    }

    @SneakyThrows
    public boolean validateCity(String city) throws IOException {
        boolean isCity = false;
        try {
            JSONObject json = new JSONObject(IOUtils.toString(new URL("https://api.openweathermap.org/data/2.5/weather?q=" + city.toLowerCase(Locale.ROOT) + "&appid=" + prop.getProp("API.WEATHER.2")), Charset.forName("UTF-8")));
            if (json.get("cod").equals(200)) {
                isCity = true;
            } else {
                isCity = false;
            }
        } catch (IOException e) {
            isCity = false;
        }
        return isCity;
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

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
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
}
