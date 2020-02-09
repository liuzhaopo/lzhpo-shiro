package com.lzhpo.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.admin.entity.Menu;
import com.lzhpo.admin.entity.vo.ShowMenuVo;
import com.lzhpo.admin.mapper.MenuMapper;
import com.lzhpo.admin.service.MenuService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> Author：lzhpo </p>
 * <p> Title：</p>
 * <p> Description：</p>
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<ShowMenuVo> getShowMenuByUser(String id) {
        Map<String,Object> map = new HashMap();
        map.put("userId",id);
        map.put("parentId",null);
        return baseMapper.selectShowMenuByUser(map);
    }

    @Override
    @Cacheable("menus")
    public List<Menu> selectAllMenus(Map<String, Object> map) {
        return baseMapper.getMenus(map);
    }

    @Override
    @Cacheable("menus")
    public List<Menu> selectAllMenuList(Map<String, Object> map) {
        return baseMapper.selectByMap(map);
    }

    @Override
    public Menu selectById(String parentId) {
        return baseMapper.selectById(parentId);
    }

    @Override
    public Integer getCountByName(String name) {
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    public Integer getCountByPermission(String permission) {
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("permission",permission);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    public Integer selectFirstLevelMenuMaxSort() {
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        Object o = getObj(wrapper.select("max(sort) as sort").isNull("parent_id"));
        return  o == null ? 1 : ((Menu)o).getSort() + 1;
    }

    @Override
    public Integer seleclMenuMaxSortByPArentId(String parentId) {
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        Object o = getObj(wrapper.select("max(sort) as sort").eq("parent_id",parentId));
        return  o == null ? 1 : ((Menu)o).getSort() + 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "menus", allEntries = true)
    public void saveOrUpdateMenu(Menu menu) {
        saveOrUpdate(menu);
    }

}
