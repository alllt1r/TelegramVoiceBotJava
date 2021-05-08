package telegram.area;

import lombok.SneakyThrows;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import telegram.ReadProperties;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

public class AreaFinding {
    ReadProperties prop = new ReadProperties();

    @SneakyThrows
    public String getCity(String longitude, String latitude) {
        String city = "";
        JSONObject temp = null;
        JSONObject json = readJsonFromUrl("https://api.weatherbit.io/v2.0/current?lat=" + latitude + "&lon=" + longitude + "&key=" + prop.getProp("API.WEATHER.1") + "&include=minutely");
        city = json.getJSONArray("data").getJSONObject(0).getString("city_name");
        return city;
    }

    @SneakyThrows
    public String getCountry(String longitude, String latitude) {
        String country = "";
        JSONObject json = readJsonFromUrl("https://api.weatherbit.io/v2.0/current?lat=" + latitude + "&lon=" + longitude + "&key=" + prop.getProp("API.WEATHER.1") + "&include=minutely");
        country = json.getJSONArray("data").getJSONObject(0).getString("timezone");
        country = country.replaceAll("(\\w*)/", "").replaceAll("_", " ");
        return country;
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
