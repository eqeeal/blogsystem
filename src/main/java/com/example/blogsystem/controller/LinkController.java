package com.example.blogsystem.controller;

import com.example.blogsystem.entity.Link;
import com.example.blogsystem.entity.Result;
import com.example.blogsystem.mapper.LinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import com.example.blogsystem.util.RedisUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author shj
 * @since 2023-06-28
 */
@RestController
@RequestMapping("/link")
public class LinkController {
    @Autowired
    private LinkMapper linkMapper;

    //查询全部链接
    @GetMapping("/findall")
    public List<Link> index() {
        return linkMapper.findAll();
    }

    //新增链接
    @PostMapping("/add")
    public Result addLink(@RequestBody Link link) {
        String message = "添加失败";
        int code = -1;
        List<Link> list = linkMapper.findLinkByName(link.getLinkName());
        //只要list 长度大于0 那么就不能添加，反之可以添加
        if (list.size() == 0) {
            linkMapper.addLink(link);
            message = "添加成功";
            code = 200;
        }
        return new Result(code, message, "");
    }

    //删除链接
    @PostMapping("/del")
    public Result<Integer> delLink(@RequestBody Link link) {
        Integer count = linkMapper.delLink(link.getId());
        if (count > 0) {
            return new Result(200, "删除成功", "");
        } else {
            return new Result(200, "删除失败", "");
        }
    }

    //修改链接
    @PostMapping("/update")
    public Result<String> updateLink(@RequestBody Link link) {
        System.out.println(link);
        Integer count = linkMapper.updateLink(link);
        if (count > 0) {
            return new Result(200, "修改成功", "");
        } else {
            return new Result(-1, "修改失败", "");
        }
    }

    //分页查询
    @GetMapping("/pagin")
    public Result<HashMap> paginLink(@RequestParam("linkName") String linkName, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        List<Link> cacheLinks = (List<Link>) redisUtil.getCache("tb_link:" + pageNum + "-" + pageSize + ":" + "link_name" + ":" + linkName);
        Integer cacheCount = (Integer) redisUtil.getCache("tb_link:count:" + pageNum + "-" + pageSize + ":" + "link_name" + ":" + linkName);
        if (cacheLinks == null || cacheCount == null) {
            //没有该缓存，先去数据库拿数据，拿到之后再保存到redis中
            //拿到总条数
            cacheCount = linkMapper.linkTotalCount(linkName);
            //拿到分页查询数据
            //将pageNum进行转换：pageNum：第几页，但是数据库limit需要的是开始查询的位置
            Integer start = (pageNum - 1) * pageSize;//开始查询的位置
            cacheLinks = linkMapper.paginLink(linkName, start, pageSize);
            //将查询的数据放入Redis中
            redisUtil.setCache("tb_users:" + pageNum + "-" + pageSize + ":" + "link_name" + ":" + linkName, cacheLinks);
            redisUtil.setCache("tb_users:count:" + pageNum + "-" + pageSize + ":" + "link_name" + ":" + linkName, cacheCount);
            System.out.println("分页数据：加入缓存");
        } else {
            System.out.println("分页数据：使用缓存");
        }
        //可以使用HashMap来保存总条数以及查询结果一起发送给前端
        HashMap<String,Object> map = new HashMap();
        map.put("count",cacheCount);
        map.put("list",cacheLinks);
        return  new Result(200,"查询成功",map);

    }
    @PostMapping("/delLinks")
    public Result<Integer>  delLinks(@RequestBody Map<String,List<Integer>> data){
        List<Integer> ids = data.get("ids");
        int count = linkMapper.delLinks(ids);
        if(count >0){
            return new Result(200,"批量删除成功",count);
        }else{
            return new Result(200,"批量删除成功",-1);
        }
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedisUtil redisUtil;

}
