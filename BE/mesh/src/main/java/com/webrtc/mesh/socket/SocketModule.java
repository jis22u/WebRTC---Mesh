package com.webrtc.mesh.socket;


import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.webrtc.mesh.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


// 3


@Component
@Slf4j
public class SocketModule {


	private final SocketIOServer server;
	private final SocketService socketService;


	public SocketModule(SocketIOServer server, SocketService socketService) {
		System.out.println("3 SocketModule. (1)");

		this.server = server;
		this.socketService = socketService;

		// 1. 누군가 소켓에 연결할 때 트리거
		server.addConnectListener(onConnected());
		// 2. 누군가 소켓에서 연결을 끊을 때 트리거
		server.addDisconnectListener(onDisconnected());
		// 3. NodeJS에서 socket.on(“send_message”)
		//    즉, 주어진 이벤트 이름과 객체 클래스로 이벤트를 처리할 수 있음
		//     브라우저에서 서버로 보낸 데이터 받기
		server.addEventListener("offer", Message.class, getOffer());
		server.addEventListener("answer", Message.class, getAnswer());
		server.addEventListener("ice", Message.class, getIce());
	}



	/*
	누군가 소켓에 연결할 때 트리거
	프론트한테 welcome 하라고 명령~~~~~
	 */
	private ConnectListener onConnected() {
		System.out.println("SocketModule - onConnected()");

		return (client) -> {
			String roomName = client.getHandshakeData().getSingleUrlParam("roomName");
			client.joinRoom(roomName);

			for (SocketIOClient first : client.getNamespace().getRoomOperations(roomName).getClients()) {
				if (!first.getSessionId().equals(client.getSessionId())) {
					first.sendEvent("welcome");
				}
			}

//			int size = client.getNamespace().getAllClients().size();
//			if(size == 2) {
//			}

			log.info("Socket ID[{}] - roomName[{}]  Connected to chat module through", client.getSessionId().toString(), roomName);
		};
	}



	//    ---------------- offer & answer & ice ------------------------
	private DataListener<Message> getOffer() {
		System.out.println("SocketModule - getOffer()");

		return (senderClient, data, ackSender) -> {
			log.info(data.toString());
			socketService.sendSocketOffer(senderClient, data);
		};
	}

	private DataListener<Message> getAnswer() {
		System.out.println("SocketModule - getAnswer()");

		return (senderClient, data, ackSender) -> {
			log.info(data.toString());
			socketService.sendSocketAnswer(senderClient, data);
		};
	}

	private DataListener<Message> getIce() {
		System.out.println("SocketModule - getIce()");

		return (senderClient, data, ackSender) -> {
			log.info(data.toString());
			socketService.sendSocketIce(senderClient, data);
		};
	}

	//  ---------------- offer & answer & ice ------------------------



	/*
	 누군가 소켓에서 연결을 끊을 때 트리거
	 */
	private DisconnectListener onDisconnected() {
		System.out.println("SocketModule - onDisconnected()");

		return client -> {
			String roomName = client.getHandshakeData().getSingleUrlParam("roomName");
			log.info("Socket ID[{}] - roomName[{}]  discnnected to chat module through", client.getSessionId().toString(), roomName);
		};
	}

}
