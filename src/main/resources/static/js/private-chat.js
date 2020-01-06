$(document).ready(function () {

    let greeting = $("#greeting"),
        greetingForm = $('#greetingForm'),
        chat = $('#chat'),
        messageForm = $('#messageForm'),
        fileInput = $('#messageForm #file-input'),
        message = $('#message'),
        messageArea = $('#messageArea'),
        connecting = $('.connecting'),
        close = $('.close'),
        username = null,
        userAvatar = null,
        replyTo = null,
        stompClient = null,
        page = 0,
        chatToken,
        url = window.location.pathname,
        id = url.substring(url.lastIndexOf('/') + 1);


    greetingForm.on('submit', connect);
    messageForm.on('submit', sendMessage);
    close.on('click', disconnect);
    $(window).on('beforeunload', disconnect);


    function checkOnline() {
        let isOnline;
        $.ajax({
            url: "/api/private/online/" + id,
            datatype: 'json',
            async: false,
            type: "get",
            contentType: 'application/json',
            success: function (data) {
                isOnline = (data === 'true');
            }
        });
        if (isOnline) {
            $('#chat .indicator-online').show();
        } else {
            $('#chat .indicator-online').hide();
        }
    }

    window.setInterval(function () {
        checkOnline();
    }, 30000);

    function connect(e) {
        e.preventDefault();
        $.get("/api/chat/user", function (data) {
            username = data["username"];
            userAvatar = data["ava"];
        });
        if (username) {
            greeting.addClass('hidden');
            chat.removeClass('hidden');
            stompClient = Stomp.over(new SockJS('/user'));
            stompClient.connect({}, onConnected, onError);
        }
    }

    function disconnect(e) {
        e.preventDefault();
        chat.addClass('hidden');
        greeting.removeClass('hidden');
        stompClient.send("/chat/del/" + chatToken, {}, JSON.stringify({sender: username, type: 'LEAVE'}));
        stompClient.disconnect();
        page = 0;
    }

    function onConnected() {
        $.ajax({
            url: '/api/private/' + id,
            datatype: 'json',
            async: false,
            type: "get",
            contentType: 'application/json',
            success: function (data) {
                chatToken = data;
            },
            error: function (data) {
                alert("Отказано в доступе");
            }
        });
        stompClient.subscribe('/channel/private/' + chatToken, onMessageReceived, {});
        stompClient.send("/chat/add/" + chatToken, {}, JSON.stringify({sender: username, type: 'JOIN'}));
        connecting.addClass('hidden');
        messageArea.empty();
        drawUserInfo();
        checkOnline();
        getMessages();
    }

    function onError(er) {
        connecting.text('Не удалось подключиться к серверу. Обновите страницу и попробуйте войти еще раз!');
        connecting.css('color', 'red');
    }

    function sendMessage(e) {
        e.preventDefault();
        let messageContent = message.val().trim();
        let originalImg;
        let thumbnailImg;


        if (fileInput.get(0).files.length !== 0) {
            let formData = new FormData(messageForm[0]);
            $.ajax({
                type: "POST",
                async: false,
                url: '/api/private/image/' + chatToken,
                cache: false,
                contentType: false,
                processData: false,
                data: formData,
                success: function (data) {
                    originalImg = data["originalImg"];
                    thumbnailImg = data["thumbnailImg"];
                }
            });
        }

        if (messageContent || (fileInput.get(0).files.length !== 0)) {
            let chatMessage = {
                sender: username,
                text: message.val(),
                senderAvatar: userAvatar,
                originalImg: originalImg,
                thumbnailImg: thumbnailImg,
                replyTo: replyTo,
                type: 'MESSAGE'
            };
            stompClient.send("/chat/send/" + chatToken, {}, JSON.stringify(chatMessage));
            message.val('');
            fileInput.val('');
            replyTo = null;
        }
    }

    function onMessageReceived(payload) {
        let message = JSON.parse(payload.body);
        drawMessage(message);
    }

    function drawMessage(message, prepend) {
        let messageElement = $('<li>');
        switch (message.type) {
            case 'JOIN':
                messageElement.addClass('event-message');
                message.text = message.sender + ' присоединился!';
                break;
            case 'LEAVE':
                messageElement.addClass('event-message');
                message.text = message.sender + ' покинул чат!';
                break;
            case 'MESSAGE':
                messageElement.addClass('chat-message');
                if (message.replyTo === username) {
                    messageElement.addClass('reply');
                }
                let avatarElement = $('<i>');
                avatarElement.css("background", "url(/img/" + message.senderAvatar + ")");
                avatarElement.css("background-size", "cover");
                messageElement.append(avatarElement);

                let usernameElement = $('<span class="sender">');
                let usernameText = document.createTextNode(message.sender);
                usernameElement.append(usernameText);
                messageElement.append(usernameElement);

                let dateElement = $('<span class="message-date">');
                let dateText = document.createTextNode(message.messageDate);
                dateElement.append(dateText);
                messageElement.append(dateElement);

                if (message.originalImg !== null) {
                    let link = $('<a target="_blank" class="image-link">');
                    link.attr("href", "http://localhost:8888/img/chat/" + message.originalImg);
                    let img = $('<img>');
                    img.attr("src", "/img/chat/" + message.thumbnailImg);
                    link.append(img);
                    messageElement.append(link);

                }

        }

        let textElement = $('<p>');
        let messageText = document.createTextNode(message.text);
        textElement.append(messageText);
        messageElement.append(textElement);
        if (prepend) {
            messageArea.prepend(messageElement);
        } else {
            messageArea.append(messageElement);
        }
        messageArea.scrollTop(messageArea[0].scrollHeight);
    }

    function getMessages(page) {
        if (page === undefined) {
            page = 0;
        }
        $.ajax({
            url: '/api/private/messages/' + chatToken + '?page=' + page,
            datatype: 'json',
            async: false,
            type: "get",
            contentType: 'application/json',
            success: function (data) {
                if (page === 0) {
                    for (i = (data.length - 1); i >= 0; i--) {
                        let test = data[i];
                        drawMessage(test);
                    }

                } else {
                    for (i = 0; i < data.length; i++) {
                        let test = data[i];
                        drawMessage(test, true);
                    }
                }

                if ($('#messageArea li').length > 19) {
                    $("<button class='prev-msg'><span class='arrow up'></span></button>").prependTo(messageArea);
                }
            },
            error: function (data) {
                $('#messageArea button.prev-msg').hide();
            }
        });
    }

    $(document).on('click', 'button.prev-msg', function (e) {

        let messageElement = $('#messageArea li'),
            firstMsg = $('#messageArea li:first');

        $(this).hide();
        getMessages(++page);
        messageArea.scrollTop(firstMsg.offset().top);
    });

    function drawUserInfo() {
        let header = $('.user');
        let nickName;
        let avatar;
        header.empty();
        $.ajax({
            url: "/api/private/user/" + chatToken,
            datatype: 'json',
            async: false,
            type: "get",
            contentType: 'application/json',
            success: function (data) {
                nickName = data["username"];
                avatar = data["ava"];
            }
        });

        let avatarElement = $('<i>');
        avatarElement.css("background", "url(/img/" + avatar + ")");
        avatarElement.css("background-size", "cover");
        header.append(avatarElement);

        let usernameElement = $('<span class="sender">');
        let usernameText = document.createTextNode(nickName);
        usernameElement.append(usernameText);
        header.append(usernameElement);

        header.append('<span class="indicator-online hidden" style="display: none"></span>')
    }
});