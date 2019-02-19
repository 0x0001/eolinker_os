package com.eolinker.controller;

import com.alibaba.fastjson.JSONArray;
import com.eolinker.pojo.Api;
import com.eolinker.pojo.Partner;
import com.eolinker.service.ApiGroupService;
import com.eolinker.service.ApiService;
import com.eolinker.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接口控制器
 *
 * @author www.eolinker.com 广州银云信息科技有限公司 2015-2018
 * eoLinker是目前全球领先、国内最大的在线API接口管理平台，提供自动生成API文档、API自动化测试、Mock测试、团队协作等功能，旨在解决由于前后端分离导致的开发效率低下问题。
 * 如在使用的过程中有任何问题，欢迎加入用户讨论群进行反馈，我们将会以最快的速度，最好的服务态度为您解决问题。
 * <p>
 * eoLinker AMS开源版的开源协议遵循GPL
 * V3，如需获取最新的eolinker开源版以及相关资讯，请访问:https://www.eolinker.com/#/os/download
 * <p>
 * 官方网站：https://www.eolinker.com/ 官方博客以及社区：http://blog.eolinker.com/
 * 使用教程以及帮助：http://help.eolinker.com/ 商务合作邮箱：market@eolinker.com
 * 用户讨论QQ群：707530721
 * @name eolinker ams open source，eolinker开源版本
 * @link https://www.eolinker.com/
 * @package eolinker
 */
@Slf4j
@Controller
@RequestMapping("/Api")
public class ApiController {

    @Resource
    private ApiService apiService;
    @Resource
    private ProjectService projectService;
    @Resource
    private ApiGroupService apiGroupService;

    private Map<String, Object> responseWithCode(String code) {
        Map<String, Object> map = new HashMap<>(3);
        map.put("statusCode", code);
        return map;
    }

    /**
     * 添加接口
     */
    @ResponseBody
    @RequestMapping(value = "/addApi", method = RequestMethod.POST)
    public Map<String, Object> addApi(Api api,
                                      @RequestParam(value = "apiHeader", required = false) String apiHeader,
                                      @RequestParam(value = "apiRequestParam", required = false) String apiRequestParam,
                                      @RequestParam(value = "apiResultParam", required = false) String apiResultParam,
                                      @SessionAttribute("useID") Integer userID
    ) {
        Integer projectID = apiGroupService.checkGroupPermission(api.getGroupID(), userID);
        Partner partner = projectService.getProjectUserType(userID, projectID);

        if (partner == null || partner.getUserType() < 0 || partner.getUserType() > 2) {
            return responseWithCode("100002");
        }

        //验证api是否已存在
        boolean checkApi = apiService.checkApi(api);

        // 验证api是否已存在:checkApi=true api已存在
        if (checkApi) {
            return responseWithCode("160002");
        }

        api.setUpdateUserID(userID);
        api.setProjectID(projectID);
        Integer apiID = apiService.addApi(api, apiHeader, apiRequestParam, apiResultParam);
        if (apiID > 0) {
            final Map<String, Object> map = responseWithCode("000000");
            map.put("statusCode", "000000");
            map.put("apiID", apiID);
            map.put("groupID", api.getGroupID());
            return map;
        }
        return responseWithCode("160000");
    }

    /**
     * 修改接口
     */
    @ResponseBody
    @RequestMapping(value = "/editApi", method = RequestMethod.POST)
    public Map<String, Object> editApi(Api api,
                                       @RequestParam(value = "apiHeader", required = false) String apiHeader,
                                       @RequestParam(value = "apiRequestParam", required = false) String apiRequestParam,
                                       @RequestParam(value = "apiResultParam", required = false) String apiResultParam,
                                       @RequestParam(value = "updateDesc", required = false) String updateDesc,
                                       @SessionAttribute("userID") Integer userID
    ) {

        Integer projectID = apiGroupService.checkGroupPermission(api.getGroupID(), userID);
        Partner partner = projectService.getProjectUserType(userID, projectID);

        //验证api是否已存在
        boolean checkApi = apiService.checkApi(api);

        if (partner == null || partner.getUserType() < 0 || partner.getUserType() > 2) {
            return responseWithCode("100002");
        }

        //验证api是否已存在:checkApi=true api已存在
        if (checkApi) {
            return responseWithCode("160002");
        }

        api.setUpdateUserID(userID);
        api.setProjectID(projectID);
        boolean result = apiService.editApi(api, apiHeader, apiRequestParam, apiResultParam, updateDesc);
        if (result) {
            final Map<String, Object> map = responseWithCode("000000");
            map.put("statusCode", "000000");
            map.put("apiID", api.getApiID());
            map.put("groupID", api.getGroupID());
            return map;
        }
        return responseWithCode("160000");
    }

    /**
     * 批量删除api,将其移入回收站
     */
    @ResponseBody
    @RequestMapping(value = "/removeApi", method = RequestMethod.POST)
    public Map<String, Object> removeApi(
        String apiID, Integer projectID,
        @SessionAttribute("userID") Integer userID
    ) {
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (partner == null || partner.getUserType() < 0 || partner.getUserType() > 2) {
            return responseWithCode("100002");
        }
        boolean result = apiService.removeApi(projectID, apiID, userID);
        return responseWithCode(result ? "000000" : "160000");
    }

    /**
     * 批量恢复接口
     */
    @ResponseBody
    @RequestMapping(value = "/recoverApi", method = RequestMethod.POST)
    public Map<String, Object> recoverApi(
        String apiID, Integer groupID,
        @SessionAttribute("userID") Integer userID
    ) {
        Integer projectID = apiGroupService.checkGroupPermission(groupID, userID);
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (partner == null || partner.getUserType() < 0 || partner.getUserType() > 2) {
            return responseWithCode("100002");
        }

        boolean result = apiService.recoverApi(projectID, groupID, apiID, userID);
        return responseWithCode(result ? "000000" : "160000");
    }

    /**
     * 切断删除接口
     */
    @ResponseBody
    @RequestMapping(value = "/deleteApi", method = RequestMethod.POST)
    public Map<String, Object> deleteApi(
        String apiID, Integer projectID,
        @SessionAttribute("userID") Integer userID
    ) {
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (partner == null || partner.getUserType() < 0 || partner.getUserType() > 2) {
            return responseWithCode("100002");
        }
        boolean result = apiService.deleteApi(projectID, apiID, userID);
        return responseWithCode(result ? "000000" : "160000");
    }

    /**
     * 清空回收站
     */
    @ResponseBody
    @RequestMapping(value = "/cleanRecyclingStation", method = RequestMethod.POST)
    public Map<String, Object> cleanRecyclingStation(Integer projectID,
                                                     @SessionAttribute("userID") Integer userID) {
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (partner == null || partner.getUserType() < 0 || partner.getUserType() > 2) {
            return responseWithCode("100002");
        }
        boolean result = apiService.cleanRecyclingStation(projectID, userID);
        return responseWithCode(result ? "000000" : "160000");
    }

    /**
     * 获取回收站接口列表
     */
    @ResponseBody
    @RequestMapping(value = "/getRecyclingStationApiList", method = RequestMethod.POST)
    public Map<String, Object> getRecyclingStationApiList(Integer projectID,
                                                          Integer orderBy, Integer asc,
                                                          @SessionAttribute("userID") Integer userID) {
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (partner == null) {
            return responseWithCode("100002");
        }
        List<Map<String, Object>> result = apiService.getRecyclingStationApiList(projectID, orderBy, asc);
        if (result != null && !result.isEmpty()) {
            final Map<String, Object> map = responseWithCode("000000");
            map.put("apiList", result);
            return map;
        }
        return responseWithCode("160000");
    }

    /**
     * 获取接口详情
     */
    @ResponseBody
    @RequestMapping(value = "/getApi", method = RequestMethod.POST)
    public Map<String, Object> getApi(Integer apiID, Integer projectID,
                                      @SessionAttribute("userID") Integer userID) {
        log.info("-----getApi-----");
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (partner == null) {
            return responseWithCode("100002");
        }
        Map<String, Object> result = apiService.getApi(projectID, apiID);
        if (result != null && !result.isEmpty()) {
            final Map<String, Object> map = responseWithCode("000000");
            map.put("apiInfo", result);
            return map;
        }
        return responseWithCode("160000");
    }

    /**
     * 获取分组接口列表
     */
    @ResponseBody
    @RequestMapping(value = "/getApiList", method = RequestMethod.POST)
    public Map<String, Object> getApiList(Integer projectID, Integer groupID,
                                          Integer orderBy, Integer asc,
                                          @SessionAttribute("userID") Integer userID) {
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (partner == null) {
            return responseWithCode("100002");
        }
        List<Map<String, Object>> result = apiService.getApiList(projectID, groupID, orderBy, asc);
        if (result != null && !result.isEmpty()) {
            final Map<String, Object> map = responseWithCode("000000");
            map.put("apiList", result);
            return map;
        }
        return responseWithCode("160000");
    }

    /**
     * 获取项目接口列表
     */
    @ResponseBody
    @RequestMapping(value = "/getAllApiList", method = RequestMethod.POST)
    public Map<String, Object> getAllApiList(Integer projectID, Integer orderBy,
                                             Integer asc,
                                             @SessionAttribute("userID") Integer userID) {
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (partner == null) {
            return responseWithCode("100002");
        }
        List<Map<String, Object>> result = apiService.getApiList(projectID, 0, orderBy, asc);
        if (result != null && !result.isEmpty()) {
            final Map<String, Object> map = responseWithCode("000000");
            map.put("apiList", result);
            return map;
        }
        return responseWithCode("160000");
    }

    /**
     * 获取接口详情
     */
    @ResponseBody
    @RequestMapping(value = "/searchApi", method = RequestMethod.POST)
    public Map<String, Object> searchApi(Integer projectID, String tips,
                                         @SessionAttribute("userID") Integer userID) {
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (partner == null) {
            return responseWithCode("100002");
        }
        List<Map<String, Object>> result = apiService.searchApi(projectID, tips);
        if (result != null && !result.isEmpty()) {
            final Map<String, Object> map = responseWithCode("000000");
            map.put("apiList", result);
            return map;
        }
        return responseWithCode("160000");
    }

    /**
     * 修改接口星标状态
     */
    @ResponseBody
    @RequestMapping(value = "/updateStar", method = RequestMethod.POST)
    public Map<String, Object> updateStar(Integer projectID, Integer apiID,
                                          Integer starred,
                                          @SessionAttribute("userID") Integer userID) {
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (partner == null) {
            return responseWithCode("100002");
        }
        boolean result = apiService.updateStar(projectID, apiID, userID, starred);
        return responseWithCode(result ? "000000" : "160000");
    }

    /**
     * 添加接口星标
     */
    @ResponseBody
    @RequestMapping(value = "/addStar", method = RequestMethod.POST)
    public Map<String, Object> addStar(Integer projectID, Integer apiID, @SessionAttribute("userID") Integer userID) {
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (partner == null) {
            return responseWithCode("100002");
        }
        Integer starred = 1;
        boolean result = apiService.updateStar(projectID, apiID, userID, starred);
        return responseWithCode(result ? "000000" : "160000");
    }

    /**
     * 去除接口星标
     */
    @ResponseBody
    @RequestMapping(value = "/removeStar", method = RequestMethod.POST)
    public Map<String, Object> removeStar(Integer projectID, Integer apiID, @SessionAttribute("userID") Integer userID) {
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (partner == null) {
            return responseWithCode("100002");
        }
        Integer starred = 0;
        boolean result = apiService.updateStar(projectID, apiID, userID, starred);
        return responseWithCode(result ? "000000" : "160000");
    }

    /**
     * 获取接口mock数据
     */
    @ResponseBody
    @RequestMapping(value = "/getApiMockData", method = RequestMethod.POST)
    public Map<String, Object> getApiMockData(Integer projectID, Integer apiID, @SessionAttribute("userID") Integer userID) {
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (partner == null) {
            return responseWithCode("100002");
        }
        Map<String, Object> result = apiService.getApiMockData(projectID, apiID);
        if (result != null && !result.isEmpty()) {
            result.put("statusCode", "000000");
            return result;
        }
        return responseWithCode("160000");
    }

    /**
     * 修改接口mock数据
     */
    @ResponseBody
    @RequestMapping(value = "/editApiMockData", method = RequestMethod.POST)
    public Map<String, Object> editApiMockData(Integer projectID, Integer apiID,
                                               String mockRule, String mockResult, String mockConfig,
                                               @SessionAttribute("userID") Integer userID) {
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (partner == null || partner.getUserType() < 0 || partner.getUserType() > 2) {
            return responseWithCode("100002");
        }
        boolean result = apiService.editApiMockData(projectID, apiID, mockRule, mockResult, mockConfig);
        return responseWithCode(result ? "000000" : "160000");
    }

    /**
     * 获取接口版本历史列表
     */
    @ResponseBody
    @RequestMapping(value = "/getApiHistoryList", method = RequestMethod.POST)
    public Map<String, Object> getApiHistoryList(Integer projectID, Integer apiID,
                                                 @SessionAttribute("userID") Integer userID) {
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (partner == null) {
            return responseWithCode("100002");
        }
        List<Map<String, Object>> result = apiService.getApiHistoryList(projectID, apiID);
        if (result != null && !result.isEmpty()) {
            final Map<String, Object> map = responseWithCode("000000");
            map.put("apiHistoryList", result);
            return map;
        }
        return responseWithCode("160000");
    }

    /**
     * 删除接口历史版本
     */
    @ResponseBody
    @RequestMapping(value = "/deleteApiHistory", method = RequestMethod.POST)
    public Map<String, Object> deleteApiHistory(Integer projectID, Integer apiID,
                                                Integer apiHistoryID, @SessionAttribute("userID") Integer userID) {
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (partner == null || partner.getUserType() < 0 || partner.getUserType() > 2) {
            return responseWithCode("100002");
        }
        boolean result = apiService.deleteApiHistory(projectID, apiID, userID, apiHistoryID);
        return responseWithCode(result ? "000000" : "160000");
    }

    /**
     * 切换接口历史版本
     */
    @ResponseBody
    @RequestMapping(value = "/toggleApiHistory", method = RequestMethod.POST)
    public Map<String, Object> toggleApiHistory(Integer projectID, Integer apiID,
                                                Integer apiHistoryID, @SessionAttribute("userID") Integer userID) {
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (partner == null || partner.getUserType() < 0 || partner.getUserType() > 2) {
            return responseWithCode("100002");
        }
        boolean result = apiService.toggleApiHistory(projectID, apiID, userID, apiHistoryID);
        return responseWithCode(result ? "000000" : "160000");
    }

    /**
     * 修改接口分组
     */
    @ResponseBody
    @RequestMapping(value = "/changeApiGroup", method = RequestMethod.POST)
    public Map<String, Object> changeApiGroup(Integer projectID, String apiID,
                                              Integer groupID, @SessionAttribute("userID") Integer userID) {
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (partner == null || partner.getUserType() < 0 || partner.getUserType() > 2) {
            return responseWithCode("100002");
        }
        boolean result = apiService.changeApiGroup(projectID, apiID, userID, groupID);
        return responseWithCode(result ? "000000" : "160000");
    }

    /**
     * 导出接口
     */
    @ResponseBody
    @RequestMapping(value = "/exportApi", method = RequestMethod.POST)
    public Map<String, Object> exportApi(
        HttpServletRequest request,
        Integer projectID, String apiID,
        @SessionAttribute("userID") Integer userID,
        @SessionAttribute("userName") String userName
    ) {
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (partner == null || partner.getUserType() < 0 || partner.getUserType() > 2) {
            return responseWithCode("100002");
        }
        List<Map<String, Object>> result = apiService.exportApi(projectID, apiID, userID);
        if (result != null && !result.isEmpty()) {
            try {
                File classPath = new File(ResourceUtils.getURL("classpath:").getPath());
                if (!classPath.exists()) {
                    classPath = new File("");
                }
                File dir = new File(classPath.getAbsolutePath(), "dump");
                if (!dir.exists() || !dir.isDirectory()) {
                    dir.mkdirs();
                }
                String path = dir.getAbsolutePath();
                String fileName = "/eoLinker_api_export_" + userName + "_"
                    + System.currentTimeMillis() + ".export";
                File file = new File(path + fileName);
                file.createNewFile();
                try (FileWriter fileWriter = new FileWriter(file)) {
                    JSONArray json = (JSONArray) JSONArray.toJSON(result);
                    fileWriter.write(json.toString());
                }
                final Map<String, Object> map = responseWithCode("000000");
                map.put("fileName", request.getContextPath() + "/dump" + fileName);
                return map;
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            }
        }
        return responseWithCode("160000");
    }

    /**
     * 导入接口
     */
    @ResponseBody
    @RequestMapping(value = "/importApi", method = RequestMethod.POST)
    public Map<String, Object> importApi(Integer projectID, String data, Integer groupID,
                                         @SessionAttribute("userID") Integer userID) {
        Partner partner = projectService.getProjectUserType(userID, projectID);
        if (data == null || data.isEmpty()) {
            return responseWithCode("160006");
        }
        if (partner == null || partner.getUserType() < 0 || partner.getUserType() > 2) {
            return responseWithCode("100002");
        }
        boolean result = apiService.importApi(projectID, groupID, userID, data);
        return responseWithCode(result ? "000000" : "160000");
    }
}
