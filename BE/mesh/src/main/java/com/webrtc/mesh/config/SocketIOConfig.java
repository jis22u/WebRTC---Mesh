package com.webrtc.mesh.config;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 2


@Configuration
public class SocketIOConfig {

	@Value("${socket-server.host}")
	private String host;

	@Value("${socket-server.port}")
	private Integer port;

	@Bean
	public SocketIOServer socketIOServer() {

		System.out.println("2 SocketIOConfig");

		com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
		config.setHostname(host);
		config.setPort(port);
		//  config.setContext("/socket.io");
		return new SocketIOServer(config);
	}

}