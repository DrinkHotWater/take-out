package com.example.reggie.dto;


import com.example.reggie.entity.Setmeal;
import com.example.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    //套餐和菜品的关联关系（多条1对1的记录）
    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
