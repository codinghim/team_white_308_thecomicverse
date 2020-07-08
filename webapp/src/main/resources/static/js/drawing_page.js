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

});



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

  // Update Series name to be added
  // $("#searchOption").val($(this).data("value"));
});


$( "#save_png" ).click(function( event ) {
  this.href = canvas.toDataURL("image/png;base64");
});
