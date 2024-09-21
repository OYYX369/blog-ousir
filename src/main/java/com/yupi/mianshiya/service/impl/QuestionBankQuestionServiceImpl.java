package com.yupi.mianshiya.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.mianshiya.common.ErrorCode;
import com.yupi.mianshiya.constant.CommonConstant;
import com.yupi.mianshiya.exception.ThrowUtils;
import com.yupi.mianshiya.mapper.QuestionBankQuestionMapper;
import com.yupi.mianshiya.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.yupi.mianshiya.model.entity.Question;
import com.yupi.mianshiya.model.entity.QuestionBank;
import com.yupi.mianshiya.model.entity.QuestionBankQuestion;
import com.yupi.mianshiya.model.entity.User;
import com.yupi.mianshiya.model.vo.QuestionBankQuestionVO;
import com.yupi.mianshiya.model.vo.UserVO;
import com.yupi.mianshiya.service.QuestionBankQuestionService;
import com.yupi.mianshiya.service.QuestionBankService;
import com.yupi.mianshiya.service.QuestionService;
import com.yupi.mianshiya.service.UserService;
import com.yupi.mianshiya.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 题库题目表服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion> implements QuestionBankQuestionService {

    @Resource
    private UserService userService;


    @Resource
    @Lazy
    private QuestionService questionService;
    @Resource
    private QuestionBankService questionBankService;

    /**
     * 校验数据
     *
     * @param questionBankquestion
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validQuestionBankQuestion(QuestionBankQuestion questionBankquestion, boolean add) {
        ThrowUtils.throwIf(questionBankquestion == null, ErrorCode.PARAMS_ERROR);
        //题目和题库必须存在

        Long questionId = questionBankquestion.getQuestionId();
        if(questionId == null ){
            Question question = questionService.getById(questionId);
            ThrowUtils.throwIf(question == null, ErrorCode.PARAMS_ERROR);
        }
        Long questionBankId = questionBankquestion.getQuestionBankId();
        if(questionBankId == null ){
            QuestionBank questionBank = questionBankService.getById(questionBankId);
            ThrowUtils.throwIf(questionBank == null, ErrorCode.PARAMS_ERROR);
        }

        // 创建数据时，参数不能为空
//        if (add) {
//            // todo 补充校验规则
//            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
//        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则

//        if (StringUtils.isNotBlank(title)) {
//            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
//        }

    }

    /**
     * 获取查询条件
     *
     * @param questionBankquestionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankquestionQueryRequest) {
        QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
        if (questionBankquestionQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = questionBankquestionQueryRequest.getId();
        Long notId = questionBankquestionQueryRequest.getNotId();

        Long questionBankId = questionBankquestionQueryRequest.getQuestionBankId();
        Long questionId = questionBankquestionQueryRequest.getQuestionId();

        String sortField = questionBankquestionQueryRequest.getSortField();
        String sortOrder = questionBankquestionQueryRequest.getSortOrder();

        Long userId = questionBankquestionQueryRequest.getUserId();
        // todo 补充需要的查询条件
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionBankId), "questionBankId", questionBankId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题库题目表封装
     *
     * @param questionBankquestion
     * @param request
     * @return
     */
    @Override
    public QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankquestion, HttpServletRequest request) {
        // 对象转封装类
        QuestionBankQuestionVO questionBankquestionVO = QuestionBankQuestionVO.objToVo(questionBankquestion);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = questionBankquestion.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionBankquestionVO.setUser(userVO);

        // endregion

        return questionBankquestionVO;
    }

    /**
     * 分页获取题库题目表封装
     *
     * @param questionBankquestionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankquestionPage, HttpServletRequest request) {
        List<QuestionBankQuestion> questionBankquestionList = questionBankquestionPage.getRecords();
        Page<QuestionBankQuestionVO> questionBankquestionVOPage = new Page<>(questionBankquestionPage.getCurrent(), questionBankquestionPage.getSize(), questionBankquestionPage.getTotal());
        if (CollUtil.isEmpty(questionBankquestionList)) {
            return questionBankquestionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionBankQuestionVO> questionBankquestionVOList = questionBankquestionList.stream().map(questionBankquestion -> {
            return QuestionBankQuestionVO.objToVo(questionBankquestion);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        questionBankquestionVOPage.setRecords(questionBankquestionVOList);
        return questionBankquestionVOPage;
    }

}
