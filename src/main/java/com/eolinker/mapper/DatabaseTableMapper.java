package com.eolinker.mapper;

import com.eolinker.pojo.DatabaseTable;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据库表[数据库操作]
 *
 * @author www.eolinker.com 广州银云信息科技有限公司 2015-2018
 * eoLinker是目前全球领先、国内最大的在线API接口管理平台，提供自动生成API文档、API自动化测试、Mock测试、团队协作等功能，旨在解决由于前后端分离导致的开发效率低下问题。
 * 如在使用的过程中有任何问题，欢迎加入用户讨论群进行反馈，我们将会以最快的速度，最好的服务态度为您解决问题。
 * <p>
 * eoLinker AMS开源版的开源协议遵循GPL V3，如需获取最新的eolinker开源版以及相关资讯，请访问:https://www.eolinker.com/#/os/download
 * <p>
 * 官方网站：https://www.eolinker.com/ 官方博客以及社区：http://blog.eolinker.com/
 * 使用教程以及帮助：http://help.eolinker.com/ 商务合作邮箱：market@eolinker.com
 * 用户讨论QQ群：707530721
 * @name eolinker ams open source，eolinker开源版本
 * @link https://www.eolinker.com/
 * @package eolinker
 */
public interface DatabaseTableMapper {
    /**
     * 导入数据表
     *
     * @param dbID
     * @param tableList
     * @return
     */
    public Integer importDBTable(DatabaseTable databaseTable);

    /**
     * 获取数据库表信息
     *
     * @param dbID
     * @return
     */
    public List<DatabaseTable> getDatabaseTableInfo(@Param("dbID") int dbID);

    /**
     * 添加表
     *
     * @param databaseTable
     * @return
     */
    public int addTable(DatabaseTable databaseTable);

    /**
     * 检查数据表权限
     *
     * @param tableID
     * @param userID
     * @return
     */
    public Integer checkTablePermission(@Param("tableID") int tableID, @Param("userID") int userID);

    /**
     * 删除表
     *
     * @param tableID
     * @return
     */
    public int deleteTable(@Param("tableID") int tableID);

    /**
     * 获取表列表
     *
     * @param dbID
     * @return
     */
    public List<DatabaseTable> getTable(@Param("dbID") int dbID);

    /**
     * 编辑表
     *
     * @param databaseTable
     * @return
     */
    public int editTable(DatabaseTable databaseTable);

    public int getTableCount(@Param("dbID") Integer dbID);

}
