package taskmanagementsystem.ai;
import java.time.Duration;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;
@Service
public class TaskAiService {
 private static final Logger log = LoggerFactory.getLogger(TaskAiService.class);
 private static final Pattern PRIORITY = Pattern.compile("\\b(HIGH|MEDIUM|LOW)\\b");
 private final String key; private final String model; private final RestClient client;
 public TaskAiService(@Value("${app.ai.api-key:}") String key, @Value("${app.ai.base-url:https://api.openai.com/v1}") String url, @Value("${app.ai.model:gpt-4o-mini}") String model) {
  this.key=key; this.model=model;
  SimpleClientHttpRequestFactory factory=new SimpleClientHttpRequestFactory(); factory.setConnectTimeout(Duration.ofSeconds(3));
  String host = URI.create(url).getHost();
  factory.setReadTimeout(Duration.ofSeconds(Set.of("localhost", "127.0.0.1", "[::1]").contains(host) ? 45 : 8));
  client=RestClient.builder().baseUrl(url).requestFactory(factory).build();
 }
 private String ask(String instruction,String text) {
  if(key.isBlank()) return "";
  try {
   JsonNode result=client.post().uri("/chat/completions").header("Authorization","Bearer "+key).body(Map.of("model",model,"messages",List.of(Map.of("role","system","content",instruction),Map.of("role","user","content",text)))).retrieve().body(JsonNode.class);
   return result==null ? "" : result.path("choices").path(0).path("message").path("content").asText("").trim();
  } catch (RestClientResponseException e) {
   log.warn("Model request returned HTTP {}. Using local task suggestions.", e.getStatusCode().value());
   return "";
  } catch (RuntimeException e) {
   log.warn("Model request could not complete ({}). Using local task suggestions.", e.getClass().getSimpleName());
   return "";
  }
 }
 public record PrioritySuggestion(String priority, String source) {}
 public PrioritySuggestion suggestPriorityWithSource(String text) {
  if(text==null || text.isBlank()) return new PrioritySuggestion("LOW", "RULES");
  String answer=ask("Assign a priority to this task. Respond only HIGH, MEDIUM, or LOW.",text).toUpperCase(Locale.ROOT);
  Matcher match = PRIORITY.matcher(answer);
  if(match.find()) {
   String priority = match.group(1);
   if(!match.find()) return new PrioritySuggestion(priority, "AI");
  }
  String lower=text.toLowerCase(Locale.ROOT);
  if(lower.contains("urgent") || lower.contains("asap") || lower.contains("immediately")) return new PrioritySuggestion("HIGH", "RULES");
  return new PrioritySuggestion(lower.contains("meeting") || lower.contains("call") ? "MEDIUM" : "LOW", "RULES");
 }
 public String suggestPriority(String text) { return suggestPriorityWithSource(text).priority(); }
 public String summarize(String text) {
  if(text==null || text.isBlank()) return "";
  String answer=ask("Summarize this task in one short sentence.",text);
  if(!answer.isBlank()) return answer.substring(0,Math.min(answer.length(),500));
  return text.length()>80 ? text.substring(0,80)+"..." : text;
 }
}
