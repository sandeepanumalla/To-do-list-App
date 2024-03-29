const Stomp = require('stompjs');
const SockJS = require('sockjs-client');

// const serverUrl = 'http://localhost:8888/test'; // Adjust the WebSocket URL accordingly
const serverUrl = 'http://localhost:8081/task-management-sockets'; // Adjust the WebSocket URL accordingly
const username = 'TestUser'; // Set your username

const socket = new SockJS(serverUrl);

// Create a Stomp client over the SockJS connection
const stompClient = Stomp.over(socket);

stompClient.connect({}, (frame) => {
    console.log('Connected to STOMP over SockJS');

    // stompClient.send('/app/chat.addUser', {}, JSON.stringify({
    //     sender: username,
    //     type: 'JOIN'
    // }));


    // Send a STOMP message
    // stompClient.send('/app/send.anything', {}, JSON.stringify("testing the stomp"));
    stompClient.send('/app/topic/notifications', {}, JSON.stringify("testing the stomp with sandeep"));
    const recipientUsername = "sandeep";

    // Subscribe to a STOMP destination
    stompClient.subscribe("/app/topic/notifications", (message) => {
        // const receivedMessage = JSON.parse(message.body);
        console.log('Received message:', message);

        // Add your assertions here to validate the received message.
    });

    stompClient.subscribe("/topic/notifications", (message) => {
        // const receivedMessage = JSON.parse(message.body);
        console.log('Received message2:', message.body);

        // Add your assertions here to validate the received message.
    });

    stompClient.subscribe("/topic/notifications/sandeep", (message) => {
        // const receivedMessage = JSON.parse(message.body);
        console.log('Received message3:', message.body);

        // Add your assertions here to validate the received message.
    });

});
