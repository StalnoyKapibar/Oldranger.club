$(document).ready(function () {

    let table = $("#draftTable");

    $('#summernote').summernote({
        lang: 'ru-RU',
        height: 150,
        toolbar: [
            ['style', ['bold', 'italic', 'underline', 'clear']],
            ['fontsize', ['fontsize']],
            ['color', ['color']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['insert', ['link']]
        ]
    });

    getEmailDraftList();

    function getEmailDraftList() {
        drawPager();
        $.get("/api/admin/getDrafts", function (resp) {
            let row = [];
            $.each(resp, function (i, item) {
                row.push("<tr><td>" + item.subject + "</td>");
                row.push("<td>" + item.lastEditDate + "</td>");
                row.push("<td>" +
                    "<a href='#' data-id='" + item.id + "' id='view' class='mr-2'><button class=\"btn btn-outline-success\">Смотреть</button></a>" +
                    "<a href='#' data-id='" + item.id + "' id='delete' ><button class=\"btn btn-danger\">Удалить</button></a>" +
                    "</td></tr>");
            });
            $('#draftTable tbody').html(row.join(""));
        });
    }

    // Delete draft
    table.on('click', '#delete', function (e) {

        let draftId = $(this).data("id");
        $.ajax({
            type: 'get',
            url: '/api/admin/deleteDraft/' + draftId,
            success: function (resp) {
                getEmailDraftList();
            },
            error: function (resp) {
                alert(" =( something went wrong 3");
            }
        });
    });


    $(document).on('submit', '#message-form', function (e) {
        e.preventDefault();
        var target = e.originalEvent || e.originalTarget;
        var clickedElement = $(target.currentTarget.activeElement);
        var formaction = $(clickedElement).attr("formaction");
        var data = $(this).serialize();

        $.ajax({
            type: "POST",
            url: formaction,
            data: data,
            success: function (data) {
                $('#summernote').summernote('reset');
                $('input[name=id]').val('');
                $('input[name=subject]').val('');
                $('#cancel').addClass('d-none');
                getEmailDraftList();
            },
            error: function (resp) {
                alert(" =( something went wrong 11111");
            }
        });
    });


    // View draft
    table.on('click', '#view', function (e) {

        var draftId = $(this).data("id");
        $.ajax({
            type: 'get',
            url: '/api/admin/getDraft/' + draftId,
            contentType: "application/json",
            dataType: "json",
            success: function (resp) {
                $("#id").val(draftId);
                $("#subject").val(resp.subject);
                $("#summernote").summernote('code', resp.message);
                $('#cancel').removeClass('d-none');
            },
            error: function (resp) {
                alert(" =( something went wrong 2");
            }
        });
    });

    $(document).on('click', '#cancel', function (e) {
        $('#summernote').summernote('reset');
        $('input[name=id]').val('');
        $('input[name=subject]').val('');
    });

    function drawPager() {
        let totalPages;
        $.get("/api/admin/getTotalPages", function (data) {
            totalPages = data["totalPages"];
        });
        if (totalPages !== 0) {
            for (i = 0; i < totalPages; i++) {
                let page = i + 1;
                let elem = $('<li class="page-item" ><a class="page-link" href="#" data-page="'+ i +'">' + page + '</a></li>');
                $('.pagination').append(elem);
            }
        }

    }


});

