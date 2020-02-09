package com.lzhpo.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.admin.entity.Role;
import com.lzhpo.admin.mapper.RoleMapper;
import com.lzhpo.admin.service.RoleService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p> Author：lzhpo </p>
 * <p> Title：</p>
 * <p> Description：</p>
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoleServiceImpl extends ServiceImpl<RoleMapper,Role> implements RoleService {

    @Override
    public long getRoleNameCount(String name) {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "roles", allEntries = true)
    public Role saveRole(Role role) {
        baseMapper.insert(role);
        if(role.getMenuSet() != null && role.getMenuSet().size() > 0) {
            baseMapper.saveRoleMenus(role.getId(),role.getMenuSet());
        }
        return role;
    }

    @Override
    public Role getRoleById(String id) {
        return baseMapper.selectRoleById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "roles", allEntries = true)
    public void updateRole(Role role) {
        baseMapper.updateById(role);
        baseMapper.dropRoleMenus(role.getId());
        if(role.getMenuSet() != null && role.getMenuSet().size() > 0) {
            baseMapper.saveRoleMenus(role.getId(), role.getMenuSet());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "roles", allEntries = true)
    public void deleteRole(Role role) {
        role.setDelFlag(true);
        baseMapper.updateById(role);
        baseMapper.dropRoleMenus(role.getId());
        baseMapper.dropRoleUsers(role.getId());
    }

    @Override
    @Cacheable("roles")
    public List<Role> selectAll() {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }
}
