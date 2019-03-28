import SockJS from 'sockjs-client';
import Stomp from 'stompjs';


    let socket = new SockJS("http://localhost:8080/gs-guide-websocket");
    var stompClient;
    export default stompClient;//= Stomp.over(socket);