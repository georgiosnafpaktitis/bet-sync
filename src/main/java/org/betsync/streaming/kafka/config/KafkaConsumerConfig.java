package org.betsync.streaming.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import lombok.extern.slf4j.Slf4j;

/** Configuration of the kafka consumer. */
@Configuration
@Slf4j
public class KafkaConsumerConfig implements KafkaListenerConfigurer {
  private final LocalValidatorFactoryBean validatorFactory;

  @Value("${kafka.consumer.auto-offset-reset:earliest}")
  private String autoOffsetReset;

  @Value("${spring.kafka.bootstrap-servers}")
  private String kafkaBrokers;

  @Value("${kafka.consumer.backoff.interval:10000}")
  private long backoffInterval;

  public KafkaConsumerConfig(LocalValidatorFactoryBean validatorFactory) {
    this.validatorFactory = validatorFactory;
  }

  @Bean
  @Primary
  public DefaultErrorHandler defaultErrorHandler() {
    final DefaultErrorHandler errorHandler =
        new DefaultErrorHandler(
            (record, ex) ->
                log.error(
                    "Skipping poison message. topic={}, partition={}, offset={}, key={}",
                    record.topic(),
                    record.partition(),
                    record.offset(),
                    record.key(),
                    ex),
            new FixedBackOff(backoffInterval, FixedBackOff.UNLIMITED_ATTEMPTS));

    // Poison messages: don't retry at all (skip immediately)
    errorHandler.addNotRetryableExceptions(
        org.springframework.messaging.converter.MessageConversionException.class,
        org.springframework.kafka.support.converter.ConversionException.class,
        org.springframework.kafka.support.serializer.DeserializationException.class,
        org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException
            .class,
        IllegalArgumentException.class);
    return errorHandler;
  }

  @Bean
  @ConditionalOnMissingBean(ConsumerFactory.class)
  public ConsumerFactory<String, String> consumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);

    return new DefaultKafkaConsumerFactory<>(
        props, new StringDeserializer(), new StringDeserializer());
  }

  @Bean(name = "containerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, String> containerFactory(
      ConsumerFactory<String, String> consumerFactory, DefaultErrorHandler defaultErrorHandler) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setCommonErrorHandler(defaultErrorHandler);
    factory.setRecordMessageConverter(new JsonMessageConverter());
    return factory;
  }

  @Override
  public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
    registrar.setMessageHandlerMethodFactory(kafkaHandlerMethodFactory());
  }

  @Bean
  public MessageHandlerMethodFactory kafkaHandlerMethodFactory() {
    DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
    factory.setValidator(validatorFactory);
    return factory;
  }
}
