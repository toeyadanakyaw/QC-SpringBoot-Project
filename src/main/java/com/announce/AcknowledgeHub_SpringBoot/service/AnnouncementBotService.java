package com.announce.AcknowledgeHub_SpringBoot.service;

import com.announce.AcknowledgeHub_SpringBoot.entity.Announcement;
import com.announce.AcknowledgeHub_SpringBoot.entity.AnnouncementReadStatus;
import com.announce.AcknowledgeHub_SpringBoot.entity.User;
import com.announce.AcknowledgeHub_SpringBoot.repository.AnnouncementReadStatusRepository;
import com.announce.AcknowledgeHub_SpringBoot.repository.AnnouncementRepository;
import com.announce.AcknowledgeHub_SpringBoot.repository.UserRepository;
import org.jvnet.hk2.annotations.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnnouncementBotService extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;
    private final UserRepository staffRepository;
    private final AnnouncementRepository announcementRepository;
    private final AnnouncementReadStatusRepository announcementReadStatusRepository;

    // Assuming you use a Map to track read status (for simplicity)
    private final Map<String, Boolean> announcementReadStatus = new HashMap<>();

    private final Map<Integer, Integer> messageStore = new HashMap<>();

    // Store the messageId associated with a chatId
    public void storeMessageId(Integer chatId, Integer messageId) {
        messageStore.put(chatId, messageId);
    }

    // Retrieve the messageId associated with a chatId
    public Integer retrieveMessageId(Integer chatId) {
        return messageStore.get(chatId);
    }

    public AnnouncementBotService(String botToken, String botUsername, UserRepository staffRepository, AnnouncementRepository announcementRepository, AnnouncementReadStatusRepository announcementReadStatusRepository) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.staffRepository = staffRepository;
        this.announcementRepository = announcementRepository;
        this.announcementReadStatusRepository = announcementReadStatusRepository;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }


    @Override
    public void onUpdateReceived(Update update) {
        // Handle text messages (e.g., /start command)
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = String.valueOf(update.getMessage().getChatId());
            String userName = update.getMessage().getFrom().getUserName();
            Long telegramUserId = update.getMessage().getFrom().getId();

            System.out.println("mt: " + messageText + " cid: " + chatId + " uname: " + userName + " tid: " + telegramUserId);

            if (messageText.equals("/start")) {
                // Display bot information
                String botInfo = "🤖 *Welcome to the Announcement Bot!*\n\n" +
                        "This bot allows you to receive important announcements and notifications directly through Telegram.\n\n" +
                        "*How to Use:*\n" +
                        "- To register, use the command `/register<your_registration_code>`. For example, `/registerXXXXXX`.\n" +
                        "- After registration, you will receive announcements from your organization.\n" +
                        "- You can mark announcements as read by clicking the 'Mark as Read' button under each announcement.\n\n" +
                        "*Available Commands:*\n" +
                        "- `/start`: Display this information.\n" +
                        "- `/register<code>`: Register with your unique registration code.\n\n" +
                        "*Features:*\n" +
                        "- Receive announcements with attached PDF documents.\n" +
                        "- Track read status for each announcement.\n" +
                        "- Comment on announcements (coming soon!).\n\n" +
                        "If you have any questions, contact your system administrator.\n\n" +
                        "Enjoy using the bot!";

                sendMessage(chatId, botInfo);
            } else if (messageText.equals("/register")) {
                // Prompt for registration code
                sendMessage(chatId, "Please provide your registration code in the format: `/register<code>`. Example: `/register123456`.");
            } else if (messageText.startsWith("/register")) {
                // Extract registration code
                String registrationCode = messageText.substring(9).trim(); // Extract registration code

                // Check if the code is valid
                if (registrationCode.length() == 6) { // assuming registration code is 6 digits
                    User staff = staffRepository.findByRegistrationCode(registrationCode);
                    if (staff != null) {
                        // Check if the user is already registered
                        if (staff.getTelegram_user_id() != null) {
                            sendMessage(chatId, "You are already registered.");
                        } else {
                            staff.setTelegram_user_id(telegramUserId);
                            staff.setTelegram_user_name(userName); // Save the username
                            staffRepository.save(staff);
                            sendMessage(chatId, "Registration successful! Your Telegram ID has been saved.");
                        }
                    } else {
                        sendMessage(chatId, "Invalid registration code.");
                    }
                } else {
                    sendMessage(chatId, "Invalid command format. Please use the format: `/register<code>`. Example: `/register123456`.");
                }
            } else {
                sendMessage(chatId, "This is an invalid command.");
            }
        } else if (update.hasMessage() && (update.getMessage().hasPhoto() || update.getMessage().hasDocument() || update.getMessage().hasVideo())) {
            // Handle non-text messages (photos, files, videos, etc.)
            String chatId = String.valueOf(update.getMessage().getChatId());
            sendMessage(chatId, "Invalid command. Please use the appropriate command to interact with the bot.");
        }

        // Handle callback queries (e.g., Mark as Read button)
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            // Fetch the custom User entity
            org.telegram.telegrambots.meta.api.objects.User telegramUser = update.getCallbackQuery().getFrom();
            Long tid = telegramUser.getId();
            User user = staffRepository.findByTelegramUserIdToGetUName(tid);

//            System.out.println("Received callback query: " + callbackData + " chatId: " + chatId + " user: " + user);
            handleButtonClick(callbackData, chatId, user);
        }
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void handleButtonClick(String data, Long chatId, User user) {
        System.out.println("handleButtonClick triggered with data: " + data + ", chatId: " + chatId);
        // Split the data to get the action and announcement ID
        String[] parts = data.split("_", 2);
        String action = parts[0];
        String announcementId = parts.length > 1 ? parts[1] : "";

        System.out.println("handleButtonClick triggered with action: " + action + ", announcementId: " + announcementId + ", chatId: " + chatId);
        if ("read".equals(action)) {
            boolean isNowRead = toggleMessageReadStatus(chatId, user, Integer.parseInt(announcementId));
            // Toggle button text
            sendUpdatedMessageWithButton(chatId, announcementId, isNowRead);
        }
    }

    private boolean toggleMessageReadStatus(Long chatId, User user, int announcementId) {
//        System.out.println("tmr: " + chatId + user + announcementId);
        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        // Find the announcement and the staff member
        Announcement announcement = announcementRepository.findById((long) announcementId).orElse(null);
        User staff = staffRepository.findByTelegramUserIdToGetUName(user.getTelegram_user_id());


        if (announcement != null && staff != null) {
            AnnouncementReadStatus readStatus = announcementReadStatusRepository.findByAnnouncementAndStaff(announcement, staff);
            boolean wasRead = false;  // Track previous read status
            if (readStatus == null) {
                readStatus = new AnnouncementReadStatus();
                readStatus.setAnnouncement(announcement);
                readStatus.setStaff(staff);
                readStatus.setIsRead(true); // Mark as read
            } else {
                readStatus.setIsRead(!readStatus.getIsRead()); // Toggle read status
            }

            readStatus.setReadAt(now);
            announcementReadStatusRepository.save(readStatus);

            // Prepare the response message based on the action
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(chatId));
            String announcementTitle = announcement.getTitle();

            if (readStatus.getIsRead()) {
                // If the announcement was marked as read
                sendMessage.setText("✅ Marked as read by " + staff.getName() + " (@" + staff.getTelegram_user_name() + ") at " + formattedDateTime + " for the announcement titled \"" + announcementTitle + "\".");
            } else {
                // If the announcement was unmarked
                sendMessage.setText("❗️ Unmarked as read by " + staff.getName() + " (@" + staff.getTelegram_user_name() + "). Please review the announcement titled \"" + announcementTitle + "\" again.");
            }


            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }


            return readStatus.getIsRead();
        }


        return false;
    }

    private void sendUpdatedMessageWithButton(Long chatId, String announcementId, boolean isRead) {
        String buttonText = isRead ? "✅ Marked" : "🔄 Mark as Read";
        String callbackData = "read_" + announcementId;

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton readButton = new InlineKeyboardButton();
        readButton.setText(buttonText);
        readButton.setCallbackData(callbackData);
        markup.setKeyboard(List.of(List.of(readButton)));

        // Find the message ID from the stored status
        AnnouncementReadStatus readStatus = announcementReadStatusRepository.findByAnnouncementId(Integer.valueOf(announcementId));
        if (readStatus != null) {
            Integer messageId = readStatus.getMessageId();

            // Update the message
            EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
            editMarkup.setChatId(String.valueOf(chatId));
            editMarkup.setReplyMarkup(markup);
            editMarkup.setMessageId(messageId);

            try {
                execute(editMarkup);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Message ID not found for announcementId: " + announcementId);
        }
    }

    public int sendAnnouncementWithPDF(Long chatId, Announcement announcement, byte[] pdfBytes) throws TelegramApiException {
        String title = announcement.getTitle();
        String description = announcement.getContent();
        String pdfFilename = announcement.getDocumentName();
        String message = title + "\n\n" + description;
        ByteArrayInputStream pdfStream = new ByteArrayInputStream(pdfBytes);
        InputFile inputFile = new InputFile(pdfStream, pdfFilename);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        User staff = staffRepository.findByTelegramUserIdToGetUName(chatId);
        String senderUsername = staff.getTelegram_user_name();

        String caption = "Sent by " + staff.getName() + " @" + senderUsername + " on " + formattedDateTime + "\n\n" + message;

        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(String.valueOf(chatId));
        sendDocument.setCaption(caption);
        sendDocument.setDocument(inputFile);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton readButton = new InlineKeyboardButton();
        readButton.setText("🔄 Mark as Read");
        readButton.setCallbackData("read_" + announcement.getId());
        markup.setKeyboard(List.of(List.of(readButton)));
        sendDocument.setReplyMarkup(markup);

        Message messageObj = execute(sendDocument);
        System.out.println(" Message messageObj = execute(sendDocument)"+messageObj);
        return messageObj.getMessageId();
    }
}
