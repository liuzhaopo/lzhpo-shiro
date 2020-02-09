var iconShow,$;
layui.use(['form','jquery','layer'],function(){
    var form = layui.form,
        layer = layui.layer;
    $    = layui.jquery;

    var color = $("#input_hidden_menu_bgcolor").val();
    color = (color != null && color != "") ? color.substring(1) : "ff8800";
    $('.color-box').colpick({
        colorScheme:'dark',
        layout:'rgbhex',
        color:color,
        onSubmit:function(hsb,hex,rgb,el) {
            $(el).css('background-color', '#'+hex);
            $(el).colpickHide();
            $("input[name='bgColor']").val("#"+hex);
        }
    }).css('background-color', '#${color}');

    $("#selectIcon").on("click",function () {
        iconShow =layer.open({
            type: 2,
            title: '选择图标',
            shadeClose: true,
            content: '/static/page/icon.html'
        });
        layer.full(iconShow);
    });

    form.on("submit(editMenu)",function(data){
        if(data.field.id == null){
            layer.msg("菜单ID不存在",{time:1000});
            return false;
        }
        if(data.field.sort<0){
            layer.msg("排序值不能为负数",{time:1000});
            return false;
        }
        if(undefined !== data.field.isShow && null != data.field.isShow){
            data.field.isShow = true;
        }else{
            data.field.isShow = false;
        }
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.post("/admin/system/menu/edit",data.field,function (res) {
            layer.close(loadIndex);
            if(res.success){
                parent.layer.msg("菜单编辑成功!",{time:1500},function(){
                    //刷新父页面
                    parent.location.reload();
                });
            }else{
                layer.msg(res.message);
            }
        });
        return false;
    });

});