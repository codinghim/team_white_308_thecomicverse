(function() {
  var mouseFrom = {},
    mouseTo = {},
    drawType = null,
    canvasObjectIndex = 0,
    textbox = null;
  var drawWidth = 10;
  var color = "#E34F51";
  var drawingObject = null;
  var selectedObject = null;
  var moveCount = 1;
  var doDrawing = false;
  var palette = document.getElementById("colorpicker");
  var canvas = new fabric.Canvas("c", {
    isDrawingMode: true,
    skipTargetFind: true,
    selectable: false,
    selection: false,
    preserveObjectStacking: true
  });

  canvas.on("object:added", function() {
    if (!isRedoing) {
      h = [];
    }
    isRedoing = false;
  });

  var isRedoing = false;
  var h = [];

  function upload() {
    var fileInput = document.getElementById("imageFile");
    var fileType = fileInput.files[0].type;
    var url = URL.createObjectURL(fileInput.files[0]);

    if (fileType === "image/png" || fileType === "image/jpeg" || fileType === "image/gif") {
      //check if png or jpg
      fabric.Image.fromURL(url, function(img) {
        img.set({
          scaleY: 0.25,
          scaleX: 0.25
        });
        canvas.add(img);
      });
    } else if (fileType === "image/svg+xml") {
      //check if svg
      fabric.loadSVGFromURL(url, function(objects, options) {
        var svg = fabric.util.groupSVGElements(objects, options);
        svg.scaleToWidth(180);
        svg.scaleToHeight(180);
        canvas.add(svg);
      });
    }
  }

  function undo() {
    if (canvas._objects.length > 0) {
      h.push(canvas._objects.pop());
      canvas.renderAll();
    }
  }
  function redo() {
    if (h.length > 0) {
      isRedoing = true;
      canvas.add(h.pop());
    }
  }

  function sendFront() {
    canvas.bringToFront(canvas.getActiveObject());
  }

  function sendForward() {
    canvas.bringForward(canvas.getActiveObject());
  }

  function sendBackward() {
    canvas.sendBackwards(canvas.getActiveObject());
  }

  function sendBack() {
    canvas.sendToBack(canvas.getActiveObject());
  }

  window.canvas = canvas;
  window.zoom = window.zoom ? window.zoom : 1;

  canvas.freeDrawingBrush.color = color;
  canvas.freeDrawingBrush.width = drawWidth;

  $(document).ready(function() {
    document.getElementById("redo").addEventListener("click", function(ev) {
      redo();
    });
    document.getElementById("undo").addEventListener("click", function(ev) {
      undo();
    });

    $("#imageFile").change(function() {
      upload();
    });

    $("#sendBackButton").click(function() {
      sendBack();
    });

    $("#sendBackwardButton").click(function() {
      sendBackward();
    });

    $("#sendForwardButton").click(function() {
      sendForward();
    });

    $("#sendFrontButton").click(function() {
      sendFront();
    });

    $( "#save" ).click(function( event ) {
      this.href = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(canvas));
    });

    $( "#load" ).change(function( event ) {
      var fr = new FileReader();
      fr.onload = function(){
        canvas.loadFromJSON(this.result, canvas.renderAll.bind(canvas));
      };
      fr.readAsText(this.files[0]);
    });

    $( "#save_png" ).click(function( event ) {
      this.href = canvas.toDataURL("image/png;base64");
    });


  });

  function onObjectSelected(e) {
    console.log(e.target.get("type"));
    if (!e.target.get("type").includes("image")) {
      color = e.target.get("stroke");
      palette.value = color;
      selectedObject = e.target;
    }
  }
  function onObjectCleared(e) {
    selectedObject = null;
  }

  canvas.on("object:selected", onObjectSelected);
  canvas.on("selection:updated", onObjectSelected);

  canvas.on("selection:cleared", onObjectCleared);

  palette.addEventListener("change", function(e) {
    color = e.target.value;
    canvas.freeDrawingBrush.color = color;

    if (selectedObject != null) {
      selectedObject.set({ stroke: color });

      if (selectedObject.get("type").includes("text")) {
        selectedObject.set({ fill: color });
      }

      canvas.renderAll();
    }
  });

  canvas.on("mouse:wheel", function(opt) {
    var delta = opt.e.deltaY;
    var zoom = canvas.getZoom();
    zoom = zoom + delta / 200;
    if (zoom > 20) zoom = 20;
    if (zoom < 0.01) zoom = 0.01;
    canvas.setZoom(zoom);
    opt.e.preventDefault();
    opt.e.stopPropagation();
  });

  canvas.on("mouse:down", function(options) {
    var evt = options.e;

    if (evt.altKey === true) {
      this.isDragging = true;
      this.isDrawingMode = false;
      this.selection = false;
      this.lastPosX = evt.clientX;
      this.lastPosY = evt.clientY;
    } else {
      var xy = transformMouse(options.e.offsetX, options.e.offsetY);
      mouseFrom.x = xy.x;
      mouseFrom.y = xy.y;
      doDrawing = true;
    }

    //drawing();
  });
  canvas.on("mouse:up", function(options) {
    this.isDragging = false;
    var xy = transformMouse(options.e.offsetX, options.e.offsetY);
    mouseTo.x = xy.x;
    mouseTo.y = xy.y;

    if (drawType == "text") {
      drawing();
    }

    drawingObject = null;
    moveCount = 1;
    doDrawing = false;
  });
  canvas.on("mouse:move", function(options) {
    if (this.isDragging) {
      var e = options.e;
      this.viewportTransform[4] += e.clientX - this.lastPosX;
      this.viewportTransform[5] += e.clientY - this.lastPosY;
      this.requestRenderAll();
      this.lastPosX = e.clientX;
      this.lastPosY = e.clientY;
    } else {
      if (moveCount % 2 && !doDrawing) {
        return;
      }
      moveCount++;
      var xy = transformMouse(options.e.offsetX, options.e.offsetY);
      mouseTo.x = xy.x;
      mouseTo.y = xy.y;
      drawing();
    }
  });

  canvas.on("selection:created", function(e) {
    if (drawType == "remove") {
      if (e.target._objects) {
        var etCount = e.target._objects.length;
        for (var etindex = 0; etindex < etCount; etindex++) {
          canvas.remove(e.target._objects[etindex]);
        }
      } else {
        canvas.remove(e.target);
      }
      canvas.discardActiveObject();
    }
  });

  function transformMouse(mouseX, mouseY) {
    return { x: mouseX / window.zoom, y: mouseY / window.zoom };
  }

  jQuery("#toolsul")
    .find("li")
    .on("click", function() {
      jQuery("#toolsul")
        .find("li>i")
        .each(function() {
          jQuery(this).attr("class", jQuery(this).attr("data-default"));
        });
      jQuery(this)
        .addClass("active")
        .siblings()
        .removeClass("active");
      jQuery(this)
        .find("i")
        .attr(
          "class",
          jQuery(this)
            .find("i")
            .attr("class")
          //.replace("black", "select")
        );
      drawType = jQuery(this).attr("data-type");
      canvas.isDrawingMode = false;
      if (textbox) {
        textbox.exitEditing();
        textbox = null;
      }
      if (drawType == "pen") {
        canvas.isDrawingMode = true;
      } else if (drawType == "select") {
        canvas.selection = true;
        canvas.skipTargetFind = false;
        canvas.selectable = true;
      } else if (drawType == "remove") {
        canvas.selection = true;
        canvas.skipTargetFind = false;
        canvas.selectable = true;
      } else {
        canvas.skipTargetFind = true;
        canvas.selection = false;
      }
    });

  function drawing() {
    if (drawingObject) {
      canvas.remove(drawingObject);
    }
    var canvasObject = null;
    switch (drawType) {
      case "arrow":
        canvasObject = new fabric.Path(
          drawArrow(mouseFrom.x, mouseFrom.y, mouseTo.x, mouseTo.y, 30, 30),
          {
            stroke: color,
            fill: "rgba(255,255,255,0)",
            strokeWidth: drawWidth
          }
        );
        break;
      case "line":
        canvasObject = new fabric.Line(
          [mouseFrom.x, mouseFrom.y, mouseTo.x, mouseTo.y],
          {
            stroke: color,
            strokeWidth: drawWidth
          }
        );
        break;
      case "dottedline":
        canvasObject = new fabric.Line(
          [mouseFrom.x, mouseFrom.y, mouseTo.x, mouseTo.y],
          {
            strokeDashArray: [3, 1],
            stroke: color,
            strokeWidth: drawWidth
          }
        );
        break;
      case "circle":
        var left = mouseFrom.x,
          top = mouseFrom.y;
        var radius =
          Math.sqrt(
            (mouseTo.x - left) * (mouseTo.x - left) +
              (mouseTo.y - top) * (mouseTo.y - top)
          ) / 2;
        canvasObject = new fabric.Circle({
          left: left,
          top: top,
          stroke: color,
          fill: "rgba(255, 255, 255, 0)",
          radius: radius,
          strokeWidth: drawWidth
        });
        break;
      case "ellipse":
        var left = mouseFrom.x,
          top = mouseFrom.y;
        var radius =
          Math.sqrt(
            (mouseTo.x - left) * (mouseTo.x - left) +
              (mouseTo.y - top) * (mouseTo.y - top)
          ) / 2;
        canvasObject = new fabric.Ellipse({
          left: left,
          top: top,
          stroke: color,
          fill: "rgba(255, 255, 255, 0)",
          originX: "center",
          originY: "center",
          rx: Math.abs(left - mouseTo.x),
          ry: Math.abs(top - mouseTo.y),
          strokeWidth: drawWidth
        });
        break;
      case "square":
        break;
      case "rectangle":
        var path =
          "M " +
          mouseFrom.x +
          " " +
          mouseFrom.y +
          " L " +
          mouseTo.x +
          " " +
          mouseFrom.y +
          " L " +
          mouseTo.x +
          " " +
          mouseTo.y +
          " L " +
          mouseFrom.x +
          " " +
          mouseTo.y +
          " L " +
          mouseFrom.x +
          " " +
          mouseFrom.y +
          " z";
        canvasObject = new fabric.Path(path, {
          left: left,
          top: top,
          stroke: color,
          strokeWidth: drawWidth,
          fill: "rgba(255, 255, 255, 0)"
        });
        break;
      case "rightangle":
        var path =
          "M " +
          mouseFrom.x +
          " " +
          mouseFrom.y +
          " L " +
          mouseFrom.x +
          " " +
          mouseTo.y +
          " L " +
          mouseTo.x +
          " " +
          mouseTo.y +
          " z";
        canvasObject = new fabric.Path(path, {
          left: left,
          top: top,
          stroke: color,
          strokeWidth: drawWidth,
          fill: "rgba(255, 255, 255, 0)"
        });
        break;
      case "equilateral":
        var height = mouseTo.y - mouseFrom.y;
        canvasObject = new fabric.Triangle({
          top: mouseFrom.y,
          left: mouseFrom.x,
          width: Math.sqrt(Math.pow(height, 2) + Math.pow(height / 2.0, 2)),
          height: height,
          stroke: color,
          strokeWidth: drawWidth,
          fill: "rgba(255,255,255,0)"
        });
        break;
      case "isosceles":
        break;
      case "text":
        textbox = new fabric.IText("", {
          left: mouseTo.x,
          top: mouseTo.y,
          width: 200,
          fontSize: 200,
          fill: color,
          strokeWidth: 5,
          stroke: color
        });

        canvas.add(textbox);
        textbox.enterEditing();
        textbox.hiddenTextarea.focus();
        break;
      case "bubble":
        fabric.Image.fromURL("../image/speech_bubble.png", function(img) {
          img.set({
            scaleY: 0.5,
            scaleX: 0.5
          });
          canvas.add(img);
        });
        break;
      case "remove":
        break;
      default:
        break;
    }
    if (canvasObject) {
      // canvasObject.index = getCanvasObjectIndex();
      canvas.add(canvasObject); //.setActiveObject(canvasObject)
      drawingObject = canvasObject;
    }
  }

  function drawArrow(fromX, fromY, toX, toY, theta, headlen) {
    theta = typeof theta != "undefined" ? theta : 30;
    headlen = typeof theta != "undefined" ? headlen : 10;
    var angle = (Math.atan2(fromY - toY, fromX - toX) * 180) / Math.PI,
      angle1 = ((angle + theta) * Math.PI) / 180,
      angle2 = ((angle - theta) * Math.PI) / 180,
      topX = headlen * Math.cos(angle1),
      topY = headlen * Math.sin(angle1),
      botX = headlen * Math.cos(angle2),
      botY = headlen * Math.sin(angle2);
    var arrowX = fromX - topX,
      arrowY = fromY - topY;
    var path = " M " + fromX + " " + fromY;
    path += " L " + toX + " " + toY;
    arrowX = toX + topX;
    arrowY = toY + topY;
    path += " M " + arrowX + " " + arrowY;
    path += " L " + toX + " " + toY;
    arrowX = toX + botX;
    arrowY = toY + botY;
    path += " L " + arrowX + " " + arrowY;
    return path;
  }

  function getCanvasObjectIndex() {
    return canvasObjectIndex++;
  }
})();
