
var websocket = null;

window.onload = function () { // URI = ws://10.16.0.165:8080/WebSocket/ws
    const dataSpan = document.querySelector('[data-username]')
    if (dataSpan) {
        username = dataSpan.getAttribute('data-username')
        if (username != "") {
            connect('ws://' + window.location.host + '/ucBusca/ws/' + username);
        }
    }
}

function connect(host) { // connect to the host websocket
    if ('WebSocket' in window)
        websocket = new WebSocket(host);
    else if ('MozWebSocket' in window)
        websocket = new MozWebSocket(host);
    else {
        return;
    }

    websocket.onopen = onOpen; // set the 4 event listeners below
    websocket.onclose = onClose;
    websocket.onmessage = onMessage;
    websocket.onerror = onError;
}

function onOpen(event) {
    console.log('Connected to ' + window.location.host + '.');
}

function onClose(event) {
    console.log('WebSocket closed (code ' + event.code + ').');
}

function onMessage(message) { // print the received message
    if (message.data === "You have been promoted to admin.") {
        $(document).ready(function () {
            $("#myToast").toast('show');
        });
    }
    console.log(message.data);
}

function onError(event) {

}

function doSend() {

}

