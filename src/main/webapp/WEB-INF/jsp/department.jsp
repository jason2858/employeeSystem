<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
  <jsp:include page="header.jsp" />
  <!-- orgChart -->
  <script src="./Style/js/go.js"></script>
  <script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
  <!-- comfirm button -->
  <link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.0/jquery-confirm.min.css">
  <script
	src="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.0/jquery-confirm.min.js"></script>

  <style>
            body
            {
               display:none;/*全部先隱藏*/
            }
  </style>
    
  <!-- Setting -->
  <script src="./Style/js/YeseeGov.js"></script>
  <script type="text/javascript">
  var isFinishLoad = false;
  var companys = [];
  var authorise = "<%= session.getAttribute( "Authorise" ) %>";
  var nameSelect = "<%= session.getAttribute( "nameSelect" ) %>";
  		$(document).ready(function () {
  			$('#loading').modal('show');
			$("#loading").on('shown.bs.modal', function () {
				 isFinishLoad = true;
		    });
			closeLoading();
			
			if (authorise != 1 && authorise != 2) {
				window.location.href = "timeout.do";
			}
			if (authorise == 1 || authorise == 2 || authorise == 3) {
				appendUnsign();
	  			getUnsign();
			}
  			
  			$.ajax({
  				type:'post',
  				url:'/rest/department/getCompanys',
  				datatype:'text',
  				success:function(data){
  					var record=JSON.parse(data);
  					for(var i=0;i<record.length;i++){
  						companys.push(record[i]["name"]);
  					}
  					getParents();
  				},
  				error:function(){ 
  					confirm("訊息",'取得公司資料失敗');
  				} 
  			});
  	  		
  			init();
  			$("body").show();
  		});
  		
  		
  		function init() {

  		    myDiagram =
  		    	go.GraphObject.make(go.Diagram, "myDiagramDiv", // must be the ID or reference to div
  		        {
  		          initialContentAlignment: go.Spot.Center,
  		          maxSelectionCount: 1, // users can select only one part at a time
  		          "clickCreatingTool.archetypeNodeData": {}, // allow double-click in background to create a new node
  	          	  "clickCreatingTool.insertPart": function() {  // customize the data for the new node
  	          		$("#addModal").modal("show");
  	       		  },
  		          layout:
  		            go.GraphObject.make(go.TreeLayout,
  		              {
  		                treeStyle: go.TreeLayout.StyleLastParents,
  		                arrangement: go.TreeLayout.ArrangementHorizontal,
  		                // properties for most of the tree:
  		                angle: 90,
  		                layerSpacing: 35,
  		                // properties for the "last parents":
  		                alternateAngle: 90,
  		                alternateLayerSpacing: 35,
  		                alternateAlignment: go.TreeLayout.AlignmentBus,
  		                alternateNodeSpacing: 20
  		              }),
  		          "undoManager.isEnabled": true // enable undo & redo
  		        });

  		    var levelColors = ["#AC193D", "#2672EC", "#8C0095", "#5133AB",
  		                       "#008299", "#D24726", "#008A00", "#094AB2"];

  		    // override TreeLayout.commitNodes to also modify the background brush based on the tree depth level
  		    myDiagram.layout.commitNodes = function() {
  		      go.TreeLayout.prototype.commitNodes.call(myDiagram.layout);  // do the standard behavior
  		      // then go through all of the vertexes and set their corresponding node's Shape.fill
  		      // to a brush dependent on the TreeVertex.level value
  		      myDiagram.layout.network.vertexes.each(function(v) {
  		        if (v.node) {
  		          var level = v.level % (levelColors.length);
  		          var color = levelColors[level];
  		          var shape = v.node.findObject("SHAPE");
  		          if (shape) shape.fill = go.GraphObject.make(go.Brush, "Linear", { 0: color, 1: go.Brush.lightenBy(color, 0.05), start: go.Spot.Left, end: go.Spot.Right });
  		        }
  		      });
  		    };

  		    var nodeIdCounter = -1; // use a sequence to guarantee key uniqueness as we add/remove/modify nodes

  		    // when a node is double-clicked, add a child to it
  		    function nodeDoubleClick(e, obj) {
  		      var clicked = obj.part;
  		      if(clicked.data.parent == null){
  		    	  return;
  		      }
  		      if (clicked !== null) {
  		        var thisdep = clicked.data;
  		        $("#managerE").empty();
  				$("#key").val(thisdep.key);
  				$("#nameE").val(thisdep.name);
  	  			getManagers(thisdep.key,thisdep.manager);
  				if(thisdep.parent != null)
  					$("#parentE").val(thisdep.parent);
  				else
  					$("#parentE").val("none");
  				if(thisdep.comments != null)
  					$("#noteE").val(thisdep.comments);
  				else
  					$("#noteE").val("");
  				$("#editModal").modal("show");
  		      }
  		    }
  		    
  		    // This function provides a common style for most of the TextBlocks.
  		    // Some of these values may be overridden in a particular TextBlock.
  		    function textStyle() {
  		      return { font: "9pt  Segoe UI,sans-serif", stroke: "white" };
  		    }

  		    // define the Node template
  		    myDiagram.nodeTemplate =
  		      go.GraphObject.make(go.Node, "Auto",
  		    	{ doubleClick: nodeDoubleClick },
  		        // for sorting, have the Node.text be the data.name
  		        new go.Binding("text", "name"),
  		        // bind the Part.layerName to control the Node's layer depending on whether it isSelected
  		        new go.Binding("layerName", "isSelected", function(sel) { return sel ? "Foreground" : ""; }).ofObject(),
  		        // define the node's outer shape
  		        go.GraphObject.make(go.Shape, "Rectangle",
  		          {
  		            name: "SHAPE", fill: "white", stroke: null,
  		            // set the port properties:
  		            portId: "", fromLinkable: false, toLinkable: false, cursor: "pointer"
  		          }),
  		        go.GraphObject.make(go.Panel, "Horizontal",
  		          // define the panel where the text will appear
  		          go.GraphObject.make(go.Panel, "Table",
  		            {
  		              maxSize: new go.Size(150, 999),
  		              margin: new go.Margin(6, 10, 0, 3),
  		              defaultAlignment: go.Spot.Left
  		            },
  		            go.GraphObject.make(go.RowColumnDefinition, { column: 2, width: 4 }),
  		            go.GraphObject.make(go.TextBlock, textStyle(),  // the name
  		              {
  		                row: 0, column: 0, columnSpan: 5,
  		                font: "12pt Segoe UI,sans-serif",
  		                editable: false, isMultiline: false,
  		                minSize: new go.Size(10, 16)
  		              },
  		              new go.Binding("text", "name").makeTwoWay()),
  		            go.GraphObject.make(go.TextBlock, textStyle(),
  		              {
  		            	row: 1, column: 0, columnSpan: 2,
	  	              	font: "12px Roboto, sans-serif"
  		              },
  		            	new go.Binding("text", "", managerConverter)),
  	  		            go.GraphObject.make(go.TextBlock, textStyle(),
  	  		              {
  	  		            	row: 2, column: 0, columnSpan: 2,
  	  	              		font: "12px Roboto, sans-serif"
  	  		              },
  	  		              new go.Binding("text", "", sumConverter)),
  		            go.GraphObject.make(go.TextBlock, textStyle(),  // the comments
  		              {
  		                row: 4, column: 0, columnSpan: 5,
  		                font: "italic 9pt sans-serif",
  		                wrap: go.TextBlock.WrapFit,
  		                editable: false,  // by default newlines are allowed
  		                minSize: new go.Size(10, 14)
  		              },
  		              new go.Binding("text", "comments").makeTwoWay())
  		          )  // end Table Panel
  		        ) // end Horizontal Panel
  		      );  // end Node

  		    // define the Link template
  		    myDiagram.linkTemplate =
  		      go.GraphObject.make(go.Link, go.Link.Orthogonal,
  		        { corner: 5, relinkableFrom: true, relinkableTo: true },
  		        go.GraphObject.make(go.Shape, { strokeWidth: 4, stroke: "#00a4a4" }));  // the link shape

  		    // read in the JSON-format data from the "mySavedModel" element
  		    load();


  		    // support editing the properties of the selected person in HTML
  		    if (window.Inspector) myInspector = new Inspector("myInspector", myDiagram,
  		      {
  		        properties: {
  		          "key": { readOnly: true },
  		          "comments": {}
  		        }
  		      });
  		  }
  		
		function sumConverter(info) {
			var str = "";
			if(info.parent!=null){
				return "部門人數： "+ info.sum;
			}
			return str;
		}
		function managerConverter(info) {
			var str = "";
			if(info.parent!=null){
				return "部門主管： "+ info.manager;
			}
			return str;
		}
  			
  	  function load() {
  		timeoutCheck();
  		$.ajax({
			type:'post',
			url:'/rest/department/getRecords',
			datatype:'text',
			success:function(data){
	  	   		myDiagram.model = go.Model.fromJson(data);
			},
			error:function(){ 
				confirm("訊息","取得部門資料失敗");
			} 
		});
  	  }
  		
  	function getParents(){
  		timeoutCheck();
  		$.ajax({
			type:'post',
			url:'/rest/department/getParents',
			datatype:'text',
			success:function(data){
				$('#parent').empty();
				$('#parentE').empty();
				$('#parent').append('<option value="none" selected>無</option>');
				$('#parentE').append('<option value="none">無</option>');
				var record=JSON.parse(data);
				var company1 = true;
				var company2 = true;
				for(var i=0;i<record.length;i++){
					if(company1){
						$('#parent').append('<option value="none" disabled>【'+companys[0]+'】</option>');
						$('#parentE').append('<option value="none" disabled>【'+companys[0]+'】</option>');
						company1= false;
					}
					if(record[i]["companyId"]=='2'&&company2){
						$('#parent').append('<option value="none" disabled>【'+companys[1]+'】</option>');
						$('#parentE').append('<option value="none" disabled>【'+companys[1]+'】</option>');
						company2= false;
					}
					$('#parent').append('<option value="' + record[i]["id"] +'">' + record[i]["parent"] + '</option>');
					$('#parentE').append('<option value="' + record[i]["id"] +'">' + record[i]["parent"] + '</option>');
				}
			},
			error:function(){ 
				confirm("訊息","取得下拉部門失敗");
			} 
		});
  	  }
  	
  	function getManagers(id,manager){
  		timeoutCheck();
  		$.ajax({
			type:'post',
			url:'/rest/department/getManagers',
			datatype:'text',
			data:{
				id:id,
				Live:"Y"
			},
			success:function(data){
				var record=JSON.parse(data);
				$('#managerE').append('<option value="none" selected>無</option>');
				for(var i=0;i<record.length;i++){
					if(nameSelect=="TW"){
						$('#managerE').append('<option value="' + record[i]["name"] +'">' + record[i]["chineseName"] + '</option>');
					} else {
						$('#managerE').append('<option value="' + record[i]["name"] +'">' + record[i]["name"] + '</option>');
					}
				}
				
				//找出manager accountName
  				var value = $("#managerE option").filter(function() {
  				  return $(this).text() === manager;
  				}).first().attr("value");
  				$("#manager").val(value);
  				
  				if(manager != "無"){
  					if(nameSelect=="TW"){
  						var managerVal = $('#managerE option').filter(function() {
  							return $(this).text() === manager;
  						}).val();
  						$("#managerE").val(managerVal);
					} else {
  						$("#managerE").val(manager);
					}
  				}else{
  					$("#managerE").val("none");
  				}
			},
			error:function(){ 
				confirm("訊息","取得部門員工資料失敗");
			} 
		});
  	}
  	
  		function add(){
  			timeoutCheck();
  			var name = $("#name").val();
  			if(name.length < 1)
  				confirm("訊息","請填寫部門名稱");
  			else{
	  			var manager = null;
	  			if($("#parent").val() != "none")
	  				var parent = $("#parent").val();
	  			else
	  				var parent = null;
	  			var note = $("#note").val();
	  			$.ajax({
					type:'post',
					url:"/rest/department/addDepartment",
					datatype:'json',
					data : {
						name : name,
						manager : manager,
						parent : parent,
						note : note
					},
					success:function(data){
						$("#name").val("");
						$("#note").val("");
						$("#noteE").val("");
						confirm("訊息","新增成功");
						getParents();
						load();
						$("#addModal").modal("hide");
					},
					error:function(){ 
						confirm("訊息","新增失敗");
					} 
				});
  			}
  		}
  		
  		 
  		function edit(){
  			timeoutCheck();
  			var id = $("#key").val();
	  		var name = $("#nameE").val();
	  		var manager = $("#manager").val();
	  		if($("#managerE").val() != "none")
	  			var newManager = $("#managerE").val();
	  		else
	  			var newManager = null;
	  		if($("#parentE").val() != "none")
	  			var parent = $("#parentE").val();
	  		else
	  			var parent = null;
	  		var note = $("#noteE").val();
	  		$.ajax({
				type:'post',
				url:"/rest/department/editDepartment",
				datatype:'json',
				data : {
					id : id,
					name : name,
					newManager : newManager,
					parent : parent,
					note : note,
					manager : manager
				},
				success:function(data){
					$("#name").val("");
					$("#note").val("");
					$("#managerE").val("empty");
					$("#noteE").val("");
					confirm("訊息","修改成功");
					getParents();
					load();
					$("#editModal").modal("hide");
				},
				error:function(){ 
					confirm("訊息","修改失敗");
				} 
			});
  		}
  		
  		function delShow(){
  			timeoutCheck();
  			var id = $("#key").val();
  			$.confirm({
  				title: '刪除確認',
  				content: '確定刪除此部門?',
  			    buttons: {
  			    	 "刪除": {btnClass: 'btn-red',
  				        	action:function () {
  			timeoutCheck();
	  			$.ajax({
					type:'post',
					url:"/rest/department/delDepartment",
					datatype:'json',
					data : {
						id : id
					},
					success:function(data){
						var record=JSON.parse(data);
						if(record.status!=200){
							confirm("提示",record.entity);
							$("#editModal").modal("hide");
						}else {
							$("#name").val("");
							$("#note").val("");
							$("#managerE").val("empty");
							$("#noteE").val("");
							confirm("訊息","刪除成功");
							getParents();
							load();
							$("#editModal").modal("hide");
						}
					},
					error:function(data){ 
						confirm("訊息","刪除失敗");
					} 
				});
  		}
  				        }
  			    	,
  			    	"取消": {
  			            action: function () {
  			            	return;
  			            }
  			        }
  			    }
  			});
  		}

   </script>
</head>

<body class="fixed-nav sticky-footer" id="page-top">
	<!-- Navigation-->
	<jsp:include page="navbar.jsp" />
	<div class="content-wrapper">
		<div class="container-fluid">
			<div class="container-fluid">
				<div class="card mb-3">
					<div class="card-header">組織關係圖</div>
					<div class="card-body">
						<div class="table-responsive">
							<div id="myDiagramDiv" style="height: 600px;"></div>
						</div>
					</div>
				</div>
			</div>
			<jsp:include page="footer.jsp" />
			<!-- Scroll to Top Button-->
			<a class="scroll-to-top rounded" href="#page-top"> <i
				class="fa fa-angle-up"></i>
			</a>
		</div>
	</div>
	<div class="modal fade" id="addModal" tabindex="-1" role="dialog" aria-labelledby="addLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="addLabel">新增部門資料</h5>
            <button class="close" id="close" type="button" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">×</span>
            </button>
          </div>
          <div class="modal-body">
         			<p style="padding-top:1%;">
	         			<span class="short_label" style="text-align:right;width:30%;height:2rem;display:block;vetical-align:top;float:left;">部門名稱 : </span>
		            	<input id="name" type="text" style="margin-left:3%;margin-right:1.5%;width:50%">
					</p>
					<p style="padding-top:1%;">
						<span class="short_label" style="text-align:right;width:30%;display:block;vetical-align:top;float:left;">隸屬 : </span>
						<select id='parent' style="margin-left:3%;margin-right:1.5%;width:50%">
						</select>
					</p>
					<p style="padding-top:1%;">
						<span class="short_label" style="text-align:right;width:30%;display:block;vetical-align:top;float:left;">備註 : </span>
						<span class="short_text"><textarea rows="3"  id="note" style="margin-left:3%;resize: none; width:50%;"></textarea></span>
					</p>
          </div>
          <div class="modal-footer">
            <button class="btn btn-secondary" type="button" data-dismiss="modal" id="cancel" style="margin-right:5px;">取消</button>
            <button class="btn btn-primary" type="button" onclick="add()" id="add">送出</button>
          </div>
    	 </div>
    	</div>
    </div>
    
  <div class="modal fade" id="editModal" tabindex="-1" role="dialog" aria-labelledby="editLabel" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="editLabel">修改部門資料</h5>
            <button class="close" id="close" type="button" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">×</span>
            </button>
          </div>
          <div class="modal-body">
          			<input id="key" type="text" disabled="disabled" style="display:none">
          			<input id="manager" type="text" disabled="disabled" style="display:none">
         			<p style="padding-top:1%;">
	         			<span class="short_label" style="text-align:right;width:30%;height:2rem;display:block;vetical-align:top;float:left;">部門名稱 : </span>
		            	<input id="nameE" type="text" style="margin-left:3%;margin-right:1.5%;width:50%">
					</p>
					<p style="padding-top:1%;">
		            	<span class="short_label" style="text-align:right;width:30%;height:2rem;display:block;vetical-align:top;float:left;">部門主管 : </span>
		            	<select id='managerE'  style="margin-left:3%;margin-right:1.5%;width:50%">
							<option style="display:none;" value='empty' selected></option>
							<option value='none'>無</option>
						</select>
					</p>
					<p style="padding-top:1%;">
						<span class="short_label" style="text-align:right;width:30%;display:block;vetical-align:top;float:left;">隸屬 : </span>
						<select id='parentE' style="margin-left:3%;margin-right:1.5%;width:50%">
						</select>
					</p>
					<p style="padding-top:1%;">
						<span class="short_label" style="text-align:right;width:30%;display:block;vetical-align:top;float:left;">備註 : </span>
						<span class="short_text"><textarea rows="3"  id="noteE" style="margin-left:3%;resize: none; width:50%;"></textarea></span>
					</p>
          </div>
          <div class="modal-footer">
        	<button class="delbutton" type="button"  onclick="delShow()" style="margin-right:5px;">刪除</button>
            <button class="btn btn-primary" type="button" onclick="edit()">修改</button>
          </div>
    	 </div>
    	</div>
    </div>
    <div class="modal fade" id="loading" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop='static'  style="text-align: center;width:100%;height:100%;padding-left:0px;">
      <div class="modal-dialog">  
        <img src="./Style/images/loading.gif" style="padding-top:12rem;">
      </div>
  </div>
	<jsp:include page="JSfooter.jsp" />
</body>

</html>