package taskmanagementsystem;

import com.fasterxml.jackson.databind.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import taskmanagementsystem.repository.UserRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskManagementSystemApplicationTests {
 @Autowired MockMvc mvc;
 @Autowired ObjectMapper json;
 @Autowired UserRepository users;
 private long register(String name) throws Exception {
  String body=mvc.perform(post("/api/v1/auth/register").contentType("application/json").content("{\"username\":\""+name+"\",\"email\":\""+name+"@example.test\",\"password\":\"test-password\",\"role\":\"ADMIN\"}"))
   .andExpect(status().isCreated()).andExpect(jsonPath("$.role").value("USER")).andExpect(jsonPath("$.password").doesNotExist()).andReturn().getResponse().getContentAsString();
  return json.readTree(body).get("id").asLong();
 }
 @Test void lifecycleAndOwnershipAreEnforced() throws Exception {
  long owner=register("owner"); long other=register("other");
  assertThat(users.findByUsername("owner").orElseThrow().getPassword()).startsWith("$2").isNotEqualTo("test-password");
  mvc.perform(post("/api/v1/auth/login").contentType("application/json").content("{\"username\":\"owner\",\"password\":\"test-password\"}"))
   .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(owner)).andExpect(jsonPath("$.password").doesNotExist());
  mvc.perform(post("/api/v1/auth/login").contentType("application/json").content("{\"username\":\"owner\",\"password\":\"wrong\"}"))
   .andExpect(status().isUnauthorized());
  mvc.perform(get("/api/v1/tasks/user/"+owner)).andExpect(status().isUnauthorized());
  mvc.perform(get("/api/v1/tasks/user/"+owner).with(httpBasic("owner","wrong"))).andExpect(status().isUnauthorized());
  mvc.perform(get("/api/v1/tasks/user/"+owner).with(httpBasic("other","test-password"))).andExpect(status().isForbidden());
  mvc.perform(post("/api/v1/tasks/user/"+owner).with(httpBasic("other","test-password")).contentType("application/json").content("{\"task\":\"foreign\"}")).andExpect(status().isForbidden());
  mvc.perform(post("/api/v1/tasks/user/"+owner).with(httpBasic("owner","test-password")).contentType("application/json").content("{\"task\":\" \"}")).andExpect(status().isBadRequest());
  String body=mvc.perform(post("/api/v1/tasks/user/"+owner).with(httpBasic("owner","test-password")).contentType("application/json").content("{\"task\":\"Urgent test\",\"details\":\"Synthetic task\",\"completed\":true,\"user\":{\"id\":"+other+"}}"))
   .andExpect(status().isCreated()).andExpect(jsonPath("$.object.completed").value(false)).andExpect(jsonPath("$.object.priority").value("HIGH")).andExpect(jsonPath("$.object.prioritySource").value("RULES")).andExpect(jsonPath("$.object.user").doesNotExist()).andReturn().getResponse().getContentAsString();
  int id=json.readTree(body).path("object").path("id").asInt();
  mvc.perform(get("/api/v1/tasks/user/"+owner).with(httpBasic("owner","test-password"))).andExpect(jsonPath("$.length()").value(1));
  mvc.perform(get("/api/v1/tasks/"+id).with(httpBasic("other","test-password"))).andExpect(status().isForbidden());
  mvc.perform(put("/api/v1/tasks/"+id).with(httpBasic("other","test-password")).contentType("application/json").content("{\"task\":\"stolen\"}")).andExpect(status().isForbidden());
  mvc.perform(patch("/api/v1/tasks/"+id+"/done").with(httpBasic("other","test-password"))).andExpect(status().isForbidden());
  mvc.perform(delete("/api/v1/tasks/"+id).with(httpBasic("other","test-password"))).andExpect(status().isForbidden());
  mvc.perform(put("/api/v1/tasks/"+id).with(httpBasic("owner","test-password")).contentType("application/json").content("{\"task\":\"Meeting tomorrow\",\"details\":\"updated\"}"))
   .andExpect(status().isOk()).andExpect(jsonPath("$.object.task").value("Meeting tomorrow")).andExpect(jsonPath("$.object.priority").value("MEDIUM")).andExpect(jsonPath("$.object.prioritySource").value("RULES"));
  mvc.perform(patch("/api/v1/tasks/"+id+"/done").with(httpBasic("owner","test-password"))).andExpect(jsonPath("$.object.completed").value(true));
  mvc.perform(patch("/api/v1/tasks/"+id+"/pending").with(httpBasic("owner","test-password"))).andExpect(jsonPath("$.object.completed").value(false));
  mvc.perform(delete("/api/v1/tasks/"+id).with(httpBasic("owner","test-password"))).andExpect(status().isNoContent());
  mvc.perform(get("/api/v1/tasks/"+id).with(httpBasic("owner","test-password"))).andExpect(status().isNotFound());
 }
 @Test void registrationValidationAndCors() throws Exception {
  register("unique");
  mvc.perform(post("/api/v1/auth/register").contentType("application/json").content("{\"username\":\"unique\",\"email\":\"unique@example.test\",\"password\":\"test-password\"}"))
   .andExpect(status().isConflict());
  mvc.perform(post("/api/v1/auth/register").contentType("application/json").content("{\"username\":\"bad:name\",\"email\":\"invalid\",\"password\":\"x\"}"))
   .andExpect(status().isBadRequest());
  mvc.perform(options("/api/v1/tasks/user/1").header("Origin","http://localhost:3000").header("Access-Control-Request-Method","GET").header("Access-Control-Request-Headers","authorization"))
   .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin","http://localhost:3000"));
  mvc.perform(options("/api/v1/tasks/user/1").header("Origin","https://untrusted.example").header("Access-Control-Request-Method","GET"))
   .andExpect(status().isForbidden());
 }
}
