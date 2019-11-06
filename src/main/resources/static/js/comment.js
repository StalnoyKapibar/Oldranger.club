let pozition = true;
let answerName = '';
let answerTime = '';
let targetAnswerTag = null;
let targetText = '';

function sendMessage() {
    let commentBody = document.getElementById('body-comments');
    let name = document.getElementById('nameUserPrincipal').textContent;
    let time = Math.floor(Date.now()/1000);
    let urlAva = document.getElementById('urlAva').textContent;
    let text = commentBody.value;
    let sendText = document.getElementById('body-comments').parentElement.children[1].children[0];

    if(sendText.textContent != '' || sendText.children.length > 0) {
        // if(answerName != '') {
        //     text = answerName + ", "  + text;
        // }
        showComments(text, time, name, urlAva);

        let data = {};
        data["idTopic"] = document.getElementById("idTopic").textContent;
        data["name"] = name;
        data["text"] = text;
        data["time"] = time * 1000;
        data["answerName"] = answerName;
        data["answerTime"] = answerTime;

        $.ajax({
            url : '/com/save',
            datatype : 'json',
            async: true,
            type : "post",
            Accept : "application/json",
            contentType : 'application/json; charset=utf-8',
            data : JSON.stringify(data),
            success : function(data) {
                console.log(data);
            }
        });

        commentBody.value = '';
        sendText.innerHTML = '';
        answerName = '';
        answerTime = '';
    }
}

function showComments(text, dateTime, name, urlAva) {
    let commenFieldTarget;
    let float = 'left';
    let pozitionMessage = 'beforeend';
    if(pozition){
        float = 'left';
        commenFieldTarget = document.getElementById('field-comments');
        pozitionMessage = 'beforeend';
    }else {
        float = 'right';
        commenFieldTarget = targetAnswerTag;
        pozitionMessage = 'afterend';
    }
    commenFieldTarget.insertAdjacentHTML(pozitionMessage,
        '<div>'+
            '<div class="pozition" >'+
                '<div class="app-custom-ava small" style="float: ' + float + '">'+
                    '<div class="ava-p">'+
                        '<img class="ava-img"'+
                            'src=' + urlAva + '>'+
                            '<!--/*картинка или текст*/-->'+
                        '</img>'+
                        '<span class="ava-online">'+
                        '</span>'+
                    '</div>'+
                '</div>'+
                '<div class="desc">'+
                    '<div class="desc-r">'+
                        '<div class="desc-p">'+
                            '<div class="datetime">'+
                                '<!--/*@thymesVar id="tempComment" type=""*/-->'+
                                    '<span>'+
                                    '<!--/*время сообщения */-->'+
                                    timeConverter(dateTime)+
                                    ' </span>'+
                            '</div>'+
                            '<!--/*@thymesVar id="tempComment" type=""*/-->'+
                            '<div class="author">'+
                            '<!--/*имя пользователя */-->'+
                            name +
                            '</div>'+
                            '<!--/*@thymesVar id="commentText" type=""*/-->'+
                            '<div class="text-author">'+
                            '<!--//здесь сообщение от пользователя-->'+
                                targetText +
                                text +
                            '</div>'+
                        '</div>'+
                    '</div>'+
                    '<div class="action">'+
                        '<div class="answer">'+
                            '<span class="icon-answer">'+
                            '</span>'+
                            '<span class="a" onclick="setAnswerName()">'+
                                '<p style="display: none">' + name + '</p>'+
                                '<p style="display: none">' + (dateTime * 1000) + '</p>'+
                            'Ответить'+
                            '</span>'+
                        '</div>'+
                        '<div class="raiting-view">'+
                            '<div class="app-comment-rating">'+
                                '<span>'+
                                '</span>'+
                            '</div>'+
                        '</div>'+
                    '</div>'+
                '</div>'+
            '</div>'+
        '</div>'
    );
    closeAnswer();
}

function timeConverter(UNIX_timestamp) {
    let a = new Date(UNIX_timestamp * 1000);
    let months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
    let year = a.getFullYear();
    let month = months[a.getMonth()];
    let date = a.getDate();
    let hour = a.getHours();
    let min = a.getMinutes();
    let sec = a.getSeconds();
    let time = date + ' ' + month + ' ' + year + ' ' + hour + ':' + min + ':' + sec ;
    return time;
}

function closeAnswer() {
    let blockAnswer = document.getElementById('block-answer');
    let block = blockAnswer.children[0].children[0].childNodes[2];
    if (block != undefined) {
        block.remove();
    }
    blockAnswer.style.display = 'none';
    document.getElementById('body-comments').value = '';
    pozition = true;
    targetAnswerTag = null;
}

function setAnswerName(event) {
    pozition = false;
    event = event || window.event;
    targetAnswerTag = event.currentTarget.parentElement.parentElement.parentElement.parentElement.parentElement;
    targetText = targetAnswerTag.children[0].children[1].children[0].innerHTML;
    let name = event.currentTarget.children[0].textContent;
    answerName = name;
    answerTime = event.currentTarget.children[1].textContent;
    let blockAnswer = document.getElementById('block-answer');
    if (blockAnswer.children[0].children[0].childNodes[2] != undefined) {
        blockAnswer.children[0].children[0].childNodes[2].remove();
    }
    blockAnswer.children[0].children[0].insertAdjacentHTML('beforeend', '<span>' + name + '</span>');
    blockAnswer.style.display = 'block';
}