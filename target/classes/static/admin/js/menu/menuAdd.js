var iconShow,$;
layui.use(['form','jquery','layer'],function(){
    var form = layui.form,
        layer = layui.layer;
    $ = layui.jquery;

    $('.color-box').colpick({
        colorScheme:'dark',
        layout:'rgbhex',
        color:'ff8800',
        onSubmit:function(hsb,hex,rgb,el) {
            $(el).css('background-color', '#'+hex);
            $(el).colpickHide();
            $("input[name='bgColor']").val("#"+hex);
        }
    }).css('background-color', '#ff8800');

    //选择图标
    $("#selectIcon").on("click",function () {
        iconShow =layer.open({
            type: 2,
            title: '选择图标',
            shadeClose: true,
            content: '/static/page/icon.html'
        });
        layer.full(iconShow);
    });

    form.on("submit(addMenu)",function(data){
        //判断左侧是否显示
        if(undefined !== data.field.isShow && null != data.field.isShow){
            data.field.isShow = true;
        }else{
            data.field.isShow = false;
        }
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.post("/admin/system/menu/add",data.field,function (res) {
            layer.close(loadIndex);
            if(res.success){
                parent.layer.msg("菜单添加成功!",{time:1500},function(){
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