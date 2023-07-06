package com.example.blogsystem.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.example.blogsystem.common.Result;
import com.example.blogsystem.dto.Dictionary;
import com.example.blogsystem.entity.Category;
import com.example.blogsystem.entity.Tag;
import com.example.blogsystem.mapper.CategoryMapper;
import com.example.blogsystem.util.RedisUtil;
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
    RedisUtil redisUtil;
    @Autowired
    CategoryService categoryService;
    /**
     * 获取全部分类(前五条)
     * @return
     */
    @GetMapping("/getCate")
    public Result<HashMap> getAllCate(){
        List<Category> list = categoryMapper.getAllCate();
        Integer count = categoryMapper.getCateCount("");
        HashMap<String,Object> map = new HashMap();
        map.put("list",list);
        map.put("count",count);
        return Result.ok(map);
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
                //新增数据后清空redis缓存
                redisUtil.cleanAll();
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
                //更新数据后清空redis缓存
                redisUtil.cleanAll();
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
        //删除前判断在tb_blog表中是否有关联的博客
        Integer rel = categoryMapper.getCateBlog(id);
        if(rel > 0){
            return Result.fail("该分类存在关联的博客，删除失败");
        }
        Integer count = categoryMapper.delCate(id);
        if(count > 0){
            //删除数据后清空redis缓存
            redisUtil.cleanAll();
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
    public Result<HashMap> pagin(@RequestParam("categoryName") String categoryName,@RequestParam("pageNum") Integer pageNum
                                ,@RequestParam("pageSize") Integer pageSize){
        //从redis获取数据
        List<Category> cacheCate =(List<Category>) redisUtil.getCache("tb_category:"+pageNum+"-"+pageSize+":"+"categoryName"+":"+categoryName);
        Integer cacheCount = (Integer) redisUtil.getCache("tb_category:count:"+pageNum+"-"+pageSize+":"+"categoryName"+":"+categoryName);
        //redis中没有缓存，再重新获取
        if(cacheCate==null || cacheCount==null){
            //拿到数据总数
            cacheCount = categoryMapper.getCateCount(categoryName);
            //将pageNum进行转换：pageNum：第几页，但是数据库limit需要的是开始查询的位置
            Integer start  = (pageNum - 1) * pageSize;//开始查询的位置
            cacheCate = categoryMapper.paginUsers(categoryName,start,pageSize);
            //将查询的数据放入Redis中
            redisUtil.setCache("tb_category:"+pageNum+"-"+pageSize+":"+"categoryName"+":"+categoryName,cacheCate);
            redisUtil.setCache("tb_category:count:"+pageNum+"-"+pageSize+":"+"categoryName"+":"+categoryName,cacheCount);
            System.out.println("分页数据：加入缓存");
        }else{
            System.out.println("分页数据：使用缓存");
        }
        //可以使用HashMap来保存总条数以及查询结果一起发送给前端
        HashMap<String,Object> map = new HashMap();
        map.put("count",cacheCount);
        map.put("list",cacheCate);
        return Result.ok(map);
    }

    /**
     * 批量删除
     * @param data
     * @return
     */
    @PostMapping("/delBatch")
    public Result<String> delBatch(@RequestBody Map<String,List<Integer>> data) {
        List<Integer> ids = data.get("ids");
        //删除前判断在tb_blog表中是否有关联的博客
        for (Integer item: ids) {
            //删除前判断在tb_blog表中是否有关联的博客
            Integer rel = categoryMapper.getCateBlog(item);
            if(rel > 0){
                return Result.fail("选择的分类存在关联的博客，删除失败");
            }
        }
        int count = categoryMapper.delBatch(ids);
        if(count >0){
            //删除数据后清空redis缓存
            redisUtil.cleanAll();
            return Result.ok("删除成功");
        }else{
            return Result.fail("删除失败");
        }
    }

    @GetMapping("/hotCate")
    public Result<List<HashMap>> getHotCate() {
        //获取到各个分类绑定的博客数量 并排序 获取前十条
        List<HashMap> map = categoryMapper.getHotCate();
        return Result.ok(map);
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

    //所有数据排序(用户id)
    @GetMapping("/hotCategory")
    public Result<List<HashMap>> getHotCategory() {
        //获取到各个分类绑定的博客数量 并排序
        List<HashMap> map = categoryMapper.getHotCategory();
        return Result.ok(map);
    }

    //所有数据排序(用户id)
    @GetMapping("/getHotCategoryByUserId")
    public Result<List<HashMap>> getHotCategoryByUserId(@RequestParam(required = false) Integer id) {
        //获取到各个分类绑定的博客数量 并排序
        List<HashMap> map = categoryMapper.getHotCategoryByUserId(id);
        return Result.ok(map);
    }

}
