$(document).ready(function() {
    document.getElementById("usernameField").value=window.sessionStorage.getItem("username");
    //console.log(window.localStorage.getItem("username"));
    //document.getElementById("usernameField").value=window.localStorage.getItem("username");
});