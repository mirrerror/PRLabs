package md.mirrerror.config;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.*;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class WebSocketHandler extends TextWebSocketHandler {

    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final String FILE_PATH = "shared_file.txt";

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        System.out.println("Received: " + payload);
        processMessage(payload, session);
    }

    private void processMessage(String message, WebSocketSession session) throws IOException {
        Random random = new Random();
        int sleepTime = 1000 + random.nextInt(7000);

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (message.startsWith("write")) {
            String data = message.substring(6).trim();
            if (data.isEmpty()) {
                session.sendMessage(new TextMessage("Error: No data provided for write command"));
            } else {
                writeFile(data);
                session.sendMessage(new TextMessage("Write operation completed"));
            }
        } else if (message.startsWith("read")) {
            String content = readFile();
            session.sendMessage(new TextMessage("Read operation completed: " + content));
        } else if (message.startsWith("delete")) {
            deleteFile();
            session.sendMessage(new TextMessage("Delete operation completed"));
        } else if (message.startsWith("append")) {
            String data = message.substring(7).trim();
            if (data.isEmpty()) {
                session.sendMessage(new TextMessage("Error: No data provided for append command"));
            } else {
                appendToFile(data);
                session.sendMessage(new TextMessage("Append operation completed"));
            }
        } else {
            session.sendMessage(new TextMessage("Unknown command"));
        }
    }

    private void writeFile(String data) {
        lock.writeLock().lock();
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(data);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void appendToFile(String data) {
        lock.writeLock().lock();
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(data);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private String readFile() {
        lock.readLock().lock();
        StringBuilder content = new StringBuilder();
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
        return content.toString();
    }

    private void deleteFile() {
        lock.writeLock().lock();
        try {
            File file = new File(FILE_PATH);
            if (file.exists()) {
                file.delete();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Connection closed: " + status);
    }
}