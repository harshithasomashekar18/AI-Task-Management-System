package taskmanagementsystem.ai;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class TaskAiServiceTest {
 @Test void modelResponseAndFallbackSourceAreDistinct() throws Exception {
  HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
  AtomicInteger calls = new AtomicInteger();
  AtomicReference<String> request = new AtomicReference<>();
  server.createContext("/v1/chat/completions", exchange -> {
   request.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
   int call = calls.incrementAndGet();
   assertThat(exchange.getRequestHeaders().getFirst("Authorization")).isEqualTo("Bearer synthetic-test-key");
   assertThat(exchange.getRequestHeaders().getFirst("Content-Type")).contains("application/json");
   String content = call == 1 ? "**HIGH**" : "One short sentence.";
   byte[] bytes = ("{\"choices\":[{\"message\":{\"content\":\"" + content + "\"}}]}").getBytes(StandardCharsets.UTF_8);
   exchange.getResponseHeaders().set("Content-Type", "application/json");
   exchange.sendResponseHeaders(200, bytes.length);
   exchange.getResponseBody().write(bytes);
   exchange.close();
  });
  server.start();
  try {
   TaskAiService ai = new TaskAiService("synthetic-test-key", "http://127.0.0.1:" + server.getAddress().getPort() + "/v1", "synthetic-model");
   assertThat(ai.suggestPriorityWithSource("write report")).isEqualTo(new TaskAiService.PrioritySuggestion("HIGH", "AI"));
   assertThat(ai.summarize("write report")).isEqualTo("One short sentence.");
   assertThat(request.get()).contains("synthetic-model").contains("write report");
   assertThat(calls.get()).isEqualTo(2);
  } finally { server.stop(0); }
  TaskAiService local = new TaskAiService("", "http://127.0.0.1:1/v1", "synthetic-model");
  assertThat(local.suggestPriorityWithSource("urgent report")).isEqualTo(new TaskAiService.PrioritySuggestion("HIGH", "RULES"));
  assertThat(local.suggestPriorityWithSource("meeting with team")).isEqualTo(new TaskAiService.PrioritySuggestion("MEDIUM", "RULES"));
  assertThat(local.suggestPriorityWithSource("plan vacation")).isEqualTo(new TaskAiService.PrioritySuggestion("LOW", "RULES"));
  assertThat(local.summarize("short task")).isEqualTo("short task");
 }
}
