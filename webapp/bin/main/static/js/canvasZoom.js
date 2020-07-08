var config = function() {
  return {
    height: 1080,
    width: 1920,
    canvasParentId: "canvasDiv",
    canvasId: "c"
  };
};

(function() {
  setZoom(window.canvas);
})();

function setZoom(canvas) {
  var canvasDiv = jQuery("#" + config().canvasParentId);
  var zoom = 1;
  var eleHeight = canvasDiv.height(),
    eleWidth = canvasDiv.width(),
    cHeight = canvas.height,
    cWidth = canvas.width;
  var height = eleHeight > cHeight ? eleHeight : cHeight;
  var width = eleWidth > cWidth ? eleWidth : cWidth;
  if (width > height) {
    width = eleWidth;
    height = eleHeight;
    zoom = width / config().width;
  } else {
    height = ((height * eleHeight) / config().height) * 0.8;
    zoom = height / config().height;
  }
  canvas.setZoom(zoom);
  canvas.setWidth(width);
  canvas.setHeight(height);

  window.zoom = zoom;
  canvas.renderAll();
}

window.onresize = function() {
  setZoom(window.canvas);
};
