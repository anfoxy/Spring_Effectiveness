let $chatHistory;
let $button;
let $textarea;
let $chatHistoryList;

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

function render(message, userName) {
    scrollToBottom();
    // responses
    var templateResponse = Handlebars.compile($("#message-response-template").html());
    var contextResponse = {
        response: message,
        time: getCurrentTime(),
        userName: userName
    };

    setTimeout(function () {
        $chatHistoryList.append(templateResponse(contextResponse));
        scrollToBottom();
    }.bind(this), 1500);
}

function sendMessage(message) {
    let username = $('#userName').val();
    console.log(username)
    sendMsg(username, message);
    scrollToBottom();
    if (message.trim() !== '') {
        var template = Handlebars.compile($("#message-template").html());
        var context = {
            messageOutput: message,
            time: getCurrentTime(),
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
    sendMessage($textarea.val());
}

function addMessageEnter(event) {
    // enter was pressed
    if (event.keyCode === 13) {
        addMessage();
    }
}

function MessageData() {

    let userName = document.getElementById("userName").value;
    let recipient = document.getElementById("recipient").value;
    $.get(url + "/fetchAllUsers/" + userName+" " +recipient, function (response) {
        let users = response;
        for (let i = 0; i < users.length; i++) {

            if(users[i].userId.username === userName){
                var template = Handlebars.compile($("#message-template").html());
                var context = {
                    messageOutput: users[i].text,
                    time: users[i].time,
                    toUserName: userName
                };
                $chatHistoryList.append(template(context));
                scrollToBottom();
            }else {
                var templateResponse = Handlebars.compile($("#message-response-template").html());
                var contextResponse = {
                    response: users[i].text,
                    time: users[i].time,
                    userName:  users[i].userId.userFN
                };
                $chatHistoryList.append(templateResponse(contextResponse));
                scrollToBottom();

            }

        }
    });
}
init();

