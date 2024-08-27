package com.announce.AcknowledgeHub_SpringBoot;

import com.announce.AcknowledgeHub_SpringBoot.repository.AnnouncementReadStatusRepository;
import com.announce.AcknowledgeHub_SpringBoot.repository.AnnouncementRepository;
import com.announce.AcknowledgeHub_SpringBoot.repository.UserRepository;
import com.announce.AcknowledgeHub_SpringBoot.service.AnnouncementBotService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class AcknowledgeHubSpringBootApplication {

	private static final String BOT_TOKEN = "7099519419:AAHrVfHrwdDCSxK4gTyZVUhDDXhXGhgr8tA";
	private static final String BOT_USERNAME = "AnnouncementBot";
	private final UserRepository userRepository;
	private final AnnouncementRepository announcementRepository;
	private final AnnouncementReadStatusRepository announcementReadStatusRepository;

    public AcknowledgeHubSpringBootApplication(UserRepository userRepository, AnnouncementRepository announcementRepository, AnnouncementReadStatusRepository announcementReadStatusRepository) {
        this.userRepository = userRepository;
        this.announcementRepository = announcementRepository;
        this.announcementReadStatusRepository = announcementReadStatusRepository;
    }

    public static void main(String[] args) {
		SpringApplication.run(AcknowledgeHubSpringBootApplication.class, args);
	}

	@Bean
	public AnnouncementBotService announcementBotService() throws TelegramApiException {
		// Initialize the Telegram Bots API
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

		// Create an instance of your bot service
		AnnouncementBotService bot = new AnnouncementBotService(BOT_TOKEN, BOT_USERNAME, userRepository, announcementRepository, announcementReadStatusRepository);

		// Register the bot with the Telegram Bots API
		telegramBotsApi.registerBot(bot);

		// Return the bot service instance
		return bot;
	}

}
