layui.config({
    base: '/static/admin/'
}).extend({
    treetable: 'js/treetable'
}).use(['layer','form','table','treetable'], function() {
    var layer = layui.layer,
        $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        treetable = layui.treetable;

    treetable.render({
        treeColIndex: 1, //树形图标显示在第几列
        treeSpid: '-1', //最上级的父级id
        treeIdName: 'id', //id字段的名称
        treePidName: 'parentId', //pid字段的名称
        treeDefaultClose: true, //是否默认折叠
        elem: '#authTable',
        url: '/admin/system/menu/treeList',
        page: false, //分页，即使设置为true也不进行分页
        treeLinkage: false, //父级展开时是否自动展开所有子级
        cols: [[
            {type: 'numbers'},
            {field: 'name', minWidth: 100, title: '权限名称'},
            {field: 'icon', width: '8%',title: '图标',templet: function (d) {
                    if (d.icon == null) {
                        return '';
                    }
                    return '<i class="layui-icon" style="font-size: 30px;">'+ d.icon + '</i>';
                }
             },
            {field: 'href', title: '菜单url', width: '20%'},
            {field: 'sort', width: '8%', align: 'center', title: '排序号'},
            {
                field: 'isShow', width: '8%', align: 'center', templet: function (d) {
                    if (d.isShow) {
                        if(d.level == 1) {
                            return '<span class="layui-badge layui-bg-red">目录</span>';
                        } else {
                            return '<span class="layui-badge layui-bg-blue">菜单</span>';
                        }
                    }else {
                        return '<span class="layui-badge layui-bg-green">按钮</span>';
                    }
                }, title: '类型'
            },
            {templet: '#authBar', width: '30%', align: 'center', title: '操作'}
        ]],
        done: function () {
            layer.closeAll('loading');
        }
    });

    function layuiresize(index) {
        $(window).resize(function(){
            layer.full(index);
        });
        layer.full(index);
    };

    table.on('tool(authList)', function(obj){
        var data = obj.data;
        if(obj.event === 'addChildMenu') {
            var addIndex = layer.open({
                title: "添加系统菜单",
                type: 2,
                content: "/admin/system/menu/add?parentId=" + data.id,
                success: function (layero, addIndex) {
                    setTimeout(function () {
                        layer.tips('点击此处返回菜单列表', '.layui-layer-setwin .layui-layer-close', {
                            tips: 3
                        });
                    }, 500);
                }
            });
            layuiresize(addIndex);
        }
        if(obj.event === 'editChildMenu'){
            var editIndex = layer.open({
                title : "编辑菜单",
                type : 2,
                content : "/admin/system/menu/edit?id="+data.id,
                success : function(layero, editIndex){
                    setTimeout(function(){
                        layer.tips('点击此处返回菜单列表', '.layui-layer-setwin .layui-layer-close', {
                            tips: 3
                        });
                    },500);
                }
            });
            layuiresize(editIndex);
        }
        if(obj.event === "delMenu"){
            layer.confirm("你确定要删除该菜单么？这将会使得其下的所有子菜单都删除",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/admin/system/menu/delete",{"id":data.id},function (res){
                        if(res.success){
                            layer.msg("删除成功",{time: 1000},function(){
                                location.reload();
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
            )
        }
    });

    var active={
        btnExpand : function() {
            treetable.expandAll('#authTable');
        },
        btnFold : function () {
            treetable.foldAll('#authTable');
        },
        addMenu : function(){
            var addIndex = layer.open({
                title : "添加系统菜单",
                type : 2,
                content : "/admin/system/menu/add",
                success : function(layero, addIndex){
                    setTimeout(function(){
                        layer.tips('点击此处返回角色列表', '.layui-layer-setwin .layui-layer-close', {
                            tips: 3
                        });
                    },500);
                }
            });
            layuiresize(addIndex);
        }

    };

    $('.layui-inline .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });

   /*  $('#btnExpand').click(function () {
        console.log("btnExpand");
        treetable.expandAll('#authTable');
    });

    $('#btnFold').click(function () {
        console.log("btnFold");
        treetable.foldAll('#authTable');
    });*/

});