$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	// 获取标题和正文
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	//发送异步请求
	$.post(
		 "/community/discuss/add",
		{"title":title,"content":content},
		function(data){
			data = $.parseJSON(data);
			// 再提示框里面显示返回消息
			$("#hintBody").text(data.msg);
			// 显示提示框
			$("#hintModal").modal("show");
			// 提示框显示两秒后自动关闭
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 如果发布成功，刷新当前页面
				if(data.code == 0){
					window.location.reload();
				}
			}, 2000);

		}
	)


}