$(".dropdown-menu a").click(function() {
  // Update dropdown UI
  $(this)
    .parents(".dropdown")
    .find(".btn")
    .html($(this).text());

  // Update drop box value
  $(this)
    .parents(".dropdown")
    .find(".btn")
    .val($(this).data("value"));

  $("#seriesID").val($(this).data("value"));
});

function encodeImageFileAsURL(inputID, outputID) {
  var file = document.getElementById(inputID).files[0];
  var reader = new FileReader();
  reader.onloadend = function() {
    console.log("RESULT", reader.result);
    document.getElementById(outputID).value = reader.result;
  };
  reader.readAsDataURL(file);
}
