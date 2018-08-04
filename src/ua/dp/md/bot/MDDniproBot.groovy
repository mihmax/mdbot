package ua.dp.md.bot

import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import org.telegram.telegrambots.api.methods.BotApiMethod
import org.telegram.telegrambots.exceptions.TelegramApiException
import org.telegram.telegrambots.exceptions.TelegramApiRequestException

import java.util.concurrent.ArrayBlockingQueue

import static ua.dp.md.bot.Language.EN
import static ua.dp.md.bot.Language.RU
import static ua.dp.md.bot.Language.UK
import net.glxn.qrgen.javase.QRCode
import net.openhft.chronicle.map.ChronicleMap
import net.openhft.chronicle.map.ChronicleMapBuilder
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.methods.send.SendPhoto
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.api.objects.CallbackQuery
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.api.objects.User
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.bots.TelegramLongPollingBot


/**
 * @author Maxym "mihmax" Mykhalchuk
 * @version 4
 *
 * Telegram Bot for Dnipro Mission Day to be held 11 August 2018.
 * Maintained by @mihmax.
 */
class MDDniproBot extends TelegramLongPollingBot {

    private static final String CMD_INFO = 'info'
    private static final String CMD_MISSIONS = 'missions'
    private static final String CMD_SETTINGS = 'settings'
    private static final String CMD_PREV = 'previous'
    private static final String CMD_NEXT = 'next'
    private static final String CMD_MAP = 'map'
    private static final String CMD_DESC = 'description'
    private static final String CMD_PREREG = 'preregister'

    private static final String UI_LANG_EN = 'English'
    private static final String UI_LANG_UK = 'Українська'
    private static final String UI_LANG_RU = 'Русский'

    private static final Map<Language, String> UI_TITLEs = [
            (EN): "*Hello, {name}!*",
            (UK): "*Добридень, {name}!*",
            (RU): "*Привет, {name}!*"
    ]

    private static final Map<Language, String> UI_INFOs = [(EN): UI_INFO_EN, (UK): UI_INFO_UK, (RU): UI_INFO_RU]
    private static final String UI_INFO_EN = """
Welcome to official Mission Day Dnipro Telegram Bot.

We all eagerly waited for this moment and it happend!
We are pleased to invite our guests on August 11 to Mission Day in glorious city on the Dnipro river which is located in the heart of Ukraine.❤️

Route was thought out to the last detail and will include a walk along the city center, visit to three park areas, acquaintance with the longest embankment of Europe and the Monastery Island with an artificial waterfall.

We will be very glad to see everyone who wants to explore Dnipro with us and invite to other pages dedicated to the event:

[Information channel Telegram](https://t.me/md_dp)
[Pre-registration](https://docs.google.com/forms/d/e/1FAIpQLSdKGCuKazJ3oWGG3ylALGI5Ydn1uar3Rbyv-36Dn-BTq23uZw/viewform)
[Chat No flood](https://t.me/joinchat/A8VHTw_O1k159V8k_vNIgQ)
[Chat Flood](https://t.me/joinchat/A8VHT0juMdLxvRddTn1v5A)
[Instagram](https://www.instagram.com/missionday.dnipro/)"""
    private static final String UI_INFO_UK = """
Ласкаво просимо до офіційного телеграм бота Mission Day Dnipro.

Ми всі з нетерпінням чекали на цей момент - і ось він настав! З радістю запрошуємо вас 11 серпня на День Місій у славному місті на річці Дніпро в самому серці України❤️

Маршрут продумано до дрібниць: прогулянка центром міста, відвідування трьох паркових зон, ознайомлення з найдовшою набережною в Європі та Монастирським островом з штучним водоспадом.

Ми з нетерпінням чекаємо на кожного, хто хотів би з нами досліджувати Дніпро, та запрошуємо до інших присвячених події сторінок:

[Інформаційний канал Telegram](https://t.me/md_dp)
[Форма пре-реєстрації](https://docs.google.com/forms/d/e/1FAIpQLSdKGCuKazJ3oWGG3ylALGI5Ydn1uar3Rbyv-36Dn-BTq23uZw/viewform)
[Чат NF](https://t.me/joinchat/A8VHTw_O1k159V8k_vNIgQ)
[Чат Флуд](https://t.me/joinchat/A8VHT0juMdLxvRddTn1v5A)
[Instagram](https://www.instagram.com/missionday.dnipro/)"""
    private static final String UI_INFO_RU = """
Добро пожаловать в официальный телеграм бот Mission Day Dnipro.

Мы все с нетерпением ждали этого момента - и вот, он настал! С радостью приглашаем вас 11 августа на День Миссий в славном городе на реке Днепр в самом сердце Украины❤️

Маршрут, продуманный до мелочей, вмещающий в себя прогулку по центру города, посещение трёх парковых зон, ознакомление с самой длинной набережной Европы и Монастырским островом с искусственным водопадом.

Мы с нетерпением ждём каждого, кто желает с нами исследовать Днепр, и приглашаем на другие посвященные мероприятию страницы:

[Інформационный канал Telegram](https://t.me/md_dp)
[Форма пре-регистрации](https://docs.google.com/forms/d/e/1FAIpQLSdKGCuKazJ3oWGG3ylALGI5Ydn1uar3Rbyv-36Dn-BTq23uZw/viewform)
[Чат NF](https://t.me/joinchat/A8VHTw_O1k159V8k_vNIgQ)
[Чат Флуд](https://t.me/joinchat/A8VHT0juMdLxvRddTn1v5A)
[Instagram](https://www.instagram.com/missionday.dnipro/)"""

    private static
    final Map<Language, String> UI_SETTINGs = [(EN): 'Select your language', (UK): 'Оберіть мову', (RU): 'Выберите язык']

    private static final InlineKeyboardButton BTN_INFO_EN = new InlineKeyboardButton('Home').tap {
        it.callbackData = CMD_INFO
    }
    private static final InlineKeyboardButton BTN_MISSIONS_EN = new InlineKeyboardButton('Missions').tap {
        it.callbackData = CMD_MISSIONS
    }
    private static final InlineKeyboardButton BTN_SETTINGS_EN = new InlineKeyboardButton('Settings').tap {
        it.callbackData = CMD_SETTINGS
    }
    private static final InlineKeyboardButton BTN_INFO_UK = new InlineKeyboardButton('Додому').tap {
        it.callbackData = CMD_INFO
    }
    private static final InlineKeyboardButton BTN_MISSIONS_UK = new InlineKeyboardButton('Міссії').tap {
        it.callbackData = CMD_MISSIONS
    }
    private static final InlineKeyboardButton BTN_SETTINGS_UK = new InlineKeyboardButton('Налаштування').tap {
        it.callbackData = CMD_SETTINGS
    }
    private static final InlineKeyboardButton BTN_INFO_RU = new InlineKeyboardButton('Домой').tap {
        it.callbackData = CMD_INFO
    }
    private static final InlineKeyboardButton BTN_MISSIONS_RU = new InlineKeyboardButton('Миссии').tap {
        it.callbackData = CMD_MISSIONS
    }
    private static final InlineKeyboardButton BTN_SETTINGS_RU = new InlineKeyboardButton('Настройки').tap {
        it.callbackData = CMD_SETTINGS
    }
    private static final InlineKeyboardButton BTN_PREREG_EN = new InlineKeyboardButton('Pre-register').tap {
        it.callbackData = CMD_PREREG
    }
    private static final InlineKeyboardButton BTN_PREREG_UK = new InlineKeyboardButton('Попередня реєстрація').tap {
        it.callbackData = CMD_PREREG
    }
    private static final InlineKeyboardButton BTN_PREREG_RU = new InlineKeyboardButton('Предварительная регистрация').tap {
        it.callbackData = CMD_PREREG
    }

    private static final Map<Language, InlineKeyboardMarkup> KEYBOARD_INFOs = [
            (EN): new InlineKeyboardMarkup().tap { it.keyboard = [[BTN_INFO_EN, BTN_MISSIONS_EN], [BTN_PREREG_EN], [BTN_SETTINGS_EN]] },
            (UK): new InlineKeyboardMarkup().tap { it.keyboard = [[BTN_INFO_UK, BTN_MISSIONS_UK], [BTN_PREREG_UK], [BTN_SETTINGS_UK]] },
            (RU): new InlineKeyboardMarkup().tap { it.keyboard = [[BTN_INFO_RU, BTN_MISSIONS_RU], [BTN_PREREG_RU], [BTN_SETTINGS_RU]] },
    ]

    private static final InlineKeyboardButton BTN_PREV = new InlineKeyboardButton('<').tap {
        it.callbackData = CMD_PREV
    }
    private static final InlineKeyboardButton BTN_MAP_EN = new InlineKeyboardButton('Portals').tap {
        it.callbackData = CMD_MAP
    }
    private static final InlineKeyboardButton BTN_MAP_UK = new InlineKeyboardButton('Портали').tap {
        it.callbackData = CMD_MAP
    }
    private static final InlineKeyboardButton BTN_MAP_RU = new InlineKeyboardButton('Порталы').tap {
        it.callbackData = CMD_MAP
    }
    private static final InlineKeyboardButton BTN_DESC_EN = new InlineKeyboardButton('Description').tap {
        it.callbackData = CMD_DESC
    }
    private static final InlineKeyboardButton BTN_DESC_UK = new InlineKeyboardButton('Опис').tap {
        it.callbackData = CMD_DESC
    }
    private static final InlineKeyboardButton BTN_DESC_RU = new InlineKeyboardButton('Описание').tap {
        it.callbackData = CMD_DESC
    }
    private static final InlineKeyboardButton BTN_NEXT = new InlineKeyboardButton('>').tap {
        it.callbackData = CMD_NEXT
    }

    private static final Map<Language, InlineKeyboardMarkup> KEYBOARD_MISSIONs = [
            (EN): new InlineKeyboardMarkup().tap {
                it.keyboard = [[BTN_PREV, BTN_INFO_EN, BTN_NEXT], [BTN_DESC_EN, BTN_MAP_EN]]
            },
            (UK): new InlineKeyboardMarkup().tap {
                it.keyboard = [[BTN_PREV, BTN_INFO_UK, BTN_NEXT], [BTN_DESC_UK, BTN_MAP_UK]]
            },
            (RU): new InlineKeyboardMarkup().tap {
                it.keyboard = [[BTN_PREV, BTN_INFO_RU, BTN_NEXT], [BTN_DESC_RU, BTN_MAP_RU]]
            },
    ]

    private static final InlineKeyboardMarkup KEYBOARD_LANGUAGES = new InlineKeyboardMarkup().tap {
        it.keyboard =
                [[
                         new InlineKeyboardButton(UI_LANG_EN).tap { it.callbackData = EN.toString() },
                         new InlineKeyboardButton(UI_LANG_UK).tap { it.callbackData = UK.toString() },
                         new InlineKeyboardButton(UI_LANG_RU).tap { it.callbackData = RU.toString() }
                 ]]
    }

    private ChronicleMap<CharSequence, Language> store_languages
    private ChronicleMap<CharSequence, List<Integer>> store_messages
    private ChronicleMap<CharSequence, Integer> store_current_mission
    private List<MissionData> missions

    MDDniproBot(String botName, String botToken, String mdEventId, boolean mdTestEvent) {
        // TODO: Configure options, e.g. multithreading
        super()

        this.botName = botName
        this.botToken = botToken
        this.mdEventId = mdEventId
        this.mdTestEvent = mdTestEvent

        store_languages = ChronicleMapBuilder.of(CharSequence.class, Language.class)
                .name("store_languages")
                .averageKey("AAA")
                .entries(5000)
                .averageValueSize(10)
                .createOrRecoverPersistedTo(new File('store_languages'))

        store_messages = ChronicleMapBuilder.of(CharSequence.class, List.class)
                .name("store_messages")
                .averageKey("AAA")
                .entries(5000)
                .averageValueSize(20)
                .createOrRecoverPersistedTo(new File('store_messages'))

        store_current_mission = ChronicleMapBuilder.of(CharSequence.class, Integer.class)
                .name("store_current_mission")
                .averageKey("AAA")
                .entries(5000)
                .createOrRecoverPersistedTo(new File('store_current_mission'))

        println store_languages
        println store_messages
        println store_current_mission

        // Read mission data
        missions = MissionData.readMissions(new File('./missions/'))
        //println missions
    }

    @Override
    void onUpdateReceived(Update update) {
        // println update
        // println '--'
        if (update.message) {
            // What's this?
            // Starting a new conversation
            sendInfo(update.message.chatId, update.message.from)
        } else if (update.callbackQuery) {
            // Inline keyboard pressed
            def query = update.callbackQuery
            switch (query.data) {
                case CMD_INFO:
                    sendInfo(query.message.chatId, query.from)
                    break
                case CMD_SETTINGS:
                    replySettings(query)
                    break
                case EN.toString():
                case UK.toString():
                case RU.toString():
                    replyLanguage(query)
                    break
                case CMD_MISSIONS:
                case CMD_DESC:
                    replyMissionDescription(query)
                    break
                case CMD_PREV:
                    replyMissionPrev(query)
                    break
                case CMD_NEXT:
                    replyMissionNext(query)
                    break
                case CMD_MAP:
                    replyMissionMap(query)
                    break
                case CMD_PREREG:
                    replyPrereg(query)
                    break
            }
        }
    }

    // v3
    private void deletePastMessages(Long chatId, User user) {
        def userName = user.userName

        // Trying to delete past conversations
        store_messages[userName]?.each { Integer messageId ->
            try {
                dniproSendMessage new DeleteMessage(chatId, messageId)
            } catch (ignored) {
                // noone cares
            }
        }
        store_messages.remove(userName)
    }

    // v3
    private void replySettings(CallbackQuery query) {
        def reply = new EditMessageText()
        reply.chatId = query.message.chatId
        reply.messageId = query.message.messageId

        Language language = detectUserLanguage(query.from)
        reply.text = UI_SETTINGs[language]
        reply.replyMarkup = KEYBOARD_LANGUAGES

        dniproSendMessage reply
    }

    // v3
    private void replyLanguage(CallbackQuery query) {
        Language language = Language.valueOf(query.data)
        store_languages.put(query.from.userName, language)

        sendInfo(query.message.chatId, query.from)
    }

    // v3
    private void sendInfo(Long chatId, User user) {
        // cleanup first in v3
        deletePastMessages(chatId, user)

        Language language = detectUserLanguage(user)

        Message infoMessage = dniproSendMessage(new SendMessage().tap {
            it.chatId = chatId
            it.parseMode = 'Markdown'
            it.text = "*${UI_TITLEs[language].replace('{name}', user.firstName)}*\n${UI_INFOs[language]}"
            it.replyMarkup = KEYBOARD_INFOs[language]
        })

        store_messages.put(user.userName, [infoMessage.messageId])
    }

    private void replyNotImplemented(CallbackQuery query) {
        def reply = new EditMessageText()
        reply.chatId = query.message.chatId
        reply.messageId = query.message.messageId

        def language = detectUserLanguage(query.from)
        reply.text = 'Not implemented ' + Math.random()
        reply.replyMarkup = KEYBOARD_INFOs[language]

        dniproSendMessage reply
    }

    /**
     * v3
     * For English - only mission number and title,
     * For other languages - additionally title in local language on a new line.
     * Both bold
     */
    private String missionTitleMarkdown(MissionData mission, Language language) {
        "*${mission.number}) ${mission.title[EN]}*" +
                (language != EN ? "\n*${mission.title[language]}*" : '')
    }

    // v3
    private void replyMissionDescription(CallbackQuery query) {
        Long chatId = query.message.chatId
        User user = query.from
        Language language = detectUserLanguage(user)

        deletePastMessages(chatId, user)

        int missionNumber = store_current_mission[query.from.userName] ?: 1
        MissionData mission = missions.get(missionNumber - 1)

        Message photoMessage = dniproSendPhoto(chatId, mission.photo)
        def mainMessage = dniproSendMessage(new SendMessage().tap {
            it.chatId = query.message.chatId
            it.parseMode = 'Markdown'
            it.text = missionTitleMarkdown(mission, language) +
                    "\n\n${mission.description[language]}"
            it.replyMarkup = KEYBOARD_MISSIONs[language]
        })

        store_messages.put(user.userName, [photoMessage.messageId, mainMessage.messageId])
        store_current_mission.put(user.userName, missionNumber)
    }

    // v3
    private void replyMissionMap(CallbackQuery query) {
        Long chatId = query.message.chatId
        User user = query.from
        Language language = detectUserLanguage(user)

        deletePastMessages(chatId, user)

        int missionNumber = store_current_mission[query.from.userName] ?: 1
        MissionData mission = missions.get(missionNumber - 1)

        def mapMessage = dniproSendPhoto(chatId, mission.map)
        def mainMessage = dniproSendMessage(new SendMessage().tap {
            it.chatId = query.message.chatId
            it.parseMode = 'Markdown'
            it.text = missionTitleMarkdown(mission, language) +
                    "\n\n" +
                    mission.portals.collect {
                        "${it.portalOrder}) [${it.portalName}](${it.googleMapLink()})"
                    }.join("\n")
            it.replyMarkup = KEYBOARD_MISSIONs[language]
        })

        store_messages.put(user.userName, [mapMessage.messageId, mainMessage.messageId])
        store_current_mission.put(user.userName, missionNumber)
    }

    // v3
    private void replyMissionPrev(CallbackQuery query) {
        User user = query.from

        int missionNumber = store_current_mission[user.userName] ?: 1
        if (missionNumber > 1) missionNumber -= 1
        else missionNumber = missions.size()
        store_current_mission.put(user.userName, missionNumber)

        replyMissionDescription(query)
    }

    // v3
    private void replyMissionNext(CallbackQuery query) {
        User user = query.from

        int missionNumber = store_current_mission[user.userName] ?: 1
        if (missionNumber < missions.size()) missionNumber += 1
        else missionNumber = 1
        store_current_mission.put(user.userName, missionNumber)

        replyMissionDescription(query)
    }

    private replyPrereg(CallbackQuery query) {
        Long chatId = query.message.chatId
        User user = query.from
        Language language = detectUserLanguage(user)

        deletePastMessages(chatId, user)

        Message photoMessage = sendPhoto generateQRcode(query.message.chatId, query.from.userName)
        def mainMessage = dniproSendMessage(new SendMessage().tap {
            it.chatId = query.message.chatId
            it.parseMode = 'Markdown'
            it.text = 'Show this QR Code to the orgs. Note - you can access it any time in the Pre-registration menu'
            it.replyMarkup = KEYBOARD_INFOs[language]
        })

        store_messages.put(user.userName, [photoMessage.messageId, mainMessage.messageId])

        sendPhoto generateQRcode(query.message.chatId, query.from.userName)
    }

    private SendPhoto generateQRcode(long chatId, String userName) {
        SendPhoto qrcode = new SendPhoto()
        qrcode.chatId = chatId

        String userId = '12345'
        String qrcodeMessage = "IngressMdRegistrar:${mdEventId}:${userName}:${userId}"
        qrcodeMessage = qrcodeMessage + qrcodeMessage.digest('SHA-256')
        QRCode code = QRCode.from(qrcodeMessage).withSize(400, 400)
        qrcode.setNewPhoto("Reg ${userName}", new ByteArrayInputStream(code.stream().toByteArray()))

        qrcode
    }


    private Language detectUserLanguage(User user) {
        // Try to read from settings
        Language language = store_languages[user.userName]

        if (language == null) {
            // Check Telegram then
            def langString = user.languageCode
            if (langString && langString.contains('-')) langString = langString.split('-')[0]

            // Russian by default
            try {
                language = Language.valueOf(langString.toUpperCase())
            } catch (ignored) {
                language = RU
            }
        }
        store_languages.put(user.userName, language)

        language
    }

    /** v4 File -> Telegram Photo ID cache */
    private Map<String, String> photoCache = [:]

    /**
     * v4
     * Sends photo message and caches the photo ID not to resend it every time.
     * The cache is valid only while the program is running, so in order to send new files,
     * Bot should be restarted.
     *
     * @param chatId Chat to send the photo to
     * @param photoFile File to send
     * @return Sent message
     */
    private Message dniproSendPhoto(long chatId, File photoFile) {
        waitABit(chatId.toString())

        def photoMessage = sendPhoto(new SendPhoto().tap {
            it.chatId = chatId
            if (photoCache.containsKey(photoFile.absolutePath)) {
                println "DEBUG: Serving $photoFile from Telegram's cache"
                it.photo = photoCache.get(photoFile.absolutePath)
            }
            else {
                it.newPhoto = photoFile
                println "DEBUG: Not cached $photoFile yet"
            }
        })
        if (photoMessage.photo) {
            // caching...
            photoCache.put(photoFile.absolutePath, photoMessage.photo[0].fileId)
        }
        photoMessage
    }

    /**
     * v4
     * Method protected from org.telegram.telegrambots.exceptions.TelegramApiRequestException:
     * Error sending message: [429] Too Many Requests: retry after SECONDS
     *
     * Second, it retries after the number of seconds specified by Telegram in the answer.
     *
     * @param message Message to send
     * @return Telegram's response
     */
    protected <T extends Serializable, Method extends BotApiMethod<T>> T dniproSendMessage(Method message) {
        try {
            waitABit(message.chatId)

            return sendApiMethod(message)
        } catch(TelegramApiRequestException tare) {
            /////////////////////////////////////////////////
            // 2 - try to be nice and handle 'Too Many Requests' Telegram error
            if (tare.errorCode == 429) {
                println "ERROR: Got DDOS warning from Telegram:\n${tare}"
                int retryAfterSeconds = tare.parameters?.retryAfter ?: 5
                sleep(1000 * retryAfterSeconds)
                return sendApiMethod(message)
            }
        }
    }

    /** v4 stores times of last 3 messages sent to a single user (single chat id) */
    private Map<String, Queue<Long>> timesCache = [:]
    private static int LIMIT = 3
    /**
     * It will try to limit the number of messages sent to the same user
     * (same chat id) to up to 3 messages per second.
     * Note that Telegram has a limit of up to 1 message to the same chat per second,
     * but allows to burst a bit temporarily.
     * https://core.telegram.org/bots/faq#my-bot-is-hitting-limits-how-do-i-avoid-this
     * When sending messages inside a particular chat, avoid sending more than one message per second.
     * We may allow short bursts that go over this limit, but eventually you'll begin receiving 429 errors.
     *
     * @param chatId
     */
    private void waitABit(String chatId) {
        // 1 - limit # of messages per chat per second
        Queue<Long> lastTimes = timesCache.get(chatId)
        if (lastTimes == null) {
            lastTimes = new ArrayBlockingQueue<Long>(5)
            timesCache.put(chatId, lastTimes)
        }
        if (lastTimes.size() == LIMIT) {
            long longAgo = lastTimes.remove()
            long delta = System.currentTimeMillis() - longAgo
            // if the first of previous messages happened less than 1 second ago
            // then we wait for LIMIT seconds :)
            if (delta < 1000) {
                println "WARNING: Suspecting DDOS in chat $chatId"
                sleep(LIMIT * 1000)
            }
        }
        lastTimes.add(System.currentTimeMillis())
    }

    /* ****************************************** */
    private String botName
    private String botToken
    private String mdEventId
    private boolean mdTestEvent

    @Override
    String getBotUsername() {
        botName
    }

    @Override
    String getBotToken() {
        botToken
    }

    /**
     * v4
     * The same class starts itself
     *
     * @param args 3-4 Arguments "Telegram Bot Name" "Telegram Bot Token" "MD Registrar Event ID" "true if this MD Registrar event is considered a test one"
     */
    static void main(String... args) {
        println 'Telegram Bot for Ingress Mission Day to be held 11 August 2018 in Dnipro, Ukraine.'
        println 'Maintained by @mihmax'
        println 'Version 4'
        println "Today is ${new Date()}"
        println ''

        if (! (args.length in [3,4])) {
            println 'ERROR: REQUIRES 3 or 4 ARGUMENTS'
            println 'Usage: java -jar mdbot.jar "Telegram Bot Name" "Telegram Bot Token" "MD Registrar Event ID" "true if this MD Registrar event is considered a test one"'
            println ''
            System.exit -1
        }

        String botName = args[0]
        String botToken = args[1]
        String mdEventId = args[2]
        boolean mdTestEvent = args.length == 4 ? args[3] == 'true' : false

        ApiContextInitializer.init()
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi()
        try {
            telegramBotsApi.registerBot(new MDDniproBot(botName, botToken, mdEventId, mdTestEvent))
        } catch (TelegramApiException e) {
            e.printStackTrace()
        }
    }

}
