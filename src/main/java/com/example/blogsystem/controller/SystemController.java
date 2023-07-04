package com.example.blogsystem.controller;

import com.example.blogsystem.common.MyResult;
import com.example.blogsystem.entity.System;
import com.example.blogsystem.entity.User;
import com.example.blogsystem.mapper.SystemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author shj
 * @since 2023-06-28
 */
@RestController
@RequestMapping("/system")
public class SystemController {
    @Autowired
    private SystemMapper systemMapper;

    //修改系统配置
    @PostMapping("update")
    public MyResult<String> updateSystem(@RequestBody System system) {
            int count =systemMapper.updateSystem(system) ;
            if (count > 0) {
                return new MyResult(200, "更新成功", system);
            }
                return new MyResult(-1, "更新失败", system);
        }
    //通过id查所有
    @PostMapping("byId")
    public MyResult<String> selectSystemById(@RequestParam("id") Integer id){
        List<System> system1 = systemMapper.findSystemById(id);
        Integer code=-1;
        if (system1.size() > 0) {
            code = 200;
            return new MyResult(code, "查询成功", system1);
        }
        return new MyResult(code, "查询失败", null);
    }
}
