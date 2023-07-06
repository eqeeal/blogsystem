package com.example.blogsystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogsystem.common.Result;
import com.example.blogsystem.dto.Dictionary;
import com.example.blogsystem.entity.Tag;
import com.example.blogsystem.mapper.TagMapper;
import com.example.blogsystem.service.TagService;
import com.example.blogsystem.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
@RequestMapping("/tag")
public class TagController {
    @Autowired
    private TagService tagService;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 传入参数，当前页，页面大小，条件
     * 分页显示所有标签，input是查询条件
     * @return
     */
    @GetMapping("/findAll")
    public Result<Page<Tag>> pageTags(@RequestParam Integer page,@RequestParam Integer pageSize,@RequestParam(required = false) String input){
        //分页构造器
        Page<Tag> pageInfo=new Page<>(page,pageSize);
        String key="cacheTags:"+page+"-"+pageSize+"-"+input;
        Page<Tag> cacheTags= (Page<Tag>) redisUtil.getCache(key);
//        Long cacheCount= (Long) redisUtil.getCache("cacheCount");
//        if (cacheCount!=null && cacheTags!=null){
        if (cacheTags!=null){
            //说明redis的cache中有缓存数据，
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("count",cacheCount);
//            map.put("list",cacheTags);
            return  Result.ok(cacheTags);
        } else {
            //没有该缓存，先去数据库拿数据，拿到之后再保存到redis中
            //拿到总条数
//            cacheCount=tagService.countTags(input);
//            cacheTags=tagService.findTags(input);
            //分页构造器
//            Page<Tag> pageInfo=new Page<>(page,pageSize);

            //条件构造器
            LambdaQueryWrapper<Tag> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.like(input!=null,Tag::getTagName,input);

            //查询分页
            tagService.page(pageInfo,queryWrapper);
            cacheTags=pageInfo;
//            cacheCount=pageInfo.getTotal();
            //缓存
            redisUtil.setCache(key,pageInfo);
//            redisUtil.setCache("cacheCount",pageInfo.getTotal());

            return Result.ok(pageInfo,"获取成功");
        }
    }

    /*
    传入标签对象
    添加标签
     */
    @PostMapping("/addTag")
    public Result<String> registTag(@RequestBody Tag tag){
        String tagName=tag.getTagName();
        Tag tag1 = tagService.query().eq("tag_name", tagName).one();
//        tagService.listByIds()
//        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq().like().ge();
//        tagService.remove(queryWrapper); delete from tag where ..
        //select tag_id from tag where
        if(tag1 != null){
            return Result.fail("标签已存在");
        }
        tagService.save(tag);
        redisUtil.cleanAll();
        return Result.ok("添加成功");
//        if (tagMapper.findTagByName(tag).size()>0){
//            //说明数据已经存在，不能进行tag的添加
//            return Result.fail("标签已经存在，无法添加");
//        }else {
//            tagMapper.insertTag(tag);
//            redisUtil.cleanAll();
//            return Result.ok(tag,"添加成功");
//        }
    }
        /**
        *传入参数，tag对象
        *更新标签
        */
    @PostMapping("/update")
    public Result<Tag> updateTag(@RequestBody Tag tag){
        QueryWrapper<Tag> queryWrapper=new QueryWrapper<>();
        QueryWrapper updateWrapper=queryWrapper.eq("id",tag.getId());
        if (tagService.update(tag,updateWrapper)){
            redisUtil.cleanAll();
            return Result.ok(tag,"标签更新成功");
        }else {
            return Result.fail("标签更新失败");
        }
    }


    /**
    *获取所有标签，没分页
     */
    @GetMapping("/findAllTags")
    public Result<List<Tag>> findAllTags(){
        List<Tag> tagList= tagService.findAll();
        System.out.println(tagList.toString());
        if (tagList.size()>0){
            return Result.ok(tagList,"所有标签");
        }else {
            return Result.fail("没有标签");
        }

    }

    /*
    传入标签id
    删除标签
     */
    @PostMapping("/delete")
    public Result<String> deleteTag(@RequestParam("id") Integer id){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",id);
        if (tagService.remove(queryWrapper)){
            redisUtil.cleanAll();
            return Result.ok(null,"删除成功");
        }else {
            return Result.fail("删除失败");
        }

    }


    //返回标签数据字典
    @GetMapping("/getTagOptions")
    public Result getTagOptions(){
        List<Tag> list = tagService.query().select("id,tag_name").list();
        List<Dictionary> tagList = list.stream().map(one -> {
            Dictionary tagDto = new Dictionary();
            tagDto.setValue(one.getId());
            tagDto.setLabel(one.getTagName());
            return tagDto;
        }).collect(Collectors.toList());

        return Result.ok(tagList);
    }


}
