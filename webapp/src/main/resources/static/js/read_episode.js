$(document).ready(function () {

  if(window.sessionStorage.getItem("username") == null){
    document.getElementById("likeButton").disabled = true;
    document.getElementById("submitComic").style.visibility = 'hidden';
  }

  console.log(window.sessionStorage.getItem("username"));

  document.getElementById(
      "dummyUsernameForm1"
  ).value = window.sessionStorage.getItem("username");
  document.getElementById(
      "dummyUsernameForm2"
  ).value = window.sessionStorage.getItem("username");
    document.getElementById(
        "dummyUsernameForm3"
    ).value = window.sessionStorage.getItem("username");
    document.getElementById(
        "dummyUsernameForm5"
    ).value = window.sessionStorage.getItem("username");
    document.getElementById(
        "dummyUsernameForm8"
    ).value = window.sessionStorage.getItem("username");

    document.getElementById("userNameInput1").value = window.sessionStorage.getItem("username");
    document.getElementById("userNameInput2").value = window.sessionStorage.getItem("username");
    document.getElementById("userNameInput3").value = window.sessionStorage.getItem("username");
    document.getElementById("userNameInput4").value = window.sessionStorage.getItem("username");
    document.getElementById("userNameInput5").value = window.sessionStorage.getItem("username");
    document.getElementById("userNameInput6").value = window.sessionStorage.getItem("username");
});
