package br.com.alurafood.pagamentos.amqp;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PagamentoAMQPConfiguration {

//  @Bean
//  public Queue criaFila(){
//    return QueueBuilder.nonDurable("pagamento.concluido").build();
//  }

  @Bean
  public RabbitAdmin criaRabbitAdmin(ConnectionFactory conn) {
    return new RabbitAdmin(conn);
  }

  @Bean
  public ApplicationListener<ApplicationReadyEvent> inicializaAdmin(RabbitAdmin rabbitAdmin){
    return event -> rabbitAdmin.initialize();
  }

  // Vamos precisar adicionar os 2 métodos de configuração para converter os objs Message para JSON
  @Bean
  public Jackson2JsonMessageConverter messageConverter(){
    return  new Jackson2JsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                       Jackson2JsonMessageConverter messageConverter){
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(messageConverter);
    return  rabbitTemplate;
  }

  // Vamos criar a nossa exchange
  @Bean
  public FanoutExchange fanoutExchange() {
    return new FanoutExchange("pagamentos.ex");
  }
}
