package br.com.alurafood.pedidos.amqp;

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
public class PedidoAMPQConfiguration {

  @Bean
  public RabbitAdmin criaRabbitAdmin(ConnectionFactory conn) {
    return new RabbitAdmin(conn);
  }

  @Bean
  public ApplicationListener<ApplicationReadyEvent> inicializaAdmin(RabbitAdmin rabbitAdmin) {
    return event -> rabbitAdmin.initialize();
  }

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

  // Vamos criar a nossa fila
  @Bean
  public Queue filaDetalhesPedido() {
    return QueueBuilder.nonDurable("pagamentos.detalhes-pedido").build();
  }

  // Vamos criar a nossa exchange
  @Bean
  public FanoutExchange fanoutExchange() {
    return ExchangeBuilder.fanoutExchange("pagamentos.ex").build();
  }

  // Vamos fazer o bind da exchange com a fila
  @Bean
  public Binding bindPagamentoPedido(FanoutExchange fanoutExchange, Queue filaDetalhesPedido) {
    return BindingBuilder.bind(filaDetalhesPedido).to(fanoutExchange);
  }
}
