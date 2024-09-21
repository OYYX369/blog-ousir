package com.yupi.mianshiya.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.mianshiya.annotation.AuthCheck;
import com.yupi.mianshiya.common.BaseResponse;
import com.yupi.mianshiya.common.DeleteRequest;
import com.yupi.mianshiya.common.ErrorCode;
import com.yupi.mianshiya.common.ResultUtils;
import com.yupi.mianshiya.constant.UserConstant;
import com.yupi.mianshiya.exception.BusinessException;
import com.yupi.mianshiya.exception.ThrowUtils;
import com.yupi.mianshiya.model.dto.questionBankQuestion.QuestionBankQuestionAddRequest;
//import com.yupi.mianshiya.model.dto.questionBankQuestion.QuestionBankQuestionEditRequest;
import com.yupi.mianshiya.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.yupi.mianshiya.model.dto.questionBankQuestion.QuestionBankQuestionRemoveRequest;
import com.yupi.mianshiya.model.dto.questionBankQuestion.QuestionBankQuestionUpdateRequest;
import com.yupi.mianshiya.model.entity.QuestionBankQuestion;
import com.yupi.mianshiya.model.entity.User;
import com.yupi.mianshiya.model.vo.QuestionBankQuestionVO;
import com.yupi.mianshiya.service.QuestionBankQuestionService;
import com.yupi.mianshiya.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题库题目表接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@RestController
@RequestMapping("/questionBankquestion")
@Slf4j
public class QuestionBankQuestionController {

    @Resource
    private QuestionBankQuestionService questionBankquestionService;

    @Resource
    private UserService userService;


    @Resource
    private QuestionBankQuestionService questionBankQuestionService;

    // region 增删改查

    /**
     * 创建题库题目表
     *
     * @param questionBankquestionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestionBankQuestion(@RequestBody QuestionBankQuestionAddRequest questionBankquestionAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankquestionAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        QuestionBankQuestion questionBankquestion = new QuestionBankQuestion();
        BeanUtils.copyProperties(questionBankquestionAddRequest, questionBankquestion);
        // 数据校验
        questionBankquestionService.validQuestionBankQuestion(questionBankquestion, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        questionBankquestion.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = questionBankquestionService.save(questionBankquestion);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionBankQuestionId = questionBankquestion.getId();
        return ResultUtils.success(newQuestionBankQuestionId);
    }

    /**
     * 删除题库题目表
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestionBankQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        QuestionBankQuestion oldQuestionBankQuestion = questionBankquestionService.getById(id);
        ThrowUtils.throwIf(oldQuestionBankQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestionBankQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionBankquestionService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新题库题目表（仅管理员可用）
     *
     * @param questionBankquestionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestionBankQuestion(@RequestBody QuestionBankQuestionUpdateRequest questionBankquestionUpdateRequest) {
        if (questionBankquestionUpdateRequest == null || questionBankquestionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        QuestionBankQuestion questionBankquestion = new QuestionBankQuestion();
        BeanUtils.copyProperties(questionBankquestionUpdateRequest, questionBankquestion);
        // 数据校验
        questionBankquestionService.validQuestionBankQuestion(questionBankquestion, false);
        // 判断是否存在
        long id = questionBankquestionUpdateRequest.getId();
        QuestionBankQuestion oldQuestionBankQuestion = questionBankquestionService.getById(id);
        ThrowUtils.throwIf(oldQuestionBankQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionBankquestionService.updateById(questionBankquestion);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题库题目表（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionBankQuestionVO> getQuestionBankQuestionVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        QuestionBankQuestion questionBankquestion = questionBankquestionService.getById(id);
        ThrowUtils.throwIf(questionBankquestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(questionBankquestionService.getQuestionBankQuestionVO(questionBankquestion, request));
    }

    /**
     * 分页获取题库题目表列表（仅管理员可用）
     *
     * @param questionBankquestionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionBankQuestion>> listQuestionBankQuestionByPage(@RequestBody QuestionBankQuestionQueryRequest questionBankquestionQueryRequest) {
        long current = questionBankquestionQueryRequest.getCurrent();
        long size = questionBankquestionQueryRequest.getPageSize();
        // 查询数据库
        Page<QuestionBankQuestion> questionBankquestionPage = questionBankquestionService.page(new Page<>(current, size),
                questionBankquestionService.getQueryWrapper(questionBankquestionQueryRequest));
        return ResultUtils.success(questionBankquestionPage);
    }

    /**
     * 分页获取题库题目表列表（封装类）
     *
     * @param questionBankquestionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionBankQuestionVO>> listQuestionBankQuestionVOByPage(@RequestBody QuestionBankQuestionQueryRequest questionBankquestionQueryRequest,
                                                                                       HttpServletRequest request) {
        long current = questionBankquestionQueryRequest.getCurrent();
        long size = questionBankquestionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<QuestionBankQuestion> questionBankquestionPage = questionBankquestionService.page(new Page<>(current, size),
                questionBankquestionService.getQueryWrapper(questionBankquestionQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionBankquestionService.getQuestionBankQuestionVOPage(questionBankquestionPage, request));
    }

    /**
     * 分页获取当前登录用户创建的题库题目表列表
     *
     * @param questionBankquestionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionBankQuestionVO>> listMyQuestionBankQuestionVOByPage(@RequestBody QuestionBankQuestionQueryRequest questionBankquestionQueryRequest,
                                                                                         HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankquestionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        questionBankquestionQueryRequest.setUserId(loginUser.getId());
        long current = questionBankquestionQueryRequest.getCurrent();
        long size = questionBankquestionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<QuestionBankQuestion> questionBankquestionPage = questionBankquestionService.page(new Page<>(current, size),
                questionBankquestionService.getQueryWrapper(questionBankquestionQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionBankquestionService.getQuestionBankQuestionVOPage(questionBankquestionPage, request));
    }


    @PostMapping("/remove")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> removeQuestionBankQuestion(@RequestBody QuestionBankQuestionRemoveRequest questionBankQuestionRemoveRequest) {
        // 参数校验
        ThrowUtils.throwIf(questionBankQuestionRemoveRequest == null, ErrorCode.PARAMS_ERROR);
        Long questionBankId = questionBankQuestionRemoveRequest.getQuestionBankId();
        Long questionId = questionBankQuestionRemoveRequest.getQuestionId();
        ThrowUtils.throwIf(questionBankId == null || questionId == null, ErrorCode.PARAMS_ERROR);
        // 构造查询
        LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                .eq(QuestionBankQuestion::getQuestionId, questionId)
                .eq(QuestionBankQuestion::getQuestionBankId, questionBankId);
        boolean result = questionBankQuestionService.remove(lambdaQueryWrapper);
        return ResultUtils.success(result);
    }

}


