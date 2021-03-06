package com.xqoo.salecenter.controller;

import com.xqoo.common.page.PageRequestBean;
import com.xqoo.common.page.PageResponseBean;
import com.xqoo.common.core.utils.StringUtils;
import com.xqoo.feign.enums.operlog.OperationTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.xqoo.salecenter.service.SaleDetailInfoService;
import com.xqoo.salecenter.entity.SaleDetailInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import com.xqoo.common.core.entity.CurrentUser;
import com.xqoo.common.entity.ResultEntity;
import com.xqoo.feign.annotations.LoginUser;
import com.xqoo.feign.annotations.OperationLog;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * SaleDetailInfoController
 *
 * @author xqoo-code-gen
 * @date 2021-02-01 15:07:13
 */

@RestController
@RequestMapping("/saleDetailInfoHandle")
@Api(tags = "售卖产品明细表控制器")
@Validated
public class SaleDetailInfoController{

    /**
    * 注入当前登录人信息请在参数中添加 @ApiIgnore @LoginUser CurrentUser currentUser, @ApiIgnore是隐藏CurrentUser中的参数不在swagger中显示
    * 接口访问自动存储操作日志请使用@OperationLog(tips="插入,更新系统角色信息操作", operatorType = OperationTypeEnum.EDIT, isSaveRequestData = true)
    * tips为简要说明，operatorType为操作类型-枚举，isSaveRequestData默认为false，不存储请求参数，isSaveResponseData默认true，存储响应参数
    * 注解@RequestParam 类的参数无法存储，抓取不到
    * 注解@NotBlank 校验只对文本型参数有效，其余类型使用@NotNull，具体参见java参数校验注解使用
    * 注解@RequestBody 参数校验需要在实体bean前面加注解@Valid
    * 访问来源ip、端口、已登录人userId，userName在请求头中，请求头名字常量在com.xqoo.common.constants.SystemPublicConstant中
    *
    */

    @Autowired
    private SaleDetailInfoService saleDetailInfoService;

                            
    @ApiOperation("分页获取售卖产品明细表表数据")
    @PostMapping("/pageGetList")
    public ResultEntity<PageResponseBean<SaleDetailInfoEntity>> pageGetList(@RequestBody PageRequestBean page){
        return saleDetailInfoService.pageGetList(page);
    }

    @ApiOperation("批量新增数据")
    @PostMapping("/addRecordByList")
    @OperationLog(tips="批量新增sale_detail_info表数据", operatorType = OperationTypeEnum.ADD, isSaveRequestData = true)
    public ResultEntity<String> addRecordByList(@ApiIgnore @LoginUser CurrentUser currentUser,
                                        @RequestBody List<SaleDetailInfoEntity> list){
        if(StringUtils.isEmpty(currentUser.getUserId())){
            return new ResultEntity<>(HttpStatus.NOT_ACCEPTABLE, "未找到当前登录人信息，请重新登录重试");
        }
        return saleDetailInfoService.insertList(list, currentUser);
    }

    @ApiOperation("根据主键查询单条记录")
    @GetMapping("/getRecordByPrimaryKey")
    public ResultEntity<SaleDetailInfoEntity> getRecordByPrimaryKey(@RequestParam(required = false, value = "id")
                                                                      @NotNull(message = "主键值不能为空") Long id){
        SaleDetailInfoEntity entity = saleDetailInfoService.getOneSaleDetailInfoEntityByPrimaryKey(id);
        return new ResultEntity<>(HttpStatus.OK, "查询成功", entity);
    }
}
