package com.example.blogsystem.controller;

import com.example.blogsystem.common.My;
import com.example.blogsystem.entity.System;
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
    public My<String> updateSystem(@RequestBody System system) {
            int count =systemMapper.updateSystem(system) ;
            if (count > 0) {
                return new My(200, "更新成功", system);
            }
                return new My(-1, "更新失败", system);
        }
    //通过id查所有
    @PostMapping("byId")
    public My<String> selectSystemById(@RequestParam("id") Integer id){
        List<System> system1 = systemMapper.findSystemById(id);
        Integer code=-1;
        if (system1.size() > 0) {
            code = 200;
            return new My(code, "查询成功", system1);
        }
        return new My(code, "查询失败", null);
    }
}
