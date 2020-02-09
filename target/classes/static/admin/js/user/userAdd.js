layui.use(['form','jquery','layer'],function(){
    var form = layui.form,
        $    = layui.jquery,
        layer = layui.layer;   //默认启用用户

    form.on("submit(addUser)",function(data){
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        //给角色赋值
        delete data.field["roles"];
        var selectRole = [];
        $('input[name="roles"]:checked').each(function(){
            selectRole.push({"id":$(this).val()});
        });
        data.field.roleLists = selectRole;

        //判断用户是否后台用户
        if(undefined !== data.field.adminUser && null != data.field.adminUser){
            data.field.adminUser = true;
        }else{
            data.field.adminUser = false;
        }

        //判断用户是否启用
        if(undefined !== data.field.locked && null != data.field.locked){
            data.field.locked = false;
        }else{
            data.field.locked = true;
        }

        $.ajax({
            type:"POST",
            url:"/admin/system/user/add",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(data.field),
            success:function(res){
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("用户添加成功!",{time:1500},function(){
                        //刷新父页面
                        parent.location.reload();
                    });
                }else{
                    layer.msg(res.message);
                }
            }
        });
        return false;
    });

    form.on('switch(adminUser)', function(data){
        $("#adminUser").val(data.elem.checked);
    });

    form.on('switch(locked)', function(data){
        $("#locked").val(data.elem.checked);
    });

});