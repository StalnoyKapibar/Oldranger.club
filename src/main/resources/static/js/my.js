$(document).ready(function () {
    setmainbody();

    new Mmenu(document.querySelector('#menu-left'));
    document.addEventListener('click', function (ev) {

    });
    var dialog, form,

        // From http://www.whatwg.org/specs/web-apps/current-work/multipage/states-of-the-type-attribute.html#e-mail-state-%28type=email%29
        emailRegex = /^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/,
        name = $("#username"),
        email = $("#email"),
        password = $("#password"),
        allFields = $([]).add(username).add(email).add(password),
        tips = $(".validateTips");

    function userLogin() {

        $.ajax({
            type: "POST",
            url: "/login",
            data: {
                username: name.val(),
                password: password.val()
            },
            success: function (e) {
                window.location = window.location;
            },
            error: function (e) {
                console.log(e.responseText);
            }
        });
        dialog.dialog("close");
    }


    dialog = $("#dialog-form").dialog({
        autoOpen: false,
        height: 400,
        width: 350,
        modal: true,
        buttons: {
            "войти": userLogin,
            "отмена": function () {
                dialog.dialog("close");
            }
        },
        close: function () {
            //   form[0].reset();
            //       allFields.removeClass("ui-state-error");
        }
    });

    $("#login_modal").on("click", function (event) {
        dialog.dialog("open");
        event.preventDefault();
    });
});

function setmainbody() {
    TopMenu = $('.menu_fixed').outerHeight(true);
    console.log(TopMenu);
    $('.main_body').css('top', TopMenu);
}