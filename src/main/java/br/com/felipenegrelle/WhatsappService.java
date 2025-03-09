package br.com.felipenegrelle;

import br.com.laider.utils.Logger;
import br.com.laider.utils.TermColors;
import it.auties.whatsapp.api.PairingCodeHandler;
import it.auties.whatsapp.api.TextPreviewSetting;
import it.auties.whatsapp.api.WebHistorySetting;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.listener.RegisterListener;
import it.auties.whatsapp.model.jid.Jid;
import it.auties.whatsapp.model.jid.JidProvider;
import it.auties.whatsapp.model.message.standard.AudioMessage;
import it.auties.whatsapp.model.message.standard.AudioMessageBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@RegisterListener
public class WhatsappService {
    private final Whatsapp api;
    private CompletableFuture<Void> connectionFuture;

    public WhatsappService(String senderNumber) {
        this.api = Whatsapp.webBuilder().newConnection("homilia").name("HomiliaSender").autodetectListeners(true).textPreviewSetting(TextPreviewSetting.ENABLED_WITH_INFERENCE).historySetting(WebHistorySetting.standard(false)).unregistered(Long.parseLong(senderNumber), PairingCodeHandler.toTerminal()).addLoggedInListener(api -> {
            Logger.info(String.format("Conectado: %s%n", api.store().jid()));

            synchronized (this) {
                this.notifyAll();
            }
        }).addDisconnectedListener(reason -> Logger.info(String.format(TermColors.getTextRed("Desconectado: %s%n"), reason)));
    }

    public CompletableFuture<Void> setup() {
        if (connectionFuture == null) {
            connectionFuture = new CompletableFuture<>();

            api.connect().thenRun(() -> {
                Logger.info(TermColors.getTextGreen("Conectando ao WhatsApp..."));
                connectionFuture.complete(null);
            }).exceptionally(throwable -> {
                connectionFuture.completeExceptionally(throwable);
                return null;
            });
        }
        return connectionFuture;
    }

    public CompletableFuture<Void> sendMessage(String recipient, String text) {
        return setup().thenCompose(v -> {
            synchronized (this) {
                try {
                    while (!api.isConnected()) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return CompletableFuture.failedFuture(e);
                }
            }

            JidProvider jid = Jid.of(recipient + "@s.whatsapp.net");

            return api.sendMessage(jid, text).thenRun(() -> Logger.info(TermColors.getTextCyan("Mensagem enviada com sucesso!"))).exceptionally(throwable -> {
                Logger.error("Erro ao enviar mensagem: " + throwable.getMessage());
                return null;
            });
        });
    }

    public CompletableFuture<Void> sendAudioMessage(String recipient, String audioPath) {
        return setup().thenCompose(v -> {
            synchronized (this) {
                try {
                    while (!api.isConnected()) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return CompletableFuture.failedFuture(e);
                }
            }
            JidProvider jid = Jid.of(recipient + "@s.whatsapp.net");

            AudioMessage audio;
            try {
                audio = new AudioMessageBuilder().mediaDirectPath(audioPath).voiceMessage(false).build().setDecodedMedia(Files.readAllBytes(Path.of(audioPath)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return api.sendMessage(jid, audio).thenRun(() -> Logger.info(TermColors.getTextCyan("Mensagem enviada com sucesso!"))).exceptionally(throwable -> {
                Logger.error(TermColors.getTextRed("Erro ao enviar mensagem: " + throwable.getMessage()));
                return null;
            });
        });
    }
}