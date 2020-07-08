jQuery(document).ready(function() {
  $("#userNotLoggedInModal").modal("hide");
  console.log(window.sessionStorage.getItem("username"));

  if (window.sessionStorage.getItem("username") == null) {
    $('#userNotLoggedInModal').modal({backdrop: 'static', keyboard: false});
    $("#userNotLoggedInModal").modal("show");
  }
});
