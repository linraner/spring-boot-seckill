package com.lin.seckill.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class MQConfig {
    public static final String SECKILL_QUEUE = "seckill.queue";
    public static final String QUEUE = "queue";
    public static final String ORDER_EXPIRE_QUEUE = "orderExpire.queue";
    public static final String ORDER_DELAY_QUEUE = "orderDelay.queue";
    /**
     * routing key 名称
     * 此处的 routingKey 很重要要,具体消息发送在该 routingKey 的
     */
    public static final String DELAY_ROUTING_KEY = "";
//    public static final String TOPIC_QUEUE1 = "topic.queue1";
//    public static final String TOPIC_QUEUE2 = "topic.queue2";
//    public static final String HEADER_QUEUE = "header.queue";
//    public static final String TOPIC_EXCHANGE = "topicExchage";
//    public static final String FANOUT_EXCHANGE = "fanoutxchage";
//    public static final String HEADERS_EXCHANGE = "headersExchage";
    public static final String REGISTER_QUEUE_NAME = "register.queue";
    public static final String REGISTER_EXCHANGE_NAME = "register.exchange";
    public static final String ROUTING_KEY = "all";
    private static final Logger log = LoggerFactory.getLogger(MQConfig.class);

    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Queue seckillQueue() {
        return new Queue(SECKILL_QUEUE, true);
    }


    @Bean
    public Queue orderExpireQueue() {
        return new Queue(ORDER_EXPIRE_QUEUE, true);
    }


    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> log.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause));
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}", exchange, routingKey, replyCode, replyText, message));
        return rabbitTemplate;
    }


    /**
     * 延迟队列配置
     * <p>
     * 1、params.put("x-message-ttl", 5 * 1000);
     * 第一种方式是直接设置 Queue 延迟时间 但如果直接给队列设置过期时间,这种做法不是很灵活,（当然二者是兼容的,默认是时间小的优先）
     * 2、rabbitTemplate.convertAndSend(book, message -> {
     * message.getMessageProperties().setExpiration(2 * 1000 + "");
     * return message;
     * });
     * 第二种就是每次发送消息动态设置延迟时间,这样我们可以灵活控制
     **/
    @Bean
    public Queue delayProcessQueue() {
        Map<String, Object> params = new HashMap<>();
        // x-dead-letter-exchange 声明了队列里的死信转发到的DLX名称，
        params.put("x-dead-letter-exchange", REGISTER_EXCHANGE_NAME);
        // x-dead-letter-routing-key 声明了这些死信在转发时携带的 routing-key 名称。
        params.put("x-dead-letter-routing-key", ROUTING_KEY);
        return new Queue(ORDER_DELAY_QUEUE, true, false, false, params);
    }


    /**
     * 需要将一个队列绑定到交换机上，要求该消息与一个特定的路由键完全匹配。
     * 这是一个完整的匹配。如果一个队列绑定到该交换机上要求路由键 “dog”，则只有被标记为“dog”的消息才被转发，不会转发dog.puppy，也不会转发dog.guard，只会转发dog。
     * TODO 它不像 TopicExchange 那样可以使用通配符适配多个
     *
     * @return DirectExchange
     */
    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange(ORDER_DELAY_QUEUE);
    }

    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(delayProcessQueue()).to(delayExchange()).with(DELAY_ROUTING_KEY);
    }


    @Bean
    public Queue registerBookQueue() {
        return new Queue(REGISTER_QUEUE_NAME, true);
    }

    /**
     * 将路由键和某模式进行匹配。此时队列需要绑定要一个模式上。
     * 符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词。因此“audit.#”能够匹配到“audit.irs.corporate”，但是“audit.*” 只会匹配到“audit.irs”。
     **/
    @Bean
    public TopicExchange registerBookTopicExchange() {
        return new TopicExchange(REGISTER_EXCHANGE_NAME);
    }

    @Bean
    public Binding registerBookBinding() {
        // TODO 如果要让延迟队列之间有关联,这里的 routingKey 和 绑定的交换机很关键
        return BindingBuilder.bind(registerBookQueue()).to(registerBookTopicExchange()).with(ROUTING_KEY);
    }




    /**
     * Topic模式 交换机Exchange
     */
//    @Bean
//    public Queue topicQueue1() {
//        return new Queue(TOPIC_QUEUE1, true);
//    }
//
//    @Bean
//    public Queue topicQueue2() {
//        return new Queue(TOPIC_QUEUE2, true);
//    }
//
//    @Bean
//    public TopicExchange topicExchage() {
//        return new TopicExchange(TOPIC_EXCHANGE);
//    }
//
//    @Bean
//    public Binding topicBinding1() {
//        return BindingBuilder.bind(topicQueue1()).to(topicExchage()).with("topic.key1");
//    }
//
//    @Bean
//    public Binding topicBinding2() {
//        return BindingBuilder.bind(topicQueue2()).to(topicExchage()).with("topic.#");
//    }
//
//    /**
//     * Fanout模式 交换机Exchange
//     */
//    @Bean
//    public FanoutExchange fanoutExchage() {
//        return new FanoutExchange(FANOUT_EXCHANGE);
//    }
//
//    @Bean
//    public Binding FanoutBinding1() {
//        return BindingBuilder.bind(topicQueue1()).to(fanoutExchage());
//    }
//
//    @Bean
//    public Binding FanoutBinding2() {
//        return BindingBuilder.bind(topicQueue2()).to(fanoutExchage());
//    }
//
//    /**
//     * Header模式 交换机Exchange
//     */
//    @Bean
//    public HeadersExchange headersExchage() {
//        return new HeadersExchange(HEADERS_EXCHANGE);
//    }
//
//    @Bean
//    public Queue headerQueue1() {
//        return new Queue(HEADER_QUEUE, true);
//    }
//
//    @Bean
//    public Binding headerBinding() {
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("header1", "value1");
//        map.put("header2", "value2");
//        return BindingBuilder.bind(headerQueue1()).to(headersExchage()).whereAll(map).match();
//    }
}
