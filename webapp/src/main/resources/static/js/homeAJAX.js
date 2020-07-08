$(document).ready(function () {





    if(window.sessionStorage.getItem("username") == null){

        $.ajax({
            type: "GET",
            url: "/homeLoggedOut",
            async: true,
            success: function (result) {
                var parsed = $.parseHTML(result);
                result = $(parsed).find("#seriesContent");
                $('#seriesTable').html(result);
                console.log(result);

            },
            error: function (result){
                console.log(result);
            }

        });

    }


    else{

        var loginData = { username : window.sessionStorage.getItem("username") };

        $.ajax({
            type: "GET",
            url: "/homeLoggedIn",
            data: loginData,
            async: true,
            success: function (result) {
                var parsed = $.parseHTML(result);
                result = $(parsed).find("#seriesContent");
                $('#seriesTable').html(result);
                console.log(result);
            },
            error: function (result) {
                console.log(result);
            }
        });

    }

});

