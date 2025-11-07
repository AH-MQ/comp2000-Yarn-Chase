import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WeatherStreamClient {
    private List<WeatherObserver> observers = new ArrayList<>();
    private volatile boolean running = false;

    public void addObserver(WeatherObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(WeatherObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(WeatherData data) {
        // Using streams and lambdas to notify all observers
        observers.stream().forEach(observer -> observer.onWeatherUpdate(data));
    }

    public void startStreaming() {
        if (running) return;
        running = true;

        CompletableFuture.runAsync(() -> {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://13.238.167.130/weather"))
                    .header("Accept", "text/event-stream")
                    .build();

            try {
                client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                        .thenApply(HttpResponse::body)
                        .thenAccept(inputStream -> {
                            try (BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                                // Using Streams API with lambda to process each line
                                reader.lines()
                                        .filter(line -> !line.trim().isEmpty()) // Filter out empty lines
                                        .map(this::parseLine) // Map to WeatherData objects
                                        .filter(data -> data != null) // Filter out invalid data
                                        .forEach(this::notifyObservers); // Notify observers
                            } catch (Exception e) {
                                System.err.println("Error reading weather stream: " + e.getMessage());
                            }
                        })
                        .join();
            } catch (Exception e) {
                System.err.println("Error connecting to weather server: " + e.getMessage());
            }
        });
    }

    private WeatherData parseLine(String line) {
        try {
            String[] parts = line.trim().split("\\s+");
            if (parts.length == 5) {
                long timestamp = Long.parseLong(parts[0]);
                String attribute = parts[1];
                int x = Integer.parseInt(parts[2]);
                int y = Integer.parseInt(parts[3]);
                double value = Double.parseDouble(parts[4]);
                return new WeatherData(timestamp, attribute, x, y, value);
            }
        } catch (Exception e) {
            System.err.println("Error parsing weather line: " + line);
        }
        return null;
    }

    public void stop() {
        running = false;
    }
}