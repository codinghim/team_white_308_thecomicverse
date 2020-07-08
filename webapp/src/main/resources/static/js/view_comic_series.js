$(document).ready(function() {
    if(window.sessionStorage.getItem("username") == null){
        document.getElementById('subscribeButton').disabled = true;
    }
    document.getElementById("userNameInput1").value = window.sessionStorage.getItem("username");
    document.getElementById("userNameInput2").value = window.sessionStorage.getItem("username");
    document.getElementById(
        "dummyUsernameForm5"
    ).value = window.sessionStorage.getItem("username");
});
