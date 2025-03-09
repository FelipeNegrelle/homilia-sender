package br.com.felipenegrelle;

import br.com.laider.utils.Logger;

public class Main {

    private static void printArgs() {
        System.out.println("Argumentos: ");
        System.out.println("\t-help");
        System.out.println("\t-send-homilia <recipient> properties.json");
        System.out.println("\t-send-audio <recipient> properties.json");
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            printArgs();
            return;
        }

        try {
            String command = args[0];
            String recipient = args[1];
            String propertiesPath = args[2];

            Properties properties = Properties.load(propertiesPath);

            if (properties == null) {
                System.out.println("Falha ao carregar as propriedades.");
                return;
            }

            switch (command) {
                case "-send-homilia":
                    MessageService.sendVideoLink(recipient, properties.getYoutubeApiKey(), properties.getChannelId(), properties.getSenderNumber());
                    break;

                case "-send-audio":
                    MessageService.sendAudio(recipient, properties.getYoutubeApiKey(), properties.getChannelId(), properties.isProduction() ? properties.getBasePath() : properties.getBasePathTest(), properties.getSenderNumber());
                    break;

                default:
                    printArgs();
                    break;
            }
        } catch (Exception e) {
            Logger.error(e);
        }
    }
}