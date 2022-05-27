package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.CustomException;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.SetmealDish;
import com.example.reggie.mapper.SetmealMapper;
import com.example.reggie.service.SetmealDishService;
import com.example.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    SetmealDishService setmealDishService;
    /**
     * 新增套餐，同时需要保存套餐setmeal表和菜品的关联关系setmeal_dish表
     * @param setmealDto
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {

        // 1、保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);

        // 2、保存套餐和菜品的关联信息，操作setmeal_dish,执行insert操作
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        for(SetmealDish setmealDish : list) {
            setmealDish.setSetmealId(setmealDto.getId()); // 在每条关联关系中插入对应的套餐id即setmeal_id
        }
        setmealDishService.saveBatch(list);

    }

    /**
     * 删除套餐，需删除套餐表setmeal和套餐菜品关联表setmeal_dish（传入的是套餐id，即setmeal_id）
     * @param ids
     */
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 1、查询套餐状态，确定是否可用删除(若有任一个套餐是启用状态，则删除失败)
        //select count(*) from setmeal where id in (1,2,3) and status = 1
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(queryWrapper);
        if(count > 0) { // 如果不能删除(有任一个套餐是启用状态)，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        // 2.1、如果可以删除，先删除套餐表setmeal中的数据
        this.removeByIds(ids);

        // 2.2 再删除套餐菜品关联表setmeal_dish中的数据
        //delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(queryWrapper1);

    }
}
