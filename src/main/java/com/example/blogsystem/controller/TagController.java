package com.example.blogsystem.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blogsystem.common.Result;
import com.example.blogsystem.dto.TagDto;
import com.example.blogsystem.entity.Tag;
import com.example.blogsystem.entity.User;
import com.example.blogsystem.mapper.TagMapper;
import com.example.blogsystem.service.BlogService;
import com.example.blogsystem.service.RelTagBlogService;
import com.example.blogsystem.service.TagService;
import com.example.blogsystem.util.RedisUtil;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
@RequestMapping("/tag")
@CrossOrigin(originPatterns = "http://localhost:8080")
public class TagController {
    @Autowired
    private TagService tagService;

    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private RelTagBlogService relTagBlogService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 传入参数，当前页，页面大小，条件
     * 分页显示所有标签，input是查询条件
     * @return
     */
    @GetMapping("/findAll")
    public Result<Page<TagDto>> pageTags(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String input){
        //分页构造器
        Page<Tag> pageInfo=new Page<>(page,pageSize);
        String key="cacheTags:"+page+"-"+pageSize+"-"+input;
        Page<TagDto> cacheTags= (Page<TagDto>) redisUtil.getCache(key);
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
            Page<TagDto> pageInfo1 = new Page<>();
           BeanUtils.copyProperties(pageInfo,pageInfo1,"records");
            List<Tag> records = pageInfo.getRecords();
            List<TagDto> list = new ArrayList<>();
            records.forEach(one->{
                TagDto tagDto = new TagDto();
                BeanUtils.copyProperties(one,tagDto);
                //查询blog表
                Integer blogCount = relTagBlogService.query().eq("tag_id", one.getId()).count();
                tagDto.setHot(blogCount);
                tagDto.setBlogCount(blogCount);
                list.add(tagDto);
            });

            pageInfo1.setRecords(list);

//            cacheCount=pageInfo.getTotal();
            //缓存
            redisUtil.setCache(key,pageInfo1);
//            redisUtil.setCache("cacheCount",pageInfo.getTotal());

            return Result.ok(pageInfo1,"获取成功");
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
            return Result.fail("添加失败");
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


    /**
     * 热门标签排行
     */
//
//    @Autowired
//    private RedisTemplate  redisTemplate;
    @GetMapping("/showHot")
    public Result<List<TagDto>> showHot(){
        String key="abc";
//        List<TagDto> cacheTags= (List<TagDto>) redisTemplate.opsForValue().get(key);
//        if (cacheTags!=null&&cacheTags.size()!=0){
//
//            return  Result.ok(cacheTags);
//        } else {
            List<Tag> list = tagService.list();
            List<TagDto> dtoList=list.stream().map((x)->{
                TagDto tmp=new TagDto();
                BeanUtils.copyProperties(x,tmp);
                tmp.setHot(relTagBlogService.query().eq("tag_id", x.getId()).count());
                tmp.setBlogCount(tmp.getHot());
                return tmp;
            }).collect(Collectors.toList());
            dtoList.sort((x,y)->{
                return y.getHot()- x.getHot();
            });
            dtoList= dtoList.subList(0,Math.min(20,dtoList.size()));
//            redisTemplate.opsForValue().set(key,dtoList);
            return Result.ok(dtoList);

//        }
    }

    public int compare(TagDto p1, TagDto p2) {
        return p1.getHot() - p2.getHot();  // 按照年龄升序排序
    }
//    @GetMapping("/showHot")
//    public Result<List<TagDto>> showHotTag() {
//        List<Tag> tagList = tagService.list();
//        Page<Tag> pageInfo = new Page<>(1, 20);
//        String key = "cacheTags:" + 1 + "-" + 20;
//        List<TagDto> tagDtoList = new ArrayList<>();
//        tagList.forEach(tag -> {
//            TagDto tagDto = new TagDto();
//            BeanUtils.copyProperties(tag, tagDto);
//            Integer blogCount = relTagBlogService.query().eq("tag_id", tag.getId()).count();
//            tagDto.setBlogCount(blogCount);
//            tagDto.setHot(blogCount);
//            tagDtoList.add(tagDto);
//        });
//        // 对tagDtoList按照bloghot降序排序
//        tagDtoList.sort((o1, o2) -> o2.getHot().compareTo(o1.getHot()));
//        // 取前20个tagDto
//        List<TagDto> result = tagDtoList.stream().limit(20).collect(Collectors.toList());
//        pageInfo.setRecords(result1);
//        return Result.ok(result, "获取成功");
//    }
}
