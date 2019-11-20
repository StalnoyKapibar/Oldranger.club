let CSRFtoken = "";
let CSRFheader = "";

$(window).on("load", function(e) {
    $.ajax({
        type: "GET",
        url: "/getToken",
        success: function (data) {
            CSRFheader = data.name;
            CSRFtoken = data.value;
            let tokenString = "<meta name='_csrf' content='"+ CSRFtoken + "'/><meta name='_csrf_header' content='"+ CSRFheader +"'/>";
            $("head").append(tokenString);
            $(document).ajaxSend(function(e, xhr, options) {
                xhr.setRequestHeader("X-XSRF-TOKEN", CSRFtoken);
            });
        },
    });

    
});
