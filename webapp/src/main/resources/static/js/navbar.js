// Signout
$("#signoutBtn").click(function() {
  window.sessionStorage.removeItem("username");
  updateLoginUI();
});

// Check username and Update LoginUI
function updateLoginUI() {
  console.log(window.sessionStorage.getItem("username"));
  if (window.sessionStorage.getItem("username") == null) {
    $("#loginSection").show();
    $("#loggedInSection").hide();
  } else {
    $("#loginSection").hide();
    $("#loggedInSection").show();
  }
}

function signout1() {
  window.sessionStorage.removeItem("username");
  updateLoginUI();
}

$(document).ready(function() {
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
      "dummyUsernameForm10"
  ).value = window.sessionStorage.getItem("username");
});
