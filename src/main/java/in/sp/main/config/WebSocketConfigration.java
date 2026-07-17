package in.sp.main.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfigration implements WebSocketMessageBrokerConfigurer{

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/chat")
		.setAllowedOrigins("http://localhost:5173")
		.withSockJS();	
	}
	
	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        // Hum size limit ko 64KB se badhakar 50 MB kar rahe hain taaki badi images crash na karein
        registration.setMessageSizeLimit(50 * 1024 * 1024); 
        registration.setSendBufferSizeLimit(50 * 1024 * 1024);
        registration.setSendTimeLimit(60 * 1000);
    }
	
	@Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(512 * 1024);   // 512 KB
        container.setMaxBinaryMessageBufferSize(512 * 1024); // 512 KB
        container.setMaxSessionIdleTimeout(60000L);
        return container;
    }

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic","/queue");
		config.setApplicationDestinationPrefixes("/app");
		 config.setUserDestinationPrefix("/user");
	}
	
}
