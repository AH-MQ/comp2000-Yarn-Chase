import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://13.238.167.130/weather"))
                .header("Accept", "text/event-stream")
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(HttpResponse::body)
                .thenAccept(inputStream -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                        // Task 27: Using Streams API with lambda
                        reader.lines().forEach(line -> explainLine(line));
                    } catch (IOException e) {
                        System.err.println("Error reading stream: " + e.getMessage());
                    }
                })
                .join();
    }

    // Task 26: Explain what each line means
    private static void explainLine(String line) {
        System.out.println("Received: " + line);
        
        if (line.trim().isEmpty()) {
            System.out.println("  → Empty line\n");
            return;
        }
        
        String[] parts = line.trim().split("\\s+");
        
        if (parts.length == 5) {
            String timestamp = parts[0];
            String type = parts[1];
            String x = parts[2];
            String y = parts[3];
            String value = parts[4];
            
            System.out.println("  → Timestamp: " + timestamp);
            System.out.println("  → Type: " + type);
            System.out.println("  → Position: (" + x + ", " + y + ")");
            System.out.println("  → Value: " + value);
        } else {
            System.out.println("  → Unknown format");
        }
        
        System.out.println();
    }
}