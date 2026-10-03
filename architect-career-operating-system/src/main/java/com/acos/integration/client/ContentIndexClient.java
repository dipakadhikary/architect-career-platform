package com.acos.integration.client;

import com.acos.integration.config.AiPlatformFeignConfiguration;
import com.acos.integration.dto.ContentIndexRequest;
import com.acos.integration.dto.ContentIndexResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/** Indexing contract with the Python AI service. */
@FeignClient(
    name = "content-index",
    url = "${ai.platform.base-url}",
    configuration = AiPlatformFeignConfiguration.class)
public interface ContentIndexClient {

  /**
   * Indexes one ACOS document.
   *
   * @param request trusted content payload
   * @return indexing acknowledgement
   */
  @PostMapping(
      value = "/api/v1/ai/index/content",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  ContentIndexResponse index(@RequestBody ContentIndexRequest request);

  /**
   * Removes vectors for one content id.
   *
   * @param contentId source content id
   * @return deletion acknowledgement
   */
  @DeleteMapping(
      value = "/api/v1/ai/index/content/{contentId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  ContentIndexResponse delete(@PathVariable("contentId") UUID contentId);
}
