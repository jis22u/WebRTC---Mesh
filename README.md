# WebRTC-Mesh
## Mesh | MCU | SFU
WebRTC를 위해 개발자가 구현할 수 있는 서버는 크게 세 종류가 있다. Signaling, SFU 그리고 MCU이다. 
1. Mesh(Signaling)   
- peer간의 직접 연결로 데이터를 송수신하기 때문에 실시간 성이 보장된다.   
- peer 간의 offer, answer라는 session 정보 signal만을 중계한다.    
- 따라서 처음 WebRTC가 peer간의 정보를 중계할 때만 서버에 부하가 발생한다.    
- peer간 연결이 완료된 후에는 서버에 별도의 부하가 없다.    
- 1:1 연결에 적합하다.   
2. SFU
- 클라이언트 peer간 연결이 아닌, 서버와 클라이언트 간의 peer를 연결한다.   
- 단순히 받은 데이터를 연결된 Peer들에게 뿌려준다.     
- 중간 처리를 하지 않고 그대로 보내주기 때문에 서버에 부하가 상대적으로 적은 방식이다.    
- WebRTC는 HTTPS를 반드시 통해야 하기 때문에 암호화, 복호화, 연결관리 등의 역할을 해준다.   
- 데이터가 서버를 거치고 Signaling 서버(P2P 방식)를 사용할 때 보다 느리긴하지만 비슷한 수준의 실시간성을 유지할 수 있다.
3. MCU
- 클라이언트 peer간 연결이 아닌, 서버와 클라이언트 간의 peer를 연결한다.   
- 모든 연결 형식에서 클라이언트는 연결된 모든 사용자에게 데이터를 보낼 필요없이 서버에게만 자신의 영상 데이터를 보내면 된다.     
- 중앙에서 비디오를 인코딩 등과 같은 전처리를 하여 피어에게 다시 전달 해주는 역할을 한다. (중앙 서버 방식)   
- 즉, 중간에서 믹싱을 해준다. 따라서, 인코딩을 통해서 압축률을 좋게 하여 각 피어들에게 던져주면 네트워크 리소스 비용에서는 유리하다.   
- 중앙에서 처리해주는 서버의 CPU리소스를 많이 잡아먹는다는 단점이 있다.
- 대표적인 오픈 소스로 쿠렌토가 있다.
    
<br>
💡 헷갈려서 추가한 정보들..

```
WebRTC 연결을 설정하기 위해 시그널링 서버가 필요합니다. 시그널링 서버는 피어 간의 정보 교환을 도와주고 연결을 설정하는 역할을 합니다.
WebRTC 연결을 위한 SDP(Session Description Protocol) 교환 및 ICE(Interactive Connectivity Establishment) 프레임워크를 사용하여
피어 간의 네트워크 정보를 수집 및 교환합니다.
```

```
시그널링 서버는 WebRTC의 연결 설정 단계에서 중요한 역할을 수행하지만, 그 자체로는 Mesh 방식과 직접적으로 연관되지는 않습니다.

Mesh 방식은 WebRTC의 통신 구조에 대한 개념이며, 피어 간에 직접적인 연결을 형성하는 방식을 말합니다. 
Mesh 방식에서는 각 피어가 다른 피어와 직접적으로 연결되어 모든 피어 간에 통신 경로가 형성됩니다. 
이는 작은 규모의 그룹에서는 효과적일 수 있지만, 대규모 그룹의 경우 피어 간의 연결 수가 급격히 증가하며 확장성이 제한될 수 있습니다.

시그널링 서버는 WebRTC의 연결 설정 단계에서 필요한 역할을 수행합니다. 시그널링 서버는 피어 간의 연결을 설정하기 위한 초기 정보 교환과
네트워크 설정을 돕는 역할을 합니다. 
SDP(Session Description Protocol) 정보와 ICE(Interactive Connectivity Establishment) 후보자 정보를 주고받고, 피어 간의 연결을 
설정하기 위한 중계 역할을 담당합니다.

Mesh 방식에서는 시그널링 서버가 모든 피어 간의 연결 정보를 직접적으로 관리하지 않습니다. 
대신, 시그널링 서버는 피어들에게 다른 피어의 연결 정보를 전달하고, 피어들은 직접적인 연결을 형성하여 통신합니다. 
따라서, 시그널링 서버의 주요 역할은 초기 연결 설정에 있으며, 피어 간의 연결은 직접적으로 형성됩니다.

즉, 시그널링 서버는 Mesh 방식에서 필요한 초기 연결 설정 단계를 지원하며, Mesh 방식과 함께 사용되어 피어 간의 연결 설정을 
돕는 역할을 수행합니다.
```
<br>

## Mesh 방식을 선택한 이유
> 해당 프로젝트는 미어캣 프로젝트를 위해 구현해보았다. 미어캣 프로젝트의 핵심 서비스는 택시를 부르듯 요청을 보내면, 요청 위치에 대한 실시간 정보를 얻을 수 있다.

첫째, 기본적으로 1:1 연결을 통해 서비스가 이루어진다. 요청자가 궁금한 장소를 중심으로 요청을 보내면, 정보를 제공해 줄 수 있는 사람은 해당 요청을 받아 실시간 정보를 제공한다. 1:1 연결을 통한 질적인 정보제공을 위해 정보제공자는 1명의 요청자에게만 정보를 제공한다. 따라서 1:N 혹은 N:M 대신 1:1 연결에 적합한 Mesh 방식을 택했다.

둘째, 사용자 입장에선 자신이 원하는 장소에 요청을 보내면 빠른 시간내로 실시간 정보를 받고 싶을 것이다. 따라서 peer 간의 직접 연결로 데이터를 송수신하는 Mesh 방식을 통해 실시간성을 보장했다.

<br>

## Mesh와 Singnaling Server
WebRTC는 실시간 음성, 영상, 데이터를 교환할 수 있는 P2P 기술이다. 서로 다른 네트워크에 있는 2개의 클라이언트 간 미디어 포맷 등을 상호 연동하기 위한 협의과정(Negotiation)이 필요하다. 이 프로세스를 시그널링 Signalling 이라고 부른다.

BE 기본 흐름
1. 연결 감지
2. SDP 교환 - Offer, Answer
3. ICE 협상 (ICE Negotiation)
4. 연결끊김 감지

BE - FE 기본 흐름
1. A가 SDP 형태의 Offer를 생성한다.
2. A가 생성한 Offer를 본인의 LocalDescription으로 등록한다.
3. A가 Offer를 시그널링 서버에게 전달한다.
4. 시그널링 서버는 상대방 B를 찾아서 SDP 정보를 전달한다.
5. B는 전달받은 Offer를 본인의 RemoteDecsription에 등록한다.
6. B는 Answer를 생성한다.
7. 생성한 Answer를 본인의 LocalDescription으로 등록한다.
8. B는 Answer를 시그널링 서버에게 전달한다.
9. 시그널링 서버는 상대방 A를 찾아서 Answer 메시지를 전달한다. 
10. A는 전달받은 Anser 메시지를 본인의 RemoteDescription에 등록한다.
11. ICE 협상을 한다.

<br>

## BACKEND
Spring Boot   
Netty Socket IO: socket server

<br>

## FRONTEND
React   
SocketIO

<br>

## ref
https://millo-l.github.io/WebRTC-%EA%B5%AC%ED%98%84-%EB%B0%A9%EC%8B%9D-Mesh-SFU-MCU/    
https://andonekwon.tistory.com/71     
https://andonekwon.tistory.com/59    
https://github.com/gurkanucar/socketio-simple-chat   
https://medium.com/folksdev/spring-boot-netty-socket-io-example-3f21fcc1147d   
https://doublem.org/webrtc-story-02/    
