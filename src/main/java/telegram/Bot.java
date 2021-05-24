package telegram;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import telegram.area.AreaFinding;
import telegram.coronavirus.CoronavirusParsing;
import telegram.weather.WeatherParsing;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import javax.validation.constraints.Max;
import java.util.*;

public class Bot extends TelegramLongPollingBot {

    WeatherParsing Weather = new WeatherParsing();
    AreaFinding Area = new AreaFinding();
    CoronavirusParsing Covid = new CoronavirusParsing();
    ReadProperties prop = new ReadProperties();

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        long chat_id = update.getMessage().getChatId();
        String message = update.getMessage().getText();

        if (update.getMessage().getLocation() != null) {
            String longitude = update.getMessage().getLocation().getLongitude().toString();
            String latitude = update.getMessage().getLocation().getLatitude().toString();
            String city = firstUpperCase(Area.getCity(longitude, latitude));
            String country = firstUpperCase(Area.getCountry(city));
            String temp = Weather.getTemperatureCelsium(city);
            String covid = Covid.getConfirmedByCountry(country);
            String currency = Area.getCurrency(country);
            sendMsg("Вы находитесь в городе " + city +
                    "\nПогода в городе " + city + " равна " + temp + "℃" +
                    "\nКоличество заболевших в стране " + country + " за сегодня равно " + covid + " человек" +
                    "\nВалюта " + currency, chat_id);
        }

        if (message != null) {

            if (message.startsWith("/weather")) {
                String city = "";
                for (int i = 0; i < message.length(); i++) {
                    if (i >= 9) {
                        city += message.charAt(i);
                        System.out.println(city);
                    }
                }
                if (Weather.validateCity(city) == true) {
                    sendMsg("Погода в городе " + city + " равна " + Weather.getTemperatureCelsium(city) + "℃", chat_id);
                } else {
                    sendMsg(
                            "К сожалению мы не смогли найти город " + city +
                                    "\nВведите команду ещё раз" +
                                    "\nПример: /weather Minsk"
                            , chat_id);
                }
                city = "";
                /*
                System.out.println(update.getMessage().getLocation());
                KeyboardButton rew = new KeyboardButton();
                rew.getRequestLocation();
                 */
            }
            if (message.startsWith("/covid")) {
                String country = "";
                for (int i = 0; i < message.length(); i++) {
                    if (i >= 7) {
                        country += message.charAt(i);
                    }
                }
                if (Covid.getValidateCountry(country) == true) {
                    sendMsg("Количество заболевших в стране " + country + " за сегодня равно " + Covid.getConfirmedByCountry(country), chat_id);
                } else {
                    sendMsg(
                            "К сожалению мы не смогли найти страну " + country +
                                    "\nВведите команду ещё раз" +
                                    "\nПример: /covid Belarus"
                            , chat_id);
                }
            }
            if (message.startsWith("/quiz")) {
                sendMsg("Проверяем наличие страны...", chat_id);
                String country = "";
                for (int i = 0; i < message.length(); i++) {
                    if (i >= 6) {
                        country += message.charAt(i);
                    }
                }
                if (country.equals(null) || country.equals("")) {
                    sendMsg("Составляем викторину со случайной страной " + country + "...", chat_id);
                    sendPoll(Covid.getRandomCountry(), chat_id);
                } else  {
                    if (Covid.getValidateCountry(country) == true) {
                        sendMsg("Составляем викторину со страной " + country + "...", chat_id);
                        sendPoll(country, chat_id);
                    } else {
                        sendMsg(
                                "К сожалению мы не смогли найти страну " + country +
                                        "\nВведите команду ещё раз" +
                                        "\nПример: /quiz Belarus"
                                , chat_id);
                    }
                }

            }
            if (message.equals("/set")) {
                sendMsgWithReply("Отправьте пожалуйста геолокацию", chat_id);
            }

            if (message.equals("/start")) {
                sendMsg(
                        "Здравствуйте, " + update.getMessage().getFrom().getFirstName() +
                                "\nВот команды, которые я могу для вас выполнить:" +
                                "\n/set" +
                                "\n/weather + город" +
                                "\n/covid + страна" +
                                "\n/quiz + страна"
                        , chat_id);
            }
        }

    }

    @SneakyThrows
    public synchronized void sendMsg(String s, long chat_id) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chat_id + "");
        sendMessage.setText(s);
        sendMessage.setReplyMarkup(null);
        execute(sendMessage);
    }

    @SneakyThrows
    public synchronized void sendMsgWithReply(String s, long chat_id) {
        ReplyKeyboardMarkup key = new ReplyKeyboardMarkup();

        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardButton btn = new KeyboardButton();
        btn.setText("Send Location");
        btn.setRequestLocation(true);
        keyboardFirstRow.add(btn);
        keyboard.add(keyboardFirstRow);
        key.setKeyboard(keyboard);
        key.setResizeKeyboard(true);
        key.setOneTimeKeyboard(true);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chat_id + "");
        sendMessage.setText(s);
        sendMessage.setReplyMarkup(key);
        execute(sendMessage);
    }

    @SneakyThrows
    public String firstUpperCase(String word){
        if(word == null || word.isEmpty()) return "";//или return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    @SneakyThrows
    public synchronized void sendPoll(String country, long chatId){
        SendPoll sendPoll = new SendPoll();
        Random random = new Random();
        sendPoll.enableNotification();
        sendPoll.setQuestion("Сколько заболевших в стране " + country + " за сегодня?");
        sendPoll.setAnonymous(true);
        ArrayList<String> answers = new ArrayList<>();
        int answer_right = Integer.parseInt(Covid.getConfirmedByCountry(country));
        int answer_id_right = random.nextInt(3);
        if (answer_right <= 1000) {
            answers.add(answer_right + "");
            answers.add(answer_right + 1000 + "");
            answers.add(answer_right + 2000 + "");
        } else {
            if (answer_right > 2000) {
                if (answer_id_right == 0) {
                    answers.add(answer_right + "");
                    answers.add(answer_right + 1000 + "");
                    answers.add(answer_right - 1000 + "");
                }
                else if (answer_id_right == 1) {
                    answers.add(answer_right + "");
                    answers.add(answer_right - 1000 + "");
                    answers.add(answer_right - 2000 + "");
                }
                else if (answer_id_right == 2) {
                    answers.add(answer_right + "");
                    answers.add(answer_right + 1000 + "");
                    answers.add(answer_right + 2000 + "");
                }
            } else {
                if (answer_id_right == 0) {
                    answers.add(answer_right + "");
                    answers.add(answer_right + 1000 + "");
                    answers.add(answer_right - 1000 + "");
                }
                else if (answer_id_right == 1) {
                    answers.add(answer_right + "");
                    answers.add(answer_right + 1000 + "");
                    answers.add(answer_right + 2000 + "");
                }
                else if (answer_id_right == 2) {
                    answers.add(answer_right + "");
                    answers.add(answer_right - 1000 + "");
                    answers.add(answer_right + 2000 + "");
                }
            }
        }
        Collections.shuffle(answers);
        sendPoll.setOptions(answers);
        sendPoll.setChatId(chatId + "");
        sendPoll.setType("quiz");
        sendPoll.setCorrectOptionId(answers.indexOf(answer_right + ""));
        execute(sendPoll);
    }


    @Override
    public String getBotUsername() {
        return prop.getProp(("BOT.MASTER.NAME"));
    }

    @Override
    public String getBotToken() {
        return prop.getProp("BOT.MASTER.TOKEN");
    }
}