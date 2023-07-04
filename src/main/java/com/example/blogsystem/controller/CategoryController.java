package com.example.blogsystem.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.example.blogsystem.common.Result;
import com.example.blogsystem.dto.Dictionary;
import com.example.blogsystem.entity.Category;
import com.example.blogsystem.entity.Tag;
import com.example.blogsystem.mapper.CategoryMapper;
import com.example.blogsystem.service.CategoryService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author shj
 * @since 2023-06-28
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    CategoryService categoryService;
    /**
     * 获取全部分类
     * @return
     */
    @GetMapping("/getCate")
    public Result<List> getAllCate(){
        List<Category> list = categoryMapper.getAllCate();
        return Result.ok(list);
    }

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping("/addCate")
    public Result<String> addCate(@RequestBody Category category){
        //先判断是否存在该分类名
        List<Category> res= categoryMapper.getByName(category.getCategoryName());
        if(res.size() == 0){
            Integer count = categoryMapper.addCate(category.getCategoryName(),category.getCategoryAvatar(),category.getCategoryDetail());
            if(count > 0){
                return Result.ok("添加成功");
            }else {
                return Result.fail("添加失败,请重试");
            }
        }else {
            return Result.fail("已存在该分类，请重新输入！");
        }
    }

    /**
     * 更新分类
     * @param category
     * @return
     */
    @PostMapping("/updateCate")
    public Result<String> updateCate(@RequestBody Category category) {
        //先判断是否存在该分类名
        List<Category> res= categoryMapper.getByName(category.getCategoryName());
        if(res.size()>0){
            return Result.fail("已存在该分类，请重新输入！");
        }else {
            Integer count = categoryMapper.updateCate(category);
            if(count > 0){
                return Result.ok("修改成功");
            }else {
                return Result.fail("修改失败");
            }
        }
    }

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @PostMapping("/delCate")
    public Result<String> delCate(@RequestParam("id") Integer id){
        //TODO:删除前判断在tb_blog表中是否有关联的博客
        Integer count = categoryMapper.delCate(id);
        if(count > 0){
            return Result.ok("删除成功");
        }else {
            return Result.fail("删除失败");
        }
    }

    /**
     * 分页查询分类信息
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping("/pagin")
    public Result<List> pagin(@RequestParam("pageNum") Integer pageNum,@RequestParam("pageSize") Integer pageSize){
        //将pageNum进行转换：pageNum：第几页，但是数据库limit需要的是开始查询的位置
        Integer start  = (pageNum - 1) * pageSize;//开始查询的位置
        List<Category> cateList = categoryMapper.paginUsers(start,pageSize);
        //可以使用HashMap来保存总条数以及查询结果一起发送给前端
        return Result.ok(cateList);
    }

    @PostMapping("/delBatch")
    public Result<String> delBatch(@RequestBody Map<String,List<Integer>> data) {
        List<Integer> ids = data.get("ids");
        int count = categoryMapper.delBatch(ids);
        if(count >0){
            return Result.ok("删除成功");
        }else{
            return Result.fail("删除失败");
        }
    }


    //返回标签数据字典
    @GetMapping("/getCategory")
    public Result getTagOptions(){
        List<Category> list = categoryService.query().select("id,category_name").list();
        List<Dictionary> dictList = list.stream().map(one -> {
            Dictionary dict = new Dictionary();
            dict.setValue(one.getId());
            dict.setLabel(one.getCategoryName());
            return dict;
        }).collect(Collectors.toList());

        return Result.ok(dictList);
    }
}
