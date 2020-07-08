// Update LoginUI
jQuery(document).ready(function() {
  updateLoginUI();
});

// Signout - Update LoginUI
$("#signoutBtn_loggedin").click(function() {
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

// Search dropdown box
$(".dropdown-menu a").click(function() {
  // Update dropdown UI
  $(this)
    .parents(".input-group-prepend")
    .find(".btn")
    .html($(this).text());

  // Update drop box value
  $(this)
    .parents(".input-group-prepend")
    .find(".btn")
    .val($(this).data("value"));

  // Update searchOption
  $("#searchOption").val($(this).data("value"));
});