let answerID = 0;
let idUser;
let answerName ='';
let targetText = '';

function sendMessage() {

    let text = $('#body-comments').val();
    let time = Math.floor(Date.now()/1000);
    let name = $('#nameUserPrincipal').text();
    let urlAva = $('#urlAva').text();
    let idTopic = $('#idTopic').text();
    idUser = $('#idUser').text();

    let sendText = $('.emojionearea-editor');

    if(sendText.text() != '' || sendText.children().length > 0) {

        showComments(text, time, name, urlAva);

        let data = {};
        data["idTopic"] = idTopic;
        data["idUser"] =  idUser;
        data["text"] = text;
        data["answerID"] = answerID;

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

        text = '';
        sendText.text("");
        answerID = 0;
        answerName = '';
        targetText = '';
    }
}

function showComments(text, time, name, urlAva) {
    let pozitionComments = $('#field-comments');
    let float = answerID ? 'right' : 'left';
    pozitionComments.append(
        '<div>'+
            '<div class="pozition">'+
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
                                    timeConverter(time)+
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
                            '<span class="answer-comment" id= '+ idUser +' onclick="setAnswerName()">'+
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

function closeAnswer() {
    $('#body-comments').val('');
    $('#block-answer').css('display', 'none');
    deleteNameAnswer();
}

function deleteNameAnswer(){
    let answer = $('.answer-desc');
    if (answer.children('span.name-answer') != undefined) {
        answer.children('span.name-answer').remove();
    }
}

function setAnswerName(event) {
    event = event || window.event;
    answerID = $(event.currentTarget).attr('id');
    answerName = $(event.currentTarget).closest('div.desc').find('div.author:first').text();
    targetText = $(event.currentTarget).closest('div.desc').find('div.desc-r:first').html();
    deleteNameAnswer();
    $('.answer-desc').children('span').after('<span class="name-answer">'+ answerName +'</span>');
    $('#block-answer').css('display', 'block');
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