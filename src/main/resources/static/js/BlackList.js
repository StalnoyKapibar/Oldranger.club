$(document).ready(function () {

    let sendBanForm = $("#sendBan");
    let myModalEdit = $("#myModalEdit");

    sendBanForm.on('submit', sendBan);

    function sendBan(e) {
        e.preventDefault();
        var formDataBlackList = {
            id : $("#userId").val(),
            dateUnblock : $("#datetimepicker5").val()
        }

        var formDataWritingBan = {
            id : $("#userId").val(),
            banType : $("#banType").val(),
            dateUnblock : $("#datetimepicker5").val()
        }

        var formData;

        if ($("#action_id").val() == "/admin/writingBan") {
            formData = formDataWritingBan;
        }
        else {
            formData = formDataBlackList;
        }

        // DO POST
        $.ajax({
            type : "POST",
            contentType : "application/json",
            url : "/api" + $("#action_id").val(),
            data : JSON.stringify(formData),
            dataType : 'json',
            success : function (user) {
                myModalEdit.modal('toggle');
            }
        });
    }
});