package com.acos.notification.template;

import com.acos.notification.api.NotificationChannel;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persistence for {@link MessageTemplate}. */
public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, UUID> {

  /**
   * Loads one template by its stable code and channel.
   *
   * @param code template code
   * @param channel delivery channel
   * @return matching template
   */
  Optional<MessageTemplate> findByCodeAndChannel(String code, NotificationChannel channel);

  /**
   * Lists templates for the designer.
   *
   * @return templates ordered by channel and code
   */
  List<MessageTemplate> findAllByOrderByChannelAscCodeAsc();

  /**
   * Returns whether a code is already used on a channel.
   *
   * @param code template code
   * @param channel delivery channel
   * @return {@code true} when the pair exists
   */
  boolean existsByCodeAndChannel(String code, NotificationChannel channel);
}
