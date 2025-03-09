package br.com.felipenegrelle;

import br.com.laider.utils.Logger;

public class MessageService {

    public static void sendMessage(String recipient, String message, String senderNumber) {
        try {
            if (br.com.laider.utils.Text.isNotEmpty(recipient) && br.com.laider.utils.Text.isNotEmpty(message)) {
                Logger.info("Enviando mensagem para: " + recipient);

                WhatsappService zap = new WhatsappService(senderNumber);

                zap.setup().thenCompose(v -> zap.sendMessage(recipient, message))
                        .thenRun(() -> Logger.info("Mensagem enviada com sucesso!"))
                        .exceptionally(throwable -> {
                            Logger.error("Falha ao enviar mensagem: " + throwable.getMessage());
                            return null;
                        }).join();
            } else {
                Logger.error("O destinatário ou a mensagem não podem ser nulos.");
            }
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    public static void sendAudioMessage(String recipient, String audioPath, String senderNumber) {
        try {
            if (br.com.laider.utils.Text.isNotEmpty(recipient) && br.com.laider.utils.Text.isNotEmpty(audioPath)) {
                Logger.info("Enviando áudio para: " + recipient);

                WhatsappService zap = new WhatsappService(senderNumber);

                zap.setup().thenCompose(v -> zap.sendAudioMessage(recipient, audioPath))
                        .thenRun(() -> Logger.info("Mensagem enviada com sucesso!"))
                        .exceptionally(throwable -> {
                            Logger.error("Falha ao enviar mensagem: " + throwable.getMessage());
                            return null;
                        }).join();
            } else {
                Logger.error("O destinatário ou o caminho do áudio não podem ser nulos.");
            }
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    public static void sendVideoLink(String recipient, String apiKey, String channelId, String senderNumber) {
        String videoUrl = YouTubeFetcher.getLatestVideoUrl(apiKey, channelId);

        if (videoUrl != null) {
            Logger.info("Enviando link: " + videoUrl);

            sendMessage(recipient, videoUrl, senderNumber);
        } else {
            Logger.error("Nenhum vídeo encontrado.");
        }
    }

    public static void sendAudio(String recipient, String apiKey, String channelId, String basePath, String senderNumber) {
        String videoUrl = YouTubeFetcher.getLatestVideoUrl(apiKey, channelId);

        if (videoUrl != null) {
            Logger.info("Baixando áudio do vídeo: " + videoUrl);

            String audioPath = YouTubeFetcher.downloadAudio(videoUrl, basePath);

            if (audioPath != null) {
                Logger.info("Enviando áudio para " + recipient);

                sendAudioMessage(recipient, audioPath, senderNumber);
            } else {
                Logger.error("Erro ao baixar áudio.");
            }
        } else {
            Logger.error("Nenhum vídeo encontrado.");
        }
    }
}