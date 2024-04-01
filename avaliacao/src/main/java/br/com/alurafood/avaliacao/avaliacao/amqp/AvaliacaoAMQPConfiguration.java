package br.com.alurafood.avaliacao.avaliacao.amqp;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AvaliacaoAMQPConfiguration {
  @Bean
  public Jackson2JsonMessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                       Jackson2JsonMessageConverter messageConverter) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(messageConverter);
    return rabbitTemplate;
  }

  // Fila principal
  @Bean
  public Queue filaDetalhesAvaliacao() {
    return QueueBuilder
            .nonDurable("pagamentos.detalhes-avaliacao")
            // Aqui vamos informar qual é o dead letter exchange
            .deadLetterExchange("pagamentos.dlx")
            .build();
  }

  // Fila para erros
  @Bean
  public Queue filaDlqDetalhesAvaliacao() {
    return QueueBuilder
            .nonDurable("pagamentos.detalhes-avaliacao-dlq")
            .build();
  }

  // Criando a exchange principal
  @Bean
  public FanoutExchange fanoutExchange() {
    return ExchangeBuilder
            .fanoutExchange("pagamentos.ex")
            .build();
  }

  // Exchange auxiliar para erros
  @Bean
  public FanoutExchange deadLetterExchange() {
    return ExchangeBuilder
            .fanoutExchange("pagamentos.dlx")
            .build();
  }

  // Criando bind da exchange principal com a fila principal
  @Bean
  public Binding bindPagamentoPedido() {
    return BindingBuilder
            .bind(filaDetalhesAvaliacao())
            .to(fanoutExchange());
  }

  // Criando bind da exchange e fila de erro
  @Bean
  public Binding bindDlxPagamentoPedido() {
    return BindingBuilder
            .bind(filaDlqDetalhesAvaliacao())
            .to(deadLetterExchange());
  }

  @Bean
  public RabbitAdmin criaRabbitAdmin(ConnectionFactory conn) {
    return new RabbitAdmin(conn);
  }

  @Bean
  public ApplicationListener<ApplicationReadyEvent> inicializaAdmin(RabbitAdmin rabbitAdmin) {
    return event -> rabbitAdmin.initialize();
  }

}
