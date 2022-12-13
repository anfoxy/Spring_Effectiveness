const url = 'http://localhost:8080';
let stompClient;
let selectedUser;
let userMain;
let newMessages = new Map();
let $chatHistory;
let $button;
let $textarea;
let $chatHistoryList;

function connectToChat(userName) {
    console.log("connecting to chat...")
    let socket = new SockJS(url + '/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("connected to: " + frame);
        stompClient.subscribe("/topic/messages_topic/" + userName, function (response) {
            let data = JSON.parse(response.body);

            if (selectedUser === data.fromLogin) {
                let user = document.getElementById("user").value;
                let userFN = document.getElementById("userFN").value;
                if(data.user !== user){

                /*  у меня мои сообщения*/
                render(data.text, userFN, data.doc);
                }
            } else {

                /*  у меня чужие сообщения*/
                newMessages.set(data.fromLogin, data.text, data.doc);
                $('#userNameAppender_' + data.fromLogin).append('<span id="newMessage_' + data.fromLogin + '" style="color: red">+1</span>');

            }
        });
    });
}

function sendMsg(from,text) {
    let user = document.getElementById("user").value;
/*
    stompClient.send("/app/chat_topic/" + selectedUser, {}, JSON.stringify({
        /!*      message: text*!/
        fromLogin: from,
        user: user,
        doc: null,
        text: text
    }));

*/

    var name = document.getElementById('file');


    if(name.files.item(0) === null){
        stompClient.send("/app/chat_topic/" + selectedUser, {}, JSON.stringify({
            /*      message: text*/
            fromLogin: from,
            user: user,
            doc: null,
            text: text
        }));

    }else {
        var formData = new FormData();
        formData.append("file", name.files.item(0));

        $.ajax({
            url: "/upload",
            type: "POST",
            data: formData,
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            cache: false,
            success: function (res) {
                console.log(res);
            },
            error: function (err) {
                console.error(err);
            }
        });

        stompClient.send("/app/chat_topic/" + selectedUser, {}, JSON.stringify({
            /*      message: text*/
            fromLogin: from,
            user: user,
            doc: name.files.item(0).name,
            text: text
        }));
    }


}

function selectUser() {

    let userName = document.getElementById("userName").value;
    console.log("selecting users: " + userName);
    selectedUser = userName;
    userMain = userName;
    let isNew = document.getElementById("newMessage_" + userName) !== null;
    if (isNew) {
        let element = document.getElementById("newMessage_" + userName);
        element.parentNode.removeChild(element);
        render(newMessages.get(userName), userName);
    }
/*    $('#selectedUserId').html('');
    $('#selectedUserId').append('Chat with ' + userName);*/
    connectToChat(userName);
    MessageData();
}



function init() {
    cacheDOM();
    bindEvents();
}

function bindEvents() {
    $button.on('click', addMessage.bind(this));
    $textarea.on('keyup', addMessageEnter.bind(this));
}

function cacheDOM() {
    $chatHistory = $('.chat-history');
    $button = $('#sendBtn');
    $textarea = $('#message-to-send');
    $chatHistoryList = $chatHistory.find('ul');
}

function render(message, userName, doc) {
    scrollToBottom();
    // responses
    var templateResponse = Handlebars.compile($("#message-response-template").html());
    var contextResponse = {
        response: message,
        time: getCurrentTime(),
        doc: doc,
        userName: userName
    };

    setTimeout(function () {
        $chatHistoryList.append(templateResponse(contextResponse));
        scrollToBottom();
    }.bind(this), 1500);
}

function sendMessage(message, doc) {
    let username = $('#userName').val();
    console.log(username)
    sendMsg(username, message);
    scrollToBottom();
    if (message.trim() !== '') {
        var template = Handlebars.compile($("#message-template").html());
        var context = {
            messageOutput: message,
            time: getCurrentTime(),
            doc: doc,
            toUserName: selectedUser
        };

        $chatHistoryList.append(template(context));
        scrollToBottom();
        $textarea.val('');
    }
}

function scrollToBottom() {
    $chatHistory.scrollTop($chatHistory[0].scrollHeight);
}

function getCurrentTime() {
    return new Date().toLocaleTimeString().replace(/([\d]+:[\d]{2})(:[\d]{2})(.*)/, "$1$3");
}

function addMessage() {
    var name = document.getElementById('file');
    if(name.files.item(0) === null)
        sendMessage($textarea.val());
    else
        sendMessage($textarea.val(),name.files.item(0).name);
}

function addMessageEnter(event) {
    // enter was pressed
    if (event.keyCode === 13) {
        addMessage();
    }
}
function savefile(name){
    window.location.href = "/chats/save_file/" + name;
}

function MessageData() {

    let user = document.getElementById("user").value;
    let userName = document.getElementById("userName").value;

    $.get(url + "/topicMessageData/" + userName, function (response) {
        let users = response;
        for (let i = 0; i < users.length; i++) {

            if(users[i].userId.username === user){
                var template = Handlebars.compile($("#message-template").html());
                var context = {
                    messageOutput: users[i].text,
                    time: users[i].time,
                    doc: users[i].doc,
                    toUserName: user
                };
                $chatHistoryList.append(template(context));
                scrollToBottom();
            }else {
                var templateResponse = Handlebars.compile($("#message-response-template").html());
                var contextResponse = {
                    response: users[i].text,
                    time: users[i].time,
                    doc: users[i].doc,
                    userName: users[i].userId.userLN
                };
                $chatHistoryList.append(templateResponse(contextResponse));
                scrollToBottom();

            }

        }
    });
}
init();

