package com.atguigu.mybatis_plus;

import com.atguigu.mybatis_plus.entity.User;
import com.atguigu.mybatis_plus.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tituo
 * @create 2020-02-11 18:57
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CRUDTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testInsert(){
        User user = new User();
        user.setName("Helen");
        user.setAge(28);
        user.setEmail("abcd@qq.com");

        int result = userMapper.insert(user);
        //影响的行数
        log.info("影响的行数："+result);
        //id自动回填
        log.info("id" + user);

    }

    /**
     * 更新操作
     * UPDATE user SET age=? WHERE id=?
     */
    @Test
    public void testUpdateById(){
        User user = new User();
        user.setId(1227201266053033986L);
        user.setAge(28);

        int result = userMapper.updateById(user);
        log.info("影响的行数：" + result);
    }

    /**
     *  场景：并发修改
     *     当A用户根据ID获取了数据更新浏览数，同时B用户也获取了数据更新浏览数；
     *     如果A用户修改成功，那么B用户是否也能修改成功？
     *
     * 结论：此时两个用户查询了数据，view_count的值应该+2，
     *      但是测试结果只加了1，并报告两条记录都更新成功，因此并发操作数据不一致
     *
     * 悲观锁：synchronized
     *      无论有没有并发线程，都把这段代码加上锁，避免并发
     * 乐观锁：
     *      认为没有并发，当出现并发时再来解决
     *  步骤：
     *      1.在数据库中添加version字段
     *      2.查询时获取当前version：select 。。。。
     *      3.更新时带上version
     *  乐观锁插件步骤
     *      1.添加乐观锁配置
     *      2.在实体类中添加乐观锁字段（version）
     *
     *  总结：修改较多的并发不建议使用乐观锁，因为更新的时候需要发起反复的重试，反而影响系统的性能
     */
    @Test
    public void tsetConcurrentUpdate(){
        //第一个用户，查询数据
        User user1 = userMapper.selectById(1L);
        //修改用户数据
        user1.setViewCount(user1.getViewCount() + 1);


        //第二个用户
//        User user2 = userMapper.selectById(1L);
//        //修改用户数据
//        user2.setViewCount(user2.getViewCount() + 1);
//        //执行修改
//        int result2 = userMapper.updateById(user1);
//        log.info(result2 >0 ? "user1更新成功" : "use1更新失败");

        //执行修改
        int result1 = userMapper.updateById(user1);
        log.info(result1 >0 ? "user1更新成功" : "use1更新失败");
    }

    /**
     * 批量查询
     *  SELECT id,create_time,update_time,name,age,email,view_count,version FROM user WHERE id IN ( ? , ? , ? )
     */
    @Test
    public void testSelectBatchIds(){

        List<User> users = userMapper.selectBatchIds(Arrays.asList(1, 2, 3));
        users.forEach(System.out::println);
    }

    /**
     *条件查询
     * SELECT id,create_time,update_time,name,age,email,view_count,version FROM user WHERE name = ? AND age = ?
     */
    @Test
    public void testSelectByMap(){

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "Helen");
        map.put("age", 18);
        List<User> users = userMapper.selectByMap(map);

        users.forEach(System.out::println);
    }

    /**
     * 分页查询
     *  步骤
     *  1.添加插件
     *  SELECT id,create_time,update_time,name,age,email,view_count,version FROM user LIMIT 0,5
     */
    @Test
    public void testSelectPage() {

        Page<User> page = new Page<>(1,5);
        //第一个参数：page对象  第二个参数：查询构造器
        userMapper.selectPage(page, null);

        page.getRecords().forEach(System.out::println);
        System.out.println(page.getCurrent());
        System.out.println(page.getPages());
        System.out.println(page.getSize());
        System.out.println(page.getTotal());
        System.out.println(page.hasNext());
        System.out.println(page.hasPrevious());
    }


    /**
     * 返回指定的列
     * 当指定了特定的查询列时，希望分页结果列表只返回被查询的列，而不是很多null值
     * 可以使用selectMapsPage返回Map集合列表
     * SELECT name,age FROM user LIMIT 0,5
     */
    @Test
    public void testSelectMapsPage() {

        Page<User> page = new Page<>(1, 5);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("name", "age");

        IPage<Map<String, Object>> mapIPage = userMapper.selectMapsPage(page, queryWrapper);

        //注意：此行必须使用 mapIPage 获取记录列表，否则会有数据类型转换错误
        List<Map<String, Object>> records = mapIPage.getRecords();
        records.forEach(System.out::println);

        System.out.println(mapIPage.getCurrent());
        System.out.println(mapIPage.getPages());
        System.out.println(mapIPage.getSize());
        System.out.println(mapIPage.getTotal());
        //        System.out.println(mapIPage.hasNext());
        //        System.out.println(mapIPage.hasPrevious());
        System.out.println(page.hasNext());
        System.out.println(page.hasPrevious());
    }

    /**
     * 根据id删除记录
     * DELETE FROM user WHERE id=?
     */
    @Test
    public void testDeleteById(){

        int result = userMapper.deleteById(1227201266053033986L);
        System.out.println(result);
    }

    /**
     * 批量删除
     */
    @Test
    public void testDeleteBatchIds() {

        int result = userMapper.deleteBatchIds(Arrays.asList(8, 9, 10));
        System.out.println(result);
    }

    /**
     * 简单条件删除
     * DELETE FROM user WHERE name = ? AND age = ?
     */
    @Test
    public void testDeleteByMap() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "Helen");
        map.put("age", 18);

        int result = userMapper.deleteByMap(map);
        System.out.println(result);
    }

    /**
     * 逻辑删除
     *      物理删除：真实删除，将对应数据从数据库中删除，之后查询不到此条被删除数据
     *      逻辑删除：假删除，将对应数据中代表是否被删除字段状态修改为“被删除状态”，之后在数据库中仍旧能看到此条数据记录
     * 逻辑删除的使用场景
     *      可以进行数据恢复
     *      有关联数据，不便删除
     */
    @Test
    public void testLogicDelete() {

        int result = userMapper.deleteById(1L);
        System.out.println(result);
    }

    /**
     * 测试 逻辑删除后的查询：
     * 不包括被逻辑删除的记录
     */
    @Test
    public void testLogicDeleteSelect() {

        List<User> users = userMapper.selectList(null);
        users.forEach(System.out::println);
    }

    /**
     * 测试 性能分析插件
     * Time：5 ms - ID：com.atguigu.mybatis_plus.mapper.UserMapper.insert
     * Execute SQL：
     *     INSERT
     *     INTO
     *         user
     *         ( id, create_time, update_time, name, age, email )
     *     VALUES
     *         ( 1227588144103288833, null, '2020-02-12 21:39:55.697', '我是Helen', 18, 'helen@sina.com' )
     */
    @Test
    public void testPerformance() {
        User user = new User();
        user.setName("我是Helen");
        user.setEmail("helen@sina.com");
        user.setAge(18);
        userMapper.insert(user);
    }




}
